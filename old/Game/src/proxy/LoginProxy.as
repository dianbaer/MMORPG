package proxy
{
	import net.CodeEvent;
	import net.TcpPacket;
	
	import org.puremvc.as3.patterns.proxy.Proxy;
	
	public class LoginProxy extends Proxy
	{
		public static const NAME:String = "LoginProxy";
		private var socketProxy:SocketProxy;
		
		public function LoginProxy()
		{
			super(NAME);
			socketProxy = facade.retrieveProxy(SocketProxy.NAME) as SocketProxy;
			socketProxy.socketClient.addEventListener(CodeEvent.CODE31,changeServer);
			socketProxy.socketClient.addEventListener(CodeEvent.CODE33,againConnectServer);
			
		}
		public function login():void{
			var packet:TcpPacket = TcpPacket.fromPool(CodeEvent.CODE1);
			packet.writeInt(GlobalData.chooseId);
			packet.writeInt(GlobalData.role["id"]);
			packet.writeInt(GlobalData.chooseCamp["id"]);
			packet.writeInt(GlobalData.sceneId);
			socketProxy.socketClient.send(packet);
		}
		//更换服务器
		private function changeServer(event:CodeEvent):void{
			var url:String = event.data.readUTF();
			var port:int = event.data.readInt();
			var sceneId:int = event.data.readInt();
			GlobalData.ip = url;
			GlobalData.port = port;
			GlobalData.sceneId = sceneId;
			changeServerReturn();
		}
		public function changeServerReturn():void{
			var packet:TcpPacket = TcpPacket.fromPool(CodeEvent.CODE32);
			socketProxy.socketClient.send(packet);
		}
		private function againConnectServer(event:CodeEvent):void{
			login();
			trace("服务器通知，再次连接服务器");
		}
		
	}
}