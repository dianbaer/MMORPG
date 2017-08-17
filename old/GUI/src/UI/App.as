package UI
{
	import UI.abstract.manager.CursorManager;
	import UI.abstract.manager.DragManager;
	import UI.abstract.manager.EventManager;
	import UI.abstract.manager.LogManager;
	import UI.abstract.manager.ToolTipManager;
	import UI.abstract.manager.UIManager;
	import UI.abstract.resources.ResourceManager;
	
	import flash.display.Sprite;
	import flash.display.Stage;

	/**
	 * 全局引用入口
	 */
	public class App 
	{
		/** 全局stage引用 **/
		public static var stage : Stage;
		

		/** 事件管理器 **/
		public static var event : EventManager;


		/** 资源加载管理器 **/
		public static var loader : ResourceManager;


		/** 日志管理器 **/
		public static var log : LogManager;        


		/** ui组件管理器 **/
		public static var ui : UIManager;          


		/** 提示管理器 **/
		public static var tip : ToolTipManager;    


		/** 拖拽管理器 **/
		public static var drag : DragManager;       


		/** 鼠标管理器 **/
		public static var cursor : CursorManager;   


		public static function init ( main : Sprite ) : void
		{
			stage = main.stage;
			ui  = new UIManager();
			ui.init( main );
			event = new EventManager();
			loader = new ResourceManager();
			log  = new LogManager();
			ui.init(main);
			tip = new ToolTipManager();
			drag = new DragManager();
			cursor = new CursorManager();
		}
	}
}
