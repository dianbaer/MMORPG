package cyou.mrd.io.tcp;


import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import cyou.mrd.util.IoUtil;

public class MinaUAEncoder extends ProtocolEncoderAdapter {

	public void encode(IoSession session, Object obj, ProtocolEncoderOutput out)
			throws Exception {
		if(obj instanceof TcpPacket){
			TcpPacket packet = (TcpPacket)obj;
			
			IoBuffer data = packet.getData();
			data.flip();
			int len = 8+data.remaining();
			IoBuffer buf = IoBuffer.allocate(len);
			IoUtil.byteOrder(buf);
			buf.put(TcpPacket.HEAD);
			buf.putInt(len);
			buf.putShort(packet.getOpCode());
			buf.put(data);
			buf.flip();
			out.write(buf);
			
			
		}
	}

}
