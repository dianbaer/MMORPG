package UI.abstract.component.event
{

	public class DataChangeEvent extends UIEvent
	{
		/** 数据已经改变 **/
		public static const DATA_CHANGE : String     = "dataChange";

		/** 数据改变之前 **/
		public static const PRE_DATA_CHANGE : String = "preDataChange";

		protected var _startIndex : uint;

		protected var _endIndex : uint;

		protected var _changeType : String;

		protected var _items : Array;

		public function DataChangeEvent ( type : String , changeType : String , items : Array , startIndex : int = -1 , endIndex : int = -1 , bubbles : Boolean =
										  true , cancelable : Boolean = false )
		{
			super( type , bubbles , cancelable );
		}

		/**
		 * 开始位置
		 */
		public function get startIndex () : uint
		{
			return _startIndex;
		}

		/**
		 * 结束位置
		 */
		public function get endIndex () : uint
		{
			return _endIndex;
		}

		/**
		 * 改变类型
		 */
		public function get changeType () : String
		{
			return _changeType;
		}

		/**
		 * 改变数据
		 */
		public function get items () : Array
		{
			return _items;
		}


	}
}
