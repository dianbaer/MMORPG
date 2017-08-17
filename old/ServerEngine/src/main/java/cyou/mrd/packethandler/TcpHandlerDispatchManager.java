package cyou.mrd.packethandler;

import java.util.Map;
import java.util.TreeMap;

public class TcpHandlerDispatchManager {

	protected static Map<String, TcpHandlerDispatch> dispatchs = new TreeMap<String, TcpHandlerDispatch>();

	public static void add(TcpHandlerDispatch dispatch) {
		dispatchs.put(dispatch.getId(), dispatch);
	}

	public static TcpHandlerDispatch remove(String id) {
		return dispatchs.remove(id);
	}

	public static TcpHandlerDispatch get(String id) {
		return dispatchs.get(id);
	}

	public static boolean register(String id, int opcode, TcpPacketHandler handler) {
		TcpHandlerDispatch dispatch = get(id);
		if (dispatch == null)
			return false;
		dispatch.register(opcode, handler);
		return true;
	}

	public static boolean register(String id, int[] opcodes, TcpPacketHandler handler) {
		TcpHandlerDispatch dispatch = get(id);
		if (dispatch == null)
			return false;
		dispatch.register(opcodes, handler);
		return true;
	}

	public static void unRegister(String id, int opcode) {
		TcpHandlerDispatch dispatch = get(id);
		if (dispatch != null)
			dispatch.unRegister(opcode);
	}

	public static void unRegister(String id, int[] opcodes) {
		TcpHandlerDispatch dispatch = get(id);
		if (dispatch != null)
			dispatch.unRegister(opcodes);
	}
}
