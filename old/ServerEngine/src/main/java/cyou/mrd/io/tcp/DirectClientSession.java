package cyou.mrd.io.tcp;

import java.net.InetSocketAddress;

import org.apache.mina.core.session.IoSession;

import cyou.mrd.packethandler.TcpPacketHandler;

public class DirectClientSession extends AbstractClientSession {
	protected String ip;

	public DirectClientSession(ClientSessionService service, IoSession session, TcpPacketHandler handler) {
		super(service, session, handler);
		InetSocketAddress addr = (InetSocketAddress) session.getRemoteAddress();
		ip = addr.getAddress().getHostAddress();
	}
	/**
	 * 取客户端IP地址
	 */
	public String getClientIP() {
		return ip;
	}
}
