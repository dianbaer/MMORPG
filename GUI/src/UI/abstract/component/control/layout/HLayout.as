package UI.abstract.component.control.layout
{
	import UI.abstract.component.control.base.UIComponent;

	import flash.display.DisplayObject;

	public class HLayout extends Layout
	{
		/**
		 * 按最大多少行排序，沿着垂直方向排序
		 * @param horizontalGap:int=0 水平间距
		 * @param verticalGap:int=0 垂直间距
		 * @param requestedRowCount:int=1 一共几行
		 */
		public function HLayout ( horizontalGap : int = 0 , verticalGap : int = 0 , count : int = 1 )
		{
			super( horizontalGap , verticalGap , count );
		}

		/**
		 * 布局
		 */
		override public function updateDisplayList () : void
		{

			var x : int                    = startX;
			var y : int                    = startY;
			var w : int                    = 0;
			var item : DisplayObject;
			var requestedColumnCount : int = Math.ceil( itemArray.length / count );
			for ( var i : int = 0 ; i < requestedColumnCount ; i++ )
			{
				y = startY;
				w = 0;
				for ( var j : int = 0 ; j < count ; j++ )
				{
					if ( i * count + j < itemArray.length )
					{
						item = itemArray[ i * count + j ];
						item.y = y;
						item.x = x;
						y += item.height + verticalGap;
						// 取宽度最大的对象
						if ( w < item.width )
							w = item.width
					}
				}
				x += w + horizontalGap;
			}
		}
	}
}
