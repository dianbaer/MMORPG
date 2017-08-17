package ak.iap;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.charge.AppStoreProduct;
import cyou.mrd.charge.ChargeClientService;
import cyou.mrd.entity.Player;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HOpCode;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;

@OPHandler(TYPE = OPHandler.HTTP_EVENT)
public class ChargeClientServiceEx extends ChargeClientService{
	private static final Logger log = LoggerFactory.getLogger(ChargeClientServiceEx.class);
	
	public List<AppStoreProduct>  products = new ArrayList<AppStoreProduct>();
	/**
	 *
	 * 
	    <purchase id="com.cyou.mrd.animalkingdom.denglong01" name="@:207" title="" price="6" type="0" extraImoneyRatio="0" imoney="30" icon="denglong001.png" noADTime="12096000"/>
		<purchase id="com.cyou.mrd.animalkingdom.denglong02_1" name="@:208" title="" price="30" type="0" extraImoneyRatio="5" imoney="150" icon="denglong002.png" noADTime="6048000"/>
		<purchase id="com.cyou.mrd.animalkingdom.denglong03_1" name="@:209" title="" price="68" type="0" extraImoneyRatio="10" imoney="340" icon="denglong003.png" noADTime="3024000"/>
		<purchase id="com.cyou.mrd.animalkingdom.denglong04_1" name="@:210" title="" price="128" type="0" extraImoneyRatio="15" imoney="640" icon="denglong004.png" noADTime="604800"/>
		<purchase id="com.cyou.mrd.animalkingdom.denglong06" name="@:1601" title="" price="518" type="0" extraImoneyRatio="25" imoney="2590" icon="denglong005.png" noADTime="604800"/>
		<purchase id="com.cyou.mrd.animalkingdom.jinbi01" name="@:1602" title="" price="6" type="1" extraImoneyRatio="50" imoney="480" icon="money1.png" noADTime="12096000"/>
		<purchase id="com.cyou.mrd.animalkingdom.jinbi02" name="@:1603" title="" price="30" type="1" extraImoneyRatio="100" imoney="2400" icon="money2.png" noADTime="6048000"/>
		<purchase id="com.cyou.mrd.animalkingdom.jinbi03" name="@:1604" title="" price="68" type="1" extraImoneyRatio="150" imoney="5440" icon="money3.png" noADTime="3024000"/>
		<purchase id="com.cyou.mrd.animalkingdom.jinbi04" name="@:1605" title="" price="128" type="1" extraImoneyRatio="200" imoney="10240" icon="money4.png" noADTime="604800"/>
		<purchase id="com.cyou.mrd.animalkingdom.jinbi05" name="@:1606" title="" price="518" type="1" extraImoneyRatio="300" imoney="41440" icon="money5.png" noADTime="604800"/>

	 */
	String[] listStr = {
			"com.cyou.mrd.animalkingdom.denglong01,@:207,,6,0,0,30,denglong001.png,12096000",
			"com.cyou.mrd.animalkingdom.denglong02_1,@:208,,30,0,5,150,denglong002.png,6048000",
			"com.cyou.mrd.animalkingdom.denglong03_1,@:209,,68,0,10,340,denglong003.png,3024000",
			"com.cyou.mrd.animalkingdom.denglong04_1,@:210,,128,0,15,640,denglong004.png,604800",
			"com.cyou.mrd.animalkingdom.denglong06,@:1601,,518,0,25,2590,denglong005.png,604800",
			"com.cyou.mrd.animalkingdom.jinbi01,@:1602,,6,1,50,480,money1.png,12096000",
			"com.cyou.mrd.animalkingdom.jinbi02,@:1603,,30,1,100,2400,money2.png,6048000",
			"com.cyou.mrd.animalkingdom.jinbi03,@:1604,,68,1,150,5440,money3.png,3024000",
			"com.cyou.mrd.animalkingdom.jinbi04,@:1605,,128,1,200,10240,money4.png,604800",
			"com.cyou.mrd.animalkingdom.jinbi05,@:1606,,518,1,300,41440,money5.png,604800",
	};
	@Override
	public void startup() throws Exception {
		super.startup();
		if(!Platform.getConfiguration().getString("sandboxversion").equals("-")) {
			log.info("[charge] @@@@@@@@ sandbox modle is start!!!!!!!!!! target version : {}", Platform.getConfiguration().getString("sandboxversion"));
			for(int i = 0; i < 10; i++) {
				AppStoreProduct product = new AppStoreProduct();
				setProduct(product, listStr[i]);
				products.add(product);
			}
		}
	}
	
