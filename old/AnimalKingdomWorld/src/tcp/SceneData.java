package tcp;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import net.sf.json.JSONObject;

import _astar.Grid;

import cyou.akworld.Monster;
import cyou.akworld.Thing;
import cyou.akworld.WebServer;
import cyou.mrd.io.tcp.TcpPacket;

public class SceneData {
	public int sceneId;
	//1这个场景需要区域广播，2不需要区域广播
	public int type;
	public int cellWidth;
	public JSONObject scene;
	public int mapWidth;
	public int mapHeight;
	//玩家数组
	public ArrayList<WebServer> userList = new ArrayList<WebServer>();
	//其他事物数组
	public ArrayList<Thing> otherList = new ArrayList<Thing>();
	public ArrayList<Monster> npcList = new ArrayList<Monster>();
	public ArrayList<Monster> deadNpcList = new ArrayList<Monster>();
	//距离映射表id_id=distance这种方式
	public HashMap<String, Double> distanceMap = new HashMap<String, Double>();
	public LinkedBlockingQueue<TcpPacket> messageQueue = new LinkedBlockingQueue<TcpPacket>();
	private ArrayList<TcpPacket> handleMessage = new ArrayList<TcpPacket>();
	
	//将要添加的玩家
	public LinkedBlockingQueue<WebServer> addUserList = new LinkedBlockingQueue<WebServer>();
	//将要移除的玩家
	public LinkedBlockingQueue<WebServer> removeUserList = new LinkedBlockingQueue<WebServer>();
	//已经移除的玩家
	public LinkedBlockingQueue<WebServer> removedUserList = new LinkedBlockingQueue<WebServer>();
	
	public Grid grid;
	public int minX = 0;
	public int minY = 0;
	public int maxX = 0;
	public int maxY = 0;
	public int totemId = -20000;
	public void countMaxAndMin(Point point){
		if(minX > point.x){
			minX = point.x;
		}
		if(minY > point.y){
			minY = point.y;
		}
		if(maxX < point.x){
			maxX = point.x;
		}
		if(maxY < point.y){
			maxY = point.y;
		}
	}
	/**
	 * 获取本次轮训要处理的信息
	 * @return
	 */
	public ArrayList<TcpPacket> getHandleMessage(){
		handleMessage.clear();
		messageQueue.drainTo(handleMessage);
		return handleMessage;
	}
}
