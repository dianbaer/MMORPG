package command
{
	import org.puremvc.as3.interfaces.INotification;
	import org.puremvc.as3.patterns.command.SimpleCommand;
	
	import proxy.LoginProxy;
	import proxy.PlayerProxy;
	
	public class InitProxySimpleCommand extends SimpleCommand
	{
		public function InitProxySimpleCommand()
		{
			super();
		}
		override public function execute(notification:INotification):void
		{
			var pro1:LoginProxy;
			pro1 = facade.retrieveProxy(LoginProxy.NAME) as LoginProxy;
			if(pro1 == null){
				facade.registerProxy(new LoginProxy());
				facade.registerProxy(new PlayerProxy());
				pro1 = facade.retrieveProxy(LoginProxy.NAME) as LoginProxy;
			}
			pro1.login();
		}
	}
}