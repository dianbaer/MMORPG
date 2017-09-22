package ui
{
	import UI.abstract.component.control.container.Box;
	import UI.theme.defaulttheme.text.TextArea;
	
	public class Debug extends Box
	{
		private var nowNum:int = 0
		private var text:TextArea;
		public function Debug()
		{
			super();
			text = new TextArea();
			text.setSize(200,300);
			addChild(text);
		}
		public function addMessage(str:String,num:int):void{
			//if(num == nowNum){
			//	return;
			//}
			nowNum++;
			text.text = text.text+"\n\r"+str;
		}
		public function clearMessage():void{
			if(nowNum == 10){
				text.text = "";
				nowNum = 0;
			}
			
		}
		override public function get width():Number{
			return getAllChildrenSize().x;
		}
		override public function get height():Number{
			return getAllChildrenSize().y;
		}
	}
}