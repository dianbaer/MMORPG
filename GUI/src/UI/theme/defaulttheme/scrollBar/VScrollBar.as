package UI.theme.defaulttheme.scrollBar
{
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.scrollBar.AVScrollbar;
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.button.Button;
	import UI.theme.defaulttheme.slider.VSlider;

	public class VScrollBar extends AVScrollbar
	{
		public function VScrollBar ( scrollUpBtnSkin : String = Skin.SCROLL_UP_BUTTON , scrollDownBtnSkin : String = Skin.SCROLL_DOWN_BUTTON , scrollBarSkin : String =
									 Skin.SCROLL_BAR , scrollTrackSkin : String = Skin.SCROLL_TRACK )
		{
//			back = new Image();
			slider = new VSlider( scrollBarSkin , scrollTrackSkin );
			slider.bar.set9Gap( 5 , 5 );
			slider.track.set9Gap( 5 , 5 );
			addBtn = new Button( scrollUpBtnSkin );
			addBtn.setSize( 18 , 18 )
			addBtn.set9Gap( 5 , 5 );
			reduceBtn = new Button( scrollDownBtnSkin );
			reduceBtn.setSize( 18 , 18 );
			reduceBtn.set9Gap( 5 , 5 );
			setSize( 18 , 200 );
		}
	}
}
