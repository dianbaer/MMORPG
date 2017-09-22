package cyou.mrd.packethandler;

import java.util.Map;
import java.util.TreeMap;


public class HttpHandlerDispatchManager {
	
	protected static Map<String,HttpHandlerDispatch> dispatchs = new TreeMap<String,HttpHandlerDispatch>();
	
	public static void add(HttpHandlerDispatch dispatch){
		dispatchs.put(dispatch.getId(), dispatch);
	}
	
	public static HttpHandlerDispatch remove(String id){
		return dispatchs.remove(id);
	}
	
	public static HttpHandlerDispatch get(String id){
		return dispatchs.get(id);
	}
	
	public static boolean register(String id,int opcode,HttpPacketHandler handler){
		HttpHandlerDispatch dispatch = get(id);
		if(dispatch == null)
			return false;
		dispatch.register(opcode, handler);
		return true;
	}
	
	public static boolean register(String id,int[] opcodes,HttpPacketHandler handler){
		HttpHandlerDispatch dispatch = get(id);
		if(dispatch == null)
			return false;
		dispatch.register(opcodes, handler);
		return true;
	}
	
	public static void unRegister(String id,int opcode){
		HttpHandlerDispatch dispatch = get(id);
		if(dispatch != null)
			dispatch.unRegister(opcode);
	}
	
	public static void unRegister(String id,int[] opcodes){
		HttpHandlerDispatch dispatch = get(id);
		if(dispatch != null)
			dispatch.unRegister(opcodes);
	}
}
