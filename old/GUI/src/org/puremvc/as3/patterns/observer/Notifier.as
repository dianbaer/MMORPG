/*
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved.
 Your reuse is governed by the Creative Commons Attribution 3.0 United States License
*/
package org.puremvc.as3.patterns.observer
{
	import org.puremvc.as3.interfaces.*;
	import org.puremvc.as3.patterns.facade.Facade;
	
	/**
	 * A Base <code>INotifier</code> implementation.
	 * 
	 * <P>
	 * <code>MacroCommand, Command, Mediator</code> and <code>Proxy</code> 
	 * all have a need to send <code>Notifications</code>. <P>
	 * <P>
	 * The <code>INotifier</code> interface provides a common method called
	 * <code>sendNotification</code> that relieves implementation code of 
	 * the necessity to actually construct <code>Notifications</code>.</P>
	 * 
	 * <P>
	 * The <code>Notifier</code> class, which all of the above mentioned classes
	 * extend, provides an initialized reference to the <code>Facade</code>
	 * Singleton, which is required for the convienience method
	 * for sending <code>Notifications</code>, but also eases implementation as these
	 * classes have frequent <code>Facade</code> interactions and usually require
	 * access to the facade anyway.</P>
	 * 
	 * @see org.puremvc.as3.patterns.facade.Facade Facade
	 * @see org.puremvc.as3.patterns.mediator.Mediator Mediator
	 * @see org.puremvc.as3.patterns.proxy.Proxy Proxy
	 * @see org.puremvc.as3.patterns.command.SimpleCommand SimpleCommand
	 * @see org.puremvc.as3.patterns.command.MacroCommand MacroCommand
	 */
	public class Notifier implements INotifier
	{
		/**
		 * Create and send an <code>INotification</code>.
		 * 
		 * <P>
		 * Keeps us from having to construct new INotification 
		 * instances in our implementation code.
		 * @param notificationName the name of the notiification to send
		 * @param body the body of the notification (optional)
		 * @param type the type of the notification (optional)
		 */ 
		public function sendNotification( notificationName:String, body:Object=null, type:String=null ):void 
		{
			facade.sendNotification( notificationName, body, type );
		}
		
		// Local reference to the Facade Singleton
		protected var facade:IFacade = Facade.getInstance();
	}
}