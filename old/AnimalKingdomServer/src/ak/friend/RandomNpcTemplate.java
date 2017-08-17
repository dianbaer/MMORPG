package ak.friend;

import ak.util.IRandom;
import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataInitException;

public class RandomNpcTemplate implements Template , IRandom{

	/**
	 * npcid
	 */
	private int id;
	
	private int npcId;
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
	private int type;
	@Override
	public Template initTemplateByTxtLine(String[] txtLineinfo)
			throws TextDataInitException {
		RandomNpcTemplate randomNpcTemplate = new RandomNpcTemplate();
		randomNpcTemplate.id = Integer.parseInt(txtLineinfo[0]);
		randomNpcTemplate.npcId = Integer.parseInt(txtLineinfo[1]);
		randomNpcTemplate.setProbability(Integer.parseInt(txtLineinfo[2]));
		randomNpcTemplate.setGameAwardId(Integer.parseInt(txtLineinfo[4]));
		randomNpcTemplate.setOpenLvl(Integer.parseInt(txtLineinfo[5]));
		randomNpcTemplate.setConsumePower(Integer.parseInt(txtLineinfo[6]));
		
		return randomNpcTemplate;
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

	public int getNpcId() {
		return npcId;
	}

	public void setNpcId(int npcId) {
		this.npcId = npcId;
	}
}
