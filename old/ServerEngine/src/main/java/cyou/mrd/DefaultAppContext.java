package cyou.mrd;

import gnu.trove.list.array.TShortArrayList;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.event.EventListener;
import cyou.mrd.event.OPEvent;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.tcp.ClientSession;
import cyou.mrd.io.tcp.TcpPacket;
import cyou.mrd.packethandler.HttpHandlerDispatch;
import cyou.mrd.packethandler.HttpHandlerDispatchManager;
import cyou.mrd.packethandler.HttpPacketHandler;
import cyou.mrd.packethandler.TcpHandlerDispatch;
import cyou.mrd.packethandler.TcpHandlerDispatchManager;
import cyou.mrd.packethandler.TcpPacketHandler;
import cyou.mrd.service.Service;

 
/**
 * Service容器实现类. 过滤了类注解@OPHandler和方法@OP. 实现协议转发.
 * 为了提高运行时性能, 采用集成并重新创建新类的方式实现类swich转发opcode
 * 创建的新类名为xxxService$Proxy
 * @author miaoshengli
 */
public class DefaultAppContext implements AppContext{
	
	@SuppressWarnings("rawtypes")
	protected static Map<Class,Object> services;
	protected static final Logger log = LoggerFactory.getLogger(DefaultAppContext.class);
	public boolean isFake;
	
	@SuppressWarnings("rawtypes")
	public DefaultAppContext() {
		services = new LinkedHashMap<Class,Object>(40,0.5f);
		//j2ee下需要设置
		ClassPool.getDefault().insertClassPath(new ClassClassPath(Service.class));
	}
	
