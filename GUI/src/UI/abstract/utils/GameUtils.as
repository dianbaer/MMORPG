package UI.abstract.utils
{
	import UI.App;
	import UI.abstract.manager.LogManager;
	
	import flash.net.LocalConnection;
	import flash.system.System;

	/**
	 * 游戏常用工具类
	 */
	public class GameUtils
	{
		/**
		 * 系统GC
		 */
		public static function GC () : void
		{
			try
			{
				new LocalConnection().connect( "GC" );
				new LocalConnection().connect( "GC" );
			}
			catch ( error : Error )
			{
			}
			App.log.info( "系统GC！" , LogManager.LOG_NORMAL , "GameUtils" , "GC" );
		}

		/**
		 * 当前内存 （ 兆 ）
		 */
		public static function get currMem () : Number
		{
			return Number( ( System.totalMemory * 0.000000954 ).toFixed( 3 ) );
		}
		
	}
}
