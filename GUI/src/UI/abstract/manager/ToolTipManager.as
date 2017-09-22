package UI.abstract.manager
{
	import UI.App;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.tooltip.BaseTooltip;
	
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.display.Stage;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.TimerEvent;
	import flash.utils.Dictionary;
	import flash.utils.Timer;

	public class ToolTipManager
	{
		private var root : Sprite;

		private var toolTipDict : Dictionary;

		private var toolTipClassDict : Dictionary;

		private var _nowTooltipVO : TooltipVO;

		private var timer : Timer              = new Timer( 500 );

		public function ToolTipManager ()
		{
//			if ( _instance != null )
//				throw Error( SINGLETON_MSG );
//			_instance = this;
			toolTipDict = new Dictionary();
			toolTipClassDict = new Dictionary();
			this.root = App.ui.root;
//			App.event.addEvent( this.root.stage , MouseEvent.MOUSE_OVER , onMouseOver );
//			App.event.addEvent( this.root.stage , MouseEvent.MOUSE_OUT , onMouseOut );
		}

		public function get nowTooltipVO () : TooltipVO
		{
			return _nowTooltipVO;
		}

		public function set nowTooltipVO ( value : TooltipVO ) : void
		{
			_nowTooltipVO = value;
			if ( _nowTooltipVO == null )
			{
				if ( timer.running )
				{
					timer.stop();
					timer.removeEventListener( TimerEvent.TIMER , onTimer );
				}
			}
			else
			{
				timer.reset();
				if ( !timer.running )
				{
					timer.addEventListener( TimerEvent.TIMER , onTimer );
				}
				timer.start();
			}
		}

		private function onTimer ( event : TimerEvent ) : void
		{
			var baseTooltip : BaseTooltip = toolTipClassDict[ nowTooltipVO.TooltipClass ];
			baseTooltip.data = nowTooltipVO.tooltip;
			root.addChild( baseTooltip );
			if ( timer.running )
			{
				timer.stop();
				timer.removeEventListener( TimerEvent.TIMER , onTimer );
			}
		}

		private function onMouseOver ( event : MouseEvent ) : void
		{
			var displayObject : DisplayObject = event.target as DisplayObject;
			//trace( "111111111111111" + event.target.toString() );
			while ( displayObject != this.root.stage )
			{
				if ( toolTipDict[ displayObject ] )
				{
					break;
				}
				displayObject = displayObject.parent;
			}
			if ( displayObject != this.root.stage )
			{
				var tooltipVO : TooltipVO = toolTipDict[ displayObject ];
				var baseTooltip : BaseTooltip;
				if ( !toolTipClassDict[ tooltipVO.TooltipClass ] )
				{
					baseTooltip = new tooltipVO.TooltipClass();
					toolTipClassDict[ tooltipVO.TooltipClass ] = baseTooltip;
				}
				baseTooltip = toolTipClassDict[ tooltipVO.TooltipClass ];
				baseTooltip.x = event.stageX;
				baseTooltip.y = event.stageY;
				nowTooltipVO = tooltipVO;
			}
		}

		private function onMouseOut ( event : MouseEvent ) : void
		{
			var displayObject : DisplayObject = event.target as DisplayObject;
			//trace( "222222222" + event.target.toString() );
			while ( displayObject != this.root.stage )
			{
				if ( toolTipDict[ displayObject ] )
				{
					if ( toolTipDict[ displayObject ] == nowTooltipVO )
					{
						break;
					}
				}
				displayObject = displayObject.parent;
			}
			if ( displayObject != this.root.stage )
			{
				var baseTooltip : BaseTooltip = toolTipClassDict[ nowTooltipVO.TooltipClass ];
				if ( baseTooltip.parent )
				{
					root.removeChild( baseTooltip );
				}
				nowTooltipVO = null;
			}
		}

		/**
		 * 添加
		 * @param uiComponent:UIComponent 添加tooltip的对象
		 * @param type : 事件类型
		 * @param callBackFun : 回调函数
		 */
		public function registerToolTip ( uiComponent : UIComponent , tooltip : Object , TooltipClass : Class ) : void
		{
			var tooltipVO : TooltipVO = toolTipDict[ uiComponent ];
			if ( tooltipVO == null )
			{
				tooltipVO = new TooltipVO();
				tooltipVO.tooltip = tooltip;
				tooltipVO.TooltipClass = TooltipClass;
				toolTipDict[ uiComponent ] = tooltipVO;
			}
			else
			{
				if ( tooltipVO.tooltip == tooltip && tooltipVO.TooltipClass == TooltipClass )
				{
					return;
				}
				else
				{
					tooltipVO.tooltip = tooltip;
					tooltipVO.TooltipClass = TooltipClass;
				}
			}
		}

		public function destroyToolTip ( uiComponent : UIComponent ) : void
		{
			var tooltipVO : TooltipVO = toolTipDict[ uiComponent ];
			if ( tooltipVO )
			{
				tooltipVO.dispose();
				delete toolTipDict[ uiComponent ];
			}
		}


//		public static function get instance () : ToolTipManager
//		{
//			if ( _instance == null )
//				_instance = new ToolTipManager();
//			return _instance;
//		}
//
//
//		protected static var _instance : ToolTipManager;
//
//		protected const SINGLETON_MSG : String = "ToolTipManager Singleton already constructed!";
	}
}

class TooltipVO
{
	public var tooltip : Object;

	public var TooltipClass : Class;

	public function TooltipVO ()
	{
	}

	public function dispose () : void
	{
		tooltip = null;
		TooltipClass = null;
	}
}
