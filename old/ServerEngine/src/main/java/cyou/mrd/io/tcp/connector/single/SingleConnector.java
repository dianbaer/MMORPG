package cyou.mrd.io.tcp.connector.single;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.io.tcp.MinaUADecoder;
import cyou.mrd.io.tcp.MinaUAEncoder;

public class SingleConnector {

	private static final Logger log = LoggerFactory.getLogger(SingleConnector.class);

	private SocketAddress runtimeAddress;
	private SingleClientSessionHandler singleClientHandler = new SingleClientSessionHandler(this);

	private NioSocketConnector connector;
	

	/**
	 * 初始化连接器 注意, 设置自动重连的参数后, <br>
	 * 不需要显示调用连接方法[SingleConnector.connect()],<br>
	 * 系统会自动连接; 设置了备机的, 系统会在尝试重连次数后选择连接备机,
	 * 
	 * @param url
	 * @param port
	 * @param manager
	 * @param autoReConncet
	 */
	public void initConnector(String url, int port, SingleConnectorManager manager, boolean autoReConncet) {
		runtimeAddress = new InetSocketAddress(url, port);
		
		connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MinaUAEncoder(), new MinaUADecoder()));
		connector.setHandler(singleClientHandler);
		connector.connect(runtimeAddress);
		


		log.info("[SingleConnector] initConnector(url[{}], port[{}], Manager[{}], autoReConnect[{}])", new Object[] { url, port,
				manager.getClass().getSimpleName(), autoReConncet });
	}
}
