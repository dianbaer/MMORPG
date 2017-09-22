package cyou.mrd.packethandler;

import gnu.trove.map.hash.TIntObjectHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.entity.Player;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.tcp.HOpCodeEx;
import cyou.mrd.lock.PlayerLockService;
import cyou.mrd.util.ErrorHandler;

public class HttpHandlerDispatch {

	protected static final Logger log = LoggerFactory.getLogger(HttpHandlerDispatch.class);

	public static int OPCODE_TIME_THRESHOLD = 1000; // 处理opcode的警告时间，超过这个时间系统将会打出日志
	public static boolean flag = true;

	public static final String PLAYER = "player_dispatch";
	public static final String ADMIN = "admin_dispatch";

	protected TIntObjectHashMap<HttpPacketHandler> handlers = new TIntObjectHashMap<HttpPacketHandler>();

	/**
	 * dispatch的id，在注册的时候需要此id定位
	 */
	protected String id;

	public HttpHandlerDispatch(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void register(int opcode, HttpPacketHandler handler) {
		handlers.put(opcode, handler);
	}

	public void register(int[] opcodes, HttpPacketHandler handler) {
		for (int opcode : opcodes) {
			register(opcode, handler);
		}
	}

	public void unRegister(int opcode) {
		handlers.remove(opcode);
	}

	public void unRegister(int[] opcodes) {
		for (int opcode : opcodes) {
			handlers.remove(opcode);
		}
	}

	public void handle(Packet packet, HSession session) throws Exception {
		PlayerLockService playerLock = Platform.getAppContext().get(PlayerLockService.class);
		Player player = null;
		try {
			int opcode = packet.getopcode();
			HttpPacketHandler handler = handlers.get(opcode);
//			packet.getRunTimeMonitor().knock("getHttpPacketHandler");
			
			if (handler != null) {
				player = (Player) session.client();
				
				if(player != null && HOpCodeEx.isLockPlayerOpCode(Short.parseShort(String.valueOf(packet.getopcode())))){
					try {
						playerLock.lock(player.getId());
					} catch (Exception e) {//锁失败或者超时
						ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_0, packet.getopcode());
						return;
					}
				}
				if (flag) {
					long l1 = System.nanoTime();
					handler.handle(packet, session);
					long l2 = System.nanoTime();
					long t = (l2 - l1) / 1000000L;
					if (t > OPCODE_TIME_THRESHOLD) {
						log.info("[OPCODETOOLONG][{},{}ms]", opcode, t);
					}
				} else {
					handler.handle(packet, session);
				}
			}else {
				log.info("[OPCODE] opcode not have handler: [{}]", opcode);
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_45, opcode);
			}
		} finally {
			if(player != null && HOpCodeEx.isLockPlayerOpCode(Short.parseShort(String.valueOf(packet.getopcode())))){
				playerLock.unlock(player.getId());
			}
		}
		
	}

}
