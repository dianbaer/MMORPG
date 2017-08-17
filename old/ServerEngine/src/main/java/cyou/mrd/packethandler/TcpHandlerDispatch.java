package cyou.mrd.packethandler;

import gnu.trove.map.hash.TIntObjectHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.io.tcp.ClientSession;
import cyou.mrd.io.tcp.TcpPacket;

public class TcpHandlerDispatch implements TcpPacketHandler {

	protected static final Logger log = LoggerFactory.getLogger(TcpHandlerDispatch.class);

	public static int OPCODE_TIME_THRESHOLD = 50; // 处理opcode的警告时间，超过这个时间系统将会打出日志
	public static boolean flag = true;

	public static final String PLAYER = "player_dispatch";
	public static final String ADMIN = "admin_dispatch";

	protected TIntObjectHashMap<TcpPacketHandler> handlers = new TIntObjectHashMap<TcpPacketHandler>();

	/**
	 * dispatch的id，在注册的时候需要此id定位
	 */
	protected String id;

	public TcpHandlerDispatch(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void register(int opcode, TcpPacketHandler handler) {
		handlers.put(opcode, handler);
	}

	public void register(int[] opcodes, TcpPacketHandler handler) {
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

	public void handle(TcpPacket packet, ClientSession session) throws Exception {
		int opcode = packet.getOpCode();
		TcpPacketHandler handler = handlers.get(opcode);
		if (handler != null) {
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
		}
	}

}
