package cyou.mrd.charge;

import java.util.Date;

import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;

/**
 * 付费请求
 * @author mengpeng
 */
public class Charge {
	
	private int id;

	private long serialNumber;//流水号
	
	private int accountId;
	
	private int playerId;
	
	private Date requestTime;
	
	private Date finishTime;
	
	private String bundleId;
	
	private String productId;
	
	private int serverReceiveClient;//游戏服务器收到付费请求
	
	private int serverSendWorld;//游戏服务器请求world
	
	private int serverReceiveWorld;//server收到world的验证结果
	
	private int result; // 充值结果
	
	private int extraImoneyRatio; // 充值结果促销比例
	
	private int type; // 充值物品类型
	
	private int price;//充值人民币数
	
	private int errorCode;//充值未成功的错误码
	
	private HSession session;
	
	public int state; // 0：未处理 1: 已处理
	
	private String receipt;//单据信息
	
	Object lock = new Object();

	JSONPacket retPacket;
	
	public static final int CHARGE_ERROR_CODE_1 = 1;//world 服务器未连接
	
	public static final int CHARGE_ERROR_CODE_2 = 2;//GameServer 未收到World的回复
	
	public static final int CHARGE_ERROR_CODE_3 = 3;//Billing 服务器未连接
	
	public static final int CHARGE_ERROR_CODE_4 = 4;//GameServer 未连接world服务器
	
	public int noADTime;
	
	public Charge(){
		
	}
	
	public Charge(HSession session) {
		this.session = session;
		this.requestTime = new Date();
		state = 0;
	}

	public void setAlreadyHandle() {
		this.state = 1;
	}

	public void setRetPacket(JSONPacket retPacket) {
		this.retPacket = retPacket;
	}

	public JSONPacket getRetPacket() {
		return this.retPacket;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getResult() {
		return this.result;
	}
	
	public long getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(long serialNumber) {
		this.serialNumber = serialNumber;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public HSession getSession() {
		return session;
	}

	public void setSession(HSession session) {
		this.session = session;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public int getServerReceiveClient() {
		return serverReceiveClient;
	}

	public void setServerReceiveClient(int serverReceiveClient) {
		this.serverReceiveClient = serverReceiveClient;
	}

	public int getServerSendWorld() {
		return serverSendWorld;
	}

	public void setServerSendWorld(int serverSendWorld) {
		this.serverSendWorld = serverSendWorld;
	}

	public int getServerReceiveWorld() {
		return serverReceiveWorld;
	}

	public void setServerReceiveWorld(int serverReceiveWorld) {
		this.serverReceiveWorld = serverReceiveWorld;
	}

	public int getNoADTime() {
		return noADTime;
	}

	public void setNoADTime(int noADTime) {
		this.noADTime = noADTime;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setType(int type) {
		this.type = type;
	}
	public int getType() {
		return this.type;
	}

	public int getExtraImoneyRatio() {
		return extraImoneyRatio;
	}

	public void setExtraImoneyRatio(int extraImoneyRatio) {
		this.extraImoneyRatio = extraImoneyRatio;
	}
}
