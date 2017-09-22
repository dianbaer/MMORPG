package UI.theme.defaulttheme.scrollBar
{
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.scrollBar.AHScrollBar;
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.button.Button;
	import UI.theme.defaulttheme.slider.HSlider;

	public class HScrollBar extends AHScrollBar
	{
		public function HScrollBar ( scrollLeftBtnSkin : String = Skin.SCROLL_LEFT_BUTTON , scrollRightBtnSkin : String = Skin.SCROLL_RIGHT_BUTTON , scrollBarSkin : String =
									 Skin.SCROLL_BAR , scrollTrackSkin : String = Skin.SCROLL_TRACK )
		{
//			back = new Image();
			slider = new HSlider( scrollBarSkin , scrollTrackSkin );
			slider.bar.set9Gap( 5 , 5 );
			slider.track.set9Gap( 5 , 5 );
			addBtn = new Button( scrollLeftBtnSkin );
			addBtn.setSize( 18 , 18 )
			addBtn.set9Gap( 5 , 5 );
			reduceBtn = new Button( scrollRightBtnSkin );
			reduceBtn.setSize( 18 , 18 );
			reduceBtn.set9Gap( 5 , 5 );
			setSize( 200 , 18 );
		}
	}
}
