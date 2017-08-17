package UI.abstract.component.control.base
{
	import UI.App;
	import UI.abstract.component.event.UIEvent;
	import UI.abstract.manager.LogManager;
	import UI.abstract.tween.TweenManager;
	
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.filters.ColorMatrixFilter;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.utils.getQualifiedClassName;

	/**
	 * 所有ui组件基类
	 */
	public class UIComponent extends Sprite
	{
		public static var drawCount:int = 0;
		public static var updateCount:int = 0;
		public static var drawGraphicsCount:int = 0;
		/** ui数量 **/
		public static var uiCount : int                  = 0;

		public static var uiList : Array                 = [];

		/** 是否支持下一帧刷新 **/
		public var isNext : Boolean                   = false;

		private var _dirtyDraw : Boolean                 = false;

		private var _dirtyDrawGraphics : Boolean         = false;
		


		protected var _width : Number                    = 0;

		protected var _height : Number                   = 0;

		protected var _tag : Object;

		private var _isDispose : Boolean; 

		/** 是否渲染过 **/
		protected var _isInitDraw : Boolean;

		protected var _isInitDrawGraphics : Boolean;

		

		/** 是否在大小改变的时候发事件 **/
		protected var _isResizeDispatchEvent : Boolean = true;

		/** 九宫格数据 **/
		protected var _scale9Grid : Rectangle;

		protected var _scale9GapW : int;

		protected var _scale9GapH : int;
		
		//没有显示区域时候渲染(用于遮罩，也得渲染)
		protected var noneHaveVisibleAreaDraw:Boolean = false;
		

		public function UIComponent ()
		{
			uiCount++;
			uiList.push( this );
			//mouseEnabled = false;
			//tabEnabled = false;
		}

		/**
		 * 销毁
		 */
		public function dispose () : void
		{
			if ( _isDispose )
			{
				App.log.info( "重复销毁对象" , LogManager.LOG_WARN , getQualifiedClassName( this ) , "dispose" );
				return;
			}
			uiCount--;
			uiList.splice( uiList.indexOf( this ) , 1 );
			if ( this.parent )
				this.parent.removeChild( this );
			/*while ( numChildren > 0 )
			{
				var obj : DisplayObject = super.removeChildAt( numChildren - 1 );
				if ( obj is UIComponent )
				{
					UIComponent( obj ).dispose();
				}
			}*/
			if(super.numChildren>0){
				App.log.info( "有子对象没移除："+super.numChildren, LogManager.LOG_WARN , getQualifiedClassName( this ) , "dispose");
			}
			removeEvent();
			this.filters = null;
			_scale9Grid = null;
			_tag = null;
			_isDispose = true;
		}

		/**
		 * 绘制对象位置大小
		 */
		protected function draw () : void
		{
			_isInitDraw = true;

			drawCount++;
			//App.log.info( "draw次数："+drawCount, LogManager.LOG_WARN , getQualifiedClassName( this ) ,"draw");
		}
		
		/**
		 * 渲染对象
		 */
		protected function drawGraphics () : void
		{
			_isInitDrawGraphics = true;

			drawGraphicsCount++;
			//App.log.info( "drawGraphics次数："+drawGraphicsCount, LogManager.LOG_WARN , getQualifiedClassName( this ) ,"drawGraphics");
		}

		/**
		 * 下一帧刷新
		 */
		protected function nextDraw () : void
		{
			//是否有显示区域
			if(hasVisibleArea || noneHaveVisibleAreaDraw){
				//visible = true;
				
				if ( isNext )
				{
					if ( !TweenManager.isTweening( update ) )
						TweenManager.delayedCall( 1 , update , null , true );
					_dirtyDraw = true;
				}
				else{
					draw();
					for(var i:int = 0;i<numChildren;i++){
						var component:DisplayObject = getChildAt(i);
						if(component is UIComponent){
							(component as UIComponent).update();
						}
					}
					
				}
			}else{
				//visible = false;
			}
			
		}

		protected function nextDrawGraphics () : void
		{
			if ( isNext )
			{
				if ( !TweenManager.isTweening( update ) )
					TweenManager.delayedCall( 1 , update , null , true );
				_dirtyDrawGraphics = true;
			}
			else
				drawGraphics();
		}
		
		/**
		 * 外部调用 相当于立即更新
		 */
		public function update ( e : Event = null ) : void
		{
			if ( _isDispose )
				return;
			if ( _dirtyDraw )
			{
				_dirtyDraw = false;
				
				if(hasVisibleArea){
					//visible = true;
					draw();
					for(var i:int = 0;i<numChildren;i++){
						var component:DisplayObject = getChildAt(i);
						if(component is UIComponent){
							(component as UIComponent).update();
						}
					}
				}else{
					//visible = false;
				}
			}
			if ( _dirtyDrawGraphics )
			{
				_dirtyDrawGraphics = false;
				drawGraphics();
			}
			updateCount++;
			//App.log.info( "update次数："+updateCount, LogManager.LOG_NORMAL , getQualifiedClassName( this ) );
		}
		/**
		 * 是否设置大小了
		 */
		public function get hasVisibleArea():Boolean
		{
			return _width != 0.0 && _height != 0.0;
		}
		/**
		 * 宽度（最好别重写）
		 */
		override public function get width () : Number
		{
			return _width;
		}

		override final public function set width ( value : Number ) : void
		{
			if ( _width == value || value<0)
				return;
			_width = value;
			//大小改变
			if ( _isResizeDispatchEvent){
				dispatchEvent( new UIEvent( UIEvent.RESIZE_UI ) );
			}
			nextDraw();
		}

		/**
		 * 高度（最好别重写）
		 */
		override public function get height () : Number
		{
			return _height;
		}

		override final public function set height ( value : Number ) : void
		{
			if ( _height == value || value<0)
				return;
			_height = value;
			//大小改变
			if ( _isResizeDispatchEvent){
				dispatchEvent( new UIEvent( UIEvent.RESIZE_UI ) );
			}
			nextDraw();
		}

		/**
		 * 设置高宽
		 */
		public function setSize ( w : Number , h : Number ) : void
		{
			if ( w < 0 )
				w = 0;
			if ( h < 0 )
				h = 0;
			if ( _width == w && _height == h )
				return;
			width = w;
			height = h;
		}

		/**
		 * 设置坐标
		 */
		public function setPosition ( x : Number , y : Number ) : void
		{
			if ( this.x != x )
				this.x = x;
			if ( this.y != y )
				this.y = y;
		}

		override public function set x(value:Number):void
		{
			if(super.x == value){
				return;
			}
			dispatchEvent( new UIEvent( UIEvent.POSITION_UI ) );
			super.x = value;
		}
		override public function set y(value:Number):void
		{
			
			if(super.y == value){
				return;
			}
			dispatchEvent( new UIEvent( UIEvent.POSITION_UI ) );
			super.y = value;
		}
		/**
		 * 获取显示对象的真正宽度
		 */
		public function get superWidth () : Number
		{
			return super.width;
		}

		/**
		 * 获取显示对象的真正高度
		 */
		public function get superHeight () : Number
		{
			return super.height;
		}

		/**
		 * 标签（临时存储数据）
		 */
		public function get tag () : Object
		{
			return _tag;
		}

		public function set tag ( value : Object ) : void
		{
			_tag = value;
		}

		

		/** 是否在大小改变的时候发事件 **/
		public function get isResizeDispatchEvent () : Boolean
		{
			return _isResizeDispatchEvent;
		}

		/**
		 * @private
		 */
		public function set isResizeDispatchEvent ( value : Boolean ) : void
		{
			_isResizeDispatchEvent = value;
		}


		/**
		 * 发送事件
		 */
		override public function dispatchEvent ( event : Event ) : Boolean
		{
			App.log.info( "ui发送了事件 ==> 类型 ==> " + event.type , LogManager.LOG_NORMAL , getQualifiedClassName( this ) ,"dispatchEvent");
			return super.dispatchEvent( event );
		}

		/**
		 * 组件舞台坐标
		 */
		public function get stagePosition () : Point
		{
			return this.localToGlobal( new Point( 0 , 0 ) );
		}

		/**
		 * 移出对象所有事件
		 */
		public function removeEvent () : void
		{
			App.event.removeEventByObj( this );
		}

		/**
		 * 九宫格
		 */
		override public function set scale9Grid ( rect : Rectangle ) : void
		{
			//清空_scale9GapW和_scale9GapH
			_scale9GapW = 0;
			_scale9GapH = 0;
			if((_scale9Grid && _scale9Grid.equals(rect)) || (!_scale9Grid && !rect))
				return;
			_scale9Grid = rect;
			
			nextDraw();
		}

		/**
		 * 设置九宫格边距
		 */
		public function set9Gap ( gapW : int , gapH : int ) : void
		{
			//清空_scale9Grid
			_scale9Grid = null;
			if(_scale9GapW == gapW && _scale9GapH == gapH)
				return;
			_scale9GapW = gapW;
			_scale9GapH = gapH;
			
			nextDraw();
		}

		/**
		 * 灰化
		 */
		public static var grayFilter : ColorMatrixFilter = new ColorMatrixFilter( [ 0.3086 , 0.6094 , 0.082 , 0 , 0 , 0.3086 , 0.6094 , 0.082 , 0 , 0 ,
																					0.3086 ,
																					0.6094 , 0.082 , 0 , 0 , 0 , 0 , 0 , 1 , 0 ] );

		private var _gray : Boolean;

		public function set gray ( isGray : Boolean ) : void
		{
			_gray = isGray;
			var filters : Array = this.filters ? this.filters : [];
			if ( isGray )
			{
				if ( filters.indexOf( grayFilter ) == -1 )
				{
					filters.push( grayFilter );
					this.filters = filters;
				}
			}
			else
			{
				var len : int = filters.length;
				for ( var i : int = 0 ; i < len ; i++ )
				{
					if ( filters[ i ] is ColorMatrixFilter )
					{
						filters.splice( i , 1 );
						this.filters = filters;
						break;
					}
				}
			}
		}

		public function get gray () : Boolean
		{
			return _gray;
		}

		/**
		 * 输出ui统计
		 */
		public static function traceDebug ( all : Boolean = false ) : void
		{
			if ( all )
			{
				var obj : Object = {};
				var str : String = "";
				var arr : Array  = [];
				for each ( var item : UIComponent in uiList )
				{
					str = getQualifiedClassName( item );
					App.log.info( "当前剩余ui对象 ==> " + str , LogManager.LOG_NORMAL , null , null , 1 );
					if ( !obj[ str ] )
						obj[ str ] = 0;
					obj[ str ] += 1;
				}
				for ( var s : String in obj )
				{
					arr.push( { name: s , num: obj[ s ] } );
					App.log.info( "对象 ==> " + s + " ==> 总数量 ==> " + obj[ s ] , LogManager.LOG_NORMAL , null , null , 1 );
				}

				arr.sortOn( "num" , Array.NUMERIC | Array.DESCENDING );
				str = "";
				for ( var i : int = 0 ; i < 3 ; i++ )
				{
					if ( arr[ i ] )
						str += arr[ i ].name + " ==> 数量：" + arr[ i ].num + "\n";
				}
				App.log.info( "ui使用数量最多前三组件： \n" + str , LogManager.LOG_NORMAL , null );
			}
			App.log.info( "ui数量统计结果 ==> " + uiCount , LogManager.LOG_DEBUG , null , null , 1 );
		}
	}
}
