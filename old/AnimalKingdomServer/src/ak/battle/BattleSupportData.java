package ak.battle;
import java.io.Serializable;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import ak.battle.BattleSupport;

// 存储各种战斗支援相关的数据
public class BattleSupportData implements Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TIntObjectMap<BattleSupport> battleSupportMap;
	
	private int battleBuildingLevel = 0;//战斗建筑等级，大于0时，表明有支援
	private int battleSupportFriendID = 0; // 战斗支援好友ID，大于0时，表明有支援
	
	public TIntObjectMap<BattleSupport> getBattleSupportMap()
	{
		if ( battleSupportMap == null )
		{
			battleSupportMap = new TIntObjectHashMap<BattleSupport>();
		}
		return battleSupportMap;
	}
	
	public void setBattleBuildingLevel(int level){
		this.battleBuildingLevel = level;
	}
	public int getBattleBuildingLevel(){
		return this.battleBuildingLevel;
	}
	
	public void setBattleSupportFriendID(int id){
		this.battleSupportFriendID = id;
	}
	public int getBattleSupportFriendID(){
		return this.battleSupportFriendID;
	}
}
