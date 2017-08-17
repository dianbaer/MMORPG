package cyou.mrd.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.updater.Updatable;

/**
 * 服务事件的管理 负责监听者的管理 和 服务事件的分发
 * 
 * @author mengpeng
 */
public class EventManager implements Updatable {
	private static final Logger log = LoggerFactory.getLogger(EventManager.class);
	/**
	 * 每次主轮训最大执行事件的数量
	 */
	private static final int EVENT_UPDATE_MAX_COUNT = 1000;
	
	protected Map<Integer, CopyOnWriteArrayList<EventListener>> listeners = new HashMap<Integer, CopyOnWriteArrayList<EventListener>>();
	protected Queue<Event> events = new ConcurrentLinkedQueue<Event>();
	/**
	 * 只是初始化时主线程调用
	 * @param listener
	 */
	public void registerListener(EventListener listener) {
		//log.info("[EventManager] registerListener(listener :{})", listener.getClass().getSimpleName());
		int[] types = listener.getEventTypes();
		for (int type : types) {
			CopyOnWriteArrayList<EventListener> lls = listeners.get(type);
			if (lls == null) {
				lls = new CopyOnWriteArrayList<EventListener>();
				listeners.put(type, lls);
			}
			lls.add(listener);
		}
	}
	/**
	 * 目前没有地方调用
	 * @param listener
	 */
	public void unregisterListener(EventListener listener) {
		//log.info("[EventManager] unregisterListener(listener :{})", listener.getClass().getSimpleName());
		for (List<EventListener> lls : listeners.values()) {
			lls.remove(listener);
		}
	}

	/**
	 * 某些确认不需要接收方就一个事件重复处理的event,请调用此方法
	 * 例如: 通知玩家保存, 
	 * @param event
	 */
	public void putEvent(Event event) {
		//log.info("[EventManager] putEvent(Event type:{})", event.type);
		if(!events.contains(event)) {
			addEvent(event);
		}
		//log.info("[putEvent]event type:{}",event.type);
	}
	/**
	 * 发布事件
	 * @param event
	 */
	public void addEvent(Event event) {
		//log.info("[EventManager] addEvent(Event type :{})", event.type);
		events.add(event);
	}
	/**
	 * 只有主线程调用
	 */
	public boolean update() {
		long t, t1, t2;
		int count = 0;
		while (!events.isEmpty()) {
			count++;
			Event evt = events.remove();
			List<EventListener> lls = listeners.get(evt.type);
			if (lls != null) {
				for (EventListener l : lls) {
					try {
						//log.info("[EventManager] update(handleEvent :{})", evt.type);
						t1 = System.nanoTime();
						l.handleEvent(evt);
						t2 = System.nanoTime();
						t = (t2 - t1) / 1000000L;
						if (t > 30) {
							log.info("[EVENT] handler({}) too long, listener[{},{}ms]",new Object[]{evt.type, l.getClass().getSimpleName(), t});
						}
					} catch (Throwable e) {
						log.error("[EVENT] handler exception:", e);
					}
				}
			}
			if(count > EVENT_UPDATE_MAX_COUNT) {
				break;
			}
		}
		return false;
	}
	/**
	 * 直接执行事件调用的方法（不用等待主线程轮训，直接响应）
	 * @param event
	 */
	public void fireEvent(Event event) {
		List<EventListener> lls = listeners.get(event.type);
		long t, t1, t2;
		if (lls != null) {
			for (EventListener l : lls) {
				try {
					//log.info("[EventManager] fireEvent(type:{})", event.type);
					t1 = System.nanoTime();
					l.handleEvent(event);
					t2 = System.nanoTime();
					t = (t2 - t1) / 1000000L;
					if(t > 100) {
						log.info("[EVENT] handler({}) too long, listener[{},{}ms]",new Object[]{event.type, l.getClass().getSimpleName(), t});
					}
				} catch (Throwable e) {
					log.error("[EVENT] handler exception:", e);
				}
			}
		}
		//log.info("[fireEvent] event type:{}",event.type);
	}
}
