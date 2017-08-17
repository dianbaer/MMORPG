package cyou.mrd.event;

/**
 * 服务之间通信事件
 * @author pmeng
 */
public class Event {
	
	public int type;
	public Object param1;
	public Object param2;
	public Object param3;
	public Object param4;
	
	public Event(int type) {
		this.type = type;
	}
	
	public Event(int type, Object param1) {
		this.type = type;
		this.param1 = param1;
	}

	public Event(int type, Object param1, Object param2) {
		this.type = type;
		this.param1 = param1;
		this.param2 = param2;
	}

	public Event(int type, Object param1, Object param2, Object param3) {
		this.type = type;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
	}
	
	public Event(int type, Object param1, Object param2, Object param3, Object param4){
		this.type = type;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
		this.param4 = param4;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Event)) {
			return false;
		}
		Event o1 = (Event)obj;
		if(o1.type != this.type) {
			return false;
		}
		if(o1.param1 != this.param1){
			return false;
		}
		if(o1.param2 != this.param2){
			return false;
		}
		if(o1.param3 != this.param3){
			return false;
		}
		if(o1.param4 != this.param4){
			return false;
		}
		return true;
	}
}
