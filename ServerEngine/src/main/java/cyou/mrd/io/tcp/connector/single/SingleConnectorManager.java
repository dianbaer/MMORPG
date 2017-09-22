package cyou.mrd.io.tcp.connector.single;



import org.apache.mina.core.session.IoSession;

import cyou.mrd.io.tcp.ClientSession;
import cyou.mrd.io.tcp.TcpPacket;

public interface SingleConnectorManager {
	public void login(IoSession session);

	public void loginSucess(TcpPacket packet, ClientSession session);
}
