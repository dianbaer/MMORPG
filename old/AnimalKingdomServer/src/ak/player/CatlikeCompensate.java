package ak.player;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.data.Data;
import cyou.mrd.data.DataKeys;
import cyou.mrd.service.PlayerService;
import cyou.mrd.service.Service;

public class CatlikeCompensate implements Service{
	private static final Logger log = LoggerFactory.getLogger(CatlikeCompensate.class);

	private static int GIFT = 0;// 额外的礼物
	public static TIntIntMap whiteList = new TIntIntHashMap();

//	public static boolean compensate(PlayerEx player) {
//		Data data = Platform.dataCenter().getData(DataKeys.compensateKey());
//		if (data != null && data.value != null) {
//			whiteList = (TIntIntMap) data.value;
//		}
//		if (whiteList.containsKey(player.getInstanceId())) {
//			int targetMonye = whiteList.get(player.getInstanceId());
//			if (player.getAccount().getCompensateDollar() < targetMonye) {
//				log.info("[CatlikeCompensate] player:[{}], account:[{}]", player, player.getAccount());
//				player.getAccount().setCompensateDollar(targetMonye + GIFT);
//				log.info("[CatlikeCompensate]setImoney yet. account:[{}]", player.getAccount());
//				PlayerService service = Platform.getAppContext().get(PlayerService.class);
//				service.savePlayer(player, true,false);
//				return true;
//			}
//		}
//		return false;
//	}

	public static int needCompensate(PlayerEx player) {
		if (whiteList.containsKey(player.getInstanceId())) {
			int targetMonye = whiteList.get(player.getInstanceId());
			if (player.getAccount().getCompensateDollar() < targetMonye) {
				log.info("[needCompensate] player:[{}], account:[{}]", player, player.getAccount());
				return targetMonye - player.getAccount().getCompensateDollar();
			}
		}
		return 0;
	}

	public static boolean addCompensate(PlayerEx player, int targetCompentDollar) {
		if (player != null && player.getAccount() != null && player.getAccount().getCompensateDollar() < targetCompentDollar) {
			log.info("[addCompensate]addCompensate(targetCompentDollar{}).player:[{}] account:[{}]",new Object[]{targetCompentDollar, player, player.getAccount()});
			whiteList.put(player.getInstanceId(), targetCompentDollar);
			Data data = Platform.dataCenter().getData(DataKeys.compensateKey());
			if(data == null) {
				Platform.dataCenter().sendNewData(DataKeys.compensateKey(), whiteList);
			}else {
				whiteList = (TIntIntMap) data.value;
				whiteList.put(player.getInstanceId(), targetCompentDollar);
				data.value = whiteList;
				Platform.dataCenter().sendData(DataKeys.compensateKey(), data);
			}
			return true;
		}
		return false;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public void startup() throws Exception {
		try {
			String[] com = Platform.getConfiguration().getStringArray("compensate");
			for (String cm : com) {
				String[] ids = cm.split("@");
				int id = Integer.parseInt(ids[0]);
				int targetMoney = Integer.parseInt(ids[1]);
				whiteList.put(id, targetMoney);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Data data = Platform.dataCenter().getData(DataKeys.compensateKey());
		if(data != null) {
			TIntIntMap map = (TIntIntMap) data.value;
			whiteList.putAll(map);
		}
	}

	@Override
	public void shutdown() throws Exception {
		
	}

}
