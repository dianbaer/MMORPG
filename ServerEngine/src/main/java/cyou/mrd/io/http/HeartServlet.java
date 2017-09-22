package cyou.mrd.io.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.io.tcp.OpCode;
import cyou.mrd.io.tcp.TcpPacket;
import cyou.mrd.io.tcp.connector.single.SingleClient;

public class HeartServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	public static final int WAIT_TIME = 1000 * 30;// http请求等待时间
	
	public static final int STATE_TIMEOUT = 0;
	public static final int STATE_OK = 1;
	public static final int STATE_WORLD_DISCONNECT = 2;
	public static final int STATE_BILLING_DISCONNECT = 3;
	
	protected static final Logger log = LoggerFactory.getLogger(HeartServlet.class);
	
	public static ServerHeart serverHeart;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("request server state ip:{}",request.getRemoteHost());
		if(serverHeart == null){
			serverHeart = new ServerHeart();
		}else{
			String s = "please wait!";
			log.info("[webHeart] retStr:[{}]", s);
			response.getOutputStream().write(s.getBytes());
			return;
		}
		
		TcpPacket pt = new TcpPacket(OpCode.SERVER_REQUEST_WORLD_CLIENT);
		pt.putInt(Platform.getServerId());
		// 需要加入worldServer为空的判断.
		SingleClient sc = Platform.worldServer();
		if (sc != null && sc.getSession().isConnected()) {
			sc.send(pt);
			synchronized(serverHeart.lock){
				try {
					serverHeart.lock.wait(WAIT_TIME);
				} catch (InterruptedException e) {
					log.error("request server heart error",e);
				}
			}
		}else{
			serverHeart.state = STATE_WORLD_DISCONNECT;
		}
		String retStr = "";
		if(serverHeart.state == STATE_TIMEOUT){
			retStr = "connect time out!";
		}
		if(serverHeart.state == STATE_OK){
			retStr = "0K:" + serverHeart.retStr;
		}
		if(serverHeart.state == STATE_WORLD_DISCONNECT){
			retStr = "world is not connect";
		}
		if(serverHeart.state == STATE_BILLING_DISCONNECT){
			retStr = "billing is not connect";
		}
		retStr += "---serverId:" + Platform.getServerId();
		response.getOutputStream().write(retStr.getBytes());
		log.info("[webHeart] retStr:[{}]", retStr);
		serverHeart = null;
	}
	
	public static void setState(int state,String retStr){
		if(serverHeart != null){
			serverHeart.setState(state);
			serverHeart.setRetStr(retStr);
			synchronized(serverHeart.lock){
				serverHeart.lock.notify();
			}
		}
	}
	
	class ServerHeart {
		Object lock = new Object();
		int state;  //1:成功状态    2:world未连接   3：billing未连接    0：超时未返回
		String retStr;// gameServer数量 ，serverId,........
		public void setState(int state){
			this.state = state;
		}
		public void setRetStr(String retStr){
			this.retStr = retStr;
		}
	}

}
