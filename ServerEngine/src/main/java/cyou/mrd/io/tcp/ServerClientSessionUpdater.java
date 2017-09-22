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
import cyou.mrd.world.WorldManager;

@OPHandler(TYPE = OPHandler.EVENT)
public class ServerClientSessionUpdater implements Updatable, Service {
	static Logger log = LoggerFactory.getLogger(ServerClientSessionUpdater.class);
	protected TIntObjectHashMap<ClientSession> sessions = new TIntObjectHashMap<ClientSession>();

	protected ServerSessionUpdateProcedure procedure = new ServerSessionUpdateProcedure();

	public String getId() {
		return "ServerClientSessionUpdater";
	}

	public void startup() throws Exception {
		Platform.getUpdater().addSyncUpdatable(this);
	}

	public void shutdown() throws Exception {

	}

	public ServerClientSessionUpdater() {
	}

	@Override
	public boolean update() {
		sessions.forEachValue(procedure);
		return false;
	}

	@OPEvent(eventCode = IoEvent.EVENT_SESSION_ADDED_SERVER)
	protected void addClientSession(Event event) {
		ClientSession session = (ClientSession) event.param1;
		sessions.put(session.getId(), session);
		//发送登录
		WorldManager wmanager = Platform.getAppContext().get(WorldManager.class);
		wmanager.login(session.getIoSession());
	}
	//这里清理了，但是并不能阻止，这一次的update
	@OPEvent(eventCode = IoEvent.EVENT_SESSION_REMOVED_SERVER)
	protected void removeClientSesion(Event event) {
		ClientSession session = (ClientSession) event.param1;
		sessions.remove(session.getId());
		session.clear();
	}

}

class ServerSessionUpdateProcedure implements TObjectProcedure<ClientSession> {
	public boolean execute(ClientSession session) {
		try {
			session.update();
		} catch (Throwable e) {
			ServerClientSessionUpdater.log.error("session update", e);
		}
		return true;
	}
}
