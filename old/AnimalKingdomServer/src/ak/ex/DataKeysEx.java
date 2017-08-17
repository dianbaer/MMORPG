/**
 * DataKeysEx.java
 * ak.ex
 *
 *   version  date      	author
 * ──────────────────────────────────
 *    1.0	 2013年11月23日 		shiwei2006
 *
 * Copyright (c) 2013, www.cyou-inc.com All Rights Reserved.
*/

package ak.ex;

import cyou.mrd.data.DataKeys;

/**
 * ClassName:DataKeysEx
 * ClassDescription:  扩展自引擎缓存定义
 *
 * @author   shiwei2006
 * @Date	 2013年11月23日		下午6:50:16
 * @version  1.0
 */
public class DataKeysEx extends DataKeys {

	/**
	 * 玩家互动key = key + player.playerId
	 * value = playerSns
	 */
	private static final String KEY_PLAYER_SNS = "playersns_";
	/**
	 * 排行榜key = key + type
	 * value = Rank
	 */
	private static final String KEY_RANK = "rank_";
	/**
	 * 贸易key = key + playerId
	 * value = Trade
	 */
	private static final String KEY_TRADE = "trade_";
	/**
	 * 市场key = key + playerId
	 * value = Market
	 */
	private static final String KEY_MARKET = "market_";
	
	/**
	 * playerSnsKey: 获取缓存定义的KEY
	 * @param playerId
	 * @return String
	*/
	public static final String playerSnsKey(int playerId){
		return KEY_PLAYER_SNS + playerId;
	}
	/**
	 * rankKey:获得相应类型的排行榜列表
	 * @param type
	 * @return String
	 */
	public static final String rankKey(int type){
		return KEY_RANK + type;
	}
	/**
	 * tradeKey:获得该玩家的贸易列表
	 * @param playerId
	 * @return String
	 */
	public static final String tradeKey(int playerId){
		return KEY_TRADE + playerId;
	}
	/**
	 * marketKey:获得该玩家的贸易列表
	 * @param playerId
	 * @return String
	 */
	public static final String marketKey(int playerId){
		return KEY_MARKET + playerId;
	}
}

