package cyou.mrd.game.relation;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.data.Data;
import cyou.mrd.data.DataKeys;
import cyou.mrd.entity.Player;
import cyou.mrd.event.Event;
import cyou.mrd.event.GameEvent;
import cyou.mrd.event.OPEvent;
import cyou.mrd.game.actor.Actor;
import cyou.mrd.game.actor.ActorCacheService;
import cyou.mrd.io.AsyncCall;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HOpCode;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;
import cyou.mrd.service.Service;
import cyou.mrd.util.ErrorHandler;

/**
 * 好友关系类
 * 
 * @author miaoshengli
 */
@OPHandler(TYPE = OPHandler.HTTP_EVENT)
public class RelationService implements Service {
	private static final Logger log = LoggerFactory.getLogger(RelationService.class);

	@Override
	public String getId() {
		return "RelationService";
	}

	@OPEvent(eventCode = GameEvent.EVENT_RELATION_ADD)
	protected void relationAdd(final Event event) {
		Platform.getThreadPool().execute(new AsyncCall() {
			@Override
			public void run() {
				PlayerRelation pr = (PlayerRelation) event.param1;
				if (pr == null) {
					log.info("[Relation] relationAdd:error PlayerRelation is null");
					return;
				}
				if (Platform.getEntityManager().find(PlayerRelation.class, pr.getId()) != null) {
					log.info("[Relation] relationAdd:update PlayerRelation to EcCache playerId:{}", pr.getId());
					Platform.getEntityManager().updateSync(pr);
				} else {
					log.info("[Relation] relationAdd:save new PlayerRelation to DB playerId:{}", pr.getId());
					Platform.getEntityManager().createSync(pr);
				}
				log.info("[Relation] relationAdd:success playerId:{}", pr.getId());
			}

			@Override
			public void callFinish() throws Exception {
			}
		});

	}

//	protected void playerLogouted(Event event) {
//		Player player = (Player) event.param1;
//		PlayerRelation relation = getRelationByDataCenter(player.getInstanceId());
//		if (relation != null) {
//			log.info("[Relation] playerLogouted try update playerRelation to DB playerId:{}", player.getId());
//			Platform.getEntityManager().updateSync(relation);
//			log.info("[Relation] playerLogouted update playerRelation to DB success playerId:{}", player.getId());
//		}
//	}

	protected PlayerRelation getRelationByDataCenter(int relationId) {
		String key = DataKeys.relationKey(relationId);
		Data data = Platform.dataCenter().getData(key);
		if (data == null) {
			return null;
		} else {
			return (PlayerRelation) data.value;
		}
	}

	protected void putRelationToDataCenter(PlayerRelation relation) {
		String key = DataKeys.relationKey(relation.getId());
		Data data = Platform.dataCenter().getData(key);
		if (data == null) {
			Platform.dataCenter().sendNewData(key, relation);
		} else {
			data.value = relation;
			Platform.dataCenter().sendData(key, data);
		}
	}

	@Override
	public void startup() throws Exception {
		log.info("RelationService startup OK");
	}

	@Override
	public void shutdown() throws Exception {
	}

	public PlayerRelation findRelation(int playerId) {
		// log.info("[Relation] findRelation findRelation playerId:{}",
		// playerId);
		PlayerRelation playerRelation = getRelationByDataCenter(playerId);
		if (playerRelation == null) {
			log.info("[Relation] findRelation get by db playerId:{}", playerId);
			playerRelation = Platform.getEntityManager().find(PlayerRelation.class, playerId);
			if (playerRelation == null) {
				log.info("[Relation] findRelation playerRelation is null playerId:{}", playerId);
				return null;
			} else {
				putRelationToDataCenter(playerRelation);
			}
		}
		// log.info("[Relation] findRelation return[playerRelation({})]",playerRelation.getId());
		return playerRelation;
	}

