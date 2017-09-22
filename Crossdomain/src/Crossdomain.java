import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class Crossdomain {

//安全策略服务   
public void startPolicyServer() throws IOException{   
        IoAcceptor acceptor = new NioSocketAcceptor();   
        acceptor.setHandler(new PolicyServerHandler());   
        acceptor.bind( new InetSocketAddress(843));   
        System.out.println("安全策略服务侦听端口:843");   
}   

  
  
 //单独的安全策略处理器   
public class PolicyServerHandler extends IoHandlerAdapter {   
    // 22字节+0占1个字节   
    String security_quest = "<policy-file-request/>";   
    // 最后以0结尾   
    String policyStr = "<?xml version=\"1.0\"?>\r\n<cross-domain-policy>\r\n<allow-access-from domain=\"*\" to-ports=\"7005,7006,843\" />\r\n</cross-domain-policy>\r\n\0";   
    private final Logger log = Logger.getLogger(PolicyServerHandler.class.getName());   
       
    public void messageReceived(IoSession session, Object message)   
            throws Exception {   
        IoBuffer processBuf = (IoBuffer) session.getAttribute("processBuf");   
        processBuf.put((IoBuffer)message);   
        processBuf.flip();   
                       
        if(getRequest(processBuf)){   
            byte[] reps = policyStr.getBytes("UTF-8");   
            IoBuffer rb = IoBuffer.allocate(reps.length);   
            rb.put(reps);//也有putString方法   
            rb.flip();   
            session.write(rb);//发回   
            System.out.println("发送了策略文件");  
        }   
    }   
       
    //获得安全请求的字符串   
    private Boolean getRequest(IoBuffer buf){   
        String req = new String(buf.array());   
         if (req.indexOf(security_quest) != -1){   
             return true;   
         }   
         return false;   
    }   
    @Override   
    public void messageSent(IoSession session, Object message) throws Exception {   
        session.close(true);   
    }   
  
    @Override   
    public void sessionClosed(IoSession session) throws Exception {   
        super.sessionClosed(session);   
        session.removeAttribute("processBuf");   
    }   
  
    @Override   
    public void sessionCreated(IoSession session) throws Exception {   
        super.sessionCreated(session);   
        IoBuffer processBuf = IoBuffer.allocate(64);   
        session.setAttribute("processBuf", processBuf);   
    }   
  
}  
}