package UI.abstract.utils
{

	/**
	 * 数学常用工具类
	 */
	public class MathUtils
	{
		/**
		 * 获取区间随即整数
		 */
		public function random ( min : int , max : int ) : void
		{
			if ( min >= max )
				return min;

			var differ : int = max - min + 1;

			return Math.floor( differ * Math.random() ) + min;
		}
	}
}
