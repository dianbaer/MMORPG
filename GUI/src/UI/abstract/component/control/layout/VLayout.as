package UI.abstract.component.control.layout
{
	import UI.abstract.component.control.base.UIComponent;

	import flash.display.DisplayObject;

	public class VLayout extends Layout
	{
		/**
		 * 按最大多少列排序，沿着水平方向排序
		 * @param horizontalGap:int=0 水平间距
		 * @param verticalGap:int=0 垂直间距
		 * @param requestedColumnCount:int=1 一共几列
		 */
		public function VLayout ( horizontalGap : int = 0 , verticalGap : int = 0 , count : int = 1 )
		{
			super( horizontalGap , verticalGap , count );
		}

		/**
		 * 布局
		 */
		override public function updateDisplayList () : void
		{

			var x : int                 = startX;
			var y : int                 = startY;
			var h : int                 = 0;
			var item : DisplayObject;
			var requestedRowCount : int = Math.ceil( itemArray.length / count );
			for ( var i : int = 0 ; i < requestedRowCount ; i++ )
			{
				x = startX;
				h = 0;
				for ( var j : int = 0 ; j < count ; j++ )
				{
					if ( i * count + j < itemArray.length )
					{
						item = itemArray[ i * count + j ];
						item.x = x
						item.y = y;
						x += item.width + horizontalGap;
						// 取高度最大的对象
						if ( h < item.height )
							h = item.height
					}
				}
				y += h + verticalGap;
			}
		}

	}
}
