package ak.friend;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ak.player.PlayerEx;
import cyou.mrd.Platform;
import cyou.mrd.game.actor.Actor;
import cyou.mrd.game.actor.ActorCacheService;

/**
 * 根据等级生成. 生成初始Actor到actorCache中<br>
 * 生成随机索引N组<br>
 * 获取随机数, 指向某组索引. <br>
 * 定时重构(外部调用重构方法)
 * 
 * @author miaoshengli
 */
public class RandomFriendPool {
	private static final Logger log = LoggerFactory.getLogger(RandomFriendPool.class);

	private static final int RANDOM_FRIEND_POOL_SPECIAL_MIN_SIZE = 100;

	/**
	 * 一个随机索引的大小, 目前是20.
	 */
	private int randomIndexSize = 20;
	/**
	 * 不同语言的用户会推荐到不同的人群下
	 */
	private int lang;

	Random random = new Random();

	private List<Actor> specialFriendList;

	private List<Actor> randomFriendPool_level_40;

	private List<Actor> randomFriendPool_level_35;

	private List<Actor> randomFriendPool_level_30;

	private List<Actor> randomFriendPool_level_25;

	private List<Actor> randomFriendPool_level_20;

	private List<Actor> randomFriendPool_level_15;

	private List<Actor> randomFriendPool_level_10;

	private List<Actor> randomFriendPool_level_5;

	private List<Actor> randomFriendPool_level_0;

	public RandomFriendPool(int lang) {
		this.lang = lang;
	}

	public void initRandomFriendPool() {
		int start = 0;
		int count = RANDOM_FRIEND_POOL_SPECIAL_MIN_SIZE;
		specialFriendList = Platform.getEntityManager().limitQuery(
//				"from Actor where exist = 0 order by level desc , money desc", start, count);
		"from Actor where exist = 0 and lang = ? order by level desc , money desc", start, count, lang);
		
		String randomFriendPoolLevelsql = "from Actor where level > ? and level < ?  and exist = 0 and lang = ? order by id desc";
		randomFriendPool_level_0 = Platform.getEntityManager().limitQuery(
				randomFriendPoolLevelsql, start, count, 0, 6, lang);
		randomFriendPool_level_5 = Platform.getEntityManager().limitQuery(
				randomFriendPoolLevelsql, start, count, 4, 11, lang);
		randomFriendPool_level_10 = Platform.getEntityManager().limitQuery(
				randomFriendPoolLevelsql, start, count, 9, 16, lang);
		randomFriendPool_level_15 = Platform.getEntityManager().limitQuery(
				randomFriendPoolLevelsql, start, count, 14, 21, lang);
		randomFriendPool_level_20 = Platform.getEntityManager().limitQuery(
				randomFriendPoolLevelsql, start, count, 19, 26, lang);
		randomFriendPool_level_25 = Platform.getEntityManager().limitQuery(
				randomFriendPoolLevelsql, start, count, 24, 31, lang);
		randomFriendPool_level_30 = Platform.getEntityManager().limitQuery(
				randomFriendPoolLevelsql, start, count, 29, 36, lang);
		randomFriendPool_level_35 = Platform.getEntityManager().limitQuery(
				randomFriendPoolLevelsql, start, count, 34, 41, lang);
		randomFriendPool_level_40 = Platform.getEntityManager().limitQuery(
				randomFriendPoolLevelsql, start, count, 39, PlayerEx.MaxLevel, lang);

		ActorCacheService actorCacheService = Platform.getAppContext().get(ActorCacheService.class);
		actorCacheService.updateActors(specialFriendList);
		actorCacheService.updateActors(randomFriendPool_level_0);
		actorCacheService.updateActors(randomFriendPool_level_5);
		actorCacheService.updateActors(randomFriendPool_level_10);
		actorCacheService.updateActors(randomFriendPool_level_15);
		actorCacheService.updateActors(randomFriendPool_level_20);
		actorCacheService.updateActors(randomFriendPool_level_25);
		actorCacheService.updateActors(randomFriendPool_level_30);
		actorCacheService.updateActors(randomFriendPool_level_35);
		actorCacheService.updateActors(randomFriendPool_level_40);

	}

