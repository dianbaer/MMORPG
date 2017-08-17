package UI.abstract.component.control.dropDownMenu
{
	import UI.abstract.component.data.DataProvider;

	public class MenuData
	{
		private var _data : Object;

		private var _items : DataProvider = new DataProvider;

		private var _level : int;
		
		private var _iconPrefix : String;
		
		
		
		public function MenuData ()
		{
		}

		/** 内容 **/
		public function get text () : Object
		{
			return _data;
		}

		/**
		 * @private
		 */
		public function set text ( value : Object ) : void
		{
			_data = value;
		}
		
		/** 前缀图标 **/
		public function get iconPrefix():String
		{
			return _iconPrefix;  
		}

		/**
		 * @private
		 */
		public function set iconPrefix ( value : String ) : void
		{
			_iconPrefix = value;
		}
		
		/** 子内容 **/
		public function get items () : DataProvider
		{
			return _items;
		}

		/**
		 * @private
		 */
		public function set items ( value : DataProvider ) : void
		{
			_items = value;
		}
		
		public function dispose () : void
		{
			_data = null;
			items.dispose();
			items = null;
		}

	}
}
