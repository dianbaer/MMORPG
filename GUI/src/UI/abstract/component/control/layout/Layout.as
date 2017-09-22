package UI.abstract.component.control.layout
{

	/**
	 * 布局对象
	 */
	public class Layout
	{
		private var _horizontalGap : int = 0;

		private var _count : int         = 0;

		private var _verticalGap : int   = 0;

		private var _itemArray : Array;

		private var _startX : int        = 0;

		private var _startY : int        = 0;

		public function Layout ( horizontalGap : int = 0 , verticalGap : int = 0 , count : int = 1 )
		{
			this._horizontalGap = horizontalGap;
			this._verticalGap = verticalGap;
			this.count = count;

		}

		/**
		 * 布局
		 */
		public function updateDisplayList () : void
		{

		}

		/**
		 * 显示对象
		 */
		public function get itemArray () : Array
		{
			return _itemArray;
		}

		public function set itemArray ( value : Array ) : void
		{
			//_itemArray = value.concat();
			_itemArray = value;
		}

		/**
		 * 行或列数
		 */
		public function get count () : int
		{
			return _count;
		}

		public function set count ( value : int ) : void
		{
			_count = value;
		}

		/**
		 * 列间距
		 */
		public function get verticalGap () : int
		{
			return _verticalGap;
		}
		
		public function set verticalGap ( value : int) : void
		{
			_verticalGap = value;
		}

		/**
		 * 行间距
		 */
		public function get horizontalGap () : int
		{
			return _horizontalGap;
		}
		
		public function set horizontalGap ( value : int) : void
		{
			_horizontalGap = value;
		}

		public function dispose () : void
		{
			if(_itemArray){
				_itemArray.length = 0;
				_itemArray = null;
			}
		}

		/**
		 * x轴起点
		 */
		public function get startX () : int
		{
			return _startX;
		}

		/**
		 * @private
		 */
		public function set startX ( value : int ) : void
		{
			_startX = value;
		}

		/**
		 * y轴起点
		 */
		public function get startY () : int
		{
			return _startY;
		}

		/**
		 * @private
		 */
		public function set startY ( value : int ) : void
		{
			_startY = value;
		}
	}
}