	/**
	 * 从改ActorPool中随机抽取部分, 并且加入部分特殊好友组成
	 * 
	 * @param randomFriendPool
	 * @return
	 */
	private Actor[] getRandomFriend(List<Actor> randomFriendPool) {
		Actor[] ret = new Actor[randomIndexSize];
		int poolLen = randomFriendPool.size();
		int specLen = specialFriendList.size();
		if (specLen == 0) {
			initRandomFriendPool();
			specLen = specialFriendList.size();
			if (specLen == 0) {
				return new Actor[0];
			}
		}
		int probability = poolLen / ret.length;

		int specSpeed = randomIndexSize / 5;// 20里选5个
		int index = 0;
		int n = 0;
		int k = 0;// 重名剔除尝试次数
		randomRetryTag: while (n < ret.length) {
			int specIndex = random.nextInt(specLen);
			if (probability == 0 || specIndex % specSpeed == 1) {
				specIndex = index + random.nextInt(10);
				Actor a = specialFriendList.get(specIndex >= specLen ? specIndex % specLen : specIndex);
				log.info("[RandomFriend]special pool :  try Actor({}): {}", a, specIndex);
				for (int m = n - 1; m >= 0; m--) {
					if (ret[m].getIcon()!= null && ret[m].getName() != null && ret[m].getIcon().equals(a.getIcon()) && ret[m].getName().equals(a.getName()) && k < 5) {
						k++;
						log.info("[RandomFriend]special pool :  Actor cann't be use({}) {}", k, a);
						continue randomRetryTag;
					}else if(k == 5){
						break;
					}
				}
				k = 0;
				ret[n] = a;
				// log.info("get a special:{} friend", ret[n].getLevel());
				index = specIndex;
			} else {
				random.nextInt(probability);
				int r = random.nextInt(probability) + index;
				Actor a = randomFriendPool.get(r >= randomFriendPool.size() ? r % randomFriendPool.size() : r);
				log.info("[RandomFriend]randomFriendPool pool :   try Actor({}): {}", a, r);
				for (int m = n - 1; m >= 0; m--) {
					if (ret[m].getIcon()!= null && ret[m].getName() != null && ret[m].getIcon().equals(a.getIcon()) && ret[m].getName().equals(a.getName()) && k < 5) {
						k++;
						log.info("[RandomFriend]randomFriendPool pool :  Actor cann't be use({}) {}", k, a);
						continue randomRetryTag;
					}else if(k == 5){
						break;
					}
				}
				k = 0;
				ret[n] = a;
				// log.info("get a level:{} friend", ret[n].getLevel());
				index += probability;
			}

			n++;
		}
		int specialNo1 = random.nextInt(5);
		int specialNo1Index = random.nextInt(5);
		if (specialNo1 < ret.length && specialNo1Index < specialFriendList.size()) {
			ret[specialNo1] = specialFriendList.get(specialNo1Index);
		}
		return ret;
	}

	/**
	 * 获取一组随机好友, 数组长度为{@RandomFriendPool.randomIndexSize
	 * 
	 * 
	 * 
	 * }
	 * 
	 * @return
	 */
	public Actor[] getRandomFriend(int level) {
		if (level > 38) {
			return getRandomFriend(randomFriendPool_level_40);
		}
		if (level > 33) {
			return getRandomFriend(randomFriendPool_level_35);
		}
		if (level > 28) {
			return getRandomFriend(randomFriendPool_level_30);
		}
		if (level > 23) {
			return getRandomFriend(randomFriendPool_level_25);
		}
		if (level > 18) {
			return getRandomFriend(randomFriendPool_level_20);
		}
		if (level > 13) {
			return getRandomFriend(randomFriendPool_level_15);
		}
		if (level > 8) {
			return getRandomFriend(randomFriendPool_level_10);
		}
		if (level > 3) {
			return getRandomFriend(randomFriendPool_level_5);
		}

		return getRandomFriend(randomFriendPool_level_0);
	}

	 
}
