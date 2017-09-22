package UI.theme.defaulttheme.text
{
	import UI.abstract.component.control.text.ALabel;

	public class Label extends ALabel
	{
		public function Label ( text : String = "" )
		{
			super();
			this.text = text;
			size = 14;
			bold = true;
			color = 0x00ff00;
		}
	}
}
