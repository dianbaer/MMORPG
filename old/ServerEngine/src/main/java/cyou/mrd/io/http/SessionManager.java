package cyou.mrd.io.http;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.ObjectAccessor;
import cyou.mrd.Platform;
import cyou.mrd.data.Data;
import cyou.mrd.data.DataKeys;
import cyou.mrd.entity.Player;
import cyou.mrd.event.Event;
import cyou.mrd.event.GameEvent;
import cyou.mrd.io.Packet;
import cyou.mrd.io.tcp.OpCode;
import cyou.mrd.io.tcp.TcpPacket;
import cyou.mrd.service.Service;
import cyou.mrd.updater.Updatable;
import cyou.mrd.util.IdUtil;
import cyou.mrd.util.Time;
import cyou.mrd.util.Utils;

/**
 * 读写锁针对的数据对象分成几个小对象, 来分担读写锁功能
 * 
 * @author Administrator
 */

public class SessionManager implements Service, Updatable {
	private static Logger log = LoggerFactory.getLogger(SessionManager.class);

	/**
	 * 本地session的超时时间限制, 这里将来可能会把时间设置的很大, 例如一天.
	 */
	public static final long SESSION_KEEP_TIME = Platform.getConfiguration().getLong("session_keep_time");// 10
																											// *
																											// 60
																											// *
																											// 1000;

	/**
	 * 同步世界session的间隔时间
	 */
	private final long SYNC_WORLD_SESSION_SLIP_TIME = Platform.getConfiguration().getLong("session_world_sync_time");// 10
																														// *
																														// 1000;

	/**
	 * 每个session表. 每次update最多处理的离线者人数为300;
	 */
	private static int REFALSHD_PLAYER_LOOP_SEG_NUM = 60;
	// /**
	// * 超过100条session数据修改立即更新
	// */
	// private final int SYNC_WORLD_SESSION_MAX_CHANGED_NUM =
	// Platform.getConfiguration().getInt("session_world_max_changed_num");//100;

	public static final String GAME_SESSIONID = Platform.getGameCode() + "_VALUEID";

	private static ReadWriteLock onlinePlayersLock_0 = new ReentrantReadWriteLock();
	private static ReadWriteLock onlinePlayersLock_1 = new ReentrantReadWriteLock();
	private static ReadWriteLock onlinePlayersLock_2 = new ReentrantReadWriteLock();
	private static ReadWriteLock onlinePlayersLock_3 = new ReentrantReadWriteLock();
	private static ReadWriteLock onlinePlayersLock_4 = new ReentrantReadWriteLock();
	private static ReadWriteLock onlinePlayersLock_5 = new ReentrantReadWriteLock();
	private static ReadWriteLock onlinePlayersLock_6 = new ReentrantReadWriteLock();
	private static ReadWriteLock onlinePlayersLock_7 = new ReentrantReadWriteLock();
	private static ReadWriteLock onlinePlayersLock_8 = new ReentrantReadWriteLock();
	private static ReadWriteLock onlinePlayersLock_9 = new ReentrantReadWriteLock();

	private static ReadWriteLock resetSessionListLock_0 = new ReentrantReadWriteLock();
	private static ReadWriteLock resetSessionListLock_1 = new ReentrantReadWriteLock();
	private static ReadWriteLock resetSessionListLock_2 = new ReentrantReadWriteLock();
	private static ReadWriteLock resetSessionListLock_3 = new ReentrantReadWriteLock();
	private static ReadWriteLock resetSessionListLock_4 = new ReentrantReadWriteLock();
	private static ReadWriteLock resetSessionListLock_5 = new ReentrantReadWriteLock();
	private static ReadWriteLock resetSessionListLock_6 = new ReentrantReadWriteLock();
	private static ReadWriteLock resetSessionListLock_7 = new ReentrantReadWriteLock();
	private static ReadWriteLock resetSessionListLock_8 = new ReentrantReadWriteLock();
	private static ReadWriteLock resetSessionListLock_9 = new ReentrantReadWriteLock();

