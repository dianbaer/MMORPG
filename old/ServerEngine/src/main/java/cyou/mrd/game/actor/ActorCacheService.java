package cyou.mrd.game.actor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.entity.Player;
import cyou.mrd.event.Event;
import cyou.mrd.event.GameEvent;
import cyou.mrd.event.OPEvent;
import cyou.mrd.io.OPHandler;
import cyou.mrd.service.Service;
import cyou.mrd.util.RunTimeMonitor;

/**
 * 存储玩家的部分信息，如果有系统需要玩家的部分信息，但是又不需要全部载入而且忽视当前玩家的在线状态，那么一般使用此类得到Actor，比如帮派，好友模块等等
 * 在做实现的是有一般会监听PLAYER_LOGIN
 * ,PLAYER_CREATED等事件，在玩家载入的同时查看是否actor被载入，如果没有载入，那么进行载入操作，
 * 另外如果Player的属性信息改变并且关联到Actor中的属性，那么需要通知此Service进行相应的Actor属性改动操作
 * 
 * 
 * @author miaoshengli
 */
@OPHandler(TYPE = OPHandler.EVENT)
public class ActorCacheService implements Service {

	// 缓存大小
	private static final int ACTOR_CACHE_MAX_SIZE = Platform.getConfiguration().getInt("actor_cache_max_size");

	public static final Logger log = LoggerFactory.getLogger(ActorCacheService.class);

	protected Map<Integer, Actor> actors = new ConcurrentHashMap<Integer, Actor>();

	protected Map<String, Actor> actorNameMap = new ConcurrentHashMap<String, Actor>();

	protected ActorDAO dao = new ActorDAO();

	public void startup() throws Exception {
		Platform.getScheduler().scheduleAtFixedRate(new ActorsSimpleGC(), 100, 1, TimeUnit.HOURS);
	}

	public void shutdown() {
	}

	/*
	 * 玩家登录时，更新Actor对象中的数据，或创建一个新的Actor对象。
	 * 
	 * @param player
	 */
	@OPEvent(eventCode = GameEvent.EVENT_PLAYER_LOGINED)
	protected void playerLogined(Event event) {
		Player player = (Player) event.param1;
		Actor actor = new Actor(player);
		actor.setOnline(true);
		actors.put(player.getInstanceId(), actor);
	}

	@OPEvent(eventCode = GameEvent.EVENT_PLAYER_CHANGEED)
	protected void actorChange(Event event) {
		Player player = (Player) event.param1;
		updateActor(player);
	}

	@OPEvent(eventCode = GameEvent.EVENT_PLAYER_CHANGEED_FORCE)
	protected void actorChangeForce(Event event) {
		Player player = (Player) event.param1;
		updateActor(player);
	}

	private void updateActor(Player player) {
		Actor actor = actors.get(player.getInstanceId());
		if (actor != null) {
			actor.setIcon(player.getIcon());
			actor.setLevel(player.getLevel());
			actor.setName(player.getName());
			actor.setStar(player.getStar());
			actor.setRaceId(player.getRaceId());
			actor.setRich(player.getRich());
		}
	}

	/**
	 * 如果缓存中的等级比较大, 说明缓存的actor比较新. 前提是玩家等级必须是线性上升的.
	 * @param newActor
	 */
	public void updateActor(Actor newActor) {
		Actor old = actors.get(newActor.getId());
		if(old!= null && old.getLevel() > newActor.getLevel()) {
			newActor = old;
		}else {
			actors.put(newActor.getId(), newActor);
		}
	}

	public void updateActors(List<Actor> actorList) {
		if (actorList != null && actorList.size() > 0) {
			for (Actor a : actorList) {
				updateActor(a);
			}
		}
	}

