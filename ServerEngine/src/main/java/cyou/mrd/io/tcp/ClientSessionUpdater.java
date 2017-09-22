package cyou.mrd.io.tcp;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.event.Event;
import cyou.mrd.event.OPEvent;
import cyou.mrd.io.OPHandler;
import cyou.mrd.service.Service;
import cyou.mrd.updater.Updatable;

@OPHandler(TYPE = OPHandler.EVENT)
public class ClientSessionUpdater implements Updatable, Service {
	static Logger log = LoggerFactory.getLogger(ClientSessionUpdater.class);
	protected TIntObjectHashMap<ClientSession> sessions = new TIntObjectHashMap<ClientSession>();

	protected SessionUpdateProcedure procedure = new SessionUpdateProcedure();

	public String getId() {
		return "ClientSessionUpdater";
	}

	public void startup() throws Exception {
		Platform.getUpdater().addSyncUpdatable(this);
	}

	public void shutdown() throws Exception {

	}

	public ClientSessionUpdater() {
	}

	@Override
	public boolean update() {
		sessions.forEachValue(procedure);
		return false;
	}

	@OPEvent(eventCode = IoEvent.EVENT_SESSION_ADDED)
	protected void addClientSession(Event event) {
		ClientSession session = (ClientSession) event.param1;
		sessions.put(session.getId(), session);
	}
	//这里清理了，但是并不能阻止，这一次的update
	@OPEvent(eventCode = IoEvent.EVENT_SESSION_REMOVED)
	protected void removeClientSesion(Event event) {
		ClientSession session = (ClientSession) event.param1;
		sessions.remove(session.getId());
		//可能没有client时，就已经断开连接了
		if(session.getClient() != null){
			if(session.getClient().getState() == 2){
				session.getClient().setState(3);
			}
		}
		session.clear();
	}

}

class SessionUpdateProcedure implements TObjectProcedure<ClientSession> {
	public boolean execute(ClientSession session) {
		try {
			session.update();
		} catch (Throwable e) {
			ClientSessionUpdater.log.error("session update", e);
		}
		return true;
	}
}
