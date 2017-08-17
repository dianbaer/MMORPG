package cyou.mrd.account;

import java.io.Serializable;
import java.util.Date;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyou.mrd.platform.snssdk.bo.PlatType;

import cyou.mrd.Platform;
import cyou.mrd.util.ConfigKeys;
import cyou.mrd.util.ErrorHandler;
import cyou.mrd.util.RunTimeMonitor;

public class Account implements Serializable {
	private transient static final Logger log = LoggerFactory.getLogger(Account.class);
	
	/**
	 * 项目应该根据数值判断改值
	 * 获得元宝的总值范围
	 */
	public static int TOTAL_REWARD_DOLLAR = Platform.getConfiguration().getInt(ConfigKeys.PLAYER_MAX_REWARD_DOLLAR);
	
	public static int DEFAULT_INIT_DOLLAR = Platform.getConfiguration().getInt(ConfigKeys.PLAYER_DEFAULT_INIT_DOLLAR);
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String mid;
	
	private Date createTime;
	
	private Date lastLoginTime;
	
	/**
	 * 没有初始化, 按需手工加载;<br>
	 * this.sns = AccountSNS.init(this.id);
	 */
	private AccountSNS sns;
	
	/**
	 * 充值金额
	 */
	private int imoney; 
	/**
	 * 补偿
	 */
	private int compensateDollar;
	/**
	 * 游戏内获得
	 */
	private int rewardDollar;
	/**
	 * 花费
	 */
	private int usedDollar;

	private int initDollar;

	private boolean isDirty;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public int getImoney() {
		return imoney;
	}

	public void setImoney(int imoney) {
		this.imoney = imoney;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	} 

	public int getCompensateDollar() {
		return compensateDollar;
	}
 

	public int getInitDollar() {
		return initDollar;
	}

	public void setCompensateDollar(int compensateDollar) {
		this.compensateDollar = compensateDollar;
	}

	public int getRewardDollar() {
		return rewardDollar;
	}

	public void setRewardDollar(int rewardDollar) {
		this.rewardDollar = rewardDollar;
	}

	public void setInitDollar(int initDollar) {
		this.initDollar = initDollar;
	}

	public int getUsedDollar() {
		return usedDollar;
	}

	public void setUsedDollar(int usedDollar) {
		this.usedDollar = usedDollar;
	}
	
	
	public int getRemainDollar(){
		return compensateDollar + imoney + rewardDollar + initDollar - usedDollar;
	}

	public void bindSNS(String type, String snsId) {
		RunTimeMonitor rt = new RunTimeMonitor();
		rt.knock("bindSNS");		
		if(this.sns == null) {
			synchronized (this) {
				rt.knock("synchronized.this");
				if(this.sns == null) {
					this.sns = AccountSNS.init(this.id);
					rt.knock("AccountSNS.init");
				}
			}
		}
		if (type.equalsIgnoreCase("cyou")) {
			this.sns.setCyouId(snsId);
		} else if (type.equalsIgnoreCase(PlatType.renren.name())) {
			this.sns.setRenrenId(snsId);
		} else if (type.equalsIgnoreCase(PlatType.kaixin.name())) {
			this.sns.setKaixinId(snsId);
		} else if (type.equalsIgnoreCase(PlatType.facebook.name())) {
			this.sns.setFacebookId(snsId);
		} else if (type.equalsIgnoreCase(PlatType.qq.name())) {
			this.sns.setQQId(snsId);
		} else if (type.equalsIgnoreCase(PlatType.weibo.name())) {
			this.sns.setWeiboId(snsId);
		} else if (type.equalsIgnoreCase(PlatType.twitter.name())) {
			this.sns.setTwitterId(snsId);
		}
		rt.knock("type.equals");
		AccountSNS.save(this.sns);
		rt.knock("AccountSNS.save");
		log.info("bindsns" + rt.toString(-1));
	}
	
	public String getSnsIdByType(String type) {
		if(this.sns == null) {
			synchronized (this) {
				if(this.sns == null) {
					this.sns = AccountSNS.init(this.id);
				}
			}
		}
		if (type.equalsIgnoreCase("cyou")) {
			return this.sns.getCyouId();
		} else if (type.equalsIgnoreCase(PlatType.renren.name())) {
			return this.sns.getRenrenId();
		} else if (type.equalsIgnoreCase(PlatType.kaixin.name())) {
			return this.sns.getKaixinId();
		} else if (type.equalsIgnoreCase(PlatType.facebook.name())) {
			return this.sns.getFacebookId();
		} else if (type.equalsIgnoreCase(PlatType.qq.name())) {
			return this.sns.getQQId();
		} else if (type.equalsIgnoreCase(PlatType.weibo.name())) {
			return this.sns.getWeiboId();
		}else {
			return null;
		}
		
	}
	

