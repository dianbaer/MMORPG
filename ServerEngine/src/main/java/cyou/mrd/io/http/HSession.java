package cyou.mrd.io.http;

import java.io.Serializable;

import net.sf.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.Platform;
import cyou.mrd.entity.Player;
import cyou.mrd.io.Packet;
import cyou.mrd.io.http.SessionManager.SessionSegment;

public class HSession implements Serializable {
	private transient static final Logger log = LoggerFactory.getLogger(HSession.class);
	
	private static final long serialVersionUID = 1L;
	private SessionSegment sessionSegment;
	private HttpClient client;
	private String ip;

	private JSONArray willSendPackets;
	/**
	 * session每次刷新间隔时间. 我们在超过此时间段的session会刷新.
	 */
	private static final int  UPDATE_PLAYER_TIME = 3 * 60 ;
	/**
	 * 如果没有登录，根据id创建session时调用此方法（只有一个地方调用）
	 * @param ip
	 */
	public HSession(String ip) {
		willSendPackets = new JSONArray();
		this.ip = ip;
	}
	/**
	 * 当前在线用户，请求的时候如果Player里没有session调用此方法（只有一个地方调用）
	 * @param sseg
	 * @param client
	 * @param ip
	 */
	public HSession(SessionSegment sseg, HttpClient client, String ip) {
		this(ip);
		this.sessionSegment = sseg;
		this.client = client;
	}

	public HttpClient client() {
		return client;
	}
	/**
	 * 登录成功后调用此方法，或者当前在线用户，请求的时候如果Player里没有session调用此方法（二个地方调用）
	 * player里设置session时调用此方法（没有其他地方调用这个方法了）
	 * @param client
	 */
	public void setClient(HttpClient client) {
		this.client = client;
	}

	public void send(Packet pt) {
		pt.setServerName(Platform.getConfiguration().getString("world.clientname"));
		pt.setThreadName(Thread.currentThread().getName());
		willSendPackets.add((JSONPacket) pt);
	}
	/**
	 * 回复请求的时候调用（只有一个地方调用）
	 * @return
	 */
	public JSONArray willSendPackets() {
		return willSendPackets;
	}

	/**
	 * 生成session;
	 * 登录成功后调用此方法（没有其他地方调用这个方法了）
	 * @param playerId
	 */
	public void convert(int playerId) {
		sessionSegment = SessionSegment.encoder(playerId);
	}

	public String getSessionId() {
		if (sessionSegment == null) {
			return "";
		} else {
			return sessionSegment.sessionId;
		}
	}
	/**
	 * 用户登出时调用此方法，设置session过期（只有一个地方调用）
	 */
	public void invalid() {
		if (sessionSegment != null) {
			sessionSegment.resetCreateTime((int) (System.currentTimeMillis() / 1000 - 10000));
		}
	}
	/**
	 * 当前在线用户，请求的时候 Player里面有session时调用，更新session创建时间，重新生成sessionId（只有一个地方调用）
	 * 超过3分钟返回true 更新一下
	 * @return 
	 */
	public boolean active() {
//		log.info("!!!!!!!!!!!!!!!!!!!!!!!{}" , this.client.hashCode());
		if (sessionSegment != null) {
			String old = this.getSessionId();
			sessionSegment.resetCreateTime((int) (System.currentTimeMillis() / 1000));
			if((int) (System.currentTimeMillis() / 1000) - sessionSegment.getLastUpatePlayerTime() > UPDATE_PLAYER_TIME){
				sessionSegment.setLastUpatePlayerTime(sessionSegment.getCreateTime());
				String now = this.getSessionId();
				log.info("[session] player:{} session active. old({}) -> new({})",new Object[]{((Player)this.client).getId(), old, now});
				return true;
			}
		}
		return false;
	}
	/**
	 * 只有putPlayer调用，登录成功时，或者当前在线用户再次请求，并且超过三分钟时
	 * @return
	 */
	public int getSessionTime() {
		if (sessionSegment == null) {
			return 0;
		} else {
			return sessionSegment.createTime;
		}
	}
	/**
	 * 回复完请求后清空时调用（只有一个地方调用）
	 */
	public void clear() {
		willSendPackets.clear();
	}

	public String ip() {
		return this.ip;
	}

	public SessionSegment getSessionSegment() {
		return sessionSegment;
	}
}
