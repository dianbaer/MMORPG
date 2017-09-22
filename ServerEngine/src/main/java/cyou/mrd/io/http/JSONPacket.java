package cyou.mrd.io.http;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cyou.mrd.io.Packet;
import cyou.mrd.util.RunTimeMonitor;

public class JSONPacket implements Packet {
	private JSONObject data;
	private int opcode;
	private String serverName;
	private String threadName;

	public JSONPacket(int opcode, JSONObject data) {
		this.opcode = opcode;
		this.data = data;
	}

	public JSONPacket(int opcode) {
		this.opcode = opcode;
		data = new JSONObject();
	}

	public int getopcode() {
		return this.opcode;
	}

	public JSONObject getData() {
		return this.data;
	}

	public void put(String key, Object value) {
		data.put(key, value);
	}

	@Override
	public int getInt(String key) {
		return data.getInt(key);
	}

	@Override
	public String getString(String key) {
		return data.getString(key);
	}

	public long getLong(String key) {
		return data.getLong(key);
	}

	public void put(String key, double value) {
		this.data.put(key, value);
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public double getDouble(String key) {
		return data.getDouble(key);
	}

	@Override
	public JSONObject getObject(String key) {
		return data.getJSONObject(key);
	}

	@Override
	public JSONArray getJSONArray(String key) {
		return data.getJSONArray(key);
	}

	@Override
	public boolean containsKey(String key) {
		return data.containsKey(key);
	}

	public String toString() {
		return data.toString();
	}

	public RunTimeMonitor rt;

	public void setRunTimeMonitor(RunTimeMonitor rt) {
		this.rt = rt;
	}

	@Override
	public RunTimeMonitor getRunTimeMonitor() {
		return this.rt;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
}
