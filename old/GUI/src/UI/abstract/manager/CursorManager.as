package UI.abstract.manager
{
	import UI.App;
	import UI.abstract.component.control.base.UIComponent;
	
	import flash.display.BitmapData;
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.utils.Dictionary;

	public class CursorManager
	{
		private var root : Sprite;
		private var cursorDict : Dictionary;
		private var cursorNameDict : Dictionary;
		public function CursorManager()
		{
//			if ( _instance != null )
//				throw Error( SINGLETON_MSG );
//			_instance = this;
			this.root = App.ui.root;
			cursorDict = new Dictionary(true);
			cursorNameDict = new Dictionary(true);
//			App.event.addEvent( this.root.stage , MouseEvent.MOUSE_MOVE , onMouseMove );
		}
		private function onMouseMove(event:MouseEvent):void{
			
		}
		public function registerCursor(uiComponent : UIComponent,bitmapData:BitmapData = null,isPixel:Boolean = false):void{
			
		}
		public function destroyCursor(uiComponent : UIComponent):void{
			
		}
//		public static function get instance () : CursorManager
//		{
//			if ( _instance == null )
//				_instance = new CursorManager();
//			return _instance;
//		}
//		protected static var _instance : CursorManager;
//		
//		protected const SINGLETON_MSG : String = "CursorManager Singleton already constructed!";
	}
}
class CursorVO
{
	
	private var isPixel:Boolean;
	private var name:String;
	public function CursorVO ()
	{
	}
	
	public function dispose () : void
	{
		
	}
}