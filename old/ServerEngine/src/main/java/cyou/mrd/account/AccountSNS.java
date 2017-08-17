package cyou.mrd.account;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyou.mrd.platform.snssdk.bo.PlatType;

import cyou.mrd.Platform;

public class AccountSNS implements Serializable {
	private transient static final Logger log = LoggerFactory.getLogger(AccountSNS.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;

	private String cyouId;

	private String facebookId;

	private String kaixinId;

	private String renrenId;

	private String weiboId;

	private String QQId;
	
	private String twitterId;

	public AccountSNS() {
	}
	
	public AccountSNS(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCyouId() {
		return cyouId;
	}

	public void setCyouId(String cyouId) {
		this.cyouId = cyouId;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getKaixinId() {
		return kaixinId;
	}

	public void setKaixinId(String kaixinId) {
		this.kaixinId = kaixinId;
	}

	public String getRenrenId() {
		return renrenId;
	}

	public void setRenrenId(String renrenId) {
		this.renrenId = renrenId;
	}

	public String getWeiboId() {
		return weiboId;
	}

	public void setWeiboId(String weiboId) {
		this.weiboId = weiboId;
	}

	public String getQQId() {
		return QQId;
	}

	public void setQQId(String qQId) {
		QQId = qQId;
	}

	public String getTwitterId() {
		return twitterId;
	}

	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}

	/**
	 * 与sns服务器下发的字段值保持一致
	 * @param snsType
	 * @return
	 */
	public static String getSNSTypeNameByType(String snsType){
		if(snsType.equals("cyou")){
			return "cyouId";
		}else if(snsType.equalsIgnoreCase(PlatType.facebook.name())){
			return "facebookId";
		}else if(snsType.equalsIgnoreCase(PlatType.kaixin.name())){
			return "kaixinId";
		}else if(snsType.equalsIgnoreCase(PlatType.qq.name())){
			return "QQId";
		}else if(snsType.equalsIgnoreCase(PlatType.renren.name())){
			return "renrenId";
		}else if(snsType.equalsIgnoreCase(PlatType.weibo.name())){
			return "weiboId";
		}else if(snsType.equalsIgnoreCase(PlatType.twitter.name())){
			return "twitterId";
		}else{
			return null;
		}
	}

	/**
	 * 如果客户端不能保证mid唯一, 如果两个人同时操作一个account, 会有可能出现异常.
	 * @param id
	 * @return
	 */
	public static AccountSNS init(int id) {
		AccountSNS sns = Platform.getEntityManager().find(AccountSNS.class, id);
		if(sns == null) {
			sns = new AccountSNS(id);
			Platform.getEntityManager().createSync(sns);
			log.info("[SNS] sns create in db.({})", id);
		}else {
			log.info("[SNS] find by db. sns({})", sns);
		}
		return sns;
	}

	public static void save(AccountSNS sns) {
		Platform.getEntityManager().updateSync(sns);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("accountId:").append(this.id);
		sb.append(", cyouId:").append(this.cyouId);
		sb.append(", facebookId:").append(this.facebookId);
		sb.append(", kaixinId:").append(this.kaixinId);
		sb.append(", QQId:").append(this.QQId);
		sb.append(", renrenId:").append(this.renrenId);
		sb.append(", weiboId:").append(this.weiboId);
		return sb.toString();
	}

}
