package UI.theme.defaulttheme.slider
{
	import UI.App;
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.slider.AVSlider;
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.button.Button;

	import flash.events.MouseEvent;

	public class VSlider extends AVSlider
	{
		public function VSlider ( sliderBarSkin : String = Skin.SCROLL_BAR , sliderTrackSkin : String = Skin.SCROLL_TRACK )
		{
			super();
			track = new Button( sliderTrackSkin );
			track.set9Gap( 5 , 5 );
			bar = new Button( sliderBarSkin );
			bar.set9Gap( 5 , 5 );
			_bar.setSize( 20 , 20 );
			App.event.removeEventByType( track , MouseEvent.MOUSE_OVER );
			showLabel = true;
			setSize( 20 , 100 );
			allowBackClick = true;
		}

		/** 背景资源 **/
		public function setTrackSkin ( sliderTrackSkin : String = Skin.HSLIDER_TRACK ) : void
		{
			( _track as Button ).setSkin( sliderTrackSkin );
		}

		/** 滑块资源 **/
		public function setBarSkin ( sliderBarSkin : String = Skin.HSLIDER_BAR ) : void
		{
			( _bar as Button ).setSkin( sliderBarSkin );
		}
	}
}
