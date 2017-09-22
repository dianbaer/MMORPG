package UI.theme.defaulttheme.dataGrid
{
	import UI.abstract.component.control.datagrid.ADataGrid;
	import UI.abstract.component.control.datagrid.DataGridItemContainer;
	import UI.abstract.component.control.layout.VLayout;
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.button.Button;
	import UI.theme.defaulttheme.scrollBar.ScrollPanel;

	public class DataGrid extends ADataGrid
	{
		public function DataGrid (skin : String = Skin.TEXTINPUT)
		{
			super();
			//滚动条
			scrollBar = new ScrollPanel();
			scrollBar.isShowHScrollbar = false;
			//this.addChild( scrollBar )

			//内容对象
			container = new DataGridItemContainer();
			//scrollBar.addChildToPanel( DataGridItemContainer( container ) );

			// render
			itemRenderer = DataGridObject;
			layout = new VLayout( 0 , 1 , 1 );
			titleHeight = 25;

			// 标题按钮类
			TitleClass = Button;

			// 显示标题
			isShowTitle = true;
			
			if ( skin )
				bgUrl = Skin.getList( skin )[ 0 ];
			set9Gap( 5 , 5 );
		}
	}
}
