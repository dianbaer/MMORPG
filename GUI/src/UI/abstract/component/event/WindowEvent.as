package UI.abstract.component.event
{

	public class WindowEvent extends UIEvent
	{
		/** 窗体点击关闭按钮事件 **/
		public static const CLOSE : String = "onClose";
		
		/** 提示窗口选择是 **/
		public static const YES : String = "YES";
		
		/** 提示窗口选择否 **/
		public static const NO : String = "NO";
		
		/** 提示窗口选择取消 **/
		public static const CANEL : String = "CANEL";

		public function WindowEvent ( type : String , bubbles : Boolean = true , cancelable : Boolean = false )
		{
			super( type , bubbles , cancelable );
		}
	}
}
