package ak.trade;

import java.util.List;

import cyou.mrd.Platform;

public class TradeDAO {
	/**
	 * 查找玩家贸易列表
	 * @param playerId
	 * @return
	 */
	public static List<Trade> getTradeById(int playerId){
		
		List<Trade> list = Platform.getEntityManager().query("from Trade where playerId = ?", playerId);
		
		return list;
	}
}	
