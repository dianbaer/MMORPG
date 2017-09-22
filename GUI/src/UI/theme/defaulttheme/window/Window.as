package UI.theme.defaulttheme.window
{
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.control.window.AWindow;
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.button.Button;

	public class Window extends AWindow
	{
		public function Window ()
		{
			super();

			bgUrl = "ui/Default_bg.png";
			set9Gap( 50 ,40 );
			closeBtn = new Button(Skin.CLOSE_BUTTON);
			closeBtn.setSize( 30, 25 )
			content = new Container();

			gapClosBtnH = 10;
			gapClosBtnV = 10;

			contentX = 30;
			contentY = 45;
		}
	}
}
