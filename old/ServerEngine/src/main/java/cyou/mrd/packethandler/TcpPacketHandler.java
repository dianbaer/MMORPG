package cyou.mrd.packethandler;

import cyou.mrd.io.tcp.ClientSession;
import cyou.mrd.io.tcp.TcpPacket;

public interface TcpPacketHandler extends PacketHandler{
	
	/**
	 * @note 系统在处理客户端上传包的时候将会调用此方法，传入上传的包以及包所在的客户端Session
	 * @param packet 客户端上传的包
	 * @param session 包所属的客户端Session
	 * @throws Exception 如果处理时出错，可以抛出异常
	 */
	public void handle(TcpPacket packet,ClientSession session) throws Exception;
}
