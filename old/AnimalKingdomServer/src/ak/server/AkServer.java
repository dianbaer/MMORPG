package ak.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.battle.BattleService;
import ak.friend.FriendService;
import ak.gm.AkGMService;
import ak.iap.ChargeClientServiceEx;
import ak.log.AkLogService;
import ak.mail.AkMailService;
import ak.mail.IAkMailService;
import ak.market.MarketService;
import ak.notice.AkNoticeService;
import ak.optLog.IUserOptLogService;
import ak.optLog.UserOptLogService;
import ak.player.CatlikeCompensate;
import ak.player.PlayerServiceEx;
import ak.playerSns.PlayerSnsService;
import ak.rank.RankService;
import ak.sdk.SdkService;
import ak.shop.ShopService;
import ak.trade.TradeService;
import ak.world.WorldManagerEx;
import cyou.mrd.Platform;
import cyou.mrd.charge.ChargeClientService;
import cyou.mrd.game.GameServer;
import cyou.mrd.game.relation.RelationService;
import cyou.mrd.lock.LoginMidLockService;
import cyou.mrd.lock.PlayerLockService;
import cyou.mrd.service.AdminService;
import cyou.mrd.service.PlayerService;
import cyou.mrd.service.VersionService;
import cyou.mrd.world.WorldManager;

public class AkServer implements GameServer {
	
	private static final Logger log = LoggerFactory.getLogger(AkServer.class);

	@Override
	public void startup() throws Exception {
		Platform.getAppContext().create(SdkService.class, SdkService.class);
		Platform.getAppContext().create(AkLogService.class, AkLogService.class);
		// 创建service
		Platform.getAppContext().create(WorldManagerEx.class, WorldManager.class);
		//系统日志
		Platform.getAppContext().create(UserOptLogService.class, IUserOptLogService.class);
		Platform.getAppContext().create(PlayerServiceEx.class, PlayerService.class);
		Platform.getAppContext().create(FriendService.class, RelationService.class);
		Platform.getAppContext().create(BattleService.class,BattleService.class);
		Platform.getAppContext().create(VersionService.class, VersionService.class);
		Platform.getAppContext().create(AdminService.class, AdminService.class);
		Platform.getAppContext().create(ShopService.class, ShopService.class);
		Platform.getAppContext().create(CatlikeCompensate.class, CatlikeCompensate.class);
		//Platform.getAppContext().create(GMService.class, GMService.class);
		Platform.getAppContext().create(AkGMService.class, AkGMService.class);
		Platform.getAppContext().create(ChargeClientServiceEx.class, ChargeClientService.class);
		Platform.getAppContext().create(AkMailService.class, IAkMailService.class);
		Platform.getAppContext().create(AkNoticeService.class, AkNoticeService.class);
		Platform.getAppContext().create(PlayerSnsService.class, PlayerSnsService.class);
		Platform.getAppContext().create(PlayerLockService.class, PlayerLockService.class);
		Platform.getAppContext().create(LoginMidLockService.class, LoginMidLockService.class);
		Platform.getAppContext().create(MarketService.class, MarketService.class);
		Platform.getAppContext().create(RankService.class, RankService.class);
		Platform.getAppContext().create(TradeService.class, TradeService.class);
		log.info("[startup]AkServer ok");
	}
	
	 
}
