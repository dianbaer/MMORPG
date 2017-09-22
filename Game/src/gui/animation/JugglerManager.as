package gui.animation
{
    import flash.display.Stage;
    import flash.events.Event;
    import flash.utils.getTimer;
    
    import UI.abstract.utils.CommonPool;

    
    public class JugglerManager
    {
        
        private static var stage:Stage;
		
		public static var processTime : uint;
		public static var oneuggler:Juggler;
		public static var twoJuggler:Juggler;
		public static var threeJuggler:Juggler;
		public static var fourJuggler:Juggler;
        public function JugglerManager()
        {
          
        }
		public static function init(stage1:Stage):void{
			stage = stage1;
			processTime = getTimer()
			oneuggler = new Juggler();
			twoJuggler = new Juggler();
			threeJuggler = new Juggler();
			fourJuggler = new Juggler();
			stage.addEventListener(Event.ENTER_FRAME,onEnterFrame);
		}
		public static function onEnterFrame(event:Event):void
        {
            var now:Number = getTimer();
            var passedTime:Number = (now - processTime)/1000.0;
			processTime = now;
            if(passedTime == 0.0){
				//var helpObj4:Object = CommonPool.fromPoolObject();
				//helpObj4.message = "当前帧经过的时间是0秒";
				//AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj4);
				return;
			}
            oneuggler.advanceTime(passedTime);
			twoJuggler.advanceTime(passedTime);
			threeJuggler.advanceTime(passedTime);
			fourJuggler.advanceTime(passedTime);
        }
    }
}