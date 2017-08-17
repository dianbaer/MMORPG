//package ak.gm;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import mrd.gm.op.GMOpCodeReceive;
//import mrd.gm.op.GMOpCodeSend;
//import mrd.gm.order.GMOrderContext;
//import mrd.gm.util.Contant;
//import mrd.gm.util.JSONContant;
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//
//import org.apache.log4j.Logger;
//
//import ak.mail.MailUseType;
//import ak.player.CatlikeCompensate;
//import ak.player.PlayerEx;
//import ak.player.PlayerServiceEx;
//import cyou.mrd.Platform;
//import cyou.mrd.account.Account;
//import cyou.mrd.game.mail.DefaultMailService;
//import cyou.mrd.game.mail.Mail;
//import cyou.mrd.game.mail.MailException;
//import cyou.mrd.game.mail.MailService;
//import cyou.mrd.io.http.SessionManager;
//import cyou.mrd.service.PlayerService;
//import cyou.mrd.service.Service;
//import cyou.mrd.util.IdUtil;
//
//public class GM extends GMOrderContext implements Service{
//
//	private static Logger log = Logger.getLogger(GM.class);
//	// 测试数据
//	private static final int SUCCESS = 1;
//	private static final int FAILED = 0;
//	@Override
//	public String getOnLineNum(String arg0) {
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put(JSONContant.OP, GMOpCodeSend.OL_NUM_RESULT);
//			obj.put(JSONContant.FROM, GMOrderContext.getServerFlag());
//			obj.put(JSONContant.TO, Contant.GM);
//			JSONObject o = new JSONObject();
//			o.put("olnum", SessionManager.worldOnlineUser);
//			obj.put(JSONContant.PARAM, o);
//			// ONLINE++;
//			return obj.toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	@Override
//	public String getPlayerInfo(String arg0) {
//		JSONObject obj = JSONObject.fromObject(arg0);
//		JSONObject param = obj.getJSONObject(JSONContant.PARAM);
//		String name = param.getString("name");
//		int id = IdUtil.deCode(name.trim());
//		PlayerServiceEx service = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
//		PlayerEx player = service.loadPlayer(service.getPlayerIdByAccountId(id));
//
//		JSONObject result = new JSONObject();
//		result.put(JSONContant.OP, GMOpCodeSend.PLAYER_INFO_RESULT);
//		result.put(JSONContant.FROM, GMOrderContext.getServerFlag());
//		result.put(JSONContant.TO, obj.getString(JSONContant.FROM));
//		if (player != null) {
//			JSONArray array = getPlayerInfo(player);
//			JSONObject p = new JSONObject();
//			p.put(JSONContant.PARAM, array);
//			result.put(JSONContant.PARAM, p);
//			return result.toString();
//		} else {
//			JSONObject no = new JSONObject();
//			no.put("novalue", "no such player" + name.trim());
//			result.put(JSONContant.PARAM, no);
//			log.info("GM:getPlayerInfo:player is null");
//			return result.toString();
//		}
//	}
//
//	@Override
//	public String modifyTemplate(String arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String sendMail(String arg0) {
//		JSONObject obj = JSONObject.fromObject(arg0);
//		JSONObject param = obj.getJSONObject(JSONContant.PARAM);
//		String name = param.getString("name");
//		String content = param.getString("content");
//		int id = IdUtil.deCode(name.trim());
//		PlayerEx player = Platform.getEntityManager().fetch(PlayerEx.class, "from PlayerEx where accountId = ? and exist = 0", id);
//		MailService<Mail> service = Platform.getAppContext().get(MailService.class);
//		Mail mail = new Mail();
//		mail.setContent(content);
//		mail.setDestId(player.getInstanceId());
//		mail.setPostTime(new Date());
//		mail.setSourceId(-1);
//		mail.setSourceName("system");
//		mail.setType(DefaultMailService.MAIL_TYPE_SYSTEM);
//		mail.setUseType(MailUseType.MAIL_GM);
//		try {
//			service.sendMail(mail);
//		} catch (MailException e) {
//			// cuowu
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	@Override
//	public String deleteNotice(String arg0) {
//		JSONObject msg = JSONObject.fromObject(arg0);
//		JSONObject param = msg.getJSONObject(JSONContant.PARAM);
//		String name = param.getString("name");
//		
//		MailService service = Platform.getAppContext().get(MailService.class);
//		boolean suc = service.delSystemNoticeMail(name);
//		JSONObject obj = new JSONObject();
//		obj.put(JSONContant.OP, GMOpCodeSend.DELETE_NOTICE_RESULT);
//		obj.put(JSONContant.FROM, GMOrderContext.getServerFlag());
//		obj.put(JSONContant.TO, msg.getString(JSONContant.FROM));
//		if(suc){
//			JSONObject p = new JSONObject();
//			p.put("result", SUCCESS);
//			obj.put(JSONContant.PARAM, p);
//			return obj.toString();
//		}else{
//			JSONObject p = new JSONObject();
//			p.put("result", FAILED);
//			obj.put(JSONContant.PARAM, p);
//			return obj.toString();
//		}
//	}
//
//	@Override
//	public String sendNotice(String arg0) {
//		JSONObject msg = JSONObject.fromObject(arg0);
//		JSONObject param = msg.getJSONObject(JSONContant.PARAM);
//		JSONArray array = param.getJSONArray("notice");
//		String[] contents = new String[13];
//		for(int i=0;i<array.size();i++){
//			JSONObject notice = (JSONObject)array.opt(i);
//			int langId = notice.getInt("langid");
//			String content = notice.getString("content");
//			contents[langId] = content;
//		}
//		for(int i=0;i<contents.length;i++){
//			if(contents[i] == null){
//				contents[i] = "";
//			}
//		}
//		MailService service = Platform.getAppContext().get(MailService.class);
//		boolean suc = service.sendSystemNoticeMail(contents);
//		JSONObject obj = new JSONObject();
//		obj.put(JSONContant.OP, GMOpCodeSend.SEND_NOTICE_RESULT);
//		obj.put(JSONContant.FROM, GMOrderContext.getServerFlag());
//		obj.put(JSONContant.TO, msg.getString(JSONContant.FROM));
//		if(suc){
//			JSONObject p = new JSONObject();
//			p.put("result", SUCCESS);
//			obj.put(JSONContant.PARAM, p);
//			return obj.toString();
//		}else{
//			JSONObject p = new JSONObject();
//			p.put("result", FAILED);
//			obj.put(JSONContant.PARAM, p);
//			return obj.toString();
//		}
//	}
//
//	/**
//	 * 获取用户信息
//	 * 
//	 * @param player
//	 * @return
//	 */
//	private JSONArray getPlayerInfo(PlayerEx player) {
//		JSONArray array = new JSONArray();
//		Account account = player.getAccount();
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//		// *********accountId****************
//		JSONObject accountId = new JSONObject();
//		accountId.put(JSONContant.ATT, "账户ID：" + player.getAccountId());
//		array.add(accountId);
//		// **********************************
//		// *********Account_Mid****************
//		if(account!=null){
//			JSONObject account_Mid = new JSONObject();
//			account_Mid.put(JSONContant.ATT, "Mid：" + account.getMid());
//			array.add(account_Mid);
//		}
//		// ************************************
//		// *********name****************
//		JSONObject name = new JSONObject();
//		name.put(JSONContant.ATT, "用户名：" + player.getName());
//		array.add(name);
//		// *****************************
//		// *********exist****************
//		JSONObject exist = new JSONObject();
//		String exist_str = (player.getExist() == 1) ? "是" : "否";
//		exist.put(JSONContant.ATT, "是否删除：" + exist_str);
//		array.add(exist);
//		// ******************************
//		// *********country****************
//		JSONObject country = new JSONObject();
//		country.put(JSONContant.ATT, "国家 ：" + player.getCountry());
//		array.add(country);
//		// ********************************
//		// *********level****************
//		JSONObject level = new JSONObject();
//		level.put(JSONContant.ATT, "等级：" + player.getLevel());
//		array.add(level);
//		// ******************************
//		// *********money****************
//		JSONObject money = new JSONObject();
//		money.put(JSONContant.ATT, "金钱：" + player.getMoney());
//		array.add(money);
//		// ******************************
//		// *********exp****************
//		JSONObject exp = new JSONObject();
//		exp.put(JSONContant.ATT, "经验值：" + player.getExp());
//		array.add(exp);
//		// ****************************
//		// *********lang****************
//		JSONObject lang = new JSONObject();
//		int l = player.getLang() - 1;//客户端上传的值是从1 开始的.
//		String lang_str = null;
//		switch (l) {
//		case 0:
//			lang_str = "中文";
//			break;
//		case 1:
//			lang_str = "英文";
//			break;
//		default:
//			lang_str = "语言" + l;
//			break;
//		}
//		lang.put(JSONContant.ATT, "语言：" + lang_str);
//		array.add(lang);
//		// *****************************
//		if (account != null) {
//			// *********Account_compensateDollar****************
//			JSONObject compensateDollar = new JSONObject();
//			compensateDollar.put(JSONContant.ATT, "系统补偿灯笼：" + account.getCompensateDollar());
//			array.add(compensateDollar);
//			// *************************************************
//			// *********Account_Imoney****************
//			JSONObject account_Imoney = new JSONObject();
//			account_Imoney.put(JSONContant.ATT, "充值灯笼：" + account.getImoney());
//			array.add(account_Imoney);
//			// ***************************************
//			// *********Account_InitDollar****************
//			JSONObject account_InitDollar = new JSONObject();
//			account_InitDollar.put(JSONContant.ATT, "初始化灯笼：" + account.getInitDollar());
//			array.add(account_InitDollar);
//			// *******************************************
//			// *********Account_RemainDollar****************
//			JSONObject account_RemainDollar = new JSONObject();
//			account_RemainDollar.put(JSONContant.ATT, "剩余灯笼：" + account.getRemainDollar());
//			array.add(account_RemainDollar);
//			// *********************************************
//			// *********Account_RewarDollar****************
//			JSONObject account_RewarDollar = new JSONObject();
//			account_RewarDollar.put(JSONContant.ATT, "获得灯笼：" + account.getRewardDollar());
//			array.add(account_RewarDollar);
//			// ********************************************
//			// *********Account_UsedDollar****************
//			JSONObject account_UsedDollat = new JSONObject();
//			account_UsedDollat.put(JSONContant.ATT, "已使用灯笼：" + account.getUsedDollar());
//			array.add(account_UsedDollat);
//			// *******************************************
//		} else {
//			log.info("GM:getPlayerInfo:account is null");
//		}
//		// *********lastLoginTime****************
//		if (player.getLastLoginTime() != null) {
//			JSONObject lastLoginTime = new JSONObject();
//			lastLoginTime.put(JSONContant.ATT, "最后登录时间：" + format.format(player.getLastLoginTime()));
//			array.add(lastLoginTime);
//		}
//		// **************************************
//		// *********lastSynchInfoTime****************
//		if (player.getLastSynchInfoTime() != null) {
//			JSONObject lastSynchInfoTime = new JSONObject();
//			lastSynchInfoTime.put(JSONContant.ATT, "最后同步时间：" + format.format(player.getLastSynchInfoTime()));
//			array.add(lastSynchInfoTime);
//		}
//		// *****************************************
//		// *********createTime****************
//		if (player.getCreateTime() != null) {
//			JSONObject createTime = new JSONObject();
//			createTime.put(JSONContant.ATT, "创建时间：" + format.format(player.getCreateTime()));
//			array.add(createTime);
//		}
//		// ***********************************
//		// *********saveTime****************
//		JSONObject saveTime = new JSONObject();
//		long time = player.getSaveTime() * 1000L;
//		Date save = new Date(time);
//		saveTime.put(JSONContant.ATT, "最后存档时间：" + format.format(save));
//		array.add(saveTime);
//		// *********************************
//		return array;
//	}
//
//	@Override
//	public void addOrder() {
//		super.mOrders.add(GMOpCodeReceive.GET_PLAYER_INFO);
//		super.mOrders.add(GMOpCodeReceive.SEND_MAIL);
//		super.mOrders.add(GMOpCodeReceive.SEND_NOTICE);
//		super.mOrders.add(GMOpCodeReceive.DELETE_NOTICE);
//		super.mOrders.add(GMOpCodeReceive.SEND_DENGLONG);
//	}
//
//	@Override
//	public String getPlayerLang(String arg0) {
//		JSONObject obj = JSONObject.fromObject(arg0);
//		JSONObject param = obj.getJSONObject(JSONContant.PARAM);
//		String name = param.getString("username");
//		String from = obj.getString(JSONContant.FROM);
//		int id = IdUtil.deCode(name.trim());
//		PlayerServiceEx service = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
//		PlayerEx player = service.loadPlayer(service.getPlayerIdByAccountId(id));
//		
//		JSONObject result = new JSONObject();
//		result.put(JSONContant.OP, GMOpCodeSend.PLAYER_LANG_RESULT);
//		result.put(JSONContant.FROM, GMOrderContext.getServerFlag());
//		result.put(JSONContant.TO, from);
//		if (player != null) {
//			int l = player.getLang()-1;//客户端上传的值是从1 开始的.
//			String lang = null;
//			switch (l) {
//			case 0:
//				lang = "中文";
//				break;
//			case 1:
//				lang = "英文";
//				break;
//			default:
//				lang = "语言" + l;
//				break;
//			}
//			JSONObject p = new JSONObject();
//			p.put("lang", lang);
//			p.put("playerName", name);
//			result.put(JSONContant.PARAM, p);
//			return result.toString();
//		} else {
//			log.info("no such player");
//			JSONObject no = new JSONObject();
//			no.put("novalue", "no such player" + name.trim());
//			result.put(JSONContant.PARAM, no);
//			return result.toString();
//		}
//	}
//
//	@Override
//	public String getLang(String arg0) {
//		JSONObject obj = JSONObject.fromObject(arg0);
//		String from = obj.getString(JSONContant.FROM);
//		int[] lang = Platform.getAppContext().get(MailService.class).getServerLangInfo();
//		JSONArray array = new JSONArray();
//		for (int i = 0; i < lang.length; i++) {
//			if (lang[i] == 1) {
//				switch (i) {
//				case 0:
//					array.add("中文");
//					break;
//				case 1:
//					array.add("英文");
//					break;
//				default:
//					array.add("语言" + i);
//					break;
//				}
//			}
//		}
//		JSONObject msg = new JSONObject();
//		msg.put(JSONContant.OP, GMOpCodeSend.LANG_RESULT);
//		msg.put(JSONContant.FROM, GMOrderContext.getServerFlag());
//		msg.put(JSONContant.TO, from);
//		JSONObject param = new JSONObject();
//		param.put(JSONContant.PARAM, array);
//		msg.put(JSONContant.PARAM, param);
//		return msg.toString();
//	}
//
//	@Override
//	public String sendDenglong(String arg0) {
//		JSONObject obj = JSONObject.fromObject(arg0);
//		JSONObject param = obj.getJSONObject(JSONContant.PARAM);
//		String name = param.getString("name");
//		int count = param.getInt("count");
//		int id = IdUtil.deCode(name.trim());
//		PlayerServiceEx service = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
//		PlayerEx player = service.loadPlayer(service.getPlayerIdByAccountId(id));
//		JSONObject msg = new JSONObject();
//		msg.put(JSONContant.OP, GMOpCodeSend.SEND_DENGLONG_RESULT);
//		msg.put(JSONContant.FROM, GMOrderContext.getServerFlag());
//		msg.put(JSONContant.TO, obj.getString(JSONContant.FROM));
//		if(player != null){
//			if(player.getAccount() != null && player.getAccount().getCompensateDollar() < count){ 
//				boolean ret = CatlikeCompensate.addCompensate(player, count);
//				if(!ret){
//					JSONObject error = new JSONObject();
//					error.put("result", FAILED);
//					error.put("msg", "补偿失败");
//					msg.put(JSONContant.PARAM, error);
//				}
//			}else {
//				JSONObject error = new JSONObject();
//				error.put("result", FAILED);
//				error.put("msg", "补偿失败（该玩家金钱已经超过目标值？）："+name);
//				msg.put(JSONContant.PARAM, error);
//			}
//			
//		}else{
//			JSONObject noPlayer = new JSONObject();
//			noPlayer.put("result", FAILED);
//			noPlayer.put("msg", "没有该玩家："+name);
//			msg.put(JSONContant.PARAM, noPlayer);
//		}
//		return msg.toString();
//	}
//
//	@Override
//	public String getId() {
//		return "GMService";
//	}
//
//	@Override
//	public void startup() {
//		try {
//			this.run("ak.gm.GM", Platform.getGameCode(), Platform.getGameId());
//			log.info("gm start ok!");
//		} catch (Throwable e) {
//			log.error(e);
//		}
//	}
//
//	@Override
//	public void shutdown() throws Exception {
//		
//	}
//}
