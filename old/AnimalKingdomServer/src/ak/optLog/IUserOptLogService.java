package ak.optLog;

import cyou.mrd.io.http.HSession;
import cyou.mrd.service.Service;

public interface IUserOptLogService extends Service {
	/**
	 * 添加日志
	 * @param playerId
	 * @param type
	 * @param typeId
	 * @param content
	 */
	public UserOptLog addUserOptLog(HSession session, int playerId, int type,int typeId,
			String content);
	/**
	 * 客户端成功相应，改变status字段
	 * @param optLogId
	 */
	public void clientBackUserOptLog(int optLogId);
}
