package cyou.mrd.util;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.ObjectAccessor;
import cyou.mrd.Platform;
import cyou.mrd.event.Event;
import cyou.mrd.event.GameEvent;
import cyou.mrd.io.http.SessionManager;

/**
 * @author Administrator
 * 
 */
public class Time {

	public static long currTime = System.currentTimeMillis();

	public static final Logger log = LoggerFactory.getLogger(Time.class);

	public static long TIME_TOLERATE_SECOND = Platform.getConfiguration().getInt(ConfigKeys.CLIENT_TOLERATE_SECOND);

	public static int day = 0;

	private static boolean firstUpdate = true;

	public static long tick;

	public static Calendar calendar = Calendar.getInstance();

	public Time() throws Exception {
	}

	public static void resetDay() {
		calendar.setTime(new Date(currTime));
		int newDay = calendar.get(Calendar.DAY_OF_YEAR);
		if (firstUpdate) {
			firstUpdate = false;
			day = newDay;
		} else {
			if (newDay != day) {
				day = newDay;
				Platform.getEventManager().addEvent(new Event(GameEvent.EVENT_CHANGE_DAY));
				log.info("[CHANGEDAY]");
			}
		}
	}

	private static long lastShowOnlineUserTime = 0;

	public static boolean update(long time) {
		if (time - lastShowOnlineUserTime > 60 * 1000) {
			lastShowOnlineUserTime = time;
			//log.info("online user [{}]", ObjectAccessor.size());
			//log.info("world online user [{}]", SessionManager.worldOnlineUser);
		}
		currTime = time;
		resetDay();
		return true;
	}

	public static boolean update_0(long time) {
		currTime = time;
		resetDay();
		return true;
	}

	/**
	 * 取下一天的最早时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getDateNextDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static boolean betweenHour(Date date, int begin, int end) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		return hour >= begin && hour <= end;
	}

	public static boolean checkClientTime(int clientTime) {
		if(Math.abs(currTime - System.currentTimeMillis()) > 5000) {
			currTime = System.currentTimeMillis();
		}
		if (Math.abs(currTime / 1000 - clientTime) < TIME_TOLERATE_SECOND) {
			return true;
		} else {
			return false;
		}
	}

}
