package ak.server;

import java.util.Date;

import cyou.mrd.LogService;
import cyou.mrd.Platform;
import cyou.mrd.util.Utils;
import ak.building.BuildingList;
import ak.player.PlayerEx;

public class LogEx {

	/**
	 * 记录当前玩家的建筑列表, 记录更新后玩家建筑列表
	 * 
	 * @param player
	 * @param buildList
	 */
//	public static void syncBuildingList(int sceneId, PlayerEx player, BuildingList buildList) {
//		//
//		player.getId();
//		logBuildingList(player.getSceneBuildingList(sceneId));
//		logBuildingList(buildList);
//	}
//
//	private static void logBuildingList(BuildingList buildingList) {
//		// TODO 记录当前玩家的建筑列表
//	}

	// 修改昵称
	public static void changeName(int playerId, String oldName, String newName) {
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.getDateString(date))
				.append(",changeName,playerId(" + playerId + "oldName(" + oldName + ")newName(" + newName + ")");
		Platform.getLog().printLog(LogService.TYPE_CATALINA, sb.toString());
	}
}
