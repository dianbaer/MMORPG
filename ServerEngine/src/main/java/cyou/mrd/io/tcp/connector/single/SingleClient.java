package cyou.mrd.io.tcp.connector.single;

import cyou.mrd.io.tcp.ClientSession;
import cyou.mrd.io.tcp.TcpClient;
import cyou.mrd.io.tcp.TcpPacket;

public class SingleClient implements TcpClient {
	private ClientSession session;

	@Override
	public void setSession(ClientSession session) {
		this.session = session;
	}

	@Override
	public ClientSession getSession() {
		return session;
	}

	public void send(TcpPacket packet) {
		session.send(packet);
	}
	public void addTcpPacket(TcpPacket packet){
		
	}
	public int getState(){
		return 0 ;
	}
	public void setState(int state){;
	
	}
}