	/**
	 * 记录最后一次同步世界session时间
	 */
	private long lastReflashSessionTime = 0;

	/**
	 * 更新了的session列表, 用以同步远程session
	 */
	private static TIntIntMap resetSessionList_0 = new TIntIntHashMap();
	private static TIntIntMap resetSessionList_1 = new TIntIntHashMap();
	private static TIntIntMap resetSessionList_2 = new TIntIntHashMap();
	private static TIntIntMap resetSessionList_3 = new TIntIntHashMap();
	private static TIntIntMap resetSessionList_4 = new TIntIntHashMap();
	private static TIntIntMap resetSessionList_5 = new TIntIntHashMap();
	private static TIntIntMap resetSessionList_6 = new TIntIntHashMap();
	private static TIntIntMap resetSessionList_7 = new TIntIntHashMap();
	private static TIntIntMap resetSessionList_8 = new TIntIntHashMap();
	private static TIntIntMap resetSessionList_9 = new TIntIntHashMap();

	/** <playerId, activeSecond> */
	private static TIntIntMap onlinePlayers_0 = new TIntIntHashMap();
	private static TIntIntMap onlinePlayers_1 = new TIntIntHashMap();
	private static TIntIntMap onlinePlayers_2 = new TIntIntHashMap();
	private static TIntIntMap onlinePlayers_3 = new TIntIntHashMap();
	private static TIntIntMap onlinePlayers_4 = new TIntIntHashMap();
	private static TIntIntMap onlinePlayers_5 = new TIntIntHashMap();
	private static TIntIntMap onlinePlayers_6 = new TIntIntHashMap();
	private static TIntIntMap onlinePlayers_7 = new TIntIntHashMap();
	private static TIntIntMap onlinePlayers_8 = new TIntIntHashMap();
	private static TIntIntMap onlinePlayers_9 = new TIntIntHashMap();
	
	public static int worldOnlineUser = 0;
	private static int worldOnlineUserTemp = 0;

	public static HSession findGameSession(HttpServletRequest request,Packet packet) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {
			return null;
		}
		int accountId = 0;
		if(Platform.getConfiguration().getInt("is_pressure_test") == 1){
			if(packet.containsKey("accountId")){
				if (Platform.getConfiguration().getBoolean("ID_ENCODE")) {
					accountId = IdUtil.deCode(packet.getString("accountId"));
				} else {
					accountId = packet.getInt("accountId");
				}
			}
		}
		
