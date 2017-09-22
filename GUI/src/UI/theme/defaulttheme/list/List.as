package UI.theme.defaulttheme.list
{
	import UI.abstract.component.control.layout.VLayout;
	import UI.abstract.component.control.list.AList;
	import UI.abstract.component.control.list.ListItemContainer;
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.scrollBar.ScrollPanel;

	public class List extends AList
	{
		public function List (skin : String = Skin.TEXTINPUT)
		{
			super();
			scrollBar = new ScrollPanel();
			scrollBar.isShowHScrollbar = false;

			container = new ListItemContainer();
			

			itemRenderer = ListObject;
			layout = new VLayout( 0 , 5 , 1 );
			if ( skin )
				bgUrl = Skin.getList( skin )[ 0 ];
			set9Gap( 5 , 5 );
		}
	}
}
