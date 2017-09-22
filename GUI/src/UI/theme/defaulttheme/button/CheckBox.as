package UI.theme.defaulttheme.button
{
	import UI.App;
	import UI.abstract.component.control.button.ACheckBox;
	import UI.abstract.component.control.button.ButtonStyle;
	import UI.abstract.resources.item.ImageResource;
	import UI.theme.defaulttheme.Skin;

	public class CheckBox extends ACheckBox
	{
		private var skinList : Array = [];
		
		private var skin:String;

		public function CheckBox ( skin : String = Skin.CHECKBOX , w : Number = 15 , h : Number = 15 )
		{
			super();
			skinList = Skin.getList( skin );
			this.skin = skin;
			setSize( w , h );
			//set9Gap( 5 , 5 );
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

			if ( App.loader.getResource( skinList[ 3 ] ) is ImageResource )
				_imageSelectUp.url = skinList[ 3 ];

			if ( App.loader.getResource( skinList[ 4 ] ) is ImageResource )
				_imageSelectDown.url = skinList[ 4 ];

			if ( App.loader.getResource( skinList[ 5 ] ) is ImageResource )
				_imageSelectOver.url = skinList[ 5 ];
		}
	}
}
