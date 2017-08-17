package UI.theme.defaulttheme.button
{
	import UI.App;
	import UI.abstract.component.control.button.ALabelImageButton;
	import UI.abstract.component.control.button.ButtonStyle;
	import UI.abstract.resources.item.ImageResource;
	import UI.theme.defaulttheme.Skin;

	public class Button extends ALabelImageButton
	{
		private var skinList : Array = [];
		
		private var skin:String;

		public function Button ( skin : String = "Button_Default.png,Button_Down.png,Button_Over.png" , w : Number = 0 , h : Number = 0 )
		{
			super();
			skinList = Skin.getList( skin );
			this.skin = skin;
			setSize( w , h );
			set9Gap( 8 , 8 );
			App.loader.loadList( skinList , onLoadImage );
			currentState = ButtonStyle.UP;
		}

		/** 资源 **/
		public function setSkin ( skin : String ) : void
		{
			if(skin == null || skin == "" || this.skin == skin){
				return;
			}
			App.loader.canelLoadList( onLoadImage );
			skinList = Skin.getList( skin );
			this.skin = skin;
			App.loader.loadList( skinList , onLoadImage );
		}

		private function onLoadImage () : void
		{
			
			if ( App.loader.getResource( skinList[ 0 ] ) is ImageResource )
				_imageUp.url = skinList[ 0 ];

			if ( App.loader.getResource( skinList[ 1 ] ) is ImageResource )
				_imageDown.url = skinList[ 1 ];

			if ( App.loader.getResource( skinList[ 2 ] ) is ImageResource )
				_imageOver.url = skinList[ 2 ];
			
			
		}

	}
}
