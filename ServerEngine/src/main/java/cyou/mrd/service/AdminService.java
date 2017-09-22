package cyou.mrd.service;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.procedure.TObjectIntProcedure;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.ObjectAccessor;
import cyou.mrd.Platform;
import cyou.mrd.data.Data;
import cyou.mrd.data.DataKeys;
import cyou.mrd.entity.Player;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HOpCode;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;
import cyou.mrd.util.ErrorHandler;

@OPHandler(TYPE = OPHandler.HTTP)
public class AdminService implements Service{
	private static final Logger log = LoggerFactory.getLogger(AdminService.class);

	@Override
	public String getId() {
		return "AdminService";
	}

	@Override
	public void startup() throws Exception {
		
	}

	@Override
	public void shutdown() throws Exception {
	}

	@OP(code = HOpCode.ADMIN_ONLINE_PLAYERS_CLIENT)
	public void online(Packet packet, HSession session) {
//		log.info("[HTTPRequest] packet[{}] session [{}]]", packet.toString(), session.getSessionId());
//		Player player = (Player) session.client();
//		if (player == null) {
//			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, 0);
//			return;
//		}
//		
//		int type = packet.getInt("type");
//		final Packet pt = new JSONPacket(HOpCode.ADMIN_ONLINE_PLAYERS_SERVER);
//		if(type == 0) {
//			Iterator<Player> iterator = ObjectAccessor.players.values().iterator();
//			
//			while(iterator.hasNext()) {
//				Player p = iterator.next();
//				pt.put(p.getName() == null ? p.getId()+"" : p.getName(), p.getInstanceId());
//			}
//		}else {
//			Data data = Platform.dataCenter().getData(DataKeys.SESSION_TABLE_KEY);
//			if(data != null) {
//				@SuppressWarnings("unchecked")
//				TObjectIntMap<String> tSessionTable = (TObjectIntMap<String>) data.value;
//				tSessionTable.forEachEntry(new TObjectIntProcedure<String>() {
//					@Override
//					public boolean execute(String sessionId, int id) {
//						pt.put(sessionId, id);
//						return true;
//					}
//				});
//			}else {
//				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_46, 0);
//				return;
//			}
//		}
//		session.send(pt);
		log.info("[online] no support!");
	}
	
	
}
