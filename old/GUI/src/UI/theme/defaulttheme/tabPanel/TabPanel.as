package UI.theme.defaulttheme.tabPanel
{
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.tabPanel.ATabPanel;
	import UI.abstract.component.control.tabPanel.TabData;
	import UI.abstract.component.control.tabPanel.TabPanelStyle;
	import UI.theme.defaulttheme.Skin;

	public class TabPanel extends ATabPanel
	{
		private var _buttonWidth : int;

		public function TabPanel ()
		{
			super();
			bgUrl = Skin.BG;
			direction = TabPanelStyle.UP;
		}

		/**
		 * 增加一组tab
		 */
		public function addTab ( content : UIComponent , text : String = "" ) : void
		{
			var btn : TabButton = new TabButton();
			if ( _buttonWidth )
				btn.width = _buttonWidth;
			btn.text = text;
			var data : TabData = new TabData( btn , content );
			this.addTabData( data );
		}

		/**
		 * 增加一组tab到某个位置
		 */
		public function addTabAt ( content : UIComponent , index : int , text : String = "" ) : void
		{
			var btn : TabButton = new TabButton();
			if ( _buttonWidth )
				btn.width = _buttonWidth;
			btn.text = text;
			var data : TabData = new TabData( btn , content );
			this.addTabDataAt( data , index );
		}

		/**
		 * 让按钮固定大小
		 */
		public function get buttonWidth () : int
		{
			return _buttonWidth;
		}

		public function set buttonWidth ( value : int ) : void
		{
			if(_buttonWidth == value){
				return;
			}
			_buttonWidth = value;
			for ( var i : int = 0 ; i < _tabData.length ; i++ )
				_tabData[ i ].button.width = _buttonWidth;
			nextDraw();
		}
	}
}