		String gameSession = null;
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if(cookie != null){
				if(Platform.getConfiguration().getInt("is_pressure_test") == 1){
					if(accountId != 0){
						if((GAME_SESSIONID+accountId).equals(cookie.getName())){
							gameSession = cookie.getValue();
							break;
						}
					}else{
						if(cookie.getName().startsWith(GAME_SESSIONID)){
							gameSession = cookie.getValue();
							break;
							
						}
					}
					
				}else{
					if(GAME_SESSIONID.equals(cookie.getName())){
						gameSession = cookie.getValue();
						break;
					}
				}
			}
//			if (cookie != null && GAME_SESSIONID.equals(cookie.getName())) {
//				gameSession = cookie.getValue();
//				break;
//			}
		}

		if (gameSession == null || gameSession.equals("")) {
			return null;
		} else {
			try {
				SessionSegment sseg = SessionSegment.decoder(gameSession);
				if (System.currentTimeMillis() - (long) sseg.getCreateTime() * 1000 > SESSION_KEEP_TIME) {
					return null;
				} else {
					Player player = ObjectAccessor.getPlayer(sseg.getPlayerId());
					if (player == null) {
						log.info("[PLAYER] Illegal Session! playerId:{} not in ObjectAccessor", sseg.getPlayerId());
						return null;
						// PlayerService playerService =
						// Platform.getAppContext().get(PlayerService.class);
						// player =
						// playerService.loadPlayer(sseg.getPlayerId());
						// if (player == null) {
						// log.info("[PLAYER] Illegal Session! player maybe deleted, playerId:{} SISSION[{}]",
						// sseg.getPlayerId(), gameSession);
						// return null;
						// }
						// ObjectAccessor.addGameObject(player);
					} else {
//						if (player.getSession() == null) {
//							log.info("[PLAYER] Illegal Session! playerId:{} Object not have session", sseg.getPlayerId());
//							//player.setSession(new HSession(sseg, player, Utils.getIp(request)));
//							return null;
//						} else {
							//把player里的lastupdateplayertime从新赋值给session解决10分钟必把用户踢下线的bug
							if(player.getSession() != null && player.getSession().getSessionSegment() != null){
								sseg.setLastUpatePlayerTime(player.getSession().getSessionSegment().getLastUpatePlayerTime());
							}
							HSession newSession = new HSession(sseg, player, Utils.getIp(request));
							boolean needUpdatePlayer = newSession.active();
							player.setSession(newSession);
							if (needUpdatePlayer) {
								updatePlayerSession(player);
							}
							return newSession;
						//}
					}
				}
			} catch (Throwable e) {
				log.error("Throwable", e);
				log.info("[PLAYER] Illegal Session Throwable! [{}]", gameSession);
				return null;
			}
		}
	}

	/**
	 * session 的刷新策略: 1. 时间到了就刷新 2. 数量累计到了就刷新
	 */
	public boolean update() {
		if (Time.currTime - lastReflashSessionTime > SYNC_WORLD_SESSION_SLIP_TIME) {
			boolean reflashRet = reflashSessionTable();
			if(reflashRet) {
				lastReflashSessionTime = Time.currTime;
			}
		}
		return true;
	}

	/**
	 * 本地刷新session table
	 */
	private boolean reflashSessionTable() {
		final int splitTime = (int) ((Time.currTime - SESSION_KEEP_TIME) / 1000);
		long t1 = System.nanoTime();

		boolean reflashdRet = true;
		reflashdRet = reflashOnlinePlayers(onlinePlayersLock_0, onlinePlayers_0, splitTime);
		reflashdRet = reflashOnlinePlayers(onlinePlayersLock_1, onlinePlayers_1, splitTime);
		reflashdRet = reflashOnlinePlayers(onlinePlayersLock_2, onlinePlayers_2, splitTime);
		reflashdRet = reflashOnlinePlayers(onlinePlayersLock_3, onlinePlayers_3, splitTime);
		reflashdRet = reflashOnlinePlayers(onlinePlayersLock_4, onlinePlayers_4, splitTime);
		reflashdRet = reflashOnlinePlayers(onlinePlayersLock_5, onlinePlayers_5, splitTime);
		reflashdRet = reflashOnlinePlayers(onlinePlayersLock_6, onlinePlayers_6, splitTime);
		reflashdRet = reflashOnlinePlayers(onlinePlayersLock_7, onlinePlayers_7, splitTime);
		reflashdRet = reflashOnlinePlayers(onlinePlayersLock_8, onlinePlayers_8, splitTime);
		reflashdRet = reflashOnlinePlayers(onlinePlayersLock_9, onlinePlayers_9, splitTime);

		long t2 = System.nanoTime();
		long t = (t2 - t1) / 1000000L;
		// log.info("[session] sync use time[{}ms]", t);
		if (Platform.worldServer() == null) {
			removeResetSession(resetSessionListLock_0, resetSessionList_0, splitTime);
			removeResetSession(resetSessionListLock_1, resetSessionList_1, splitTime);
			removeResetSession(resetSessionListLock_2, resetSessionList_2, splitTime);
			removeResetSession(resetSessionListLock_3, resetSessionList_3, splitTime);
			removeResetSession(resetSessionListLock_4, resetSessionList_4, splitTime);
			removeResetSession(resetSessionListLock_5, resetSessionList_5, splitTime);
			removeResetSession(resetSessionListLock_6, resetSessionList_6, splitTime);
			removeResetSession(resetSessionListLock_7, resetSessionList_7, splitTime);
			removeResetSession(resetSessionListLock_8, resetSessionList_8, splitTime);
			removeResetSession(resetSessionListLock_9, resetSessionList_9, splitTime);
		} else {
			TcpPacket packet = new TcpPacket(OpCode.SESSION_CHANGED_CLIENT);
			packet.put(Platform.getServerId());
			sendResetSessionList(packet, resetSessionListLock_0, resetSessionList_0);
			sendResetSessionList(packet, resetSessionListLock_1, resetSessionList_1);
			sendResetSessionList(packet, resetSessionListLock_2, resetSessionList_2);
			sendResetSessionList(packet, resetSessionListLock_3, resetSessionList_3);
			sendResetSessionList(packet, resetSessionListLock_4, resetSessionList_4);
			sendResetSessionList(packet, resetSessionListLock_5, resetSessionList_5);
			sendResetSessionList(packet, resetSessionListLock_6, resetSessionList_6);
			sendResetSessionList(packet, resetSessionListLock_7, resetSessionList_7);
			sendResetSessionList(packet, resetSessionListLock_8, resetSessionList_8);
			sendResetSessionList(packet, resetSessionListLock_9, resetSessionList_9);
			Platform.worldServer().send(packet);
		}
		long t3 = System.nanoTime();
		t = (t3 - t2) / 1000000L;
		// log.info("[session] send to world time[{}s]", t);
		return reflashdRet;
	}

	private void sendResetSessionList(final TcpPacket packet, ReadWriteLock resetSessionListLock, TIntIntMap resetSessionList) {
		Lock writeOnlinePlayersLock = resetSessionListLock.writeLock();
		writeOnlinePlayersLock.lock();
		try {
			packet.putInt(resetSessionList.size());
			resetSessionList.forEachEntry(new TIntIntProcedure() {
				@Override
				public boolean execute(int playerId, int activeSecond) {
					packet.putInt(playerId);
					packet.putInt(activeSecond);
					return true;
				}
			});
			resetSessionList.clear();
		} finally {
			writeOnlinePlayersLock.unlock();
		}
	}

	private void removeResetSession(ReadWriteLock resetSessionListLock, final TIntIntMap resetSessionList, final int splitTime) {
		Lock writeOnlinePlayersLock = resetSessionListLock.writeLock();
		writeOnlinePlayersLock.lock();
		try {
			resetSessionList.forEachEntry(new TIntIntProcedure() {
				@Override
				public boolean execute(int playerId, int activeSecond) {
					if (activeSecond < splitTime) {
						resetSessionList.remove(playerId);
					}
					return true;
				}
			});
		} finally {
			writeOnlinePlayersLock.unlock();
		}
	}


	/**
	 * 每一个session表中多只处理最多300个离线者. 如果本次未处理完则在下次update中再次处理
	 * 如果处理完成则会在下一个间隔时间(10s)后处理. 
	 * @param onlinePlayersLock
	 * @param onlinePlayers
	 * @param splitTime
	 * @return
	 */
	private boolean reflashOnlinePlayers(ReadWriteLock onlinePlayersLock, final TIntIntMap onlinePlayers, final int splitTime) {
		Lock writeOnlinePlayersLock = onlinePlayersLock.writeLock();
		writeOnlinePlayersLock.lock();
		try {
			boolean overLoop = onlinePlayers.forEachEntry(new TIntIntProcedure() {
				int reflashdPlayerNum = 0;
				int exPlayerNum = 0;
				@Override
				public boolean execute(int playerId, int activeSecond) {
					if (activeSecond < splitTime) {
						onlinePlayers.remove(playerId);
						// player logout;
						Player player = ObjectAccessor.getPlayer(playerId);
						if (player != null) {
							if(player.getLoginServerId() != -1){
								Platform.getEventManager().putEvent(new Event(GameEvent.EVENT_PLAYER_LOGOUTED, player));
							}
//							Platform.getEventManager().putEvent(new Event(GameEvent.EVENT_SESSION_TIME_OUT, player));
						}
						player = null;
						reflashdPlayerNum++;
						if (reflashdPlayerNum > REFALSHD_PLAYER_LOOP_SEG_NUM) {
							log.info("[online players] reflashednum:{}, exNum:{}, totle:{} ", new Object[]{reflashdPlayerNum, exPlayerNum, onlinePlayers.size()});
							return false;
						}
					}else {
						exPlayerNum++;
					}
					return true;
				}
			});
			if (overLoop) {
				return true;
			} else {
				return false;
			}
		} finally {
			writeOnlinePlayersLock.unlock();
		}
	}

	/**
	 * 更新player的session 登录成功时，或者当前在线用户再次请求，并且超过三分钟时（只有两个地方调用）
	 * @param player
	 */
	public static void updatePlayerSession(final Player player) {
		int playerKey = player.getInstanceId() % 10;
		switch (playerKey) {
		case 0:
			putPlayer(onlinePlayersLock_0, onlinePlayers_0, player, resetSessionList_0);
			log.info("[sessionTable] updatePlayerSession[{}] playerId:{}", 0, player.getInstanceId());
			break;
		case 1:
			putPlayer(onlinePlayersLock_1, onlinePlayers_1, player, resetSessionList_1);
			log.info("[sessionTable] updatePlayerSession[{}] playerId:{}", 1, player.getInstanceId());
			break;
		case 2:
			putPlayer(onlinePlayersLock_2, onlinePlayers_2, player, resetSessionList_2);
			log.info("[sessionTable] updatePlayerSession[{}] playerId:{}", 2, player.getInstanceId());
			break;
		case 3:
			putPlayer(onlinePlayersLock_3, onlinePlayers_3, player, resetSessionList_3);
			log.info("[sessionTable] updatePlayerSession[{}] playerId:{}", 3, player.getInstanceId());
			break;
		case 4:
			putPlayer(onlinePlayersLock_4, onlinePlayers_4, player, resetSessionList_4);
			log.info("[sessionTable] updatePlayerSession[{}] playerId:{}", 4, player.getInstanceId());
			break;
		case 5:
			putPlayer(onlinePlayersLock_5, onlinePlayers_5, player, resetSessionList_5);
			log.info("[sessionTable] updatePlayerSession[{}] playerId:{}", 5, player.getInstanceId());
			break;
		case 6:
			putPlayer(onlinePlayersLock_6, onlinePlayers_6, player, resetSessionList_6);
			log.info("[sessionTable] updatePlayerSession[{}] playerId:{}", 6, player.getInstanceId());
			break;
		case 7:
			putPlayer(onlinePlayersLock_7, onlinePlayers_7, player, resetSessionList_7);
			log.info("[sessionTable] updatePlayerSession[{}] playerId:{}", 7, player.getInstanceId());
			break;
		case 8:
			putPlayer(onlinePlayersLock_8, onlinePlayers_8, player, resetSessionList_8);
			log.info("[sessionTable] updatePlayerSession[{}] playerId:{}", 8, player.getInstanceId());
			break;
		case 9:
			putPlayer(onlinePlayersLock_9, onlinePlayers_9, player, resetSessionList_9);
			log.info("[sessionTable] updatePlayerSession[{}] playerId:{}", 9, player.getInstanceId());
			break;

		default:
			log.info("[session] updatePlayerSession noHandler[{}]!!", player.getInstanceId());
			break;
		}

		// resetSessionList.add(player);

		// // FIXME 每登录一个player就向世界服务器发一个信息, 这是临时测试用的
		// if (Platform.worldServer() != null) {
		// TcpPacket pt = new TcpPacket(OpCode.PLAYER_LOGIN_CLIENT);
		// pt.put(Platform.getServerId());
		// pt.putInt(player.getInstanceId());
		// Platform.worldServer().send(pt);
		// }
	}
	/**
	 * 只有updatePlayerSession调用
	 * @param onlinePlayersLock
	 * @param onlinePlayers
	 * @param player
	 * @param resetSessionList
	 */
	private static void putPlayer(ReadWriteLock onlinePlayersLock, TIntIntMap onlinePlayers, final Player player,
			TIntIntMap resetSessionList) {
//		Lock writeOnlinePlayersLock = onlinePlayersLock.writeLock();
//		writeOnlinePlayersLock.lock();
//		try {
			onlinePlayers.put(player.getInstanceId(), player.getSession().getSessionTime());
			resetSessionList.put(player.getInstanceId(), player.getSession().getSessionTime());
//		} finally {
//			writeOnlinePlayersLock.unlock();
//		}
	}

	@Override
	public String getId() {
		return "SessionManager";
	}

	@Override
	public void startup() throws Exception {
		Platform.getUpdater().addAsyncUpdatable(this);
	}

	@Override
	public void shutdown() throws Exception {
	}

	/**
	 * 集群间同步session table
	 * 
	 * @return
	 */
	public boolean syncSessionTable() {
		boolean ret = true;
		worldOnlineUserTemp = 0;
		ret &= syncSessionTableSeagment(0, onlinePlayersLock_0, onlinePlayers_0, resetSessionList_0);
		ret &= syncSessionTableSeagment(1, onlinePlayersLock_1, onlinePlayers_1, resetSessionList_1);
		ret &= syncSessionTableSeagment(2, onlinePlayersLock_2, onlinePlayers_2, resetSessionList_2);
		ret &= syncSessionTableSeagment(3, onlinePlayersLock_3, onlinePlayers_3, resetSessionList_3);
		ret &= syncSessionTableSeagment(4, onlinePlayersLock_4, onlinePlayers_4, resetSessionList_4);
		ret &= syncSessionTableSeagment(5, onlinePlayersLock_5, onlinePlayers_5, resetSessionList_5);
		ret &= syncSessionTableSeagment(6, onlinePlayersLock_6, onlinePlayers_6, resetSessionList_6);
		ret &= syncSessionTableSeagment(7, onlinePlayersLock_7, onlinePlayers_7, resetSessionList_7);
		ret &= syncSessionTableSeagment(8, onlinePlayersLock_8, onlinePlayers_8, resetSessionList_8);
		ret &= syncSessionTableSeagment(9, onlinePlayersLock_9, onlinePlayers_9, resetSessionList_9);
		worldOnlineUser = worldOnlineUserTemp;
		return ret;
	}

	private boolean syncSessionTableSeagment(int index, ReadWriteLock onlinePlayersLock, TIntIntMap onlinePlayers,
			TIntIntMap resetSessionList) {
		// log.info("[sessionTable] syncSessionTableSeagment[{}]", index);
		String dataKey = DataKeys.sessionTableKey(index);
		Data data = Platform.dataCenter().getData(dataKey);
		if (data != null) {
			final TIntIntMap tSessionTable = (TIntIntMap) data.value;
			// Player[] addPlayers = null;
			// addPlayers = resetSessionList.toArray(new Player[0]);
			// for (int i = 0; i < addPlayers.length; i++) {
			// if (addPlayers[i].getInstanceId() % 10 == index) {
			// tSessionTable.put(addPlayers[i].getInstanceId(),
			// addPlayers[i].getSession().getSessionTime());
			// }
			// }
			Lock writeOnlinePlayersLock = onlinePlayersLock.writeLock();
			writeOnlinePlayersLock.lock();
			try {
				resetSessionList.forEachEntry(new TIntIntProcedure() {
					@Override
					public boolean execute(int playerId, int time) {
						tSessionTable.put(playerId, time);
						return true;
					}
				});
				onlinePlayers = tSessionTable;
			} finally {
				writeOnlinePlayersLock.unlock();
			}
			if (onlinePlayers.size() > 0) {
				worldOnlineUserTemp +=  onlinePlayers.size();
//				log.info("[session] session table[{}] sync[{}]", index, onlinePlayers.size());
			}
			return true;
		} else {
			return false;
		}
	}

	public static class SessionSegment implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private static final int BIT_TOY = 5;
		int playerId;
		int createTime;
		String sessionId;
		int lastUpatePlayerTime;

		SessionSegment(int pid, int cTime, String sid) {
			playerId = pid;
			createTime = cTime;
			sessionId = sid;
			lastUpatePlayerTime = cTime;
		}

		public int getPlayerId() {
			return playerId;
		}

		public int getCreateTime() {
			return createTime;
		}

		public int getLastUpatePlayerTime() {
			return lastUpatePlayerTime;
		}

		public void setLastUpatePlayerTime(int lastUpatePlayerTime) {
			this.lastUpatePlayerTime = lastUpatePlayerTime;
		}

		public void setCreateTime(int createTime) {
			this.createTime = createTime;
		}

		/**
		 * 只有登录成功时，调用此方法（没有其他地方调用这个方法了）
		 * @param pid
		 * @return SessionSegment
		 */
		public static SessionSegment encoder(int pid) {
			int time = (int) (System.currentTimeMillis() / 1000);
			return new SessionSegment(pid, time, encrypt(pid, time));
		}

		/**
		 * 加密 pid = [pid_1,pid_2]<br>
		 * [pid_1,time,pid_2]
		 */
		private static String encrypt(int pid, long time) {
			long pid1 = pid >> BIT_TOY;
			int pid2 = pid & ~(0xffffffff << BIT_TOY);
			long scode = pid1 << (32 + BIT_TOY) | (time << BIT_TOY) | pid2;

			char[] rets = Long.toHexString(scode).toCharArray();
			// char[] timeChars = Long.toOctalString(time).toCharArray();
			for (int i = 0; i < rets.length; i++) {
				rets[i] = (char) (3 * BIT_TOY + rets[i]);
			}
			// for (int i = 0; i < timeChars.length; i++) {
			// timeChars[i] = (char) ('b' + timeChars[i]);
			// }
			// char[] ret = new char[rets.length + timeChars.length];
			// System.arraycopy(timeChars, 0, ret, 0, timeChars.length);
			// System.arraycopy(rets, 0, ret, timeChars.length, rets.length);
			return new String(rets);
		}

		/**
		 * 解密
		 * 当前在线用户，请求的时候调用（只有一个地方调用）
		 * @param sessionId
		 * @return SessionSegment
		 */
		public static SessionSegment decoder(String sessionId) {
			char[] schars = sessionId.toCharArray();
			for (int i = 0; i < schars.length; i++) {
				schars[i] = (char) (schars[i] - 3 * BIT_TOY);
			}
			long scode = Long.parseLong(new String(schars), 16);

			int time = (int) (scode >> BIT_TOY);
			int pid1 = (int) (scode >> (BIT_TOY + 32));
			int pid2 = (int) scode & ~(0xffffffff << BIT_TOY);
			int pid = (pid1 << BIT_TOY) | pid2;

			return new SessionSegment(pid, time, sessionId);
		}
		/**
		 * 重置创建时间，生成sessionId 登出时，或者当前在线用户，请求的时候 Player里面有session时调用（两个地方调用）
		 * @param time
		 */
		public void resetCreateTime(int time) {
			createTime = time;
			sessionId = encrypt(this.playerId, time);
		}
	}
	/**
	 * 把最新的session存入cookie
	 * @param session
	 * @param response
	 */
	public static void activeSession(HSession session, HttpServletResponse response) {
		String sid = session.getSessionId();
		Cookie cookie = null;
		if(Platform.getConfiguration().getInt("is_pressure_test") == 1){
			Player player = (Player) session.client();
			if(player != null){
				cookie = new Cookie(GAME_SESSIONID+player.getAccount().getId(), sid);
			}
		}else{
			cookie = new Cookie(GAME_SESSIONID, sid);
		}
		if(cookie != null){
			response.addCookie(cookie);
		}
	}

}
