package UI.abstract.component.control.text
{
	import flash.geom.Point;

	public class ALabel extends BaseTextField
	{
		public function ALabel ()
		{
			super();
			this.enabled = false;
			select = false
			//textField.selectable = false;
			autoSize = true;
		}

		override public function set enabled ( value : Boolean ) : void
		{
			if(_enabled == value){
				return;
			}
			_enabled = value;
			if ( value )
			{
				this.mouseChildren = value;
				textMouse = true;
				//this.textField.mouseEnabled = true;
//				textField.selectable = true;
			}
			else
			{
				this.mouseChildren = false;
				this.mouseEnabled = false;
				this.tabChildren = false;
				this.tabEnabled = false;
				textMouse = false;
				//this.textField.mouseEnabled = false;
			}
		}

		
		
		/**
		 * 获得label在一个范围内偏移量
		 */
		public function offsetPos ( w : Number, h : Number ) : Point
		{
			var pos : Point = new Point();
			if ( _align == TextStyle.CENTER )
				pos.x = ( w - width ) >> 1;
			else if ( _align == TextStyle.LEFT )
				pos.x = 2;
			else if ( _align == TextStyle.RIGHT )
				pos.x = ( w - width ) - 2;
			pos.y = ( h - height ) >> 1;
			return pos;
		}
	}
}
