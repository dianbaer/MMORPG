package cyou.mrd.io.tcp;

import gnu.trove.map.hash.TIntObjectHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.event.Event;
import cyou.mrd.packethandler.TcpPacketHandler;


public class ServerClientSessionService extends AbstractClientSessionService {
	private static final Logger log = LoggerFactory.getLogger(ServerClientSessionService.class);
	
	public TIntObjectHashMap<DirectClientSession> sessions = new TIntObjectHashMap<DirectClientSession>();
	
	public ServerClientSessionService(String address, int port, TcpPacketHandler handler){
		super(address, port, handler);
	}
	
	public void startup() throws Exception {
	}

	
	/**
	 * mina线程调用（一个单独的阻塞线程）
	 */
	public void addClientSession(ClientSession session){
		synchronized (sessions) {
			sessions.put(session.getId(),(DirectClientSession)session);
		}
		Platform.getEventManager().addEvent(new Event(IoEvent.EVENT_SESSION_ADDED_SERVER, session));
	}
	/**
	 * 主线程调用(必须是主线程调用，因为需要判断isAuthenticated这个)
	 */
	public void removeClientSession(ClientSession session){
		synchronized (sessions) {
			sessions.remove(session.getId());
		}
		Platform.getEventManager().addEvent(new Event(IoEvent.EVENT_SESSION_REMOVED_SERVER, session));
		
	}
	
	
	@Override
	public String getId() {
		return getClass().getName();
	}

	
}
