package UI.abstract.component.control.tabPanel
{
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.button.ITriggerButton;

	public class TabData
	{
		/** 按钮 **/
		public var button : ITriggerButton;

		/** 内容 **/
		public var content : UIComponent;

		public function TabData ( button : ITriggerButton , content : UIComponent )
		{
			this.button = button;
			this.content = content;
		}

		public function dispose () : void
		{
			if ( button && button is UIComponent )
			{
				UIComponent( button ).dispose();
				button = null;
			}
			if ( content && content is UIComponent )
			{
				UIComponent( content ).dispose();
				content = null;
			}
		}
	}
}
