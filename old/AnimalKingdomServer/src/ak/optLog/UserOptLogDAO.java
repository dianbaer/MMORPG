package ak.optLog;

import cyou.mrd.Platform;

public class UserOptLogDAO {
	
	/**
	 * 根据id获取日志
	 * @param optLogId
	 * @return
	 */
	public static UserOptLog getOptLogById(int optLogId){
		UserOptLog userOptLog = Platform.getEntityManager().find(UserOptLog.class, optLogId);
		return userOptLog;
	}
}
