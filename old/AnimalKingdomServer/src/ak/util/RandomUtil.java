package ak.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ak.friend.HelpFriendTemplate;
import ak.friend.RandomNpcTemplate;
import ak.friend.SendGiftTemplate;
import ak.player.PlayerEx;
import cyou.mrd.Platform;
import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataService;

/**
 * 只要是等级区间，有百分比的概率问题都可以用到这个类，去计算随机值
 * @author xuepeng
 *
 */
public class RandomUtil {
	/**
	 * 缓存
	 */
	private static Map<String, ConcurrentHashMap<Integer, ArrayList<IRandom>>> randomMapMap = new ConcurrentHashMap<String, ConcurrentHashMap<Integer,ArrayList<IRandom>>>();
	/**
	 * 田地
	 */
	public static final String FIELD = "field";
	/**
	 * 房屋
	 */
	public static final String HOUSE = "house";
	/**
	 * npc
	 */
	public static final String NPC = "npc";
	/**
	 * 礼物
	 */
	public static final String GIFT = "gift";
	/**
	 * 根据列表里面的概率 返回一个值
	 * @param list
	 * @return
	 */
	public static IRandom getRandom(List<IRandom> list){
		int num = (int) (Math.random()*100);
		int add = 0;
		int size = list.size();
		for(int i = 0 ; i < size; i++){
			if(add <= num && num < list.get(i).getProbability()+add ){
				return list.get(i) ;
			}
			add += list.get(i).getProbability();
		}
		return null;
	}
	/**
	 * 根据大类型与小类型 获得随机的参数
	 * @param player player对象
	 * @param type 大类型
	 * @param tinyType 小类型
	 * @return
	 */
	public static IRandom getRandomGift(PlayerEx player,String type,int tinyType,boolean firstVisit){
		ArrayList<IRandom> randomList = null;
		ConcurrentHashMap<Integer, ArrayList<IRandom>> randomMap = randomMapMap.get(type);
		if(randomMap == null){
			randomMap = new ConcurrentHashMap<Integer, ArrayList<IRandom>>();
			randomMapMap.put(type, randomMap);
		}
		//从缓存里找
		if(randomMap.size() > 0){
			Object[] s = randomMap.keySet().toArray();
			for(int i = 0;i < s.length; i++){
				int value = Integer.parseInt(s[i].toString());
				if(player.getLevel() >= value-5 && player.getLevel() < value+5){
					randomList = randomMap.get(s[i]);
					break;
				}
			}
		}
		//找不到遍历表里面的所有数据 放入缓存
		if(randomList == null){
			Hashtable<Integer, Template> templates = null;
			if(type == FIELD){
				templates = Platform.getAppContext().get(TextDataService.class).getTemplates(HelpFriendTemplate.class);
			}else if(type == HOUSE){
				templates = Platform.getAppContext().get(TextDataService.class).getTemplates(HelpFriendTemplate.class);
			}else if(type == NPC){
				templates = Platform.getAppContext().get(TextDataService.class).getTemplates(RandomNpcTemplate.class);
			}else{
				templates = Platform.getAppContext().get(TextDataService.class).getTemplates(SendGiftTemplate.class);
			}
			Iterator<Template> it = templates.values().iterator();
			IRandom t = null;
			randomList = new ArrayList<IRandom>();
			while (it.hasNext()) {
				t = (IRandom) it.next();
				if(tinyType != 0){
					if(player.getLevel() >= t.getOpenLvl()-5 && player.getLevel() < t.getOpenLvl()+5 && t.getType() == tinyType){
						randomList.add(t);
					}
				}else{
					if(player.getLevel() >= t.getOpenLvl()-5 && player.getLevel() < t.getOpenLvl()+5){
						randomList.add(t);
					}
				}
			}
			if(randomList.size() > 0){
				randomMap.put(randomList.get(0).getOpenLvl(), randomList);
			}
			
		}
		IRandom random = null;
		if(firstVisit){
			if(randomList.size()>0){
				random = randomList.get(0);
			}
		}else{
			random = RandomUtil.getRandom(randomList);
		}
		
		return random;
	}
}
