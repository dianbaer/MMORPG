package cyou.akworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.io.tcp.ClientSession;
import cyou.mrd.io.tcp.TcpClient;
import cyou.mrd.io.tcp.TcpPacket;

public class WebServer implements TcpClient {
	private static Logger log = LoggerFactory.getLogger(WebServer.class);
	
	
	protected ClientSession session;

	private int _toSceneId;
	
	/**
	 * 0：存在；1：应该溢出了，2：已经移除了
	 */
	private int state = 0;
	
	public Boolean isLoadOk = false;
	//接收到更换的服务器ip地址
	public Boolean isReceiveChangeServer = false;
	public long loadTime = 0;
	protected int serverId;
	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	
	public int getToSceneId(){
		return _toSceneId;
	}
	public void setToSceneId(int sceneId){
		_toSceneId = sceneId;
	}

	public void addTcpPacket(TcpPacket packet){
		
	}


	
	
	@Override
	public void setSession(ClientSession session) {
		
		this.session = session;
	}

	@Override
	public ClientSession getSession() {
		return session;
	}

	
	
	public void send(TcpPacket packet){
		
		session.send(packet);
	}
	public int getState(){
		return state;
	}
	public void setState(int state){
		this.state = state;
	}
	public void clear(){
		session = null;
	}
	public void save(){
		
	}
	
}
