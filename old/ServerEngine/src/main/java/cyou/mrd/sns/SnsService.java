package cyou.mrd.sns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyou.mrd.platform.snssdk.SNSConfig;
import com.cyou.mrd.platform.snssdk.SNSFactory;
import com.cyou.mrd.platform.snssdk.bo.PlatType;
import com.cyou.mrd.platform.snssdk.exceptions.SNSException;
import com.cyou.mrd.platform.snssdk.service.SNSClient;

import cyou.mrd.Platform;
import cyou.mrd.account.AccountSNS;
import cyou.mrd.account.AccountService;
import cyou.mrd.entity.Player;
import cyou.mrd.game.actor.Actor;
import cyou.mrd.game.relation.RelationService;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HOpCode;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;
import cyou.mrd.service.Service;
import cyou.mrd.util.ErrorHandler;

@OPHandler(TYPE = OPHandler.HTTP)
public class SnsService implements Service {
	private static final Logger log = LoggerFactory.getLogger(SnsService.class);

	public String getId() {
		return "snsService";
	}

	public void startup() throws Exception {
		String url = Platform.getConfiguration().getString("sns_server_url");
		String port = Platform.getConfiguration().getString("sns_server_port");

		SNSConfig.setServer(url);
		SNSConfig.setPort(port);
		SNSConfig.setSourceId(Platform.getGameCode());

		// SNSConfig.setServer("10.6.34.122");
		// SNSConfig.setPort("8290");
		log.info("[SNS] init url[{}], port[{}], sns sourceId:{}", new Object[] { url, port, Platform.getGameCode() });
	}

	public void shutdown() throws Exception {

	}

