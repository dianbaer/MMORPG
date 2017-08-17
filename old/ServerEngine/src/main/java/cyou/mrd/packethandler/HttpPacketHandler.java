package cyou.mrd.packethandler;

import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HSession;

public interface HttpPacketHandler extends PacketHandler{
	
	/**
	 * @note 系统在处理客户端上传包的时候将会调用此方法，传入上传的包以及需要下发的包对象
	 * @param JSONPacket 本次会话上传包
	 * @param HSendPacket 本次会话的下发包
	 * @throws Exception 如果处理时出错，可以抛出异常
	 */
	public void handle(Packet packet, HSession session) throws Exception;
}