	/*
	 * 玩家下线时，更新玩家在线状态。
	 * 
	 * @param player
	 */
	@OPEvent(eventCode = GameEvent.EVENT_PLAYER_LOGOUTED)
	protected void playerLogouted(Event event) {
//		RunTimeMonitor rt = new RunTimeMonitor();
//		rt.knock("playerLogouted");
		Player player = (Player) event.param1;
//		rt.knock("event.param1");
//		log.info("[ActorCacheService] playerLogouted playerId:{}, Actor(online = false)", player.getInstanceId());
//		rt.knock("log.info");
		Actor actor = actors.get(player.getInstanceId());
//		rt.knock("actors.get");
		if (actor != null) {
			actor.setOnline(false);
//			rt.knock("actors.setOnline");
		}
//		rt.knock("playerLogouted.ok");
//		log.info(rt.toString(-5));
	}

	/**
	 * 根据ID查找一个玩家的信息。如果此玩家没有被载入过，则尝试从数据库载入。
	 * 
	 * @param id
	 * @return
	 */
	public Actor findActor(int id) {
		// 先在缓存中查找
		Actor actor = actors.get(id);
		if (actor != null) {
			return actor;
		}

		// 从数据库载入
		// Actor
		actor = dao.getActor(id);
		if (actor == null) {
			log.info("[Actor] not find actor({})", id);
			return null;
		}
		actors.put(actor.getId(), actor);
		if (actor.getName() != null) {// 测试时会有大量的没有设置昵称的玩家
			actorNameMap.put(actor.getName(), actor);
		}
		log.info("[METHODEND] return[actor({})]", actor == null ? "null" : actor.getId());
		return actor;
	}

	/**
	 * 只在缓存中查找
	 * 
	 * @param id
	 * @return
	 */
	public Actor findActorByCache(int id) {
		return actors.get(id);
	}

	/**
	 * 根据名字查找一个玩家的信息。如果此玩家没有被载入过，则尝试从数据库载入。
	 * 
	 * @param id
	 * @return
	 */
	public List<Actor> findActorsByName(String name) {
		// 从数据库载入
		List<Actor> searchedActors = null;
		long t, t1, t2;
		t1 = System.nanoTime();
		searchedActors = Platform.getEntityManager()
				.limitQuery("from Actor where name like ? and exist=0", 0, 100, name + "%");
		t2 = System.nanoTime();
		t = (t2 - t1) / 1000000000L;
		if (t > 10) {
			log.info("[findActorsByName]  name like({}%, {}s)", name, t);
		}
		log.info("[METHODEND] return[actorsSize({})]", searchedActors == null ? "0" : searchedActors.size());
		// 缓存中覆盖
//		for (int i = 0; i < searchedActors.size(); i++) {
//			Actor actor = searchedActors.get(i);
//			Actor cacheActor = actors.get(actor.getId());
//			if (cacheActor != null) {
//				searchedActors.set(i, cacheActor);
//			}
//		}
		this.updateActors(searchedActors);
		return searchedActors;
	}

	public void addActor(Actor actor) {
		actors.put(actor.getId(), actor);
		actorNameMap.put(actor.getName(), actor);
		log.info("[Actor] addActor({})", actor);
	}

	@Override
	public String getId() {
		return "ActorCacheService";
	}

	// public Actor addActor(int id, String name) {
	// Actor actor = actors.get(id);
	// if (actor == null) {
	// actor = new Actor();
	// actor.setId(id);
	// actor.setName(name);
	// actors.put(id, actor);
	// }
	// log.info("[METHODEND] return[actor({})]",actor==null?"null":actor.getId());
	// return actor;
	// }

	class ActorsSimpleGC implements Runnable {
		@Override
		public void run() {
			try {
				if (actors.size() > ACTOR_CACHE_MAX_SIZE) {
					actors = new ConcurrentHashMap<Integer, Actor>();
					actorNameMap = new ConcurrentHashMap<String, Actor>();
					log.info("ActorsSimpleGC ok");
				}
			} catch (Throwable e) {
				log.error("ActorsSimpleGC", e);
			}
		}
	}

	public int cacheSize() {
		return actors.size();
	}

}
