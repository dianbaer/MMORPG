package ak.shop;

import cyou.mrd.projectdata.Template;
import cyou.mrd.projectdata.TextDataInitException;

public class ShopItemTemplate implements Template {

	/**
	 * 爱心值
	 */
	public static final int CURRENCY_TYPE_LOVE = 4;

	private int id;
	private int shopType;
	private int moneyType;
	private int money;

	@Override
	public Template initTemplateByTxtLine(String[] txtLineinfo) throws TextDataInitException {
		ShopItemTemplate shopItem = new ShopItemTemplate();
		shopItem.id = Integer.parseInt(txtLineinfo[0]);
		shopItem.shopType = Integer.parseInt(txtLineinfo[1]);
		shopItem.moneyType = Integer.parseInt(txtLineinfo[5]);
		shopItem.money = Integer.parseInt(txtLineinfo[6]);

		return shopItem;
	}

	@Override
	public int getId() {
		return id;
	}

	public int getShopType() {
		return shopType;
	}

	public void setShopType(int shopType) {
		this.shopType = shopType;
	}

	public int getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(int moneyType) {
		this.moneyType = moneyType;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public void setId(int id) {
		this.id = id;
	}

}
