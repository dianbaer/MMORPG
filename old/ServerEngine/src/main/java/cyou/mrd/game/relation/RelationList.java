package cyou.mrd.game.relation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.game.actor.Actor;
import cyou.mrd.game.actor.ActorCacheService;
import cyou.mrd.io.http.HttpClientTransfer;
import cyou.mrd.persist.UseTypeAdapter;

/**
 * 好友列表
 */
public class RelationList implements HttpClientTransfer, UseTypeAdapter, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(RelationList.class);
	// 玩家列表
	public List<Actor> actors = new ArrayList<Actor>();
	
	/**
	 * 从存储玩家列表的字符串中恢复。 玩家ID,好友度
	 * 
	 * @param data
	 */
	public void parse(byte[] bytes) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bais);

		actors.clear();
		int total = dis.readInt();
		ActorCacheService actorCacheService = Platform.getAppContext().get(ActorCacheService.class);
		
		for (int i = 0; i < total; i++) {
			int playerID = dis.readInt();
			String name = dis.readUTF();
			String icon = dis.readUTF();
			int level = dis.readInt();
			int star = dis.readInt();
			int raceId = dis.readInt();
			int rich = dis.readInt();
			Actor actor = actorCacheService.findActorByCache(playerID);
			if (actor == null) { 
				actor = new Actor(playerID, name, icon, level, star, raceId, rich);
				actorCacheService.addActor(actor);
			}
			actors.add(actor);
		}
	}

	/**
	 * 转换为存储格式。
	 */
	public byte[] toDbData() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			int count = actors.size();
			dos.writeInt(count);
			ActorCacheService actorCacheService = Platform.getAppContext().get(ActorCacheService.class);
			for (int i = 0; i < count; i++) {
				Actor actor = actors.get(i);
				dos.writeInt(actor.getId());
				Actor cachedActor = actorCacheService.findActorByCache(actor.getId());
				if(cachedActor != null) {
					actor = cachedActor;
				}
				dos.writeUTF(actor.getName() == null ? "" : actor.getName());
				dos.writeUTF(actor.getIcon() == null ? "" : actor.getIcon());
				dos.writeInt(actor.getLevel());
				dos.writeInt(actor.getStar());
				dos.writeInt(actor.getRaceId());
				dos.writeInt(actor.getRich());
			}
		} catch (IOException e) {
			log.error("IOException", e);
		}
		return baos.toByteArray();
	}

	/**
	 * 复制此玩家列表。
	 * 
	 * @return
	 */
	@Override
	public RelationList clone() {
		RelationList ret = new RelationList();
		ret.actors.addAll(actors);
		return ret;
	}

	/**
	 * 判断两个列表是否完全相同。
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof RelationList)) {
			return false;
		}
		RelationList oo = (RelationList) o;
		int size = actors.size();
		if (oo.actors.size() == size) {
			for (int i = 0; i < size; i++) {
				Actor actor1 = oo.actors.get(i);
				Actor actor2 = actors.get(i);
				if (actor1.getId() != actor2.getId()) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 向列表中添加一个新玩家。新添加的玩家会出现在列表头，如果此玩家已经在列表中存在了， 则此玩家被移动到第一个。
	 * 返回是否新增了好友
	 * @param id
	 * @param name
	 */
	public boolean addPlayer(Actor actor) {
		log.info("[RelationList] addPlayer(Actor [{}])", actor);
		int index = findPlayer(actor.getId());
		if (index == -1) {
			actors.add(0, actor);
			return true;
		} else {
			actor = actors.remove(index);
			actors.add(0, actor);
			return false;
		}
	}

	/**
	 * 裁剪玩家列表，以确保其数量不超过指定数目。
	 * 
	 * @param maxCount
	 *            最大玩家数量
	 */
	public void truncate(int maxCount) {
		log.info("[RelationList] truncate(maxCount [{}])", maxCount);
		int size = actors.size();
		if (size > maxCount) {
			while (actors.size() > maxCount) {
				actors.remove(maxCount);
			}
		}
	}

	/**
	 * 从列表中删除一个玩家。
	 * 
	 * @param id
	 */
	public void removePlayer(int id) {
		log.info("[RelationList] removePlayer(playerId:{})", id);
		int index = findPlayer(id);
		if (index != -1) {
			actors.remove(index);
		}
	}

	/*
	 * 在列表中查找某个玩家。
	 * 
	 * @param id 玩家ID
	 * 
	 * @return 玩家在列表中的索引，如果找不到返回-1.
	 */
	public int findPlayer(int id) {
//		log.info("[RelationList] findPlayer(playerId:{})", id);
		int size = actors.size();
		for (int i = 0; i < size; i++) {
			if (id == actors.get(i).getId()) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public String toClientData() {
		JSONArray temp = new JSONArray();
		JSONObject act;
		for(Actor actor : actors) {
			act = new JSONObject();
			act.put("id", actor.getId());
			act.put("name", actor.getName());
			act.put("icon", actor.getIcon());
			act.put("level", actor.getLevel());
			act.put("star", actor.getStar());
			act.put("raceId", actor.getRaceId());
			act.put("rich", actor.getRich());
			temp.add(act);
		}

		log.info("[RelationList] toClientData(return [{}])", temp.toString());
		return temp.toString();
	}
}