	public ResetImoneyRet verifyImoney(int playerId,int buyDollar, int compensateDollar, int remainDollar, int rewardDollar, int usedDollar, int initDollar) {
		log.info("[Account] [Begin Verify Dollar] playerId:{} accountId:{} buyDollar:{},compensateDollar:{},remainDollar:{},rewardDollar:{},usedDollar:{},initDollar:{}",
				new Object[]{playerId,this.getId(), buyDollar, compensateDollar,remainDollar,rewardDollar,usedDollar,initDollar});
		ResetImoneyRet ret = new ResetImoneyRet();
		ret.isClientNotSyncDollar = false;
		ret.result = true;
		if(buyDollar < this.imoney) {//客户端没有及时同步已经购买了的钻石；
//			ret.result = false;
//			ret.errorCode = ErrorHandler.ERROR_CODE_IMONEY_BUYDOLLAR_NOLINEAR;
			ret.isClientNotSyncDollar = true;
			log.info("[Account] [Dollar] [syncno] playerId:{} accountId:{} buyDollar({}) <  this.buyDollar({})", new Object[]{playerId,this.getId(), buyDollar, this.imoney});
			return ret;
		}
		if(buyDollar != this.imoney) {
			ret.result = false;
			ret.errorCode = ErrorHandler.ERROR_CODE_IMONEY_BUYDOLLAR_NOLINEAR;
			log.info("[Account] [Dollar] [syncno] playerId:{} accountId:{} buyDollar({}) > this.buyDollar({})", new Object[]{playerId,this.getId(), buyDollar, this.imoney});
		}
		if(compensateDollar > this.compensateDollar) {
			ret.result = false;
			ret.errorCode = ErrorHandler.ERROR_CODE_IMONEY_COMPENSATEDOLLAR_NOLINEAR;
			log.info("[Account] [Dollar] [syncno] playerId:{} accountId:{} compensateDollar({}) > this.compensateDollar({})", new Object[]{playerId,this.getId(), compensateDollar, this.compensateDollar});
		}
		if (remainDollar != buyDollar + compensateDollar + rewardDollar + initDollar - usedDollar) {
			ret.result = false;
			ret.errorCode = ErrorHandler.ERROR_CODE_IMONEY_REMAINDOLLAR_NOLINEAR;
			log.info("[Account] [Dollar] [syncno] playerId:{} accountId:{} remainDollar({}) != buyDollar({}) + compensateDollar({}) + rewardDollar({}) + initDollar({}) - usedDollar({})", new Object[]{playerId,this.getId(), remainDollar , buyDollar , compensateDollar , rewardDollar ,initDollar ,usedDollar});
		}
		if (rewardDollar > TOTAL_REWARD_DOLLAR) {
			ret.result = false;
			ret.errorCode = ErrorHandler.ERROR_CODE_IMONEY_REWARDDOLLAR_OUT_MAX;
			log.info("[Account] [Dollar] [syncno] playerId:{} accountId:{} rewardDollar({}) > TOTAL_REWARD_DOLLAR({})", new Object[]{playerId,this.getId(), rewardDollar, TOTAL_REWARD_DOLLAR});
		}
		if (usedDollar < this.usedDollar) {
			ret.result = false;
			ret.errorCode = ErrorHandler.ERROR_CODE_IMONEY_USEDDOLLAR_NOLINEAR;
			log.info("[Account] [Dollar] [syncno] playerId:{} accountId:{} usedDollar({}) < this.usedDollar({})", new Object[]{playerId,this.getId(), usedDollar, this.usedDollar});
		}
		if (initDollar > DEFAULT_INIT_DOLLAR) {
			ret.result = false;
			ret.errorCode = ErrorHandler.ERROR_CODE_INITDOLLAR_NOLINEAR;
			log.info("[Account] [Dollar] [syncno] playerId:{} accountId:{} initDollar({}) > SERVER initDollar({})", new Object[]{playerId,this.getId(), initDollar, DEFAULT_INIT_DOLLAR});
		}
		return ret;
	}
	
	public void resetImoney(int initDollar, int rewardDollar, int usedDollar) {
		if (initDollar != this.initDollar || rewardDollar != this.rewardDollar || usedDollar != this.usedDollar) {
			log.info(
					"[Account] [Dollar] [syncok] accountId:{} old data:[rewardDollar={},usedDollar={}] new data:[rewardDollar={},usedDollar={}]",
					new Object[] { this.getId(), this.rewardDollar, this.usedDollar, rewardDollar, usedDollar });
			this.rewardDollar = rewardDollar;
			this.usedDollar = usedDollar;
			this.initDollar = initDollar;
			isDirty = true;
		}
	}
	
	public static class ResetImoneyRet {

		public int errorCode;
		public boolean result;
		public boolean isClientNotSyncDollar;
	}

	public JSONObject toVerifyJson() {
		JSONObject verify = new JSONObject();
		verify.put("BuyDollar", this.imoney);
		verify.put("CompensateDollar", this.compensateDollar);
		verify.put("InitDollar", this.initDollar);
		verify.put("RemainDollar", this.imoney + this.compensateDollar + this.rewardDollar + this.initDollar - this.usedDollar);
		verify.put("RewardDollar", this.rewardDollar);
		verify.put("UseDollar", this.usedDollar);
		return verify;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("act[");
		sb.append("accountId:").append(this.id);
		sb.append(",buyDollar:").append(this.imoney);
		sb.append(",compensateDollar:").append(this.compensateDollar);
		sb.append(",rewardDollar:").append(this.rewardDollar);
		sb.append(",usedDollar:").append(this.usedDollar);
		sb.append("]");
		return sb.toString(); 
	}

	public boolean needUpdateToDb() {
		return isDirty;
	}
	 
}
