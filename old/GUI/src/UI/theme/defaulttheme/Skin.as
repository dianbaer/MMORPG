package UI.theme.defaulttheme
{
	import UI.abstract.resources.ResourceUtil;
	
	import flash.geom.Rectangle;

	/**
	 * 默认皮肤资源
	 * 资源图顺序 1、默认状态 2、按下状态 3、划过状态 4、其他状态 5.....
	 */
	public class Skin
	{
		/**
		 * 获得资源数据
		 */
		public static function getList ( skin : String ) : Array
		{
			var arr : Array = skin.split( "," );
			var len : int   = arr.length;
			for ( var i : int = 0 ; i < len ; ++i )
				arr[ i ] = "ui/" + arr[ i ];
			return arr;
		}

		/** bg **/
		public static const BG : String                          = "bg.png";

		/** Button **/
		public static const BUTTON : String                      = "Button_Default.png,Button_Down.png,Button_Over.png";
		/** Button **/
		public static const BUTTON1 : String                      = ResourceUtil.getResourcePathByXML("mySpritesheet.xml",ResourceUtil.IMAGE,"obstacle4_0001")+","+ResourceUtil.getResourcePathByXML("mySpritesheet.xml",ResourceUtil.IMAGE,"obstacle4_crash")+","+ResourceUtil.getResourcePathByXML("mySpritesheet.xml",ResourceUtil.IMAGE,"obstacle4_0002");

		/** Button **/
		public static const CLOSE_BUTTON : String                = "CloseButton_Default.png,CloseButton_Down.png,CloseButton_Over.png";

		/** TextInput **/
		public static const TEXTINPUT : String                   = "TextInput_Bg.png";

		/** CheckBox **/
		public static const CHECKBOX : String                    = "CheckBox_Default.png,CheckBox_Down.png,CheckBox_Over.png,CheckBox_SelectedDefault.png,CheckBox_SelectedDown.png,CheckBox_SelectedOver.png";

		/** RadioButton **/
		public static const RADIOBUTTON : String                 = "RadioButton_Default.png,RadioButton_Down.png,RadioButton_Over.png,RadioButton_SelectedDefault.png,RadioButton_SelectedDown.png,RadioButton_SelectedOver.png";

		/** List **/
		public static const LIST_ITEM : String                   = "ListItem_Down.png,ListItem_Over.png";

		public static const LIST_ITEM_BG : String                = "ListItem_Bg.png";

		/** Slider **/
		public static const HSLIDER_BAR : String                 = "hSliderBar_Default.png,hSliderBar_Down.png,hSliderBar_Over.png";

		public static const HSLIDER_TRACK : String               = "hSliderTrack_Default.png,hSliderTrack_Down.png,hSliderTrack_Over.png";

		/** ScrollBar **/
		public static const SCROLL_BAR : String                  = "scrollBar_Default.png,scrollBar_Down.png,scrollBar_Over.png";

		public static const SCROLL_TRACK : String                = "scrollTrack_Default.png,scrollTrack_Down.png,scrollTrack_Over.png";

		public static const SCROLL_UP_BUTTON : String            = "scrollUpBtn_Default.png,scrollUpBtn_Down.png,scrollUpBtn_Over.png";

		public static const SCROLL_DOWN_BUTTON : String          = "scrollDownBtn_Default.png,scrollDownBtn_Down.png,scrollDownBtn_Over.png";

		public static const SCROLL_LEFT_BUTTON : String          = "scrollLeftBtn_Default.png,scrollLeftBtn_Down.png,scrollLeftBtn_Over.png";

		public static const SCROLL_RIGHT_BUTTON : String         = "scrollRightBtn_Default.png,scrollRightBtn_Down.png,scrollRightBtn_Over.png";

		/** TabPanel **/
		public static const TABPANEL_BUTTON : String             = "tabPanelBtn_Default.png,tabPanelBtn_Down.png,tabPanelBtn_Over.png";

		/** Tree **/
		public static const TREE_BUTTON_EXPAND : String          = "treeExpandBtn_Default.png,treeExpandBtn_Down.png,treeExpandBtn_Over.png";

		public static const TREE_BUTTON_SHRINK : String          = "treeShrinkBtn_Default.png,treeShrinkBtn_Down.png,treeShrinkBtn_Over.png";

		/** DropDownMenu **/
		public static const DROPDOWNMENU_ITEMICONPREFIX : String = "DropDownMenu_ItemIconPrefix.png";

		/** ProgressBar **/
		public static const PROGRESSBAR : String                 = "ProgressBar_Bg.png,ProgressBar_Bar.png";

	}
}
