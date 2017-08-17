package cyou.mrd.io.tcp.connector.single;



import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.io.tcp.AbstractClientSession;
import cyou.mrd.io.tcp.DirectClientSession;
import cyou.mrd.io.tcp.ServerClientSessionService;
import cyou.mrd.io.tcp.TcpPacket;
import cyou.mrd.packethandler.TcpHandlerDispatch;
import cyou.mrd.packethandler.TcpHandlerDispatchManager;

public class SingleClientSessionHandler extends IoHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(SingleClientSessionHandler.class);

	public IoSession ioSession;

	

	public SingleClientSessionHandler(SingleConnector singleConnector) {
		
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		log.error("Throwable", cause);
	}

	@Override
	public void messageReceived(IoSession session, Object msg) throws Exception {
		DirectClientSession s = (DirectClientSession) session.getAttribute(AbstractClientSession.SESSION_ATTRIBUTE);
		if (s != null && msg instanceof TcpPacket) {
			s.addPacket((TcpPacket) msg);
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		DirectClientSession ds = (DirectClientSession) session.getAttribute(AbstractClientSession.SESSION_ATTRIBUTE);
		//可能为null，因为可能还没有连接上就断开了
		if (ds != null) {
		//	session.setAttachment(null);
			ioSession = null;
			ds.close();
		}
		log.info("[SessionHandler] session closed.");
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		ioSession = session;
		ServerClientSessionService dcService = Platform.getAppContext().get(ServerClientSessionService.class);
		DirectClientSession dSession = new DirectClientSession(dcService, session,
				TcpHandlerDispatchManager.get(TcpHandlerDispatch.PLAYER));
		session.setAttribute(AbstractClientSession.SESSION_ATTRIBUTE, dSession);
		dcService.addClientSession(dSession);
	}

	public void keepLive() {
		DirectClientSession ds = (DirectClientSession) ioSession.getAttribute(AbstractClientSession.SESSION_ATTRIBUTE);
		if (ds != null) {
			ds.keepLive();
		}
	}

	public boolean isConnected() {
		if (ioSession != null) {
			DirectClientSession ds = (DirectClientSession) ioSession.getAttribute(AbstractClientSession.SESSION_ATTRIBUTE);
			if (ds != null) {
				return ds.isConnected();
			}
		}
		return false;
	}

	public boolean isAuthenticated() {
		if (ioSession != null) {
			DirectClientSession ds = (DirectClientSession) ioSession.getAttribute(AbstractClientSession.SESSION_ATTRIBUTE);
			if (ds != null) {
				return ds.isAuthenticated();
			}
		}
		return false;
	}

	public void authenticated() {
		if (ioSession != null) {
			DirectClientSession ds = (DirectClientSession) ioSession.getAttribute(AbstractClientSession.SESSION_ATTRIBUTE);
			if (ds != null) {
				ds.authenticate(null);
			}
		}
	}
}