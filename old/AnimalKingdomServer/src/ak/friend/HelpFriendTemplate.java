package ak.friend;

import ak.util.IRandom;
import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataInitException;

public class HelpFriendTemplate implements Template , IRandom{

	/**
	 * npcid
	 */
	private int id;
	/**
	 * 概率
	 */
	private int probability;
	/**
	 * 奖励
	 */
	private int gameAwardId;
	/**
	 * 开放等级
	 */
	private int openLvl;
	
	/**
	 * 消耗体力值
	 */
	private int consumePower;
	/**
	 * 类型
	 */
	private int type;
	private int operateType;
	@Override
	public Template initTemplateByTxtLine(String[] txtLineinfo)
			throws TextDataInitException {
		HelpFriendTemplate helpFriendTemplate = new HelpFriendTemplate();
		helpFriendTemplate.id = Integer.parseInt(txtLineinfo[0]);
		helpFriendTemplate.setType(Integer.parseInt(txtLineinfo[2]));
		helpFriendTemplate.setProbability(Integer.parseInt(txtLineinfo[3]));
		helpFriendTemplate.setGameAwardId(Integer.parseInt(txtLineinfo[4]));
		helpFriendTemplate.setOpenLvl(Integer.parseInt(txtLineinfo[5]));
		helpFriendTemplate.setConsumePower(Integer.parseInt(txtLineinfo[6]));
		helpFriendTemplate.setOperateType(Integer.parseInt(txtLineinfo[7]));
		
		return helpFriendTemplate;
	}

	@Override
	public int getId() {
		
		return id;
	}

	public int getProbability() {
		return probability;
	}

	public void setProbability(int probability) {
		this.probability = probability;
	}

	public int getGameAwardId() {
		return gameAwardId;
	}

	public void setGameAwardId(int gameAwardId) {
		this.gameAwardId = gameAwardId;
	}

	public int getOpenLvl() {
		return openLvl;
	}

	public void setOpenLvl(int openLvl) {
		this.openLvl = openLvl;
	}

	public int getConsumePower() {
		return consumePower;
	}

	public void setConsumePower(int consumePower) {
		this.consumePower = consumePower;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getOperateType() {
		return operateType;
	}

	public void setOperateType(int operateType) {
		this.operateType = operateType;
	}
}
