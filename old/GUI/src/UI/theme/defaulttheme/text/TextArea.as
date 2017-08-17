package UI.theme.defaulttheme.text
{
	import UI.abstract.component.control.text.ATextArea;
	import UI.abstract.component.control.text.ATextInput;
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.scrollBar.ScrollPanel;

	public class TextArea extends ATextArea
	{
		public function TextArea ( skin : String = Skin.TEXTINPUT )
		{
			super();
			scrollBar = new ScrollPanel();
			scrollBar.isDown = true;
			//scrollBar.isShowHScrollbar = false;

			textInput = new ATextInput();
			textInput.wordWrap = true;
			textInput.isResizeDispatchEvent = true;
			if ( skin )
				bgUrl = Skin.getList( skin )[ 0 ];
			set9Gap( 5 , 5 );
		}
	}
}
