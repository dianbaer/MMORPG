package UI.theme.defaulttheme.tree
{
	import UI.abstract.component.control.layout.VLayout;
	import UI.abstract.component.control.tree.ATree;
	import UI.abstract.component.control.tree.TreeItemContainer;
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.scrollBar.ScrollPanel;

	public class Tree extends ATree
	{
		public function Tree (skin : String = Skin.TEXTINPUT)
		{
			super();

			scrollBar = new ScrollPanel();
			scrollBar.isShowHScrollbar = false;

			container = new TreeItemContainer();
			

			itemRenderer = TreeObject;
			layout = new VLayout( 0 , 2 , 1 );
			
			if ( skin )
				bgUrl = Skin.getList( skin )[ 0 ];
			set9Gap( 5 , 5 );
		}
	}
}
