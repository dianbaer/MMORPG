package ak.playerSns;

import java.util.Hashtable;
import java.util.List;

import cyou.mrd.Platform;
import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataService;

public class PlayerSnsDAO {
	/**
	 * 根据playerId获取PlayerSns
	 * @param playerId
	 * @return
	 */
	public static PlayerSns getPlayerSnsByPlayerId(int playerId){
		List<PlayerSns> list = Platform.getEntityManager().limitQuery("from PlayerSns where playerId=?", 0, 1, playerId);
		if(list.get(0) != null){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * getMoneyTreeTemplate: 根据等级ID获取对应的摇钱树配置。
	 * @param gradeId
	 * @return MoneyTreeTemplate
	*/
	public static MoneyTreeTemplate getMoneyTreeTemplate(int gradeId){
		Hashtable<Integer, Template> templates = Platform.getAppContext().get(TextDataService.class).getTemplates(MoneyTreeTemplate.class);
		return (MoneyTreeTemplate)templates.get(gradeId);
	}
}
