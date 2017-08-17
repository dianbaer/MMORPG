package cyou.mrd.io.tcp;




import org.apache.mina.core.session.IoSession;

import cyou.mrd.io.Identity;
import cyou.mrd.packethandler.PacketHandler;


/**
 * @brief 此接口代表一个客户端的连接，一旦客户端跟服务器端建立起一个连接，那么相应的会建立一个此接口实现类的实例
 * @note 客户端连接在服务器上的对应体，通过此对应体，服务器端能够接受客户端发送上来的包，并且下发包给客户端，还能知道客户端连接的状态
 * @author mengpeng
 *
 */
public interface ClientSession {
	
	/**
	 * @note 连接的Id号，每个连接的Id号都是不相同的，含义跟文件的句柄有些类似
	 * @return 连接的Id号
	 */
	public int getId();
	
	/**
	 * @note 当前连接是否处于连接状态
	 */
	public boolean isConnected();

	/**
	 * @note 发送包给客户端
	 * 
	 * @param packet 要发送的包
	 */
	public void send(TcpPacket packet);

	/**
	 * @note 关闭连接
	 */
	public void close();

	/**
	 * @note 获取此连接的包处理器
	 */
	public PacketHandler getHandler();

	/**
	 * @note 获取ClientSession对应的Client，Client可以是任何实现Client接口的对象，比如玩家上线以后，Client对象可以返回Player
	 */
	public TcpClient getClient();

	/**
	 * @note 设置当前的Client对象
	 */
	public void setClient(TcpClient client);
	
	/**
	 * @note 更新ClientSession，返回值无意义
	 */
	public boolean update();
	
	public void keepLive();
	
	/**
	 * @note 添加包，一般用在网络层收取到客户端包以后用次方法添加到接受队列中
	 * @param packet 服务器收到的客户端上传的包
	 */
	public void addPacket(TcpPacket packet);
	
	/**
	 * @note 取客户端IP地址
	 */
	public String getClientIP();
	
	/**
	 * @note 检查是否允许登录（如果在线数过多则不允许登录）
	 */
	//public boolean checkOnlineCount(int currentLoginedAccounts);
	
	
	/**
	 * @note 将一个AsyncCall加到ClientSession的call队列中，在下一个cycle中，将会遍历所有加入的call，并且执行call的callFinish方法
	 */
	//public void addAsyncCall(AsyncCall call);
	
	/**
	 * @note 传入一个Identity对象，一般用来在认证成功以后，设置账号对象使用
	 * @throws SecurityException 如果当前ClientSession的状态错误，比如已经处于认证成功状态，抛出SecurityException
	 */
	public void authenticate(Identity identity) throws SecurityException;
	public boolean isAuthenticated();
	
	/**
	 * @note 获取当前的认证对象
	 */
	//public Identity getIdentity();
	/**
	 * 清理
	 */
	public void clear();
	public IoSession getIoSession();
	public boolean getIsClear();
	
}
