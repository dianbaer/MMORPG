package cyou.mrd.io.tcp;



import org.apache.mina.core.service.IoAcceptor;

import cyou.mrd.Platform;
import cyou.mrd.event.Event;
import cyou.mrd.packethandler.TcpPacketHandler;



public abstract class AbstractClientSessionService implements ClientSessionService {

	protected IoAcceptor acceptor;
	protected TcpPacketHandler handler;
	protected String address;
	protected int port;
	
	public AbstractClientSessionService(String address, int port, TcpPacketHandler handler){
		this.address = address;
		this.port = port;
		this.handler = handler;
	}

	protected void notifySessionRemoved(ClientSession session){
		if(session.isAuthenticated()){
			Platform.getEventManager().addEvent(new Event(IoEvent.EVENT_SESSION_REMOVEING, session));
		}else{
			Platform.getEventManager().addEvent(new Event(IoEvent.EVENT_SESSION_REMOVED, session));
		}
		
	}
	
	protected void notifySessionAdded(ClientSession session){
		Platform.getEventManager().addEvent(new Event(IoEvent.EVENT_SESSION_ADDED, session));
	}

	public void shutdown() {
		if(acceptor!=null)
			acceptor.unbind();
	}
	
	public String getAddress(){
		return this.address;
	}
	
	public int getPort(){
		return this.port;
	}
}
