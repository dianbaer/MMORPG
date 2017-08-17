package cyou.akworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tcp.SceneData;
import cyou.mrd.io.tcp.ClientSession;
import cyou.mrd.io.tcp.TcpClient;
import cyou.mrd.io.tcp.TcpPacket;

public class WebServer extends ActivityThing implements TcpClient {
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
	
	
	public int getToSceneId(){
		return _toSceneId;
	}
	public void setToSceneId(int sceneId){
		_toSceneId = sceneId;
	}

	public void addTcpPacket(TcpPacket packet){
		SceneData sceneDataKeep = sceneData;
		
		if(sceneDataKeep == null){
			packet.clear();
			return;
		}
		//这里如果玩家切换了场景也没关系，那个包后续就不处理了
		packet.setClient(this);
		try {
			sceneDataKeep.messageQueue.put(packet);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	//是否可以使用这个技能
	public boolean isUseSkill(int skillId){
		long nowTime = System.currentTimeMillis();
		if(upAttackTime != 0 && upAttackTime+attackSpeed>nowTime){
			return false;
		}
		return true;
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
		
		super.clear();
	}
	public void save(){
		
	}
	
}
