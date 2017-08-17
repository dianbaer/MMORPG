package ak.building;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import net.sf.json.JSONArray;

public class BuildingList implements Serializable, Cloneable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ArrayList<Building> buildingList;

	public BuildingList(int total) {
		buildingList = new ArrayList<Building>(total);
	}
	
	public byte[] toDbData() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		int count = buildingList.size();
		dos.writeInt(count);
		for (int i = 0; i < count; i++) {
//			Building building = buildingList.get(i);
//			dos.writeByte(building.getVersion());
//			dos.writeInt(building.getInstanceId());//del
//			dos.writeShort(building.getTemplateId());
//			dos.writeByte(building.getX());
//			dos.writeByte(building.getY());
//			
//			dos.writeShort(building.getAssembleId());
//			dos.writeInt(building.getHavestTimes());//byte
//			dos.writeBoolean(building.isHarvest());
//			dos.writeByte(building.getFlip());
//			dos.writeInt(building.getInstanceId());//del
			Building building = buildingList.get(i);
			dos.writeByte(building.getVersion());
//			dos.writeInt(building.getInstanceId());//del
			dos.writeInt(building.getTemplateId());
			dos.writeInt(building.getX());
			dos.writeInt(building.getY());
			
			dos.writeInt(building.getAssembleId());
			dos.writeInt(building.getHavestTimes());//byte
			dos.writeBoolean(building.isHarvest());
			dos.writeInt(building.getFlip());
//			dos.writeInt(building.getInstanceId());//del
			
			dos.writeInt(building.getInstanceId());
			dos.writeInt(building.getState());
			dos.writeInt(building.getStateTime());
			dos.writeInt(building.getTechHarvst());
			dos.writeDouble(building.getTechTime());
			dos.writeInt(building.getClickFarmID());
			dos.writeInt(building.getmFarmSelfId());
			dos.writeInt(building.getClickChopWoodID());
			dos.writeInt(building.getClickOreID());
			dos.writeInt(building.getStartTimer());
			dos.writeInt(building.getTechId());
			dos.writeInt(building.getComplete());
			dos.writeInt(building.getmChopWoodId());
			dos.writeDouble(building.getmProduceWoodTime());
			dos.writeInt(building.getmOreId());
			dos.writeDouble(building.getmProduceOreTime());
		}
		return baos.toByteArray();
	}

	public static BuildingList parse(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bais);

		int total = dis.readInt();
		BuildingList ret = new BuildingList(total);
		for (int i = 0; i < total; i++) {
			byte version = dis.readByte();
			if (version == Building.BUILDING_VERSION_TREE) {
//				int instanceId = dis.readInt();
//				int templateId = dis.readShort();
//				int x = dis.readByte();
//				int y = dis.readByte();
//				Building building = new Building(instanceId, templateId, x, y);
//				building.setAssembleId(dis.readShort());
//				building.setHavestTimes(dis.readInt());
//				building.setHarvest(dis.readBoolean());
//				building.setFlip(dis.readByte());
//				building.setInstanceId(dis.readInt());
//				ret.add(building);
			}else if(version == Building.BUILDING_VERSION_NO_INSTANCEID) {
				int templateId = dis.readInt();
				int x = dis.readInt();
				int y = dis.readInt();
				Building building = new Building(templateId, x, y);
				building.setAssembleId(dis.readInt());
				building.setHavestTimes(dis.readInt());
				building.setHarvest(dis.readBoolean());
				building.setFlip(dis.readInt());
				building.setInstanceId(dis.readInt());
				building.setState(dis.readInt());
				building.setStateTime(dis.readInt());
				building.setTechHarvst(dis.readInt());
				building.setTechTime(dis.readDouble());
				building.setClickFarmID(dis.readInt());
				building.setmFarmSelfId(dis.readInt());
				building.setClickChopWoodID(dis.readInt());
				building.setClickOreID(dis.readInt());
				building.setStartTimer(dis.readInt());
				building.setTechId(dis.readInt());
				building.setComplete(dis.readInt());
				building.setmChopWoodId(dis.readInt());
				building.setmProduceWoodTime(dis.readDouble());
				building.setmOreId(dis.readInt());
				building.setmProduceOreTime(dis.readDouble());
				ret.add(building);
			}
		}
		return ret;
	}
	
	public void add(Building building) {
		buildingList.add(building);
	}

	public String toJsonString() {
		JSONArray array = new JSONArray();
		for(Building building : buildingList) {
			array.add(building.toJSONObject());
		}
		return array.toString();
	}
	
	public JSONArray toJsonArray() {
		JSONArray array = new JSONArray();
		for(Building building : buildingList) {
			array.add(building.toJSONObject());
		}
		return array;
	}

	public int size() {
		return buildingList.size();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	
}
