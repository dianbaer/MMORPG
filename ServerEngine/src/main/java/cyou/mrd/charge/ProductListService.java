package cyou.mrd.charge;

import gnu.trove.procedure.TObjectProcedure;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.event.Event;
import cyou.mrd.event.OPEvent;
import cyou.mrd.io.OP;
import cyou.mrd.io.OPHandler;
import cyou.mrd.io.tcp.ClientSession;
import cyou.mrd.io.tcp.DirectClientSession;
import cyou.mrd.io.tcp.DirectClientSessionService;
import cyou.mrd.io.tcp.IoEvent;
import cyou.mrd.io.tcp.OpCode;
import cyou.mrd.io.tcp.TcpPacket;
import cyou.mrd.service.Service;
import cyou.mrd.updater.Updatable;

/**
 * @brief  商品列表的管理
 * @author mengpeng
 */
@OPHandler(TYPE = OPHandler.TCP_EVENT)
public class ProductListService implements Service,Updatable{

	private static final Logger log = LoggerFactory.getLogger(ProductListService.class);
	
	//商品列表
	private List<AppStoreProduct> products = new ArrayList<AppStoreProduct>();
	
	public static final int TIME_RETRY = 10 * 1000;
	
	public Long lastSendTime = 0L;
	
	public String getId() {
		return "ProductListService";
	}

	public void startup() throws Exception {
		Platform.getUpdater().addSyncUpdatable(this);
	}

	public void shutdown() throws Exception {
		
	}
	
	@OPEvent(eventCode = IoEvent.EVENT_SESSION_REMOVED)
	protected void removeClientSesion(Event event) {
		ClientSession session = (ClientSession) event.param1;
		if(Platform.worldServer() != null){
			if(session.equals(Platform.worldServer().getSession())){//作为gameserver时  world断开
				clearProductList();
			}
		}
		if(Platform.getBillingServer() != null){
			if(session.equals(Platform.getBillingServer().getSession())){//做完world时  billing断开
				clearProductList();
				broadcastProductList();
			}
		}
	}
	
	/**
	 * 作为gameserver时有效
	 * world 回复GameServer应用商品
	 * world --> gameserver
	 * 		size:int						数量
	 * 		(循环size次)
	 * 	 		id:xxx			String      商品id
     * 			name:xxx		String      商品name
     * 			des:xxx			String		商品描述
     * 			price:xxx		int		 	人民币价值
     * 			demon:xxx		int			游戏内价格
     * 			icon:xxx		String		图标
     * 			AD				int			广告时间
     * 			type			int 		商品类型
     * 			extraImoneyRatio		int		促销加成比例
	 */
	@OP(code = OpCode.SERVER_REQUEST_PRODUCT_SERVER)
	public void initProductsGameServer(TcpPacket packet, ClientSession session){
		synchronized(products){
			//log.info("init GameServer products begin  old Size:{}",products.size());
			products.clear();
			int size = packet.getInt();
			for(int i = 0;i < size;i++){
				String id = packet.getString();
				String name = packet.getString();
				String title = packet.getString();
				int price = packet.getInt();
				int imoney = packet.getInt();
				String icon = packet.getString();
				int noADTime = packet.getInt();
				int type = packet.getInt();
				int extraImoneyRatio = packet.getInt();
				AppStoreProduct asp = new AppStoreProduct(id,name,title,price,imoney,extraImoneyRatio,icon,noADTime,type);
				products.add(asp);
			}
			//log.info("init GameServer products begin  new Size:{}",products.size());
		}
	}
	
	/**
	 * 作为gameServer有效  向world请求商品列表
	 */
	public void gameServerRequestList(){
		if(Platform.worldServer() != null){
			TcpPacket pt = new TcpPacket(OpCode.SERVER_REQUEST_PRODUCT_CLIENT);
			Platform.worldServer().send(pt);
			//log.info("request Product List To World");
		}

	}
	
