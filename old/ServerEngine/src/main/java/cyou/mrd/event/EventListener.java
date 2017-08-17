package cyou.mrd.event;


/**
 * 服务事件监听接口  一个服务如果想监听事件必须实现此接口
 * @author pmeng
 */
public interface EventListener {
	
	public int[] getEventTypes();
	
	public void handleEvent(Event event);
}