	@SuppressWarnings("rawtypes")
	protected Class generateHttpPacketHandlerClass(Class clazz, TShortArrayList opcodes) throws Exception{
		Map<Short,String> opMethods = new TreeMap<Short,String>();
		Method[] methods = clazz.getDeclaredMethods();
		for(Method method:methods){ //遍历所有方法，将其中标注了是包处理方法的方法名加入到opMethods中
			OP op = method.getAnnotation(OP.class);
			if(op != null){  
				Class[] parameterTypes= method.getParameterTypes();
				//检查方法的合法性
				if(parameterTypes.length != 2){
					throw new IllegalStateException("Method "+method.getName()+" Parameter Error");
				}
				if(parameterTypes[0] != Packet.class || parameterTypes[1] != HSession.class){
					throw new IllegalStateException("Method "+method.getName()+" Parameter Error");
				}
				opMethods.put(op.code(), method.getName());
			}
		}
		if(opMethods.size() > 0){
			ClassPool pool = ClassPool.getDefault();
			CtClass oldClass =  pool.get(clazz.getName());
//			log.info("oldClass: " + oldClass);
			CtClass ct = pool.makeClass(oldClass.getName()+"$Proxy", oldClass); //这里需要生成一个新类，并且继承自原来的类
			CtClass superCt = pool.get(HttpPacketHandler.class.getName());  //需要实现HttpPacketHandler接口
			ct.addInterface(superCt);
			//添加handler方法，在其中添上switch...case段
			StringBuilder sb = new StringBuilder("public void handle(cyou.mrd.io.Packet packet,cyou.mrd.io.http.HSession session) throws Exception{");
			sb.append("short opCode = $1.getopcode();");
			sb.append("switch (opCode) {");
			Iterator<Map.Entry<Short,String>> ite = opMethods.entrySet().iterator();
			while(ite.hasNext()){
				Map.Entry<Short, String> entry = ite.next();
				sb.append("case ").append(entry.getKey()).append(":");
				sb.append(entry.getValue()).append("($$);"); //注意，这里所有的方法都必须是protected或者是public的，否则此部生成会出错
				sb.append("break;");
				opcodes.add(entry.getKey());
			}
			sb.append("}");
			sb.append("}");
			CtMethod method = CtMethod.make(sb.toString(), ct);
			ct.addMethod(method);
			return ct.toClass();
		}else{
			return clazz;
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected Class generatePacketHandlerClass(Class clazz, TShortArrayList opcodes) throws Exception{
		Map<Short,String> opMethods = new TreeMap<Short,String>();
		Method[] methods = clazz.getDeclaredMethods();
		for(Method method:methods){ //遍历所有方法，将其中标注了是包处理方法的方法名加入到opMethods中
			OP op = method.getAnnotation(OP.class);
			if(op != null){  
				Class[] parameterTypes= method.getParameterTypes();
				//检查方法的合法性
				if(parameterTypes.length != 2){
					throw new IllegalStateException("Method "+method.getName()+" Parameter Error");
				}
				if(parameterTypes[0] != TcpPacket.class || parameterTypes[1] != ClientSession.class){
					throw new IllegalStateException("Method "+method.getName()+" Parameter Error");
				}
				opMethods.put(op.code(), method.getName());
			}
		}
		if(opMethods.size() > 0){
			ClassPool pool = ClassPool.getDefault();
			CtClass oldClass =  pool.get(clazz.getName());
			CtClass ct = pool.makeClass(oldClass.getName()+"$Proxy", oldClass); //这里需要生成一个新类，并且继承自原来的类
			CtClass superCt = pool.get(TcpPacketHandler.class.getName());  //需要实现PacketHandler接口
			ct.addInterface(superCt);
			//添加handler方法，在其中添上switch...case段
			StringBuilder sb = new StringBuilder("public void handle(cyou.mrd.io.tcp.TcpPacket packet,cyou.mrd.io.tcp.ClientSession session) throws Exception{");
			sb.append("short opCode = $1.getOpCode();");
			sb.append("switch (opCode) {");
			Iterator<Map.Entry<Short,String>> ite = opMethods.entrySet().iterator();
			while(ite.hasNext()){
				Map.Entry<Short, String> entry = ite.next();
				sb.append("case ").append(entry.getKey()).append(":");
				sb.append(entry.getValue()).append("($$);"); //注意，这里所有的方法都必须是protected或者是public的，否则此部生成会出错
				sb.append("break;");
				opcodes.add(entry.getKey());
			}
			sb.append("}");
			sb.append("}");
			CtMethod method = CtMethod.make(sb.toString(), ct);
			ct.addMethod(method);
			return ct.toClass();
		}else{
			return clazz;
		}
	}

	@SuppressWarnings("rawtypes")
	private Class generateEventListenerClass(Class clazz,int OpType) throws Exception{
//		log.info("get Regeister Class:{}",clazz.getName());
		Class registerClass = clazz;
		Map<Integer,String> eventMethods = new TreeMap<Integer,String>();
		//提取关心的事件
		Class targetClazz = null;
		if(OpType == OPHandler.EVENT){//直接对原类进行操作
			targetClazz = clazz;
		}else if(OpType == OPHandler.HTTP_EVENT){
			targetClazz = (Class)clazz.getGenericSuperclass();//由于已经继承了一次 要对父类进行处理
		}
		Method[] methods = targetClazz.getDeclaredMethods();
		for(Method method:methods){
			OPEvent event = method.getAnnotation(OPEvent.class);
			if(event != null){
				//由于事件处理方法的参数个数不一定   这里不再检查参数个数是否合法
				int eventCode = event.eventCode();
				eventMethods.put(eventCode, method.getName());
			}
		}
		//让类实现EventListener接口
		if(eventMethods.size() > 0){
			ClassPool pool = ClassPool.getDefault();
			CtClass oldClass =  pool.get(clazz.getName());
//			log.info("oldClass: " + oldClass);
			CtClass ct = pool.makeClass(oldClass.getName()+"$ProxyEvent", oldClass); //这里需要生成一个新类，并且继承自原来的类
			CtClass superCt = pool.get(EventListener.class.getName());  //需要实现EventListener接口
			ct.addInterface(superCt);
			//实现接口的两个方法
			StringBuilder getEventTypes = new StringBuilder("public int[] getEventTypes() {return new int[] {");
			int i = 0;
			for(int key:eventMethods.keySet()){
				i ++;
				if(i == eventMethods.size()){
					getEventTypes.append(key);
				}else{
					getEventTypes.append(key).append(",");
				}
				
			}
			getEventTypes.append("};}");
			log.info("getEventTypes:{}",getEventTypes.toString());
			StringBuilder handler = new StringBuilder("public void handleEvent(cyou.mrd.event.Event event) {switch (event.type) {");
			Iterator<Map.Entry<Integer,String>> ite = eventMethods.entrySet().iterator();
			while(ite.hasNext()){
				Map.Entry<Integer, String> entry = ite.next();
				handler.append("case ").append(entry.getKey()).append(":");
				handler.append(entry.getValue()).append("($$);"); 
				handler.append("break;");
			}
			handler.append("}");
			handler.append("}");
			log.info("handler:{}",handler.toString());
			CtMethod method1 = CtMethod.make(getEventTypes.toString(), ct);
			ct.addMethod(method1);
			CtMethod method2 = CtMethod.make(handler.toString(), ct);
			ct.addMethod(method2);
			registerClass = ct.toClass();
		}
		return registerClass;
		
	}
	
	@SuppressWarnings("unchecked")
	public <X, Y extends X> void create(Class<Y> clazz, Class<X> inter) throws Exception {
//		log.info("create:  " + clazz.getName() + "---" + inter.getName());
		TShortArrayList opcodes = new TShortArrayList(10);
		OPHandler opHandler = clazz.getAnnotation(OPHandler.class);
		if (opHandler != null) { // 如果是Service同时又是一个包处理类，那么要做类转换，让类实现PacketHandler接口，并且生成handler方法
			if (opHandler.TYPE() == OPHandler.HTTP) {
				clazz = generateHttpPacketHandlerClass(clazz, opcodes);
			} else if (opHandler.TYPE() == OPHandler.TCP) {
				clazz = generatePacketHandlerClass(clazz, opcodes);
			} else if (opHandler.TYPE() == OPHandler.EVENT) {
				clazz = generateEventListenerClass(clazz,OPHandler.EVENT);
			} else if(opHandler.TYPE() == OPHandler.HTTP_EVENT){
				clazz = generateHttpPacketHandlerClass(clazz, opcodes);
				clazz = generateEventListenerClass(clazz,OPHandler.HTTP_EVENT);
			} else if(opHandler.TYPE() == OPHandler.TCP_EVENT){
				clazz = generatePacketHandlerClass(clazz, opcodes);
				clazz = generateEventListenerClass(clazz,OPHandler.HTTP_EVENT);
			}
		}
//		log.info("create:  " + clazz.getName() + "---" + inter.getName());
		Object o = clazz.newInstance();
		if(o instanceof Service){
			((Service)o).startup();
		}
		add(o,inter);
		//注册事件监听器
		if(o instanceof EventListener){
			Platform.getEventManager().registerListener((EventListener) o);
		}
		if(opcodes.size() > 0){  //如果opcodes不为空，那么就代表此Service是一个包处理类
			for(int i=0;i<opcodes.size();i++){
				short op = opcodes.get(i);
				if (opHandler.TYPE() == OPHandler.HTTP || opHandler.TYPE() == OPHandler.HTTP_EVENT) {
					HttpHandlerDispatchManager.register(HttpHandlerDispatch.PLAYER,op, (HttpPacketHandler)o);
				} else if(opHandler.TYPE() == OPHandler.TCP || opHandler.TYPE() == OPHandler.TCP_EVENT){
					TcpHandlerDispatchManager.register(TcpHandlerDispatch.PLAYER,op, (TcpPacketHandler)o);
				}
			}
		}
	}

	@Override
	public <T> void add(Object service, Class<T> inter) {
		if(service.getClass()!=inter&&!inter.isAssignableFrom(service.getClass())) //接口和实现类必须相等或者继承关系
			throw new IllegalArgumentException();
		services.put(inter, service);
	}


	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz) {
		return (T)services.get(clazz);
	}

	
	public void shutdown(){
		Object[] ss = new Object[services.size()];
		services.values().toArray(ss);
		for(int i=ss.length-1;i>0;i--){
			if(ss[i] instanceof Service) {
				try {
					((Service)ss[i]).shutdown();
				} catch (Exception e) {
					log.error(e.toString(),e);
				}
			}
		}
	}
	
}
