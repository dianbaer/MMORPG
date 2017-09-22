package cyou.mrd.io.tcp;

/**
 * tcp连接客户端
 * @author mengpeng
 */
public interface TcpClient {

	public void setSession(ClientSession session);
	
	public ClientSession getSession();
	public void addTcpPacket(TcpPacket tcpPacket);
	public int getState();
	public void setState(int state);
}
