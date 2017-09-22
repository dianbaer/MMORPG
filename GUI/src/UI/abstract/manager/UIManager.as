package UI.abstract.manager
{
	import UI.App;
	import UI.abstract.component.control.base.UIComponent;
	
	import flash.display.DisplayObject;
	import flash.display.DisplayObjectContainer;
	import flash.display.Sprite;
	import flash.utils.getQualifiedClassName;

	public class UIManager
	{
		/** 顶层 **/
		private var _root : Sprite;

		/** 层级列表 **/
		private var _layers : Array = [];
		
		public function UIManager ()
		{
//			if (_instance != null) throw Error(SINGLETON_MSG);
//			_instance = this;
		}

		public function init ( root : Sprite ) : void
		{
			this._root = root;
		}
		
		/**
		 * 顶层
		 */
		public function get root () : Sprite
		{
			return _root;
		}
		
		/**
		 * 注册层级
		 */
		public function registerLayer ( index : int ) : void
		{
			if ( _layers[index] )
				throw new Error( "注册层级已经存在" );
			
			var layer : UIComponent = new UIComponent();
			_layers[index] = layer;
			var j : int = 0;
			for ( var i : int; i < _layers.length; i++ )
			{
				if ( _layers[i] )
				{
					_root.addChildAt( _layers[i], j );
					j++;
				}
			}
		}
		
		/**
		 * 移出层级
		 */
		public function removeLayer ( index : int ) : void
		{
			if ( !_layers[index] )
			{
				App.log.info( "移出的层级不存在", LogManager.LOG_WARN, getQualifiedClassName(this), "removeLayer" );				
				return;
			}
			var layer : UIComponent = _layers[index];
			this._root.removeChild( layer );
			layer.dispose();
		}
		
		/**
		 * 获取层级
		 */
		public function getLayer ( index : int ) : UIComponent
		{
			if ( !_layers[index] )
			{
				App.log.info( "获取的层级不存在", LogManager.LOG_WARN, getQualifiedClassName(this), "getLayer" );				
				return null;
			}
			
			return _layers[index];
		}
		
		/**
		 * 隐藏显示层级
		 */
		public function visibleLayer ( index : int, isVisible : Boolean ) : void
		{
			if ( !_layers[index] )
			{
				App.log.info( "隐藏显示层级不存在", LogManager.LOG_WARN, getQualifiedClassName(this), "visibleLayer" );				
				return;
			}
			_layers[index].visible = isVisible
		}
		
		/**
		 * 遍历父类，查找需要的父类对象
		 *
		 */
		public function selectParent ( target : DisplayObject , className : Class = null , object : DisplayObject = null ) : DisplayObject
		{
			while ( target != this.root.stage )
			{
				if ( className )
				{
					if ( target is className )
					{
						break;
					}
				}
				if ( object )
				{
					if ( target == object )
					{
						break;
					}
				}
				target = target.parent;
			}
			return target;
		}

		/**
		 * 搜索容器中指定类型的对象并以数组方式返回
		 */
		public static function searchChild ( container : DisplayObjectContainer , searchType : Class ) : Array
		{

			var list_child : Array = [];

			var i : int            = 0;
			var c : int            = container.numChildren;
			while ( i < c )
			{

				var child : DisplayObject = container.getChildAt( i );

				if ( child is searchType )
				{
					list_child.push( child );
				}
				if ( child is DisplayObjectContainer )
				{
					list_child = list_child.concat( searchChild( child as DisplayObjectContainer , searchType ) );
				}

				i++;
			}

			return list_child;

		}
		
		/**
		 * 居中
		 */
		public function center ( ui : UIComponent ) : void
		{
			ui.x = ( root.stage.stageWidth - ui.width ) >> 1;
			ui.y = ( root.stage.stageHeight - ui.height ) >> 1;
		}

//		public static function get instance() : UIManager
//		{
//			if ( _instance == null ) _instance = new UIManager();
//			return _instance;
//		}
//		protected static var _instance : UIManager;
//		protected const SINGLETON_MSG : String = "UIManager Singleton already constructed!";
	}
}