	/**
	 * 获取好友关系列表
	 * 
	 * @param session
	 */
	@OP(code = HOpCode.PLAYER_FRIEND_LIST_CLIENT)
	protected void list(Packet packet, HSession session) {
		log.info("[HTTPRequest] packet[{}] session [{}]]", packet.toString(), session.getSessionId());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		PlayerRelation playerRelation = findRelation(player.getId());
		Packet pt = new JSONPacket(HOpCode.PLAYER_FRIEND_LIST_SERVER);
		if (playerRelation == null || playerRelation.getFriends() == null || playerRelation.getFriends().actors.size() == 0) {
			pt.put("friends", "");
			log.info("[list] size:0");
		} else {
			pt.put("friends", playerRelation.getFriends().toClientData());
			log.info("[list] size:{}", playerRelation.getFriends().actors.size());
		}

		session.send(pt);
	}

	/**
	 * 新增好友
	 * 
	 * @param session
	 */
	@OP(code = HOpCode.PLAYER_FRIEND_ADD_CLIENT)
	protected void add(Packet packet, HSession session) {
		log.info("[HTTPRequest] packet[{}] session [{}]]", packet.toString(), session.getSessionId());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		int friendId = packet.getInt("id");
		if (player.getInstanceId() == friendId) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_43, packet.getopcode());
			return;
		}
		ActorCacheService actorCacheService = Platform.getAppContext().get(ActorCacheService.class);
		Actor actor = actorCacheService.findActor(friendId);
		if (actor == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_5, packet.getopcode());
			return;
		}
		PlayerRelation playerRelation = findRelation(player.getId());
		if (playerRelation == null) {
			playerRelation = new PlayerRelation();
			playerRelation.setFriends(new RelationList());
			playerRelation.setId(player.getId());
			putRelationToDataCenter(playerRelation);
			log.info("[Relation] relationAdd:save new PlayerRelation to DB playerId:{}", playerRelation.getId());
			Platform.getEntityManager().createSync(playerRelation);
			log.info("[Relation] relationAdd:success playerId:{}", playerRelation.getId());
		}
		if (playerRelation.getFriends().findPlayer(actor.getId()) != -1) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_6, packet.getopcode());
			return;
		} else {
			playerRelation.getFriends().addPlayer(actor);
			putRelationToDataCenter(playerRelation);
			Platform.getEntityManager().updateSync(playerRelation);
		}
		Packet pt = new JSONPacket(HOpCode.PLAYER_FRIEND_ADD_SERVER);
		pt.put("result", 1);
		session.send(pt);
		log.info("[METHODEND] return[null]");
	}

	/**
	 * 删除好友
	 * 
	 * @param session
	 */
	@OP(code = HOpCode.PLAYER_FRIEND_DEL_CLIENT)
	protected void delete(Packet packet, HSession session) {
		log.info("[HTTPRequest] packet[{}] session [{}]]", packet.toString(), session.getSessionId());
		Player player = (Player) session.client();
		if (player == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_1, packet.getopcode());
			return;
		}
		int friendId = packet.getInt("id");
		if (player.getInstanceId() == friendId) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_4, packet.getopcode());
			return;
		}
		ActorCacheService actorCacheService = Platform.getAppContext().get(ActorCacheService.class);
		Actor actor = actorCacheService.findActor(friendId);
		if (actor == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_12, packet.getopcode());
			return;
		}
		PlayerRelation playerRelation = findRelation(player.getId());
		if (playerRelation == null) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_7, packet.getopcode());
			return;
		}
		if (playerRelation.getFriends().findPlayer(actor.getId()) == -1) {
			ErrorHandler.sendErrorMessage(session, ErrorHandler.ERROR_CODE_7, packet.getopcode());
			return;
		} else {
			playerRelation.getFriends().removePlayer(actor.getId());
			putRelationToDataCenter(playerRelation);
			Platform.getEntityManager().updateSync(playerRelation);
			// Platform.getEventManager().addEvent(new
			// Event(GameEvent.EVENT_RELATION_CHANGE, playerRelation));
			// 将自己从删除的好友的好友列表中删除
			Actor myActor = Platform.getAppContext().get(ActorCacheService.class).findActor(player.getId());
			if (myActor != null) {
				PlayerRelation friendRelation = findRelation(friendId);
				if (friendRelation != null && friendRelation.getFriends().findPlayer(myActor.getId()) != -1) {
					friendRelation.getFriends().removePlayer(myActor.getId());
					Platform.getEntityManager().updateSync(friendRelation);
					this.putRelationToDataCenter(friendRelation);
				}
				// Platform.getEventManager().addEvent(new
				// Event(GameEvent.EVENT_RELATION_CHANGE, friendRelation));
			}
		}
		Packet pt = new JSONPacket(HOpCode.PLAYER_FRIEND_DEL_SERVER);
		pt.put("result", 1);
		pt.put("id", actor.getId());
		session.send(pt);
		log.info("[METHODEND] return[null]");
	}

	public JSONArray addSNSFriend(Player player, List<Actor> friends) throws Exception {
		log.info("[RelationService] addFriend(Player [id={}], List<Actor>[size={})]", player.getInstanceId(), friends.size());
		JSONArray ja = new JSONArray();
		PlayerRelation playerRelation = findRelation(player.getId());
		if (playerRelation == null) {
			playerRelation = new PlayerRelation();
			playerRelation.setFriends(new RelationList());
			playerRelation.setId(player.getId());
			log.info("[Relation] relationAdd:save new PlayerRelation to DB playerId:{}", playerRelation.getId());
			Platform.getEntityManager().createSync(playerRelation);
			log.info("[Relation] relationAdd:success playerId:{}", playerRelation.getId());
			this.putRelationToDataCenter(playerRelation);
		}
		boolean isChange = false;
		ActorCacheService acService = Platform.getAppContext().get(ActorCacheService.class);
		Actor playerActor = acService.findActor(player.getId());
		for (Actor actor : friends) {
			log.info("[RelationService] addFriend loop: find actor: {}", actor.getId());
			if (playerRelation.getFriends().findPlayer(actor.getId()) != -1) {
				log.info("[RelationService] addFriend loop: continue actor: {}", actor.getId());
				continue;
			} else {
				log.info("[RelationService] addFriend loop: addPlayer actor: {}", actor.getId());

				// 将sns好友加到自己的好友中
				boolean hasAdded = playerRelation.getFriends().addPlayer(actor);
				this.putRelationToDataCenter(playerRelation);
				Platform.getEntityManager().updateSync(playerRelation);
				if (hasAdded) {
					JSONObject jo = new JSONObject();
					jo.put("id", actor.getId());
					jo.put("name", actor.getName());
					jo.put("icon", actor.getIcon());
					jo.put("level", actor.getLevel());
					jo.put("star", actor.getStar());
					jo.put("raceId", actor.getRaceId());
					jo.put("rich", actor.getRich());
					//是否在线
					Actor newActor = acService.findActorByCache(actor.getId());
					if(newActor != null){
						jo.put("online", newActor.isOnline() ? 1 : 0);
					}
					else{
						jo.put("online", actor.isOnline() ? 1 : 0);
					}
					ja.add(jo);
				}
				// 将自己加到sns好友的游戏内好友中
				PlayerRelation friendRelation = findRelation(actor.getId());
				log.info("[RelationService] addFriend loop: friendRelation add player: {}", friendRelation == null ? "[null]"
						: "[friendRelation]");
				if (friendRelation == null) {
					friendRelation = new PlayerRelation();
					friendRelation.setFriends(new RelationList());
					friendRelation.setId(actor.getId());
					this.putRelationToDataCenter(friendRelation);
					log.info("[Relation] relationAdd:save new PlayerRelation to DB playerId:{}", friendRelation.getId());
					Platform.getEntityManager().createSync(friendRelation);
					log.info("[Relation] relationAdd:success playerId:{}", friendRelation.getId());
				}
				friendRelation.getFriends().addPlayer(playerActor);
				this.putRelationToDataCenter(friendRelation);
				Platform.getEntityManager().updateSync(friendRelation);
				isChange = true;
			}
		}
		if (isChange) {
			// Platform.getEventManager().addEvent(new
			// Event(GameEvent.EVENT_RELATION_CHANGE, playerRelation));
			return ja;
		}
		log.info("[RelationService] addFriend return [null]");
		return null;
	}

	// public static void main(String[] args) {
	// try{
	// Object a = null;
	// a.wait();
	// }catch(Exception e) {
	// log.info(e.getMessage());
	// }
	// // log.info("OK");
	// }

}
