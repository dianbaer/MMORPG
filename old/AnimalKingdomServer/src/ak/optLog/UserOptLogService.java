package ak.optLog;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.player.PlayerEx;
import ak.server.ErrorHandlerEx;
import cyou.mrd.Platform;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;
import cyou.mrd.io.tcp.HOpCodeEx;
@OPHandler(TYPE = OPHandler.HTTP)
public class UserOptLogService implements IUserOptLogService {
	
	private static final Logger log = LoggerFactory.getLogger(UserOptLogService.class);
	@Override
	public String getId() {
		
		return "UserOptLogService";
	}

	@Override
	public void startup() throws Exception {
		

	}

	@Override
	public void shutdown() throws Exception {
		

	}

	@Override
	public UserOptLog addUserOptLog(HSession session, int playerId, int type, int typeId, String content) {
		UserOptLog userOptLog = new UserOptLog();
		userOptLog.setPlayerId(playerId);
		userOptLog.setType(type);
		userOptLog.setTypeId(typeId);
		userOptLog.setContent(content);
		userOptLog.setStatus(UserOptLog.STATUS_SERVER_ADD);
		userOptLog.setAddTime(new Date());
		Platform.getEntityManager().createSync(userOptLog);
		
		//发送log
		Packet pt = new JSONPacket(HOpCodeEx.USER_OPT_LOG_CLIENT);
		pt.put("optLogId", userOptLog.getId());
		session.send(pt);
		return userOptLog;
	}

	@Override
	public void clientBackUserOptLog(int optLogId) {
		UserOptLog userOptLog = UserOptLogDAO.getOptLogById(optLogId);
		if(userOptLog != null){
			userOptLog.setStatus(UserOptLog.STATUS_CLIENT_BACK);
			Platform.getEntityManager().updateSync(userOptLog);
		}

	}
	/**
	 * 统一用户日志回馈
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.USER_OPT_LOG_BACK_CLIENT)
	public void userOptLogBack(Packet packet, HSession session){
		try {
			PlayerEx player = (PlayerEx) session.client();
			//发送人没有登陆
			if (player == null) {
				ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_1, packet.getopcode());
				return;
			}
			int optLogId = packet.getInt("optLogId");
			//更新log的状态为客户端已经获得奖励
			clientBackUserOptLog(optLogId);
			
			//发送成功
			Packet pt = new JSONPacket(HOpCodeEx.USER_OPT_LOG_BACK_SERVER);
			pt.put("result", 1);
			session.send(pt);
		} catch (Throwable e) {
			log.error("userOptLogBack error",e);
			ErrorHandlerEx.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_0, packet.getopcode());
		}
	}

}
