package UI.abstract.manager
{

	/**
	 * 日志管理类
	 */
	public class LogManager
	{
		/** ----------日志类型---------- **/
		/** 正常 **/
		public static const LOG_NORMAL : int = 0;

		/** 警告 **/
		public static const LOG_WARN : int   = 1;

		/** 错误 **/
		public static const LOG_ERROR : int  = 2;
		
		/** 调试 **/
		public static const LOG_DEBUG : int = 99;

		/** 需要输出级别 (大于等于此级别全部输出) **/
		private static var logLevel : int    = 1;

		/**
		 * 输出日志
		 * @param msg : 输出信息
		 * @param type : 日志类型
		 * @param className : 具体类名
		 * @param funName : 具体函数名
		 * @param line :  换行数
		 */
		public function info ( msg : String , type : int = 0 , className : String = null , funName : String = null , line : int = 0 ) : void
		{
			if ( type < logLevel )
				return;
			var str : String;
			switch ( type )
			{
				case LOG_NORMAL:
				case LOG_DEBUG:
					str = "[ 正常 ] ==> ";
					break;
				case LOG_WARN:
					str = "[ 警告 ] ==> ";
					line++;
					break;
				case LOG_ERROR:
					str = "[ 错误 ] ==> ";
					line+=2;
					break;
				default:
					trace( "日志类型错误 : " , type );
					return;
					break;
			}
			trace( str , msg , className ? " ==> 对象 : " + className : "" , funName ? " ==> 函数名 : " + funName : "" );
			for ( line; line > 0; line-- )
				trace();
		}
	}
}
