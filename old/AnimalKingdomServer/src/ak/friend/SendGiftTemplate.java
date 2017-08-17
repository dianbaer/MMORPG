package ak.friend;

import ak.util.IRandom;
import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataInitException;

public class SendGiftTemplate implements Template , IRandom{

	/**
	 * 送礼奖励礼品的id
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
	private int type;
	@Override
	public Template initTemplateByTxtLine(String[] txtLineinfo)
			throws TextDataInitException {
		SendGiftTemplate sendGiftTemplate = new SendGiftTemplate();
		sendGiftTemplate.id = Integer.parseInt(txtLineinfo[0]);
		sendGiftTemplate.setProbability(Integer.parseInt(txtLineinfo[1]));
		sendGiftTemplate.setGameAwardId(Integer.parseInt(txtLineinfo[3]));
		sendGiftTemplate.setOpenLvl(Integer.parseInt(txtLineinfo[4]));
		sendGiftTemplate.setConsumePower(Integer.parseInt(txtLineinfo[5]));
		
		return sendGiftTemplate;
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
}
