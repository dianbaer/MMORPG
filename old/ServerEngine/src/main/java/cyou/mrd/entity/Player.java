package cyou.mrd.entity;

import java.io.Serializable;
import java.util.Date;

import net.sf.json.JSONObject;
import cyou.mrd.Platform;
import cyou.mrd.account.Account;
import cyou.mrd.event.Event;
import cyou.mrd.event.GameEvent;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.HttpClient;
import cyou.mrd.io.http.HttpClientTransfer;

public class Player implements HttpClient, GameObject, HttpClientTransfer, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String POOL_KEY_SNSTOKEY = "pool_key_snstokey_";// 数据池key,snstoken,后面加snstype;

	public static final String POOL_KEY_I18N_LANGUAGE = "i18n_language";
	
	public static final String POOL_KEY_SYSTEM_NOTICE_READED = "system_notice";

	private int id;

	private int accountId;

	private String name;

	private String icon;

	private int money;

	private int level;

	private int exist;

	private PropertyPool pool = new PropertyPool();

	private Date lastLoginTime;

	private Date lastLogoutTime;

	private Date lastSynchInfoTime;

	private Date createTime;

	private HSession session;

	private Account account;

	private String area; // 登录地区名称

	private String country; // 登录国家名称

	private String device; // 登录设备名称

	private String deviceSystem; // 登录系统名称

	private String downloadType; // 下载类型

	private String networkType; // 联网类型名称

	private String prisonBreak; // 是否越狱(0:否 1:是)

	private String operator; // 运营商名称

	private int star;

	private boolean transactionStatus;// 人物身上是否有未完成的交易

	private boolean isDirty; // 数据变脏了, 需要存储到数据库中
	
	private int saveTime; // 数据保存时间
	
	private int loginServerId; //记录登陆的serverId；
	
	private int exp;
	
	private String clientVersion;//客户端版本号
	
	//新版兔村需要添加属性
	private int raceId;//种族ID
	
	private int rich; //繁荣度
	
	//种族标识
	/**
	 * 种族：兔族
	 */
	public static final int RACE_RABIT = 1;
	
	/**
	 * 种族：猫族
	 */
	public static final int RACE_CAT = 2;
	
	/**
	 * 种族：狼族
	 */
	public static final int RACE_WOLF = 3;

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Date getLastLogoutTime() {
		return lastLogoutTime;
	}

	public void setLastLogoutTime(Date lastLogoutTime) {
		this.lastLogoutTime = lastLogoutTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastSynchInfoTime() {
		return lastSynchInfoTime;
	}

	public void setLastSynchInfoTime(Date lastSynchInfoTime) {
		this.lastSynchInfoTime = lastSynchInfoTime;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public int getId() {
		return id;
	}

	public int getInstanceId() {
		return id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public HSession getSession() {
		return this.session;
	}
	/**
	 * 登录成功后调用此方法，或者当前在线用户，请求的时候如果没有Player里没有session调用此方法（二个地方调用）
	 * @param session
	 */
	public void setSession(HSession session) {
		this.session = session;
		this.session.setClient(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getExist() {
		return exist;
	}

	public void setExist(int exist) {
		this.exist = exist;
	}

	public PropertyPool getPool() {
		return pool;
	}

	public void setPool(PropertyPool pool) {
		this.pool = pool;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getDeviceSystem() {
		return deviceSystem;
	}

	public void setDeviceSystem(String deviceSystem) {
		this.deviceSystem = deviceSystem;
	}

	public String getDownloadType() {
		return downloadType;
	}

	public void setDownloadType(String downloadType) {
		this.downloadType = downloadType;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getPrisonBreak() {
		return prisonBreak;
	}

	public void setPrisonBreak(String prisonBreak) {
		this.prisonBreak = prisonBreak;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public boolean isTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(boolean transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public void notifySave() {
		this.isDirty = true;
		Platform.getEventManager().putEvent(new Event(GameEvent.EVENT_PLAYER_CHANGEED, this));
	}

	public void notifySaveForce() {
		this.isDirty = true;
		Platform.getEventManager().fireEvent(new Event(GameEvent.EVENT_PLAYER_CHANGEED_FORCE, this));
	}
	
	public int getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(int saveTime) {
		this.saveTime = saveTime;
	}
	
	

	public int getLoginServerId() {
		return loginServerId;
	}

	public void setLoginServerId(int loginServerId) {
		this.loginServerId = loginServerId;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	@Override
	public String toClientData() {
		JSONObject temp = new JSONObject();
		temp.put("id", id);
		temp.put("level", level);
		temp.put("money", money);
		temp.put("imoney", this.getAccount().getImoney());
		return temp.toString();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("p[");
		sb.append("playerId:").append(this.id);
		sb.append(",accountId:").append(this.accountId);
		sb.append(",name:").append(this.name);
		sb.append(",money:").append(this.money);
		sb.append(",level:").append(this.level);
		sb.append(",isDirty:").append(this.isDirty);
		sb.append(",transactionStatus:").append(this.transactionStatus);
		sb.append(",area:").append(this.area);
		sb.append(",country:").append(this.country);
		sb.append(",device:").append(this.device);
		sb.append(",deviceSystem:").append(this.deviceSystem);
		sb.append(",downloadType:").append(this.downloadType);
		sb.append(",networkType:").append(this.networkType);
		sb.append(",operator:").append(this.operator);
		sb.append(",prisonBreak:").append(this.prisonBreak);
		sb.append("]");
		return sb.toString();
	}

	/**
	 * 语言
	 * @return
	 */
	public int getLang() {
		return pool.getInt(POOL_KEY_I18N_LANGUAGE);
	}

	public void setLanguage(int language) {
		pool.setInt(POOL_KEY_I18N_LANGUAGE, language);
	}


	/**
	 * 已读的公告
	 * @return
	 */
	public int[] getReadedNoticeIds() {
		String readedSrc = this.getPool().getString(Player.POOL_KEY_SYSTEM_NOTICE_READED);
		if (readedSrc.equals("")) {
			return new int[0];
		} else {
			String[] readeds = readedSrc.split(",");
			int[] readedIds = new int[readeds.length];
			for (int i = 0; i < readedIds.length; i++) {
				String idSrc = readeds[i];
				readedIds[i] = Integer.parseInt(idSrc);
			}
			return readedIds;
		}
	}

	/**
	 * 设置公告已读
	 * @param id
	 */
	public void setNoticeReaded(int id) {
		String readedSrc = this.getPool().getString(Player.POOL_KEY_SYSTEM_NOTICE_READED);
		String[] readeds;
		if (readedSrc.equals("")) {
			readeds = new String[0];
		} else {
			readeds = readedSrc.split(",");
		}
		boolean repeat = false;
		for (String rid : readeds) {
			if (rid.equals(String.valueOf(id))) {
				repeat = true;
				break;
			}
		}
		if (!repeat) {
			if (readedSrc.equals("")) {
				readedSrc = String.valueOf(id);
			} else {
				readedSrc += "," + id;
			}
		}
		this.getPool().setString(Player.POOL_KEY_SYSTEM_NOTICE_READED, readedSrc);
		this.notifySave();
	}

	
	public int getRaceId() {
		return raceId;
	}

	
	public void setRaceId(int raceId) {
		this.raceId = raceId;
	}

	
	public int getRich() {
		return rich;
	}

	
	public void setRich(int rich) {
		this.rich = rich;
	}
}
