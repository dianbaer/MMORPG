package UI.abstract.component.control.image
{
	import UI.App;
	import UI.abstract.manager.LogManager;
	
	import flash.display.BitmapData;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.utils.getQualifiedClassName;

	public class BitmapScale9Grid
	{
		/**切图次数**/
		private static var cutCount:int = 0;
		
		
		
		
		

		private static var _scale9GridRect : Array

		

		//private var _dirty : Boolean = false;

		public function BitmapScale9Grid ()
		{
			
		}

		
		private static function updataCutRectangle (_bitmapdata:BitmapData ,_scale9Grid:Rectangle,_width:Number,_height:Number,_x:Number,_y:Number) : void
		{
			if ( _bitmapdata == null || ( _bitmapdata.width == 0 && _bitmapdata.height == 0 ) )
				return;

			//_dirty = false;
			// 实际图剪切矩形
			var rect : Rectangle = _bitmapdata.rect;
			var width : int      = _width - _scale9Grid.x - ( rect.right - _scale9Grid.right )
			var height : int     = _height - _scale9Grid.y - ( rect.bottom - _scale9Grid.bottom )
			width < 0 ? width = 0 : "";
			height < 0 ? height = 0 : "";
			var _cutRect:Rectangle = new Rectangle( _scale9Grid.x , _scale9Grid.y , width , height );

			// 原图的9个矩形 x，y，width，height
			var sx : Vector.<int> = new Vector.<int>/*[ 0 , _scale9Grid.x , _scale9Grid.right ]*/;
			sx[0] = 0;
			sx[1] = _scale9Grid.x;
			sx[2] = _scale9Grid.right;
			var sy : Vector.<int> = new Vector.<int>/*[ 0 , _scale9Grid.y , _scale9Grid.bottom ]*/;
			sy[0] = 0;
			sy[1] = _scale9Grid.y;
			sy[2] = _scale9Grid.bottom;
			var sw : Vector.<int> = new Vector.<int>/*[ _scale9Grid.x , _scale9Grid.width , rect.right - _scale9Grid.right ]*/;
			sw[0] = _scale9Grid.x;
			sw[1] = _scale9Grid.width;
			sw[2] = rect.right - _scale9Grid.right;
			var sh : Vector.<int> = new Vector.<int>/*[ _scale9Grid.y , _scale9Grid.height , rect.bottom - _scale9Grid.bottom ]*/;
			sh[0] = _scale9Grid.y;
			sh[1] = _scale9Grid.height;
			sh[2] = rect.bottom - _scale9Grid.bottom;

			// 实际图剪切的9个矩形 x，y，width，height
			var x : Vector.<int>  = new Vector.<int>/*[ 0 , _cutRect.x , _cutRect.right ]*/;
			x[0] = 0;
			x[1] = _cutRect.x;
			x[2] = _cutRect.right;
			var y : Vector.<int>  = new Vector.<int>//[ 0 , _cutRect.y , _cutRect.bottom ];
			y[0] = 0;
			y[1] = _cutRect.y;
			y[2] = _cutRect.bottom;
			var w : Vector.<int>  = new Vector.<int>//[ _cutRect.x , _cutRect.width , rect.right - _scale9Grid.right ];
			w[0] = _cutRect.x;
			w[1] = _cutRect.width;
			w[2] = rect.right - _scale9Grid.right;
			var h : Vector.<int>  = new Vector.<int>//[ _cutRect.y , _cutRect.height , rect.bottom - _scale9Grid.bottom ];
			h[0] =  _cutRect.y;
			h[1] = _cutRect.height;
			h[2] = rect.bottom - _scale9Grid.bottom;

			// 竖着开始计算
			_scale9GridRect = [];
			for ( var i : int = 0 ; i < 3 ; i++ )
			{
				for ( var j : int = 0 ; j < 3 ; j++ )
				{
					var matrix : Matrix = new Matrix();
					var scalew : Number;
					var scaleh : Number;

					scalew = w[ i ] / sw[ i ];
					scaleh = h[ j ] / sh[ j ];
					matrix.scale( scalew , scaleh );
					if ( scalew != 1 )
						matrix.tx = ( 1 - scalew ) * x[ i ] + _x;
					else
						matrix.tx = x[ i ] - sx[ i ] + _x;
					
					if ( scaleh != 1 )
						matrix.ty = ( 1 - scaleh ) * y[ j ] + _y;
					else
						matrix.ty = y[ j ] - sy[ j ] + _y;
					_scale9GridRect.push( { targetRect: new Rectangle( x[ i ] , y[ j ] , w[ i ] , h[ j ] ) ,
											  scoureRect: new Rectangle( sx[ i ] , sy[ j ] , sw[ i ] , sh[ j ] ) ,
											  matrix: matrix } )
				}
			}
		}


//		public function drawGraphics ( graphics : Graphics , clear : Boolean = true , drawCutRect : Boolean = true ) : void
//		{
//			if ( clear )
//				graphics.clear();
//			for ( var i : int = 0 ; i < 9 ; i++ )
//			{
//
//				var rect : Rectangle = _scale9GridRect[ i ].rect;
//				var matrix : Matrix  = _scale9GridRect[ i ].matrix
//				//                                        graphics.lineStyle(1,0xff0000)
//				graphics.beginBitmapFill( this._bitmapdata , matrix , false , true );
//				if ( !( !drawCutRect && i == 4 ) )
//				{
//					graphics.drawRect( rect.x + _x , rect.y + _y , rect.width , rect.height )
//				}
//
//			}
//		}

		public static function drawBitmapData (_bitmapdata:BitmapData ,_scale9Grid:Rectangle,_width:Number,_height:Number,_x:Number = 0,_y:Number = 0) : BitmapData
		{
//			if ( _bitmapdata == null || ( _bitmapdata.width == 0 && _bitmapdata.height == 0 ) )
//				return null;
			
			//if ( _dirty )
			updataCutRectangle(_bitmapdata,_scale9Grid,_width,_height,_x,_y);
			var bmd : BitmapData = new BitmapData( _width , _height , true , 0 );
			for ( var i : int = 0 ; i < 9 ; i++ )
			{
//				if ( i!=1 ) continue;
				var scoureRect : Rectangle = _scale9GridRect[ i ].scoureRect;
				var targetRect : Rectangle = _scale9GridRect[ i ].targetRect;
				var matrix : Matrix        = _scale9GridRect[ i ].matrix;
				if ( i % 2 == 1 || i == 4 )
					bmd.draw( _bitmapdata , matrix , null , null , targetRect , true );
				else
					bmd.copyPixels( _bitmapdata , scoureRect , new Point( targetRect.x , targetRect.y ) );
			}
			cutCount++;
			App.log.info( "切图次数："+cutCount, LogManager.LOG_WARN  );
			_scale9GridRect = null;
			return bmd;
		}

	}
}