	/**
	 * 作为world时有效    处理gameserver的请求商品列表请求  回传商品列表
	 * 	size:int						数量
	 * 	(循环size次)
	 * 	 	id:xxx			String      商品id
     * 		name:xxx		String      商品name
     * 		des:xxx			String		商品描述
     * 		price:xxx		int		 	人民币价值
     * 		demon:xxx		int			游戏内价格
     * 		icon:xxx		String		图标
     * 		AD				int			广告时间
     * 		type			int 		商品类型
     * 		extraImoneyRatio		int		促销加成比例
	 */
	@OP(code = OpCode.SERVER_REQUEST_PRODUCT_CLIENT)
	public void gameServerRequestList(TcpPacket packet, ClientSession session) {
		session.send(structProductList());
	}
	
	/**
	 * world时有效
	 * 构造返回给gameServer的商品列表的tcp包
	 */
	private TcpPacket structProductList(){
		TcpPacket pt = new TcpPacket(OpCode.SERVER_REQUEST_PRODUCT_SERVER);
		synchronized(products){
			pt.putInt(products.size());
			for(AppStoreProduct asp:products){
				pt.putString(asp.getProductID());
				pt.putString(asp.getProductName());
				pt.putString(asp.getTitle());
				pt.putInt(asp.getPrice());
				pt.putInt(asp.getImoney());
				pt.putString(asp.getIcon());
				pt.putInt(asp.getNoADTime());
				pt.putInt(asp.getType());
				pt.putInt(asp.getExtraImoneyRatio());
			}
		}
		return pt;
	}
	
	/**
	 * 作为world时有效     向billing请求商品列表
	 */
	public void worldRequestList(){
		TcpPacket pt = new TcpPacket(OpCode.WORLD_REQUEST_PRODUCT_CLIENT);
		if(Platform.getBillingServer() != null){
			Platform.getBillingServer().send(pt);
			log.info("request Product List To Billing");
		}
	}
	
	/**
	 * 作为world时有效     billing回复商品列表
	 */
	@OP(code = OpCode.WORLD_REQUEST_PRODUCT_SERVER)
	public void billingResponseWorld(TcpPacket packet, ClientSession session) {
		synchronized (products) {
			log.info("init World products begin  old Size:{}", products.size());
			products.clear();
			int size = packet.getInt();
			for (int i = 0; i < size; i++) {
				String id = packet.getString();
				String name = packet.getString();
				String title = packet.getString();
				int price = packet.getInt();
				int imoney = packet.getInt();
				String icon = packet.getString();
				int noADTime = packet.getInt();
				int type = packet.getInt();
				int extraImoneyRatio = packet.getInt();
				AppStoreProduct asp = new AppStoreProduct(id,name,title,price,imoney,extraImoneyRatio,icon,noADTime,type);
				products.add(asp);
			}
			log.info("init World products begin new Size:{}", products.size());
		}
		//广播商品列表
		broadcastProductList();
	}
	
	/**
	 *做为world时有效
	 *当billng的商品列表重载时  需要向所有world广播最新的商品列表  wolrd收到后需要向所有gameserver广播商品列表
	 */
	private void broadcastProductList(){
		log.info("broadcastProductList begin!");
		DirectClientSessionService dcs = Platform.getAppContext().get(DirectClientSessionService.class);
		synchronized (dcs.sessions) {
			if(dcs != null && dcs.sessions.size() > 0){
				log.info("broadcastProductList gameserver size:{},productListSize:{}",dcs.sessions.size(),products.size());
				dcs.sessions.forEachValue(new TObjectProcedure<DirectClientSession>() {
					@Override
					public boolean execute(DirectClientSession websession) {
						if(Platform.getBillingServer() != null){
							if(websession.equals(Platform.getBillingServer().getSession()))
								return true;
						}
						if (websession.getClient() != null) {
							websession.send(structProductList());
						}
						return true;
					}
				});
			}
		}
		log.info("broadcastProductList over!");
	}
	
	@Override
	public boolean update() {
		Long time = System.currentTimeMillis();
		if(time - lastSendTime > TIME_RETRY){
			synchronized(products){
				if(products.size() == 0){
					if(Platform.worldServer() != null){
						gameServerRequestList();
					}
					if(Platform.getBillingServer() != null){
						worldRequestList();
					}
				}
				lastSendTime = time;
			}
		}
		return false;
	}
	
	public List<AppStoreProduct> getProducts(){
		synchronized(products){
			return products;
		}
	}
	
	public void clearProductList(){
		synchronized(products){
			if(products.size() > 0){
				products.clear();
			}
		}
	}
	
}
