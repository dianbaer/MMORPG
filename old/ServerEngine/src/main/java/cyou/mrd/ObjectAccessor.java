package cyou.mrd;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.entity.GameObject;
import cyou.mrd.entity.Player;

/**
 * 池.
 * 所有的游戏对象中有instanceId的都应该放到这里来. 
 * 
 * @author miaoshengli
 */
public class ObjectAccessor {
	private static final Logger log = LoggerFactory.getLogger(ObjectAccessor.class);

	//protected static ConcurrentHashMap<Integer, GameObject> objects = new ConcurrentHashMap<Integer, GameObject>(500);
	
	public static final ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<Integer, Player>(500);
	
	//map.size的性能无法容忍, 我们中间做一个size. 不保证精确
	private static int size = 0;
	/**
	 * 添加在线用户
	 * @param object
	 */
	public static void addGameObject(GameObject object) {
		//objects.put(object.getInstanceId(), object);
		if (object instanceof Player) {
			Player old = players.put(object.getInstanceId(), (Player) object);
			log.info("[ADDPLAYER][OBJECTACCESSOR] size:{} playerId:{}", size, object.getInstanceId());
			if (old == null) {
				size++;
			}
		}
	}
	/**
	 * 根据id获取在线用户
	 * @param id
	 * @return
	 */
	public static Player getPlayer(int id) {
		return players.get(id);
	}
	/**
	 * 删除在线用户
	 * @param object
	 */
	public static void removeGameObject(GameObject object) {
		if (object instanceof Player) {
			Player p = players.remove(object.getInstanceId());
			log.info("[REMOVEPLAYER][OBJECTACCESSOR] size:{} playerId:{}", size, object.getInstanceId());
			if(p != null) {
				size--;
			}
		}
		//objects.remove(object.getInstanceId());
		
	}
	/**
	 * 获取在线用户个数（不准确）
	 * @return
	 */
	public static int size() {
		if(size < 0) {
			size = players.size();
		}
		return size;
	}
}
