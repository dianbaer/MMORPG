package UI.theme.defaulttheme
{
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.progressBar.AProgressBar;
	
	public class ProgressBar extends AProgressBar
	{
		
		public function ProgressBar( skin : String = "ProgressBar_Bg.png,ProgressBar_Bar.png" )
		{
			super();
			var arr : Array = Skin.getList( skin );
			bgUrl = arr[0];
			set9Gap( 5, 5 )
			bar = new Image();
			bar.url = arr[1];
			bar.set9Gap( 5,5);
			setRange( 0, 1 );
			isShowInfo = true;
		}
	}
}