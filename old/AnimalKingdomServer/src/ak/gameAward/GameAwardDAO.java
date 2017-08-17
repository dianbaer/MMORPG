package ak.gameAward;

import java.util.Hashtable;

import cyou.mrd.Platform;
import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataService;

public class GameAwardDAO {
	
	/**
	 * 根据id获取GameAwardTemplate
	 * @param id
	 * @return
	 */
	public static GameAwardTemplate getGameAward(int id){
		Hashtable<Integer, Template> templates = Platform.getAppContext().get(TextDataService.class).getTemplates(GameAwardTemplate.class);
		GameAwardTemplate gameAwardTemplate = (GameAwardTemplate)templates.get(id);
		return gameAwardTemplate;
	}
}
