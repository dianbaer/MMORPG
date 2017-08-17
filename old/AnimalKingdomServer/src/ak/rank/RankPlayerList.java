package ak.rank;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import net.sf.json.JSONArray;

public class RankPlayerList implements Serializable, Cloneable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<RankPlayer> rankPlayerList;

	public RankPlayerList(int total) {
		rankPlayerList = new ArrayList<RankPlayer>(total);
	}
	
	public byte[] toDbData() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		int count = rankPlayerList.size();
		dos.writeInt(count);
		for (int i = 0; i < count; i++) {
			RankPlayer rankPlayer = rankPlayerList.get(i);
			dos.writeInt(rankPlayer.getType());
			dos.writeInt(rankPlayer.getLastRank());
			dos.writeInt(rankPlayer.getNowRank());
			
			dos.writeInt(rankPlayer.getValue());
			dos.writeInt(rankPlayer.getLastUpdateDay());
		}
		return baos.toByteArray();
	}

	public static RankPlayerList parse(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bais);

		int total = dis.readInt();
		RankPlayerList ret = new RankPlayerList(total);
		for (int i = 0; i < total; i++) {
			RankPlayer rankPlayer = new RankPlayer();

			rankPlayer.setType(dis.readInt());
			rankPlayer.setLastRank(dis.readInt());
			rankPlayer.setNowRank(dis.readInt());
			rankPlayer.setValue(dis.readInt());
			rankPlayer.setLastUpdateDay(dis.readInt());
			ret.add(rankPlayer);
			
		}
		return ret;
	}

	public void add(RankPlayer rankPlayer) {
		rankPlayerList.add(rankPlayer);
	}

	public String toJsonString() {
		JSONArray array = new JSONArray();
		for(RankPlayer rankPlayer : rankPlayerList) {
			array.add(rankPlayer.toJSONObject());
		}
		return array.toString();
	}
	
	public JSONArray toJsonArray() {
		JSONArray array = new JSONArray();
		for(RankPlayer rankPlayer : rankPlayerList) {
			array.add(rankPlayer.toJSONObject());
		}
		return array;
	}

	public int size() {
		return rankPlayerList.size();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	
}
