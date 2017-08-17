package ak.censor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.building.Building;
import ak.player.PlayerEx;

public class CensorUtils {
	private static Logger log = LoggerFactory.getLogger(CensorUtils.class);

	public static boolean building(JSONArray builds, List<Building> buildingList, long lastSyncTime) {
		log.info("[CensorUtils] building(builds:{}, buildingList:{}, lastSyncTime:{})",
				new Object[] { builds == null ? "null" : builds.size(), buildingList == null ? "null" : buildingList.size(), lastSyncTime });
		return true;
	}

	public static boolean player(JSONObject playerInfo, JSONArray builds, PlayerEx player) {
		log.info("[CensorUtils] player Censor(JSONObject playerInfo size:{}, JSONArray builds size:{}, player:{})", new Object[] {
				playerInfo == null ? "null" : playerInfo.size(), builds == null ? "null" : builds.size(), player });
		if (player == null || player.getLastSynchInfoTime() == null || player.getLastSynchInfoTime().getTime() == 0) {
			// 检测开放游戏以来的最大值
		} else {
			// 检测玩家信息
			if (playerInfo.getInt("Level") > PlayerEx.MaxLevel) {
				log.info("[CensorUtils] Fail, case: Level({})>MaxLevel({})  playerId:{}", new Object[] { playerInfo.getInt("Level"),
						PlayerEx.MaxLevel, player.getInstanceId() });
				return false;
			}
			if (playerInfo.getInt("Money") < 0) {
				log.info("[CensorUtils] Fail, case: Money <0,  playerId:{}", player.getInstanceId());
				return false;
			}
			log.info("[CensorUtils] OK, player:{}", player.getInstanceId());
		}
		return true;
	}

}
