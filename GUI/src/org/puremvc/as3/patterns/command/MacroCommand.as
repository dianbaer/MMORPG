/*
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved.
 Your reuse is governed by the Creative Commons Attribution 3.0 United States License
*/
package org.puremvc.as3.patterns.command 
{

	import org.puremvc.as3.interfaces.*;
	import org.puremvc.as3.patterns.observer.*;
	
	/**
	 * A base <code>ICommand</code> implementation that executes other <code>ICommand</code>s.
	 *  
	 * <P>
	 * A <code>MacroCommand</code> maintains an list of
	 * <code>ICommand</code> Class references called <i>SubCommands</i>.</P>
	 * 
	 * <P>
	 * When <code>execute</code> is called, the <code>MacroCommand</code> 
	 * instantiates and calls <code>execute</code> on each of its <i>SubCommands</i> turn.
	 * Each <i>SubCommand</i> will be passed a reference to the original
	 * <code>INotification</code> that was passed to the <code>MacroCommand</code>'s 
	 * <code>execute</code> method.</P>
	 * 
	 * <P>
	 * Unlike <code>SimpleCommand</code>, your subclass
	 * should not override <code>execute</code>, but instead, should 
	 * override the <code>initializeMacroCommand</code> method, 
	 * calling <code>addSubCommand</code> once for each <i>SubCommand</i>
	 * to be executed.</P>
	 * 
	 * <P>
	 * 
	 * @see org.puremvc.as3.core.controller.Controller Controller
	 * @see org.puremvc.as3.patterns.observer.Notification Notification
	 * @see org.puremvc.as3.patterns.command.SimpleCommand SimpleCommand
	 */
	public class MacroCommand extends Notifier implements ICommand, INotifier
	{
		
		private var subCommands:Array;
		
		/**
		 * Constructor. 
		 * 
		 * <P>
		 * You should not need to define a constructor, 
		 * instead, override the <code>initializeMacroCommand</code>
		 * method.</P>
		 * 
		 * <P>
		 * If your subclass does define a constructor, be 
		 * sure to call <code>super()</code>.</P>
		 */
		public function MacroCommand()
		{
			subCommands = new Array();
			initializeMacroCommand();			
		}
		
		/**
		 * Initialize the <code>MacroCommand</code>.
		 * 
		 * <P>
		 * In your subclass, override this method to 
		 * initialize the <code>MacroCommand</code>'s <i>SubCommand</i>  
		 * list with <code>ICommand</code> class references like 
		 * this:</P>
		 * 
		 * <listing>
		 *		// Initialize MyMacroCommand
		 *		override protected function initializeMacroCommand( ) : void
		 *		{
		 *			addSubCommand( com.me.myapp.controller.FirstCommand );
		 *			addSubCommand( com.me.myapp.controller.SecondCommand );
		 *			addSubCommand( com.me.myapp.controller.ThirdCommand );
		 *		}
		 * </listing>
		 * 
		 * <P>
		 * Note that <i>SubCommand</i>s may be any <code>ICommand</code> implementor,
		 * <code>MacroCommand</code>s or <code>SimpleCommands</code> are both acceptable.
		 */
		protected function initializeMacroCommand():void
		{
		}
		
		/**
		 * Add a <i>SubCommand</i>.
		 * 
		 * <P>
		 * The <i>SubCommands</i> will be called in First In/First Out (FIFO)
		 * order.</P>
		 * 
		 * @param commandClassRef a reference to the <code>Class</code> of the <code>ICommand</code>.
		 */
		protected function addSubCommand( commandClassRef:Class ): void
		{
			subCommands[subCommands.length] = commandClassRef;
		}
		
		/** 
		 * Execute this <code>MacroCommand</code>'s <i>SubCommands</i>.
		 * 
		 * <P>
		 * The <i>SubCommands</i> will be called in First In/First Out (FIFO)
		 * order. 
		 * 
		 * @param notification the <code>INotification</code> object to be passsed to each <i>SubCommand</i>.
		 */
		public final function execute( notification:INotification ) : void
		{
			while ( subCommands.length > 0) {
				var commandClassRef : Class = subCommands.shift();
				var commandInstance : ICommand = new commandClassRef();
				commandInstance.execute( notification );
			}
		}
								
	}
}