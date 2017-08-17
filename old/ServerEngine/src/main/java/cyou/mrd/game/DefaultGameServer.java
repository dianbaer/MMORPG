package cyou.mrd.game;

import cyou.mrd.Platform;
import cyou.mrd.service.AdminService;
import cyou.mrd.service.PlayerService;
import cyou.mrd.service.VersionService;

/**
 * @author 
 *	默认的游戏服务，提供的基本的玩家、版本和GM服务
 */
public class DefaultGameServer implements GameServer {

	@Override
	public void startup() throws Exception {
		// 创建service
		Platform.getAppContext().create(PlayerService.class, PlayerService.class);

		Platform.getAppContext().create(VersionService.class, VersionService.class);
		Platform.getAppContext().create(AdminService.class, AdminService.class);
	}

}
