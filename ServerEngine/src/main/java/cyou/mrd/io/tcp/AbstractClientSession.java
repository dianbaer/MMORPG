package cyou.mrd.io.tcp;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.io.Identity;
import cyou.mrd.packethandler.TcpPacketHandler;


public abstract class AbstractClientSession implements ClientSession{
	
	//protected static final long DISCONNECTING_TIME = 20 * 60 * 1000L;//鉴于server有可能出现长达9分钟的静寂. 将断线等待时间设成20, 原来的值为4;
	public static final String SESSION_ATTRIBUTE = "CLIENTSESSION";
	private static final Logger log = LoggerFactory.getLogger(AbstractClientSession.class);
	//private static int IDNUM = 0;
	private static final int QUEUE_MAX_COUNT = 512;
	
	protected IoSession session;
	//连接，登录成功，正在断开连接，已经断开连接
	protected enum State{CONNECTED,DISCONNECTED};
	
	protected State state = State.CONNECTED;
	
	protected TcpPacketHandler handler;

	protected TcpClient client;

	protected ArrayBlockingQueue<TcpPacket> queue = new ArrayBlockingQueue<TcpPacket>(1024 * 100);

	//protected ArrayBlockingQueue<AsyncCall> calls = new ArrayBlockingQueue<AsyncCall>(256 * 100);
	
	protected ClientSessionService service;
	
	//protected Identity identity;
	//这个id值不要清空，判断时需要用的
	protected int id;
	
	//protected static final AtomicInteger id_gen = new AtomicInteger(0);

	
	
	//protected long lastReceiveStamp = System.currentTimeMillis();
	
	//protected long disconnectedStamp = 0L;
	public boolean isAuthenticated = false;
	//这个isClear值不要清空，判断时需要用的
	public boolean isClear = false;
	
	public AbstractClientSession(ClientSessionService service,IoSession session,TcpPacketHandler handler){
		//XXX这个id不需要阻塞类，因为创建socket的是单独的一个线程，如果更新版本是流程改了，这个需要考虑修改
		//mina2.0都是单独的线程创建每个用户
		//IDNUM++;
		//this.id = IDNUM;
		this.id = ClientSessionUtil.getSessionId();
		this.service = service;
		this.session = session;
		this.handler = handler;
	}
	
	//public AbstractClientSession(ClientSessionService service,IoSession session,TcpPacketHandler handler,int id){
	//	this.id = id;
	//	this.service = service;
	//	this.session = session;
	//	this.handler = handler;
	//}
	/**
	 * 可能会被主线程或者场景线程调用
	 */
	public void send(TcpPacket packet){
		if(isConnected() && session.isConnected()){
			session.write(packet);
		}else{
			log.info("rpgServer:socket已经关闭，不能再发送包");
		}
			
	}
	
	public TcpPacketHandler getHandler(){
		return handler;
	}
	
	public IoSession getIoSession(){
		return session;
	}

	
	public TcpClient getClient() {
		//if (client == null) {
		//	log.info("[SESSION_DIE]SESID[" + this.getId() + "]");
		//}
		return client;
	}

	public void setClient(TcpClient client) {
		this.client = client;
		//if (client != null) {
			client.setSession(this);
		//}
	}
	
	public void keepLive() {
		//lastReceiveStamp = System.currentTimeMillis();
	}
	
	public void addPacket(TcpPacket packet){
		if(state == State.DISCONNECTED){
			log.info("rpgServer:socket关闭了，不应该再有消息报增加，哪有问题？");
			return;
		}
		queue.offer(packet);
		//lastReceiveStamp = System.currentTimeMillis();
		if (queue.size() > 10) {
			log.info("rpgServer:某个玩家的包已经超过10个了");
		}
		//if (calls.size() > 10) {
		//	log.info("[ClientSession] calls block? size[{}]", calls.size());
		//}
	}
	
	/**
	 * 只被主线程调用
	 */
	public boolean update(){
//		if ((state != State.DISCONNECTING && state != State.DISCONNECTED)
//				&& (System.currentTimeMillis() - lastReceiveStamp) > DISCONNECTING_TIME) {
//			log.info("update: setDisconnecting state:{}, DISCONNECTING_TIME:{}", state, System.currentTimeMillis() - lastReceiveStamp - DISCONNECTING_TIME);
//			//setDisconnecting();
//			close();
//		}
		if(isClear){
			log.info("rpgServer:已经被清理了，不应该在调用update函数了");
			return false;
		}
		//这里不应该做空判断啊
		if(state == State.DISCONNECTED/* && service != null*/){
			service.removeClientSession(this);
			isClear = true;
			return false;
		}
		TcpPacket packet = null;
		//TcpPacketHandler handler = getHandler();
		int queueCount = 0;
		//这里不应该做空判断啊
		//if(queue != null){
			while((packet=queue.poll())!=null){
				try {
					queueCount++;
					handler.handle(packet, this);
					packet.clear();
					if(queueCount > QUEUE_MAX_COUNT) {
						log.info("rpgServer:某个玩家处理的消息包已经超过"+QUEUE_MAX_COUNT+"个，太多了");
						break;
					}
				} catch (Exception e) {
					log.error(e.toString(),e);
					log.info("rpgServer:处理玩家主线程的消息包错误");
				}
			}
		//}
//		AsyncCall call = null;
//		int callCount = 0;
//		while((call = calls.poll())!=null){
//			try {
//				callCount++;
//				call.callFinish();
//				if(callCount > QUEUE_MAX_COUNT) {
//					break;
//				}
//			} catch (Exception e) {
//				log.error(e.toString(),e);
//			}
//		}
		
//		if(state == State.DISCONNECTING){
//			setDisconnected();
//			return true;
//		}
		return false;
	}
	
	public void cleanMessageQueue(){
		queue.clear();
	}
	
	public void close() {
		//if (isConnected()) {
			//session.close();
			cleanMessageQueue();
			setDisconnected();
			log.info("rpgServer:某个玩家断开连接");
		//}
	}
	
//	private void setDisconnecting(){
//		state = State.DISCONNECTING;
//		//addPacket(new TcpPacket(OpCode.DISCONNECTED));
//	}
	
	private void setDisconnected(){
		state = State.DISCONNECTED;
		//disconnectedStamp = System.currentTimeMillis();
	}
	
	public int getId(){
		return id;
	}
	
//	public void addAsyncCall(AsyncCall call){
//		calls.offer(call);
//	}
	
	public void authenticate(Identity identity) throws SecurityException{
		//this.identity = identity;
		isAuthenticated = true;
	}
	
//	public Identity getIdentity(){
//		return identity;
//	}
	public boolean isConnected() {
		return state == State.CONNECTED;
	}

	public boolean isAuthenticated() {
		return isAuthenticated;
	}
	public boolean getIsClear(){
		return isClear;
	}
	
	public void clear(){
		session.setAttribute(SESSION_ATTRIBUTE, null);
		session = null;
		handler = null;
		client = null;
		//这里不用做清理了
		//queue.clear();
		queue = null;
		service = null;
		
	}
}
