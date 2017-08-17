package cyou.mrd.io.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrd.encode.HTTPEncodeUtil;

import cyou.mrd.ObjectAccessor;
import cyou.mrd.Platform;
import cyou.mrd.game.Server;
import cyou.mrd.io.Packet;
import cyou.mrd.packethandler.HttpHandlerDispatch;
import cyou.mrd.packethandler.HttpHandlerDispatchManager;
import cyou.mrd.util.ConfigKeys;
import cyou.mrd.util.ErrorHandler;
import cyou.mrd.util.RunTimeMonitor;
import cyou.mrd.util.Utils;

/**
 * 
 * HTTP入口类
 * 
 * @author Administrator
 * @File MRDServlet.java
 * @Time 2012-3-20 下午1:44:45
 */
public class MRDServlet extends HttpServlet {
	protected static final Logger log = LoggerFactory.getLogger(MRDServlet.class);

	private static final String CONTENT_TYPE = "json";
	private static final String Encoding = "UTF-8";
	private static final String IsEncodeHeaderKey = "IsEncode";
	private static boolean beEncode = false;

	/**
	 * vision 1.0
	 */
	private static final long serialVersionUID = 3841127544892979963L;

	private static final long STD_HTTP_REQUEST_TIME = 100;

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			Platform.setWebRootPath(this.getServletContext().getRealPath(""));
			log.info("MRDServlet.init() in [{}]!!", Platform.getWebRootPath());
			
