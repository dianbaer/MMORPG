package ak.gameAward;

import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataInitException;
/**
 * 奖励表
 * @author xuepeng
 *
 */
public class GameAwardTemplate implements Template {
	/**
	 * 发送邮件奖励爱心值
	 */
	public static final int ID_SEND_MAIL_REWARD_LOVE = 201;
	/**
	 * 帮助贸易获得爱心值
	 */
	public static final int ID_HELP_TRADE_REWARD_LOVE = 202;
	/**
	 * 送礼
	 */
	public static final int IDS_SEND_GIFT = 8;
	/**
	 * id
	 */
	private int id;
	/**
	 * 奖励类型：1. 打扫2. 用户发邮件的爱心奖励3. 系统发送的附件奖励
	 */
	private int type;
	/**
	 * 奖励说明
	 */
	private String desc;
	/**
	 * 爱心值
	 */
	private int love;
	/**
	 * 金币
	 */
	private int goldNum;
	/**
	 * 木材
	 */
	private int woodNum;
	/**
	 * 石材
	 */
	private int stoneNum;
	/**
	 * 粮食
	 */
	private int foodNum;
	/**
	 * 道具id    eq:1,2,3
	 */
	private String itemId;
	/**
	 * 道具个数		eq:1,2,3
	 */
	private String itemNumber;
	/**
	 * 道具百分率	eq:2500,10000,8500
	 */
	private String itemProbability;
	@Override
	public Template initTemplateByTxtLine(String[] txtLineinfo)
			throws TextDataInitException {
		GameAwardTemplate gameAwardTemplate = new GameAwardTemplate();
		gameAwardTemplate.id = Integer.parseInt(txtLineinfo[0]);
		gameAwardTemplate.type = Integer.parseInt(txtLineinfo[1]);
		gameAwardTemplate.desc = txtLineinfo[2];
		gameAwardTemplate.love = Integer.parseInt(txtLineinfo[4]);
		gameAwardTemplate.goldNum = Integer.parseInt(txtLineinfo[5]);
		gameAwardTemplate.woodNum = Integer.parseInt(txtLineinfo[6]);
		gameAwardTemplate.stoneNum = Integer.parseInt(txtLineinfo[7]);
		gameAwardTemplate.foodNum = Integer.parseInt(txtLineinfo[8]);
		gameAwardTemplate.itemId = txtLineinfo[9];
		gameAwardTemplate.itemNumber = txtLineinfo[10];
		gameAwardTemplate.itemProbability = txtLineinfo[11];
		return gameAwardTemplate;
	}

	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getLove() {
		return love;
	}

	public void setLove(int love) {
		this.love = love;
	}

	public int getGoldNum() {
		return goldNum;
	}

	public void setGoldNum(int goldNum) {
		this.goldNum = goldNum;
	}

	public int getWoodNum() {
		return woodNum;
	}

	public void setWoodNum(int woodNum) {
		this.woodNum = woodNum;
	}

	public int getStoneNum() {
		return stoneNum;
	}

	public void setStoneNum(int stoneNum) {
		this.stoneNum = stoneNum;
	}

	public int getFoodNum() {
		return foodNum;
	}

	public void setFoodNum(int foodNum) {
		this.foodNum = foodNum;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getItemProbability() {
		return itemProbability;
	}

	public void setItemProbability(String itemProbability) {
		this.itemProbability = itemProbability;
	}

}
