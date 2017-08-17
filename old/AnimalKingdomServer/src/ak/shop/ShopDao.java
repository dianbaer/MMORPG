package ak.shop;

import java.util.Hashtable;

import cyou.mrd.Platform;
import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataService;

public class ShopDao {
	/**
	 * 获取商店物品根据id
	 * @param id
	 * @return
	 */
	public static ShopItemTemplate getShopItem(int id){
		Hashtable<Integer, Template> templates = Platform.getAppContext().get(TextDataService.class).getTemplates(ShopItemTemplate.class);
		ShopItemTemplate shopItemTemplate = (ShopItemTemplate)templates.get(id);
		return shopItemTemplate;
	}
}
