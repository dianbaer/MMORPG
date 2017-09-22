package UI.abstract.component.control.image
{
	import UI.App;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.event.UIEvent;
	import UI.abstract.resources.item.ImageResource;
	
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.geom.Rectangle;

	/**
	 * 显示图片组件
	 * 只能设置资源地址或者bitmapData其中一种方式
	 */

	public class Image extends Container //此类 有问题 需要优化
	{
		protected var _url : String;

		protected var _bitmap : Bitmap;

		protected var _bitmapData : BitmapData;

		protected var _res : ImageResource;

//		protected var _autoSize : Boolean;

		/**是否切九宫格**/
		private var _isSquared:Boolean = false;
		//protected var _scale9Grid : Rectangle;

//		protected var _scale9GapW : int;
//
//		protected var _scale9GapH : int;

		public function Image ()
		{
			super();
//			_autoSize = autoSize;
			_bitmap = new Bitmap();
			addChild( _bitmap );
		}

		override protected function draw () : void
		{
			super.draw();
			//走切图的流程
			if ( _isSquared )
			{
				if ( _bitmapData )
				{
					var _scale9Grid1 : Rectangle;
					if ( _scale9GapW > 0 && _scale9GapH > 0 )
					{
						_scale9Grid1 = new Rectangle();
						_scale9Grid1.x = _scale9GapW;
						_scale9Grid1.y = _scale9GapH;
						_scale9Grid1.width = _bitmapData.width - ( _scale9GapW << 1 );
						_scale9Grid1.height = _bitmapData.height - ( _scale9GapH << 1 );
					}
					else
					{
						_scale9Grid1 = _scale9Grid;
					}
					if(_res){
						_bitmap.bitmapData = _res.squaredBitmapData(_scale9Grid1,_width,_height);
						
					}else{
						_bitmap.bitmapData = BitmapScale9Grid.drawBitmapData(_bitmapData,_scale9Grid1,_width,_height);
						
					}
					_bitmap.width = _width;
					_bitmap.height = _height;
				}
				return;
			}
			//走不切图的流程
			_bitmap.bitmapData = _bitmapData;
			graphics.clear();
			//_res.frame有就是使用实际大小，就画一个跟宽高大小一样
			if(_res && _res.frame){
				graphics.beginFill(0x000000,0);
				graphics.drawRect(0,0,_width,_height);
				graphics.endFill();
				_bitmap.width = _bitmapData.width;
				_bitmap.height = _bitmapData.height;
			}else{
				_bitmap.width = _width;
				_bitmap.height = _height;
			}	
			updatePosition();
			
		}
		public function updatePosition():void{
			if(_res && _res.frame){
				_bitmap.x = -_res.frame.x;
				_bitmap.y = -_res.frame.y;
			}else{
				_bitmap.x = 0;
				_bitmap.y = 0;
			}
		}
		public function set url ( str : String ) : void
		{
			if ( _url == str )
				return;

			// 取消上次加载
			if ( ( _res && _url && _url != _res.url ) || ( !_res && _url ) )
				App.loader.canelLoad( _url , onComplete );

			//地址为空，图像存在 说明是外部直接设置的bitmapdata，先清理
			if ( !_url && _bitmap.bitmapData )
			{
				_bitmap.bitmapData = null;
				_bitmapData = null;
				
			}

			_url = str;

			if ( _url )
			{
				App.loader.load( _url , onComplete );
			}
			else
			{
//				imageResource = null;
				if ( _res )
				{
					App.loader.subtractUseNumber( _res );
					_res = null;
					_bitmap.bitmapData = null;
					_bitmapData = null;
					
				}
			}
		}

		public function get url () : String
		{
			return _url;
		}

		/**
		 * 资源加载完成
		 */
		public function onComplete ( res : ImageResource ) : void
		{
			if ( _url == res.url )
			{
				this.imageResource = res;
				dispatchEvent( new UIEvent( UIEvent.LOADER_COMPLETE ) );
			}
		}

		/**
		 * 设置九宫格
		 */
		override public function set scale9Grid ( rect : Rectangle ) : void
		{
			super.scale9Grid = rect;
			
			if ( rect == null )
			{
				_isSquared = false
			}
			else
			{
				_isSquared = true;
			}
			nextDraw();
			
		}

		override public function get scale9Grid () : Rectangle
		{
			
			return _scale9Grid;
		}

		/**
		 * 设置九宫格边距
		 */
		override public function set9Gap ( gapW : int , gapH : int ) : void
		{
			super.set9Gap( gapW , gapH );
			if ( gapW <= 0 || gapH <= 0 )
			{
				
				_isSquared = false
			}
			else
			{
				
				_isSquared = true;
			}
			nextDraw();
			
		}

		protected function get imageResource () : ImageResource
		{
			return _res;
		}

		public function get bitmapData () : BitmapData
		{
			return _bitmapData;
		}

		/**
		 * 设置资源
		 */
		protected function set imageResource ( res : ImageResource ) : void
		{
			// 清理现有资源和外部穿的bitmapdata
			if ( res == null )
			{
				// 加载中
				if ( _url )
				{
					if ( ( _res && _url != _res.url ) || ( !_res && _url ) )
						App.loader.canelLoad( _url , onComplete );
					if ( _res )
						App.loader.subtractUseNumber( _res );
				}
				_url = null;
				_res = null;
				_bitmap.bitmapData = null;
				_bitmapData = null;
				
				return;
			}

			//更新 新资源
			var oldRes : ImageResource = _res;
			_res = res;
			_url = _res.url;

			if ( oldRes )
				App.loader.subtractUseNumber( oldRes );
			App.loader.addUseNumber( _res );

			_bitmap.bitmapData = _res.bitmapData;
			_bitmapData = _res.bitmapData;

			//有可能不设置大小，就不会掉draw方法，得更新一下位置
			updatePosition();
			
			nextDraw();
		}

		public function set bitmapData ( value : BitmapData ) : void
		{
			imageResource = null;
			if ( value == null )
				return;

			_bitmap.bitmapData = value;
			_bitmapData = value;


			nextDraw();
//			if ( !isChangeSize  )
//			{
//				_bitmap.width = bitmapData.width;
//				_bitmap.height = bitmapData.height;
//				dispatchEvent( new UIEvent( UIEvent.RESIZE_UI ) );
//			}
//			else
//			{
//				_bitmap.width = width;
//				_bitmap.height = height;
//			}
		}


		override public function dispose () : void
		{
			
			imageResource = null;
			_bitmap = null;
			super.dispose();
		}


	}
}
