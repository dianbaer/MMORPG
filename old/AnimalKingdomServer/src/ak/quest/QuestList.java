package ak.quest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import net.sf.json.JSONArray;

/**
 * 
 * @author miaoshengli
 */
public class QuestList implements Serializable, Cloneable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Quest> list;

	public QuestList() {
	}

	public QuestList(int size) {
		list = new ArrayList<Quest>(size);
	}

	public void add(Quest quest) {
		list.add(quest);
	}

	public byte[] toDbData() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		int count = this.size();
		dos.writeInt(count);
		for (int i = 0; i < count; i++) {
			Quest quest = this.get(i);
			dos.writeByte(quest.getVersion());
			dos.writeInt(quest.getId());
			dos.writeByte(quest.getState());
			dos.writeByte(quest.getCondition());
			dos.writeInt(quest.getStartTime());
			dos.writeInt(quest.getTimeLong());
			dos.writeInt(quest.getBattleWinTimes());
			dos.writeUTF(quest.getBuilding());
			dos.writeUTF(quest.getItem());
		}
		return baos.toByteArray();
	}

	private Quest get(int index) {
		return list.get(index);
	}


	public static QuestList parse(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bais);

		int total = dis.readInt();
		QuestList ret = new QuestList(total);
		for (int i = 0; i < total; i++) {
			byte version = dis.readByte();
			if (version == Quest.Quest_VERSION_0) {
				int id = dis.readInt();
				int state = dis.readByte();
				int condition = dis.readByte();
				int startTime = dis.readInt();
				int timeLong = dis.readInt();
				int battleWinTimes = dis.readInt();
				String build = dis.readUTF();
				String item = dis.readUTF();

				Quest Quest = new Quest(id, state, condition, startTime, timeLong, battleWinTimes, build, item);
				ret.add(Quest);
			}
		}
		return ret;
	}
	
	public int size() {
		return list.size();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public JSONArray toJsonArray() {
		JSONArray array = new JSONArray();
		for(Quest quest : this.list) {
			array.add(quest.toJson());
		}
		return array;
	}
}