			Server.init();
			beEncode = Platform.getConfiguration().getBoolean(ConfigKeys.SERVER_HTTP_ENCODE_MODE);
		} catch (Throwable e) {
			log.error("[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[SERVLET START ERROR!!!]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]", e);
			System.exit(0);
		}
		
		
	}

	@Override
	public void destroy() {
		super.destroy();
		Platform.shutdown();

	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		long  t1 = System.nanoTime();
		try {
			if(Thread.currentThread().getPriority() != Thread.MIN_PRIORITY) {
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			}
			
			RunTimeMonitor rt = new RunTimeMonitor();
			String clientEncode = request.getHeader(IsEncodeHeaderKey);
			// 客户端是否为加密模式
			boolean isClientEncode = clientEncode != null && clientEncode.equals("1");
			// 客户端与服务器的加密模式不一致时 返回协议 要求客户端修改加密模式 只含有包头
			if (beEncode != isClientEncode) {
				log.info("server encode model != client encode model   server:{},client:{}", beEncode ? "YES" : "NO",
						isClientEncode ? "YES" : "NO");
				sendError(response, isClientEncode, ErrorHandler.ERROR_CODE_ENCODE, 0);
				log.info(rt.toString(0));
				return;
			}
			// 从流中读取数据
			String jsonStr = this.read(request, isClientEncode, rt);
			rt.knock("read");
			if (jsonStr == null || jsonStr.length() == 0) {
				log.info(rt.toString(0));
				return;
			}
			JSONObject json = JSONObject.fromObject(jsonStr);
			rt.knock("StrToJson");
			int opcode = 0;
			try {
				opcode = json.getInt("opcode");
			} catch (JSONException e) {
				log.error("[HTTP] get opcode Error", e);
				this.sendError(response, isClientEncode, ErrorHandler.ERROR_CODE_2, opcode);
				rt.knock("JSONException");
				log.info(rt.toString(opcode));
				return;
			}
			Packet packet = new JSONPacket(opcode, json.getJSONObject("data"));
			HSession session = SessionManager.findGameSession(request,packet);
			rt.knock("findSession");
			if (session == null) {
				if (opcode == HOpCode.PLAYER_LOGIN_CLIENT) {
					if (Platform.willBomb()) {
						log.error("[HTTP] server overload! ObjectAccessor:{}", ObjectAccessor.size());
						response.sendError(500);
						rt.knock("willBomb");
						log.info(rt.toString(opcode));
						return;
					}
					session = new HSession(Utils.getIp(request));
				} else {
					this.sendError(response, isClientEncode, ErrorHandler.ERROR_CODE_1, opcode);
					rt.knock("ERROR_CODE_1");
					log.info(rt.toString(opcode));
					return;
				}
			}
			// 如果有admin需求，考虑新写一个servlet， 那里再来取得管理员的分配器
			HttpHandlerDispatch handler = HttpHandlerDispatchManager.get(HttpHandlerDispatch.PLAYER);
//			rt.knock("findDispatch");
			try {
				packet.setRunTimeMonitor(rt);
				handler.handle(packet, session);
				packet = null;
				rt.knock("handle");
				SessionManager.activeSession(session, response);
//				rt.knock("activeSession");
				this.send(response, isClientEncode, session.willSendPackets());
//				rt.knock("sendResponse");
				session.clear();
			} catch (Throwable e) {
				log.error("MRDServletException", e);
				// 下发错误信息
				this.sendError(response, isClientEncode, ErrorHandler.ERROR_CODE_0, opcode);
				rt.knock("Exception");
				log.info(rt.toString(opcode));
				return;
			}
			rt.knock("over");
			log.info(rt.toString(opcode));
		} catch (Throwable a) {
			log.error("Service error", a);
			this.sendError(response, request.getHeader(IsEncodeHeaderKey) != null && request.getHeader(IsEncodeHeaderKey).equals("1")?true:false, ErrorHandler.ERROR_CODE_0, 0);
		}finally {
			long t, t2;
			t2 = System.nanoTime();
			t = (t2 - t1)/1000000L;
			if(t < STD_HTTP_REQUEST_TIME) {
				try {
					Thread.sleep(STD_HTTP_REQUEST_TIME - t);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private String read(HttpServletRequest request, boolean isClientEncode, RunTimeMonitor rt) throws IOException {
//		log.info("[Encode] server EnCode model:{},client EnCode model:{}", beEncode ? "YES" : "NO", isClientEncode ? "YES" : "NO");
		InputStream is = request.getInputStream();
		int bufSize = request.getContentLength();
		// 获得原始数据
		byte[] buffer = new byte[bufSize];
		int size = is.read(buffer);
		int readedSize = size;
		if (size != bufSize) {
			while (size > -1) {
				size = is.read(buffer, readedSize, bufSize - readedSize);
				readedSize += size;
			}
		}
		// 加密模式以服务器为准
		if (beEncode) {
			byte[] newbuffer = new byte[bufSize * 16];
			long t1 = System.nanoTime();
			int ret = HTTPEncodeUtil.Decode(buffer, buffer.length, newbuffer, newbuffer.length);
			long t2 = System.nanoTime();
			log.info("[Decode] contentLength:{}, return lengrh:{}, Use Time:{}ms", new Object[] { bufSize, ret, (t2 - t1) / 1000000 });
			return new String(newbuffer, 0, ret, Encoding);
		} else {// 明文模式
			return new String(buffer, Encoding);
		}
	}

	private void send(HttpServletResponse response, boolean isClientEncode, JSONArray jsons) {
		response.setContentType(CONTENT_TYPE);
		response.setCharacterEncoding(Encoding);
		if (jsons.size() > 0) {
			PrintWriter write = null;
			try {
				String info = "{\"oparray\":" + jsons.toString() + "}";
				byte[] encodeBytes;
				byte[] bytes = info.getBytes(Encoding);
				int length = 0;
				// 加密模式以服务器为准
				if (beEncode) {
					encodeBytes = new byte[bytes.length * 2];
					long t1 = System.nanoTime();
					length = HTTPEncodeUtil.Encode(bytes, bytes.length, encodeBytes, encodeBytes.length);
					long t2 = System.nanoTime();
					log.info("Encode Use Time:{}ms,return lengrh:{}", (t2 - t1) / 1000000, length);
//					log.info("[packetsend] Encode[YES]" + info);
				} else {// 服务器客户端都为加密模式
					encodeBytes = bytes;
					length = bytes.length;
//					log.info("[packetsend] Encode[NO]" + info);
				}
				response.setHeader(IsEncodeHeaderKey, beEncode ? "1" : "0");
				response.setContentLength(length);
				response.getOutputStream().write(encodeBytes, 0, length);
			} catch (IOException e) {
				log.error("MRDServletIOException", e);
			} finally {
				if (write != null) {
					write.close();
				}
			}
		}
	}

	private void sendError(HttpServletResponse response, boolean isClientEncode, int errorCode, int opcode) {
		JSONArray array = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("opcode", 0);
		JSONObject data = new JSONObject();
		data.put("error", errorCode);
		data.put("opcode", opcode);
		jo.put("data", data);
		array.add(jo);

		this.send(response, isClientEncode, array);
	}
}
