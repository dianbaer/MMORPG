package UI.abstract.component.control.tree
{

	/**
	 * 树中每个数据集基本对象
	 */
	public class TreeData
	{
		protected var _text : String;

		protected var _id : int;

		protected var _parentId : String;

		protected var _parent : TreeData;

		protected var _opened : Boolean;

		protected var _canExpand : Boolean;

		protected var _hasChildren : Boolean;

		protected var _level : int;

		/**
		 *	文本内容
		 */
		public function get text () : String
		{
			return _text;
		}

		public function set text ( value : String ) : void
		{
			_text = value;
		}

		/**
		 * 自己id 同一级里相当于循序
		 */
		public function get id () : int
		{
			return _id;
		}

		public function set id ( value : int ) : void
		{
			_id = value;
		}

		/**
		 * 父id
		 */
		public function get parentId () : String
		{
			return _parentId;
		}

		public function set parentId ( value : String ) : void
		{
			_parentId = value;
		}

		/**
		 * 是否打开
		 */
		public function get opened () : Boolean
		{
			return _opened;
		}

		public function set opened ( value : Boolean ) : void
		{
			_opened = value;
		}

		/**
		 * 能否扩展
		 */
		public function get canExpand () : Boolean
		{
			return _canExpand;
		}

		public function set canExpand ( value : Boolean ) : void
		{
			_canExpand = value;
		}

		/**
		 * 是否有子项
		 */
		public function get hasChildren () : Boolean
		{
			return _hasChildren;
		}

		public function set hasChildren ( value : Boolean ) : void
		{
			_hasChildren = value;
		}

		/**
		 * 树中层级
		 */
		public function get level () : int
		{
			return _level;
		}

		public function set level ( value : int ) : void
		{
			_level = value;
		}

		public function get parent () : TreeData
		{
			return _parent;
		}

		public function set parent ( value : TreeData ) : void
		{
			_parent = value;
		}

		public function dispose () : void
		{
			_parent = null;
		}
	}
}
