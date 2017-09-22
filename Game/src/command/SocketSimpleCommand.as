package command
{
	import org.puremvc.as3.interfaces.INotification;
	import org.puremvc.as3.patterns.command.SimpleCommand;
	
	import proxy.SocketProxy;
	
	public class SocketSimpleCommand extends SimpleCommand
	{
		public function SocketSimpleCommand()
		{
			super();
		}
		
		override public function execute(notification:INotification):void
		{
			
			var pro:SocketProxy = facade.retrieveProxy(SocketProxy.NAME) as SocketProxy;
			pro.connect();
		}
		
	}
}