/*
 PureMVC - Copyright(c) 2006-08 Futurescale, Inc., Some rights reserved.
 Your reuse is governed by the Creative Commons Attribution 3.0 United States License
*/
package org.puremvc.as3.core
{
	
	import org.puremvc.as3.interfaces.*;
	
	/**
	 * A Singleton <code>IModel</code> implementation.
	 * 
	 * <P>
	 * In PureMVC, the <code>Model</code> class provides
	 * access to model objects (Proxies) by named lookup. 
	 * 
	 * <P>
	 * The <code>Model</code> assumes these responsibilities:</P>
	 * 
	 * <UL>
	 * <LI>Maintain a cache of <code>IProxy</code> instances.</LI>
	 * <LI>Provide methods for registering, retrieving, and removing 
	 * <code>IProxy</code> instances.</LI>
	 * </UL>
	 * 
	 * <P>
	 * Your application must register <code>IProxy</code> instances 
	 * with the <code>Model</code>. Typically, you use an 
	 * <code>ICommand</code> to create and register <code>IProxy</code> 
	 * instances once the <code>Facade</code> has initialized the Core 
	 * actors.</p>
	 *
	 * @see org.puremvc.as3.patterns.proxy.Proxy Proxy
	 * @see org.puremvc.as3.interfaces.IProxy IProxy
	 */
	public class Model implements IModel
	{
		/**
		 * Constructor. 
		 * 
		 * <P>
		 * This <code>IModel</code> implementation is a Singleton, 
		 * so you should not call the constructor 
		 * directly, but instead call the static Singleton 
		 * Factory method <code>Model.getInstance()</code>
		 * 
		 * @throws Error Error if Singleton instance has already been constructed
		 * 
		 */
		public function Model( )
		{
			if (instance != null) throw Error(SINGLETON_MSG);
			instance = this;
			proxyMap = new Array();	
			initializeModel();	
		}
		
		/**
		 * Initialize the Singleton <code>Model</code> instance.
		 * 
		 * <P>
		 * Called automatically by the constructor, this
		 * is your opportunity to initialize the Singleton
		 * instance in your subclass without overriding the
		 * constructor.</P>
		 * 
		 * @return void
		 */
		protected function initializeModel(  ) : void 
		{
		}
				
		/**
		 * <code>Model</code> Singleton Factory method.
		 * 
		 * @return the Singleton instance
		 */
		public static function getInstance() : IModel 
		{
			if (instance == null) instance = new Model( );
			return instance;
		}

		/**
		 * Register an <code>IProxy</code> with the <code>Model</code>.
		 * 
		 * @param proxy an <code>IProxy</code> to be held by the <code>Model</code>.
		 */
		public function registerProxy( proxy:IProxy ) : void
		{
			proxyMap[ proxy.getProxyName() ] = proxy;
			proxy.onRegister();
		}

		/**
		 * Retrieve an <code>IProxy</code> from the <code>Model</code>.
		 * 
		 * @param proxyName
		 * @return the <code>IProxy</code> instance previously registered with the given <code>proxyName</code>.
		 */
		public function retrieveProxy( proxyName:String ) : IProxy
		{
			return proxyMap[ proxyName ];
		}

		/**
		 * Check if a Proxy is registered
		 * 
		 * @param proxyName
		 * @return whether a Proxy is currently registered with the given <code>proxyName</code>.
		 */
		public function hasProxy( proxyName:String ) : Boolean
		{
			return proxyMap[ proxyName ] != null;
		}

		/**
		 * Remove an <code>IProxy</code> from the <code>Model</code>.
		 * 
		 * @param proxyName name of the <code>IProxy</code> instance to be removed.
		 * @return the <code>IProxy</code> that was removed from the <code>Model</code>
		 */
		public function removeProxy( proxyName:String ) : IProxy
		{
			var proxy:IProxy = proxyMap [ proxyName ] as IProxy;
			if ( proxy ) 
			{
				proxyMap[ proxyName ] = null;
				proxy.onRemove();
			}
			return proxy;
		}

		// Mapping of proxyNames to IProxy instances
		protected var proxyMap : Array;

		// Singleton instance
		protected static var instance : IModel;
		
		// Message Constants
		protected const SINGLETON_MSG	: String = "Model Singleton already constructed!";

	}
}