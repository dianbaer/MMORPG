package example
{
	import flash.display.Sprite;
	import flash.display.StageAlign;
	import flash.display.StageScaleMode;
	import flash.events.KeyboardEvent;
	import flash.ui.Keyboard;
	
	import UI.App;
	import UI.abstract.component.control.button.RadioButtonGroup;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.control.dropDownMenu.MenuData;
	import UI.abstract.component.control.grid.GridData;
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.mc.MovieClip;
	import UI.abstract.component.control.text.TextStyle;
	import UI.abstract.component.control.tree.TreeData;
	import UI.abstract.component.data.DataProvider;
	import UI.abstract.component.event.DragEvent;
	import UI.abstract.resources.AnCategory;
	import UI.abstract.resources.ResourceUtil;
	import UI.abstract.tween.TweenManager;
	import UI.abstract.utils.Stats;
	import UI.theme.defaulttheme.Grid;
	import UI.theme.defaulttheme.ProgressBar;
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.button.Button;
	import UI.theme.defaulttheme.button.CheckBox;
	import UI.theme.defaulttheme.button.RadioButton;
	import UI.theme.defaulttheme.button.ToggleButton;
	import UI.theme.defaulttheme.dataGrid.DataGrid;
	import UI.theme.defaulttheme.dropDownList.DropDownList;
	import UI.theme.defaulttheme.dropDownMenu.DropDownMenu;
	import UI.theme.defaulttheme.list.List;
	import UI.theme.defaulttheme.menuBar.MenuBar;
	import UI.theme.defaulttheme.scrollBar.HScrollBar;
	import UI.theme.defaulttheme.scrollBar.ScrollPanel;
	import UI.theme.defaulttheme.slider.HSlider;
	import UI.theme.defaulttheme.tabPanel.TabPanel;
	import UI.theme.defaulttheme.text.Label;
	import UI.theme.defaulttheme.text.TextArea;
	import UI.theme.defaulttheme.text.TextInput;
	import UI.theme.defaulttheme.tree.Tree;
	import UI.theme.defaulttheme.window.Window;
	
	

	[SWF(width="1000", height="580")]
	public class GUI1 extends Sprite
	{
		private var tabPanel : TabPanel;
		public function GUI1()
		{
			super();
			stage.align = StageAlign.TOP_LEFT;
			stage.scaleMode = StageScaleMode.NO_SCALE;
			
			App.init( this );
			//JugglerManager.init(this.stage);
			TweenManager.initClass();
			createUi();
		}
		private function onClick(event:KeyboardEvent):void{
			if(event.keyCode == Keyboard.A){
				removeEventListener(KeyboardEvent.KEY_DOWN,onClick);
				tabPanel.dispose();
				tabPanel = null;
				TweenManager.clearTween();
			}
			
		}
		private function createUi() : void
		{
			
			
			var arr : Array = [];
			var i : int;
			var j : int;
			addEventListener(KeyboardEvent.KEY_DOWN,onClick);
			/** TabPanel **/
			tabPanel = new TabPanel();
			//tabPanel.addEventListener(MouseEvent.CLICK,onClick);
			tabPanel.setSize( 600 , 500 );
			this.addChild( tabPanel );
			
			
			
			var star:Stats = new Stats();
			star.x = 600;
			addChild(star);
			
			
			//			return;
			var box1 : Container = new Container();
			tabPanel.addTab( box1 , "Text" );
			
			
			/** Label **/
			var lb : Label = new Label( "Label" );
			lb.width = 200
			box1.addChild( lb );
			
			/** TextInput **/
			var tp : TextInput = new TextInput();
			tp.size = 12;
			tp.width = 100;
			tp.height = 50;
			tp.y = 50;
			tp.text = "TextInput";
			box1.addChild( tp );
			
			/** TextArea **/
			var textArea : TextArea = new TextArea();
			textArea.text = "TextArea";
			textArea.setSize( 150 , 150 );
			textArea.x = 150;
			box1.addChild( textArea );
			
			var box2 : Container = new Container();
			tabPanel.addTab( box2 , "Image" );
			
			/** Image **/
			var image : Image = new Image();
			//			image.setSize(50,50)
			image.url = ResourceUtil.getResourcePathByXML("ui/mySpritesheet.xml",ResourceUtil.IMAGE,"bgLayer2");
			image.setSize(200,200);
			//			image.url = "2.png";
			box2.addChild( image );
			
			var box3 : Container = new Container();
			tabPanel.addTab( box3 , "Button" );
			
			/** Button **/
			var btn : Button = new Button(Skin.BUTTON1);
			btn.setSize(145,61);
			btn.set9Gap(0,0)
			btn.text = "Button";
			box3.addChild( btn );
			
			/** CheckBox **/
			var check : CheckBox = new CheckBox();
			check.text = "CheckBox";
			check.y = 50;
			box3.addChild( check );
			
			/** RadioButton RadioButtonGroup **/
			var radio : RadioButton = new RadioButton();
			radio.text = "RadioButton1";
			radio.selected = true;
			radio.x = 150;
			box3.addChild( radio );
			
			var radio1 : RadioButton = new RadioButton();
			radio1.text = "RadioButton2";
			radio1.y = radio.height + 5;
			radio1.x = 150;
			box3.addChild( radio1 );
			
			var radio2 : RadioButton = new RadioButton();
			radio2.text = "RadioButton3";
			radio2.y = radio1.y + radio1.height + 5;
			radio2.x = 150;
			box3.addChild( radio2 );
			
			var radioGroup : RadioButtonGroup = new RadioButtonGroup();
			radioGroup.addButton( radio );
			radioGroup.addButton( radio1 );
			radioGroup.addButton( radio2 );
			radioGroup.selected = radio;
			
			/** ToggleButton RadioButtonGroup **/
			var toggle : ToggleButton = new ToggleButton();
			toggle.text = "ToggleButton1";
			toggle.setSize( 200, 30 );
			toggle.selected = true;
			toggle.x = 350;
			box3.addChild( toggle );
			
			var toggle1 : ToggleButton = new ToggleButton();
			toggle1.text = "ToggleButton2";
			toggle1.setSize( 200, 30 );
			toggle1.y = toggle.height + 5;
			toggle1.x = 350;
			box3.addChild( toggle1 );
			
			var toggle2 : ToggleButton = new ToggleButton();
			toggle2.text = "ToggleButton3";
			toggle2.setSize( 200, 30 );
			toggle2.y = toggle1.y + toggle1.height + 5;
			toggle2.x = 350;
			box3.addChild( toggle2 );
			
			var toggleGroup : RadioButtonGroup = new RadioButtonGroup();
			toggleGroup.addButton( toggle );
			toggleGroup.addButton( toggle1 );
			toggleGroup.addButton( toggle2 );
			toggleGroup.selected = toggle;
			
			
			var box4 : Container = new Container();
			tabPanel.addTab( box4 , "other" );
			
			/** Window **/
			var btn2 : Button = new Button();
			btn2.text = "window";
			var win : Window = new Window();
			win.x = 230;
			win.x = 100;
			win.setSize( 250 , 250 );
			win.addChild( btn2 );
			box4.addChild( win );
			
			/** Slider **/
			var slider : HSlider = new HSlider();
			slider.setRange( 0 , 150 );
			slider.y = 50;
			slider.tick = 30;
			slider.setSize( 150 , 20 );
			box4.addChild( slider );
			
			var hscrollbar : HScrollBar = new HScrollBar();
			hscrollbar.x = 200;
			hscrollbar.y = 30
			hscrollbar.tick = 30;
			hscrollbar.trackTick = 20;
			box4.addChild(hscrollbar);
			
			

			
			var box5 : Container = new Container();
			tabPanel.addTab( box5 , "ScrollBar" );
			
			var scroll : ScrollPanel = new ScrollPanel();
			var im : Image           = new Image();
			im.isResizeDispatchEvent = true;
			im.setSize(350,350);
			im.url = "image.png";
			scroll.addChildToPanel( im );
			scroll.setSize( 300 , 300 );
			box5.addChild( scroll );
			
			var box6 : Container = new Container();
			tabPanel.addTab( box6 , "List" );
			
			/** List **/
			var imm : Image = new Image();
			imm.setSize( 200 , 100 );
			imm.url = "image.png";
			var arr1 : Array         = [ "0" , "1" , "2" , "3" , imm , "4" , "5" , "6" , "7" , "8" , "9" ];
			var data1 : DataProvider = new DataProvider( arr1 );
			var list : List          = new List();
			//			UIComponent(list.container).removeEvent();
			list.scrollBar.isShowHScrollbar = false;
			list.setSize( 200 , 200 );
			list.dataProvider = data1;
			list.selectedIndex = 0;
			box6.addChild( list );
			
			/** DropDownList **/
			var dropList : DropDownList = new DropDownList();
			var arr2 : Array            = [ "0" , "1" , "2" , "3" , "4" , "5" , "6" , "7" , "8" , "9" ];
			var data2 : DataProvider    = new DataProvider( arr2 );
			dropList.setSize( 100 , 200 );
			dropList.dataProvider = data2;
			dropList.x = 10;
			dropList.y = 210;
			dropList.selectedIndex = 0;
			dropList.isClickText = true;
			box6.addChild( dropList );
			
			
			var box7 : Container = new Container();
			tabPanel.addTab( box7 , "Tree" );
			/** tree **/
			arr = []
			var treeData : TreeData;
			for ( i = 0 ; i < 3 ; i++ )
			{
				var treeData1 : TreeData = new TreeData();
				treeData1.level = 0;
				treeData1.id = i;
				treeData1.canExpand = true;
				//treeData1.opened = true
				treeData1.text = "id:" + i + "level" + 0;
				arr.push( treeData1 );
				for ( j = 0 ; j < 4 ; j++ )
				{
					var treeData2 : TreeData = new TreeData();
					treeData2.parent = treeData2;
					treeData2.level = 1;
					treeData2.id = j;
					treeData2.text = "id:" + j + "level:" + 1;
					treeData2.canExpand = true;
					//treeData2.opened = true
					arr.push( treeData2 );
					for ( var n : int = 0 ; n < 5 ; n++ )
					{
						var treeData3 : TreeData = new TreeData();
						treeData3.parent = treeData3;
						treeData3.level = 2;
						treeData3.id = n;
						treeData3.text = "id:" + n + "level:" + 2;
						arr.push( treeData3 );
					}
				}
			}
			var dataProvider : DataProvider = new DataProvider( arr );
			var tree : Tree                 = new Tree();
			tree.scrollBar.isShowHScrollbar = false;
			tree.setSize( 200 , 300 );
			tree.dataProvider = dataProvider;
			box7.addChild( tree );
			
			var box8 : Container = new Container();
			tabPanel.addTab( box8 , "grid" );
			
			/** Grid **/
			var grid : Grid;
			for ( i = 0; i < 5; i++ )
			{
				for ( j = 0; j < 5; j++ )
				{
					grid = new Grid();
					grid.setSize( 40, 40 );
					grid.x = i*45;
					grid.y = j*45;
					box8.addChild( grid );
					if ( i == 2 && j == 3 )
					{
						grid.imageUrl = "image.png";
						grid.num = "2";
					}
					if ( i == 4 && j == 1 )
					{
						grid.imageUrl = "scale9.png"; 
						grid.num = "99";
					}
				}
			}
			
			/** ProgressBar **/
			var progress : ProgressBar = new ProgressBar();
			progress.setSize( 200, 40 );
			box4.addChild( progress );
			progress.value = 0;
			progress.x = 200;
			progress.y = 250;
			//TweenManager.to( progress, 3, { repeat : -1, repeatDelay : 1, value : 1} );
			
			
			/** DataGrid **/
			arr = [];
			for ( i = 0 ; i < 20 ; i++ )
				for ( j = 0 ; j < 3 ; j++ )
				{
					if ( !arr[ i ] )
						arr[ i ] = [];
					if ( j == 0 && i == 1 )
					{
						var image11 : Image = new Image();
						image11.url = "image.png";
						image11.setSize( 50 , 50 );
						arr[ i ].push( image11 );
						continue;
					}
					if ( j == 1 && i == 1 )
					{
						var image22 : Image = new Image();
						image22.url = "image.png";
						image22.setSize( 100 , 100 );
						arr[ i ].push( image22 );
						continue;
					}
					
					arr[ i ].push( ( i + j ).toString() );
				}
			var dgData : DataProvider = new DataProvider( arr );
			var dg : DataGrid         = new DataGrid();
			dg.x = 220;
			dg.setSize( 300 , 300 );
			dg.column = 3;
			dg.align = TextStyle.CENTER;
			dg.itemHeight = 20;
			for ( i = 0 ; i < dg.column ; i++ )
			{
				dg.setTitleWidth( i , 1/3 );
				dg.setTitleText( i , "列表" + i.toString() );
			}
			dg.dataProvider = dgData;
			box6.addChild( dg );
			
			App.event.addEvent( box8, DragEvent.DRAG_COMPLETE, onGridDragComplete );
			
			
			
			var menu : MenuData;
			var menu1 : MenuData;
			var menu2 : MenuData;
			var menuArr : Array = [];
			var menuArr1 : Array = [];
			var menuArr2 : Array = [];
			/** MenuBar **/
			var menuBar : MenuBar = new MenuBar();
			box4.addChild(menuBar);
			menuBar.setSize( 200, 25 );
			
			for ( i = 0 ; i < 5 ; i++ )
			{
				menu = new MenuData();
				menu.text = "菜单1";
				menuArr.push( menu );
				if ( i > 1 )
				{
					menuArr1 = [];
					for ( j = 0 ; j < 4 ; j++ )
					{
						menu1 = new MenuData();
						menu1.text =  "菜单子2";
						menuArr1.push( menu1 );
						if ( j == 0 ) 
						{
							menuArr2 = [];
							for ( n = 0 ; n < 3 ; n++ )
							{
								menu2 = new MenuData();
								menu2.text = "菜单子3";
								menuArr2.push( menu2 );
							}
							menu1.items = new DataProvider( menuArr2 );
						}
					}
					menu.items = new DataProvider( menuArr1 );
				}
			}
			var dataprovider : DataProvider = new DataProvider( menuArr );
			menuBar.addItem("文件", dataprovider ); 
			
			menuArr = [];
			for ( i = 0 ; i < 5 ; i++ )
			{
				menu = new MenuData();
				menu.text = "菜单2";
				menuArr.push(menu);
			} 
			dataprovider = new DataProvider( menuArr );
			menuBar.addItem("编辑", dataprovider ); 
			
			menuArr = [];
			for ( i = 0 ; i < 5 ; i++ )
			{
				menu = new MenuData();
				menu.text = "菜单3";
				menuArr.push(menu);
			}
			dataprovider = new DataProvider( menuArr );
			menuBar.addItem("工具", dataprovider ); 
			
			menuArr = [];
			for ( i = 0 ; i < 5 ; i++ )
			{
				menu = new MenuData();
				menu.text = "菜单4";
				menuArr.push(menu);
			}
			dataprovider = new DataProvider( menuArr );
			menuBar.addItem("帮助", dataprovider ); 
			
			
			
			var arrData : Array = [];
			arr     = [ "常规模式" , "设置不可达点" , "设置可达点" , "设置遮挡区域" , "设置孤岛区域" ];
			var data : MenuData;
			for ( i = 0 ; i < arr.length ; i++ )
			{
				data = new MenuData();
				data.text = arr[ i ];
				arrData.push( data );
			}
			var _rightMenuData:DataProvider = new DataProvider( arrData );
			//右键菜单
			var _rightMenu:DropDownMenu = new DropDownMenu();
			_rightMenu.setPosition(50,250);
			_rightMenu.width = 110;
			_rightMenu.align = TextStyle.LEFT;
			_rightMenu.isSelectIcon = true;
			_rightMenu.isAutoDispose = false;
			_rightMenu.dataProvider = _rightMenuData;
			_rightMenu.selectIndex( 0 );
			box4.addChild(_rightMenu);
			
			
			var mc:MovieClip = new MovieClip(ResourceUtil.getResourcePathByXML("ui/anger.xml",ResourceUtil.MC,"jlkj/image "),30);
			mc.x = 0;
			mc.y = 0;
			mc.widthPercent = 0.5;
			mc.heightPercent = 0.5;
			box4.addChild(mc);
			
			var image1:Image = new Image();
			image1.url = ResourceUtil.getAnimationBitmapData5(ResourceUtil.getAnimationURL(AnCategory.NPC,"500000025",0),2,3,2);
			image1.x = 300;
			image1.y = 250;
			box4.addChild(image1);
			
			//var animation:Animation = new Animation(AnCategory.NPC,"500000025",0,true,1.0,AnConst.DOWN);
			//animation.dir = 4;
			//animation.action = 1;
			//animation.x = 200;
			//animation.y = 350;
			//box4.addChild(animation);
		}
		private function onGridDragComplete( e : DragEvent) : void
		{
			var grid1 : Grid = e.dragTarget as Grid;
			var grid2 : Grid = e.dropTarget as Grid;
			
			if ( grid2 )
			{
				var obj : GridData = grid1.data;
				grid1.data = grid2.data;
				grid2.data = obj;
			}
			else
			{
				//grid1.num = grid1.num - 1;
			}
		}
	}
}