package cyou.mrd.io.tcp;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.packethandler.TcpPacketHandler;


public class DirectClientSessionService extends AbstractClientSessionService {
	private static final Logger log = LoggerFactory.getLogger(DirectClientSessionService.class);
	
	public TIntObjectHashMap<DirectClientSession> sessions = new TIntObjectHashMap<DirectClientSession>();
	
	public DirectClientSessionService(String address, int port, TcpPacketHandler handler){
		super(address, port, handler);
	}
	
	public void startup() throws Exception {
	}

	public void bind() throws IOException{
		log.info("URL binding {}:{}", address, port);
		acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MinaUAEncoder(), new MinaUADecoder()));
		acceptor.setHandler(new DirectClientSessionHandler());
        acceptor.bind( new InetSocketAddress(address, port));
	}
	

//	public DirectClientSession connectWorld(String url, int port) {
//		SocketConnector connector = new SocketConnector();
//		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(MinaUAEncoder.class, MinaUADecoder.class));
//		ConnectFuture cf = connector.connect(new InetSocketAddress(url, port), new DirectClientSessionHandler());
//		try {
//			Thread.sleep(60);
//		} catch (InterruptedException e) {
//			log.error("InterruptedException", e);
//		}
//		
//		if (!cf.isConnected()) {
//			return null;
//		} else {
//			return (DirectClientSession) cf.getSession().getAttachment();
//		}
//	}
	
	/**
	 * mina线程调用（一个单独的阻塞线程）
	 */
	public void addClientSession(ClientSession session){
		synchronized (sessions) {
			sessions.put(session.getId(),(DirectClientSession)session);
		}
		notifySessionAdded(session);
	}
	/**
	 * 主线程调用(必须是主线程调用，因为需要判断isAuthenticated这个)
	 */
	public void removeClientSession(ClientSession session){
		//DirectClientSession directClientSession;
		synchronized (sessions) {
			/*directClientSession = */sessions.remove(session.getId());
		}
		//if(directClientSession != null){
			notifySessionRemoved(session);
		//}
		
	}
	
	class DirectClientSessionHandler extends IoHandlerAdapter {
		
		@Override
		public void exceptionCaught(IoSession session, Throwable t) throws Exception {
			log.error("Throwable",t);
		}
		/**
		 * mina线程调用（cpu双倍的线程）
		 */
		@Override
		public void messageReceived(IoSession session, Object msg) throws Exception {
			DirectClientSession s = (DirectClientSession)session.getAttribute(AbstractClientSession.SESSION_ATTRIBUTE);
			if(s != null && msg instanceof TcpPacket){
				TcpPacket tcpPacket = (TcpPacket)msg;
				if(tcpPacket.opCode < 20000){
					if(s.getClient() != null){
						s.getClient().addTcpPacket(tcpPacket);
					}
				}else{
					s.addPacket((TcpPacket)msg);
				}
			}
		}
		/**
		 * mina线程调用（cpu双倍的线程）
		 */
		@Override
		public void sessionClosed(IoSession session) throws Exception{
			DirectClientSession ds = (DirectClientSession)session.getAttribute(AbstractClientSession.SESSION_ATTRIBUTE);
			//if(ds!=null){
				//session.setAttachment(null);
				ds.close();
				
			//}
		}
		/**
		 * mina线程调用（一个单独的阻塞线程）
		 */
		@Override
		public void sessionCreated(IoSession session) throws Exception {
			DirectClientSession dSession = new DirectClientSession(DirectClientSessionService.this,session,handler);
			session.setAttribute(AbstractClientSession.SESSION_ATTRIBUTE, dSession);
			addClientSession(dSession);
		}
	}

	@Override
	public String getId() {
		return getClass().getName();
	}
	
	
//	public ClientSession getClientSession(int id){
//		synchronized (sessions) {
//			return sessions.get(id);
//		}
//	}
	
}
