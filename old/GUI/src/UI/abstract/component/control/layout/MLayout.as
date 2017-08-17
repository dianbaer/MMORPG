package UI.abstract.component.control.layout
{
	import flash.display.DisplayObject;
	
	public class MLayout extends Layout
	{
		
		public function MLayout ( count : int = 10 )
		{
			super( 0 , 0 , count );
		}
		/**不支持间距**/
		override public function set verticalGap ( value : int) : void
		{
			throw new Error();
		}
		/**不支持间距**/
		override public function set horizontalGap ( value : int) : void
		{
			throw new Error();
		}
		/**
		 * 布局（个数要大于等于2个）
		 */
		override public function updateDisplayList () : void
		{
			if(count<2){
				throw new Error();
				return;
			}
			var requestedRowCount : int = Math.ceil( itemArray.length / count );
			var type:int = 0;
			if(count%2 == 0){
				type = 1;
			}else{
				type = 2;
			}
			var y : int                 = (requestedRowCount-1);
			var x : Number                 = 0;
			var item : DisplayObject;
			for ( var i : int = 0 ; i < requestedRowCount ; i++ )
			{
				switch(type){
					case 1:
						if(i+1 == requestedRowCount){
							if((itemArray.length%count)%2 == 0){
								x = count/2-1;
							}else{
								x = count/2-0.5;
							}
						}else{
							x = count/2-1;
						}
						break;
					case 2:
						if(i+1 == requestedRowCount){
							if(itemArray.length%count == 0){
								x = count/2-0.5;
							}
							else if((itemArray.length%count)%2 == 0){
								x = count/2-1;
							}else{
								x = count/2-0.5;
							}
						}else{
							x = count/2-0.5;
						}
						break;
				}
				
				
				var bool:Boolean = false;
				for ( var j : int = 0 ; j < count ; j++ )
				{
					if ( i * count + j < itemArray.length )
					{
						
						item = itemArray[ i * count + j ] as DisplayObject;
						item.x = x*item.width+startX;
						item.y = y*item.height+startY;
						if(bool){
							x = x-(j+1);
						}else{
							x = x+(j+1);
						}
						bool = !bool;
					}
				}
				y -= 1;
			}
		}
		
	}
}
