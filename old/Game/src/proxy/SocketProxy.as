package proxy
{
	import net.CodeEvent;
	import net.SocketClient;
	
	import org.puremvc.as3.patterns.proxy.Proxy;
	
	public class SocketProxy extends Proxy
	{
		public static const NAME:String = "SocketProxy";
		public var socketClient:SocketClient;
		public function SocketProxy()
		{
			super(NAME);
			
		}
		public function connect():void{
			if(!socketClient){
				socketClient = new SocketClient();
				socketClient.addEventListener(CodeEvent.CONNECT,onConnect);
				socketClient.addEventListener(CodeEvent.DISCONNECT,onDisConnect);
			}else{
				socketClient.clear();
			}
			socketClient.resetConnect(GlobalData.ip,GlobalData.port);
			//清理上一次换服务器的数据
			GlobalData.ip = null;
			GlobalData.port = 0;
		}
		private function onConnect(event:CodeEvent):void{
			
			sendNotification(NotiConst.CONNECT_SOCKET_R);
		}
		private function onDisConnect(event:CodeEvent):void{
			
			//看看是换服务器，还是被断开离线。
			if(GlobalData.ip != null){
				sendNotification(NotiConst.CONNECT_SOCKET_S);
			}else{
				sendNotification(NotiConst.DISCONNECT_SOCKET);
			}
			
			
		}
	}
}