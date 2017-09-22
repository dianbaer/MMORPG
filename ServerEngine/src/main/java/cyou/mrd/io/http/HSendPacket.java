package cyou.mrd.io.http;

import net.sf.json.JSONObject;

public class HSendPacket {

	private JSONObject sendData;
	
	public HSendPacket() {
		this.sendData = new JSONObject();
	}
	
	public void put(String key, int value) {
		sendData.put(key, value);
	}

	public String getData() {
		return sendData.toString();
	}

	public int getInt(String key) {
		return sendData.getInt(key);
	}

	 
}
