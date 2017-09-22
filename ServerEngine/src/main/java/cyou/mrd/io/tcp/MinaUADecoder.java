package cyou.mrd.io.tcp;

import java.io.IOException;


import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import cyou.mrd.util.IoUtil;

public class MinaUADecoder extends ProtocolDecoderAdapter {

	
	private static final String BUFFER = ".UABuffer";
	private static final IoBuffer EMPTY = IoBuffer.allocate(0);
	

	public void decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		boolean useSessionBuffer = false;
		//boolean consumed = false;
		IoBuffer buf = (IoBuffer)session
		.getAttribute(BUFFER);
		if(buf!=null){
//			buf.setAutoExpand(true);
			buf.put(in);
			buf.flip();
			useSessionBuffer = true;
		}else{
			buf = in;
		}
		IoUtil.byteOrder(buf);
		
		for(;;){
			if(buf.remaining()>6){
				int pos = buf.position();
				
				int head1 = buf.get();
				int head2 = buf.get();
				//System.out.println("头："+head1+","+head2);
				if(head1==85&&head2==65){
					int len = buf.getInt();
					//System.out.println("长度："+len);
					if(buf.remaining()>=(len-6)){  //去掉head以及len一共6个字节
						short opCode = buf.getShort();  
						IoBuffer data = EMPTY;
						byte[] bytes = new byte[len - 8];
						buf.get(bytes);
						data = IoBuffer.wrap(bytes);
						TcpPacket packet = new TcpPacket(opCode, data);
						out.write(packet);
						//consumed = true;
					}else{
						buf.position(pos);
						break;
					}
				}else{
					System.out.println(head1+","+head2);
					session.setAttribute(BUFFER,null);
					throw new IOException("UA head error.");
				}
				
				
			}else{
				break;
			}
		}
		if (buf.hasRemaining()) {
//			if(!useSessionBuffer||consumed){
				storeRemainingInSession(buf,session);
//			}
//			if()
//			else{
//				buf.position(buf.limit());
//			}
		}else{
			if(useSessionBuffer)
				session.setAttribute(BUFFER,null);
		}
	}
	
    private void storeRemainingInSession(IoBuffer buf, IoSession session) {
    	IoBuffer remainingBuf = IoBuffer.allocate(buf.capacity());
        remainingBuf.setAutoExpand(true);
        remainingBuf.order(buf.order());
        remainingBuf.put(buf);
        session.setAttribute(BUFFER, remainingBuf);
    }

}
