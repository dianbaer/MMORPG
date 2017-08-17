package UI.abstract.component.event
{
	import UI.abstract.component.control.base.UIComponent;

	import flash.display.DisplayObject;

	public class DragEvent extends UIEvent
	{
		/** 拖拽完成 **/
		public static const DRAG_COMPLETE : String = "drag_complete";

		private var _dragTarget : UIComponent;

		private var _dropTarget : UIComponent;

		private var _data : Object;

		public function DragEvent ( type : String , dragTarget : UIComponent = null , dropTarget : UIComponent = null , data : Object = null , bubbles : Boolean =
									true , cancelable : Boolean = false )
		{
			super( type , bubbles , cancelable );
			_dragTarget = dragTarget;
			_dropTarget = dropTarget;
			_data = data;
		}

		/** 拖动目标 **/
		public function get dragTarget () : UIComponent
		{
			return _dragTarget;
		}

		/** 拖动完成下方目标 **/
		public function get dropTarget () : UIComponent
		{
			return _dropTarget;
		}

		/** 携带数据 **/
		public function get data () : Object
		{
			return _data;
		}

	}
}
