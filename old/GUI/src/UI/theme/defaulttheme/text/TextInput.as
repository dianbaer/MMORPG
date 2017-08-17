package UI.theme.defaulttheme.text
{
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.text.ATextInput;
	import UI.theme.defaulttheme.Skin;

	import flash.display.DisplayObject;

	public class TextInput extends ATextInput
	{
		public function TextInput ( skin : String = "TextInput_Bg.png" )
		{
			super();
			if ( skin )
				bgUrl = Skin.getList( skin )[ 0 ];
			set9Gap( 5 , 5 );
			gapH = 5;
			
		}

	}
}
