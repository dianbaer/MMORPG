package ak.shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.optLog.IUserOptLogService;
import ak.optLog.UserOptLog;
import ak.player.PlayerEx;
import ak.player.PlayerServiceEx;
import ak.server.ErrorHandlerEx;
import cyou.mrd.Platform;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;
import cyou.mrd.io.tcp.HOpCodeEx;
import cyou.mrd.service.PlayerService;
import cyou.mrd.service.Service;
import cyou.mrd.util.ErrorHandler;

/**
 * 游戏不需要做成实时购买需要访问服务器. 同步金钱时附带消费记录. 服务器比对,验证.
 * 
 * @author Administrator
 * 
 */
@OPHandler(TYPE = OPHandler.HTTP)
public class ShopService implements Service {
	private static final Logger log = LoggerFactory.getLogger(ShopService.class);


	//private TIntObjectMap<ShopItemTemplate> shopItems;

	@Override
	public String getId() {
		return "ShopService";
	}

	@Override
	public void startup() throws Exception {
		// 读表
//		Map<Integer, Template> items = Platform.getAppContext().get(TextDataService.class).getTemplates(ShopItemTemplate.class);
//		if (items != null) {
//			shopItems = new TIntObjectHashMap<ShopItemTemplate>();
//			Set<Entry<Integer, Template>> itemSet = items.entrySet();
//			for (Entry<Integer, Template> entry : itemSet) {
//				shopItems.put(entry.getKey(), (ShopItemTemplate) entry.getValue());
//			}
//		}
//		log.info("[ShopService] items:{}", items == null ? 0 : items.size());
	}

	@Override
	public void shutdown() throws Exception {

	}
	/**
	 * 用爱心值购买商品
	 * @param packet
	 * @param session
	 */
	@OP(code = HOpCodeEx.CLIENT_BUY_SHOPITEM_ONLOVE)
	public void BuyShopItem(Packet packet, HSession session) {
		try {
			PlayerEx player = (PlayerEx) session.client();
			if (player == null) {
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
				return;
			}
			int shopId = packet.getInt("shopId");
			ShopItemTemplate shopItemTemplate = ShopDao.getShopItem(shopId);
			//如果不存在或者货币类型不是爱心值
			if(shopItemTemplate == null || shopItemTemplate.getMoneyType() != ShopItemTemplate.CURRENCY_TYPE_LOVE){
				ErrorHandler.sendErrorMessage(session, ErrorHandlerEx.ERROR_CODE_70, packet.getopcode());
				return;
			}
			Packet retPt = new JSONPacket(HOpCodeEx.SERVER_BUY_SHOPITEM_ONLOVE);
			if(player.getLove() >= shopItemTemplate.getMoney()){
				//增加日志用来记录
				IUserOptLogService userOptLogService = Platform.getAppContext().get(IUserOptLogService.class);
				userOptLogService.addUserOptLog(session, player.getId(), UserOptLog.TYPE_LOVE_SHOP_ITEM, shopItemTemplate.getId(),UserOptLog.CONTENT_1);
				
				//减去爱心值
				player.setLove(player.getLove()-shopItemTemplate.getMoney());
				player.notifySave();
				retPt.put("result", 1);
				retPt.put("shopId", shopId);
				retPt.put("love", player.getLove());
			}else{
				retPt.put("result", 0);
				retPt.put("shopId", shopId);
				retPt.put("love", player.getLove());
			}
			session.send(retPt);
			PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
			playerService.addLove(session,player);
		} catch (Throwable e) {
			log.error("BuyShopItem error",e);
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
		}
		
	}
//	public int getImoneyByTemplateId(int templateId) {
//		ShopItemTemplate itemTemplate = shopItems.get(templateId);
//		if (itemTemplate != null && itemTemplate.getManeyType() == ShopItemTemplate.CURRENCY_TYPE_IMONEY) {
//			int imoney = itemTemplate.getManey();
//			log.info("[METHODEND] return[imoney({})]",imoney);
//			return imoney;
//		}
//		log.info("[METHODEND] return[imoney({})]",0);
//		return 0;
//	}
}
