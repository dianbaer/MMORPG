package cyou.mrd.io;

import cyou.mrd.util.RunTimeMonitor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface Packet {
	public int getopcode();

	public void put(String key, Object value);

	public int getInt(String key);

	public String getString(String key);

	public JSONObject getObject(String key);

	public JSONArray getJSONArray(String string);

	public boolean containsKey(String key);
	
	public String toString();

	public void setRunTimeMonitor(RunTimeMonitor rt);
	public RunTimeMonitor getRunTimeMonitor();

	public long getLong(String key);
	public void setServerName(String name);
	public String getServerName();
	public String getThreadName();

	public void setThreadName(String threadName);

}
