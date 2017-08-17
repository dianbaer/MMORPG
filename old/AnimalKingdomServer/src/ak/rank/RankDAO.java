package ak.rank;

import java.util.List;

import cyou.mrd.Platform;

public class RankDAO {
	/**
	 * 根据类型获取排行榜信息列表
	 * @param type
	 * @return
	 */
	public static List<Rank> getRankListByType(int type){
		List<Rank> list = Platform.getEntityManager().query("from Rank where type=? order by nowRank asc", type);
		
		return list;
	}
//	public static List<Rank> getRankListByTypeNotNull(int type){
//		List<Rank> list = Platform.getEntityManager().query("from Rank where type=? and playerId > 0 order by nowRank asc", type);
//		
//		return list;
//	}
}
