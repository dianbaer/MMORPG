package UI.abstract.component.control.grid
{
	import UI.abstract.component.control.IDrop;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.container.Box;

	public class BaseGrid extends Box implements IDrop
	{
		/** 格子类型 **/
		protected var _gridType : String;

		/** 格子数据 **/
		protected var _data : GridData;

		public function BaseGrid ()
		{
			super();
			this.drag = true;
		}

		/**
		 * 是否能可以接受
		 */
		public function canDrop ( dragObject : UIComponent ) : Boolean
		{
			return (dragObject is BaseGrid) && dragObject != this;
		}

		/**
		 * 接受拖动对象
		 */
		public function drop ( dragObject : UIComponent ) : void
		{

		}

		/** 格子类型 **/
		public function get gridType () : String
		{
			return _gridType;
		}

		/**
		 * @private
		 */
		public function set gridType ( value : String ) : void
		{
			if(_gridType == value){
				return;
			}
			_gridType = value;
		}

		/** 格子数据(副本) **/
		public function get data () : GridData
		{
			return _data;
		}

		/**
		 * @private
		 */
		public function set data ( value : GridData ) : void
		{
			if(_data == value){
				return;
			}
			_data = value;
			nextDraw();
		}
		override public function dispose () : void
		{
			_data = null;
			super.dispose();
		}
	}
}
