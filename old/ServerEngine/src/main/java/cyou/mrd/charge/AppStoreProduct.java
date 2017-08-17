package cyou.mrd.charge;

/**
 * @brief 商品模型
 * @author mengpeng
 */
public class AppStoreProduct {
	
	private String productID;		// 产品ID
	
	private String productName;		// 产品名称
	
	private String title;			// 产品描述
	
	private int price;				// 价格（人民币）
	
	private int imoney;				// 对应i币
	
	private int extraImoneyRatio;	// 对应i币的促销比例1 = 1%
	
	private String icon;			// 对应图标
	
	private int noADTime;			// 无广告时间
	
	private int type;				// 商品类型（imoney意义变为数量）
	
	public AppStoreProduct() {}
	
	public AppStoreProduct(String id, String name,String title, int price, int imoney ,int extraImoneyRatio ,String icon,int noADTime, int type) {
		this.productID = id;
		this.productName = name;
		this.title = title;
		this.price = price;
		this.imoney = imoney;
		this.extraImoneyRatio = extraImoneyRatio;
		this.icon = icon;
		this.noADTime = noADTime;
		this.type = type;
	}

	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getImoney() {
		return imoney;
	}

	public void setImoney(int imoney) {
		this.imoney = imoney;
	}

	public int getExtraImoneyRatio() {
		return extraImoneyRatio;
	}

	public void setExtraImoneyRatio(int extraImoneyRatio) {
		this.extraImoneyRatio = extraImoneyRatio;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getNoADTime() {
		return noADTime;
	}

	public void setNoADTime(int noADTime) {
		this.noADTime = noADTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
	
}
