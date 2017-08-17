/**
 * MarketDAO.java
 * ak.market
 *
 *   version  date      	author
 * ──────────────────────────────────
 *    1.0	 2013年11月30日 		shiwei2006
 *
 * Copyright (c) 2013, www.cyou-inc.com All Rights Reserved.
 */

package ak.market;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataService;

/**
 * ClassName:MarketDAO ClassDescription: 市场相关持久化存取类
 * 
 * @author shiwei2006
 * @Date 2013年11月30日 下午8:05:56
 * @version 1.0
 */
public class MarketDAO {

	public static final Logger log = LoggerFactory.getLogger(MarketDAO.class);

	/**
	 * getPosCount: 获取当前玩家的格子总数量
	 * 
	 * @param playerId
	 * @return int
	 */
//	public static int getPosCount(int playerId) {
//		int num = (int) Platform.getEntityManager().count("select count(*) from Market where playerId = ?", playerId);
//		log.info("[MarketDAO] [getPosCount] playerId:{}, count:{}", playerId, num);
//		return num;
//	}

	/**
	 * getMarketList: 获取当前玩家所有格子列表数据
	 * 
	 * @param playerId
	 * @return List<Market>
	 */
	public static List<Market> getMarketList(int playerId) {
		List<Market> list = Platform.getEntityManager().query("from Market where playerId = ? order by pos asc", playerId);
		log.info("[MarketDAO] [getMarketList] playerId:{}, marketSize:{}", playerId, list == null ? 0 : list.size());
		return list;
	}

	/**
	 * queryMarketList: 按照物品ID进行搜索，过滤自己
	 * @param playerId
	 * @param itemId
	 * @param count
	 * @param now
	 * @return 
	 * @return List<Market>
	*/
	public static List<Market> queryMarketList(int playerId, int itemId, int count, int now) {
		List<Market> list = Platform.getEntityManager().limitQuery(
				"from Market where itemId = ? and status = 2 and player_id != ? and expireTime > ? order by addTime desc", 0, count, itemId, playerId, now);
		log.info("[MarketDAO] [queryMarketList] itemId:{}, marketSize:{}", itemId, list == null ? 0 : list.size());
		return list;
	}
	
	/**
	 * getMarketById: 联合主键查找对应记录，没有返回NULL
	 * @param playerId
	 * @param pos
	 * @return Market
	*/
//	public static Market getMarketById(int playerId, int pos){
//		//暂时由查询语句获取，直接联合主键查找待测试
//		List<Market> list = Platform.getEntityManager().query(Market.class, "from Market where playerId = ? and pos = ?", playerId, pos);
//		if(list != null && !list.isEmpty()){
//			return list.get(0);
//		}
//		return null;
//	}
	
	/**
	 * findFriendMarketPosList: 查找格子配置中所有需要好友数量条件开启的记录，可能不是排好序的。
	 * @return List<MarketPosTemplate>没有返回空列表
	*/
	public static List<MarketPosTemplate> findFriendMarketPosList(){
		Hashtable<Integer, Template> templates = Platform.getAppContext().get(TextDataService.class).getTemplates(MarketPosTemplate.class);
		List<MarketPosTemplate> list = new ArrayList<MarketPosTemplate>();
		Set<Integer> keySet = templates.keySet();
		for(Integer id : keySet){
			MarketPosTemplate mt = (MarketPosTemplate)templates.get(id);
			if(mt.getOpenType() == MarketPosTemplate.OPEN_TYPE_FRIEND){
				list.add(mt);
			}
		}
		return list;
	}
}