	/**
	 * 批量导入sns好友
	 * 
	 * @param snsType
	 * @param accessToken
	 */
	@OP(code = HOpCode.SNS_LOADFRIEND_CLIENT)
	public void snsLoadFriends(Packet packet, HSession session) {
		log.info("[HTTPRequest] session[{}]  packet[{}]]", session.getSessionId(), packet.toString());
		Player p = (Player) session.client();
		packet.getRunTimeMonitor().knock("snsLoadFriends");
		if (p == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		String snsType = packet.getString("snsType");
		String accessToken = p.getPool().getString(Player.POOL_KEY_SNSTOKEY + snsType);
		if (accessToken == null || accessToken.length() == 0) {
			log.info("[SNS] player({}) loadFriend[Fail] case[accessToken(null)]", p.getInstanceId());
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_37, HOpCode.SNS_LOADFRIEND_CLIENT);
			return;
		}

		packet.getRunTimeMonitor().knock("accessToken");
		SNSClient snsClient = null;
		List<String> friendIds = null;
		try {
			snsClient = SNSFactory.getServiceByPlatType(PlatType.valueOf(snsType));
			packet.getRunTimeMonitor().knock("snsClient");
			if (snsClient == null) {
				log.info("[SNS] playerId:{} loadFriend[Fail] case[snsClient(null)]", p.getInstanceId());
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_38, HOpCode.SNS_LOADFRIEND_CLIENT);
				packet.getRunTimeMonitor().knock("snsClient == null");
				return;
			}
			friendIds = snsClient.getFriendIds(accessToken);
			packet.getRunTimeMonitor().knock("getFriendIds");
			if (friendIds == null || friendIds.size() == 0 || (friendIds.size() == 1 && friendIds.get(0).length() == 0)) {
				log.info("[SNS] playerId:{} loadFriend[Fail] case[friendIds(null)]", p.getInstanceId());
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_39, HOpCode.SNS_LOADFRIEND_CLIENT);
				packet.getRunTimeMonitor().knock("friendIds == null");
				return;
			}else {
				log.info("[SNS] playerId:{} loadFriend[OK] size:{} friendIds({})", new Object[]{p.getInstanceId(),friendIds.size(), Arrays.toString(friendIds.toArray())});
			}
		} catch (SNSException e) {
			log.error(e.getMessage());
			log.info("[SNS] playerId:{} loadFriend[Fail] case[Exception({}:{})]",
					new Object[] { p.getInstanceId(), e.getErrorCode(), e.getMessage() });

			switch (e.getErrorCode()) {
			case 101: // Access Token无效
				log.info("[SNS] Access Token out of work");
				ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_37, HOpCode.SNS_LOADFRIEND_CLIENT);
				return;
			case 102: // SNS平台其他异常
			case 103: // 103 ： SNS服务器异常
			case 104: // 104 ： 网络异常 对应游戏服务器或客户端与SNS服务器之间的网络连接状况
			case 105:// 105 ： 不支持的接口异常 对应qq平台目前不支持好友列表的获取
			case 106: // 106 ： 发送feed内容重复或不合法
			case 107: // 107 ： 参数错误 对应客户端或游戏服务器向SNS服务器传递的参数错误异常
			default:
				break;
			}

			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_39, HOpCode.SNS_LOADFRIEND_CLIENT);
			packet.getRunTimeMonitor().knock("exception:" + e.getMessage());
			return;
		}
		JSONPacket retP = new JSONPacket(HOpCode.SNS_LOADFRIEND_SERVER);
		JSONArray ja = null;
		if (friendIds != null && friendIds.size() > 0) {
			List<Actor> wantAddFriends = getSnsFriendsFast(snsType, friendIds);
			packet.getRunTimeMonitor().knock("getSnsFriendsFast!");
			if (wantAddFriends != null && wantAddFriends.size() > 0) {
				try {
					ja = Platform.getAppContext().get(RelationService.class).addSNSFriend(p, wantAddFriends);
					packet.getRunTimeMonitor().knock("RelationServiceAddFriend!");
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		} else {
			retP.put("friends", "[]");
			session.send(retP);
		}
		if (ja == null) {
			retP.put("friends", "[]");
			session.send(retP);
		} else {
			retP.put("friends", ja.toString());
			session.send(retP);
		}
		log.info("[METHODEND] return[null]");
	}

	/**
	 * 根据sns好友列表 查找游戏内好友
	 * 
	 * @param snsType
	 * @param friendIds
	 * @return
	 * @deprecated 由于 批量查询数据库查询效率低下. 废弃.请使用getSnsFriendsFast
	 */
	public List<Actor> getSnsFriends(String snsType, List<String> friendIds) {
		List<Actor> friends = new ArrayList<Actor>();
		for (String id : friendIds) {
			List<AccountSNS> accountSNSList = Platform.getAppContext().get(AccountService.class).getAccountBySNS(snsType, id);
			if (accountSNSList != null && accountSNSList.size() > 0) {
				for (AccountSNS accountSns : accountSNSList) {
					// TODO 提高效率, 批量查询数据库
					Actor actor = Platform.getEntityManager().fetch("from Actor where accountId = ? and exist = 0", accountSns.getId());
					if (actor != null && !friends.contains(actor)) {
						friends.add(actor);
					}
				}
			}
		}
		log.info("[METHODEND] return[friends({})]", friends.size());
		return friends;
	}

	/**
	 * 根据sns好友列表 查找游戏内好友
	 * 
	 * @param snsType
	 * @param friendIds
	 * @return
	 */
	public List<Actor> getSnsFriendsFast(String snsType, List<String> friendIds) {
		log.info("[getSnsFriendsFast] snsType:{}, friendIds:{}", snsType, friendIds);
		List<Actor> friends = new ArrayList<Actor>();
		String snsTypeName = AccountSNS.getSNSTypeNameByType(snsType);
		if (snsTypeName == null || snsTypeName.equals("") || friendIds == null || friendIds.size() == 0) {
			return friends;
		}
		String inStr = Arrays.toString(friendIds.toArray());
		inStr = inStr.substring(1, inStr.length() - 1);
		if(inStr.equals("")) {
			return friends;
		}
		String hql = new StringBuilder("select id from AccountSNS  where ").append(snsTypeName).append(" in (").append(inStr).append(")").toString();
		List<Integer> accountIds = Platform.getEntityManager().limitQuery(hql, 0, 500);
		if(accountIds == null || accountIds.size() == 0) {
			return friends;
		}
		String inStr3 = Arrays.toString(accountIds.toArray());
		inStr3 = inStr3.substring(1, inStr3.length() - 1);
		log.info("[getAccountBySNS] return[friendsSize:{}]", friends == null ? "0" : friends.size());
		String hql2 = new StringBuilder("from Actor where exist = 0 and accountId in (").append(inStr3).append(" )").toString();
		friends = Platform.getEntityManager().limitQuery(hql2, 0, 500);
		
		return friends;
	}
	
	/**
	 * 根据sns好友列表 查找游戏内好友
	 * 
	 * @param snsType
	 * @param friendIds
	 * @return
	 * @deprecated 由于当前数据库不支持in内的limit的导致查询效率反降. 废弃.请使用getSnsFriendsFast
	 */
	public List<Actor> getSnsFriendsFast1(String snsType, List<String> friendIds) {
		List<Actor> friends = new ArrayList<Actor>();
		String snsTypeName = AccountSNS.getSNSTypeNameByType(snsType);
		if (snsTypeName == null || snsTypeName.equals("") || friendIds == null || friendIds.size() == 0) {
			return friends;
		}
		String inStr = Arrays.toString(friendIds.toArray());
		inStr = inStr.substring(1, inStr.length() - 1);
		String hql = new StringBuilder("from Actor where exist = 0 and accountId in (select id from AccountSNS where ").append(snsTypeName).append(" in (").append(inStr).append(") limit 500)").toString();

		friends = Platform.getEntityManager().limitQuery(hql, 0, 100);
		System.out.println("[METHODEND] return[friends+,"+ friends.size());
		log.info("[METHODEND] return[friends({})]", friends.size());
		return friends;
	}
	
	public static void main(String[] args) {
//		ArrayList<String> friendIds = new ArrayList<String>();
//		friendIds.add("1");
//		friendIds.add("2");
//		String snsTypeName = "weiboId";
//		
//		if (snsTypeName == null || snsTypeName.equals("") || friendIds == null || friendIds.size() == 0) {
//			System.out.println("return ;");
//			return;
//		}
//		
//		String inStr = Arrays.toString(friendIds.toArray());
//		inStr = inStr.substring(1, inStr.length() - 1);
//		
//		String hql = new StringBuilder("select id from AccountSNS  where ").append(snsTypeName).append(" in (").append(inStr).append(")").toString();
//		
//		System.out.println( friendIds);
//		
		
		String mid = "7C:11:BE:E6:2D:A6";
		String mid2;
		
		if(mid.length() < 3) {
			mid2 = mid;
		}
		if(mid.charAt(2) == ':') {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < mid.length(); i++) {
				char ch = mid.charAt(i);
				if(ch != ':') {
					sb.append(ch);
				}
			}
			mid2 = sb.toString();
		}else {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < mid.length(); i++) {
				char ch = mid.charAt(i);
				if(i != 0 && i != mid.length()-1 && i %2 == 0) {
					sb.append(':');
				}
				sb.append(ch);
			}
			mid2 = sb.toString();
		}
		
		System.out.println( mid);
		System.out.println( mid2);
	}
}
