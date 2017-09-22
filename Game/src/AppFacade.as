package
{
	import flash.display.Sprite;
	
	import command.InitProxySimpleCommand;
	import command.SocketSimpleCommand;
	
	import org.puremvc.as3.patterns.facade.Facade;
	
	import proxy.SocketProxy;
	
	import view.MainMediator;
	
	public class AppFacade extends Facade
	{
		public function AppFacade()
		{
			super();
		}
		public static function getInstance():AppFacade {
			if (instance == null) instance = new AppFacade( );
			return instance as AppFacade;
		}
		public function startUp(app : Sprite):void{
			registerMediator(new MainMediator(app));
			sendNotification(NotiConst.CONNECT_SOCKET_S);
		}
		
		override protected function initializeController():void
		{
			
			super.initializeController();
			registerCommand(NotiConst.CONNECT_SOCKET_S,SocketSimpleCommand);
			registerCommand(NotiConst.CONNECT_SOCKET_R,InitProxySimpleCommand);
		}
		
		override protected function initializeModel():void
		{
			
			super.initializeModel();
			registerProxy(new SocketProxy());
			
		}
		
		override protected function initializeView():void
		{
			
			super.initializeView();
		}
		
		
	}
}