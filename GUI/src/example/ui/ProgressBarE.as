package example.ui
{
	import UI.abstract.tween.TweenManager;
	import UI.theme.defaulttheme.ProgressBar;
	
	import flash.display.Sprite;
	
	public class ProgressBarE extends Sprite
	{
		public function ProgressBarE()
		{
			super();
			/** ProgressBar **/
			var progress : ProgressBar = new ProgressBar();
			progress.setSize( 200, 50 );
			addChild( progress );
			progress.value = 0;
			progress.x = 100;
			progress.text = "提示";
			progress.y = 100;
			//TweenManager.to( progress, 3, { repeat : 3, repeatDelay : 0.5, value : 1,yoyo:1,delay:1} );
		}
	}
}