	/**
	 * 请求应用商品列表 client-->server bid:xxx String 应用的bandle id
	 * eg.{"opcode":97,"data":{ "bid":"com.cyou.dracula"}}
	 */
	@OP(code = HOpCode.PRODUCE_LIST_CLIENT)
	public void requestProductList(Packet packet, HSession session) {
		super.requestProductList(packet, session);
	}
	
	/**
	 * 请求校验账单 client--->server bid:xxx String 应用bandle id pid:xxx String 商品的id
	 * receipt:xxx String Base64编码的单据信息
	 * eg.{"opcode":95,"data":{"bid":"com.cyou.dracula"
	 * ,"pid":"com.cyou.dracula.productid_0.99"
	 * ,"receipt":"eivmxlzjieowqhfewpqtojma"}}
	 */
	@OP(code = HOpCode.BILLING_VERIFY_CLIENT)
	public void requestCharge(Packet packet, HSession session) {
		super.requestCharge(packet, session);
	}
	
	private void setProduct(AppStoreProduct product, String src) {
		String[] productInfo = src.split(",");
		int k = 0;
		product.setProductID(productInfo[k++]);
		product.setProductName(productInfo[k++]);
		product.setTitle(productInfo[k++]);
		product.setPrice(Integer.parseInt(productInfo[k++]));
		product.setType(Integer.parseInt(productInfo[k++]));
		product.setExtraImoneyRatio(Integer.parseInt(productInfo[k++]));
		product.setImoney(Integer.parseInt(productInfo[k++]));
		product.setIcon(productInfo[k++]);
		product.setNoADTime(Integer.parseInt(productInfo[k++]));
	}

	/**
	 * 工程自己实现   伪造商品列表的功能（苹果审核使用）
	 * @return
	 */
	protected List<AppStoreProduct> getSandBoxProducts() {
		return products;
	}
	
	/**
	 * 下发沙盒验证成功状态 需要在此方法内手动增加钱或灯笼
	 * @param p
	 * @param pid
	 * @param tid
	 * @param session
	 */
	protected void sandboxVerfiy(Player p, String pid, String tid, HSession session) {
		int amount = 100;
		int type = 0;
		int extraImoneyRatio = 0;
		for(AppStoreProduct product : products) {
			if(product.getProductID().equalsIgnoreCase(pid)) {
				amount = product.getImoney();
				type = product.getType();
				extraImoneyRatio = product.getExtraImoneyRatio();
			}
		}
		
		amount = Math.round((float)amount * ((float)(100 + extraImoneyRatio))/100);
		if(type == 0) {
			int imoney = p.getAccount().getImoney() + amount;
			p.getAccount().setImoney(imoney);
		}else if(type == 1){
			int money = p.getMoney();
			money += amount;
			p.setMoney(money);//不能保证好使， 为回档做一点贡献。
		}else {
			log.info("[charge] charge.getType() == {}!! not have handler!", type);
		}
		
		p.notifySaveForce();
		
		JSONPacket pa = new JSONPacket(HOpCode.BILLING_VERIFY_SERVER);
		pa.put("result", amount);
		pa.put("accountId", p.getAccountId());
		pa.put("tid",tid);
		pa.put("type",type);
		session.send(pa);
	}
	
}
