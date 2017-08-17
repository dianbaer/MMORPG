package example.ui
{
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.text.TextStyle;
	import UI.abstract.component.control.tree.TreeData;
	import UI.abstract.component.data.DataProvider;
	import UI.theme.defaulttheme.dataGrid.DataGrid;
	import UI.theme.defaulttheme.text.TextInput;
	import UI.theme.defaulttheme.tree.Tree;
	
	import flash.display.Sprite;
	import flash.events.FocusEvent;
	
	public class TreeE extends Sprite
	{
		private var tree : Tree;
		public function TreeE()
		{
			super();
			/** tree **/
			var arr : Array = [];
			var i : int;
			var j : int;
			var treeData : TreeData;
			for ( i = 0 ; i < 3 ; i++ )
			{
				var treeData1 : TreeData = new TreeData();
				treeData1.level = 0;
				treeData1.id = i;
				treeData1.canExpand = true;
				treeData1.opened = true;
				treeData1.text = "id:" + i + "level" + 0;
				arr.push( treeData1 );
				for ( j = 0 ; j < 4 ; j++ )
				{
					var treeData2 : TreeData = new TreeData();
					treeData2.parent = treeData1;
					treeData2.level = 1;
					treeData2.id = j;
					treeData2.text = "id:" + j + "level:" + 1;
					treeData2.canExpand = true;
					//treeData2.opened = true;
					arr.push( treeData2 );
					for ( var n : int = 0 ; n < 5 ; n++ )
					{
						var treeData3 : TreeData = new TreeData();
						treeData3.parent = treeData2;
						treeData3.level = 2;
						treeData3.id = n;
						treeData3.text = "id:" + n + "level:" + 2;
						arr.push( treeData3 );
					}
				}
			}
			
			var dataProvider : DataProvider = new DataProvider( arr );
			tree                 = new Tree();
			tree.scrollBar.isShowHScrollbar = false;
			tree.setSize( 200 , 300 );
			tree.setPosition(50,50);
			tree.dataProvider = dataProvider;
			addChild( tree );
			
			var textInput:TextInput = new TextInput();
			textInput.name = "width";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 0;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "height";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 30;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "scrollToTop";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 60;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "selectedIndex";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 90;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "itemHeight";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 120;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			
		}
		private function onFocusIn(event:FocusEvent):void{
			
		}
		private function onFocusOut(event:FocusEvent):void{
			if(event.currentTarget.name == "gapH" || event.currentTarget.name == "gapW"){
				tree.set9Gap(int((getChildByName("gapW") as TextInput).text),int((getChildByName("gapH") as TextInput).text));
			}
			else{
				
				tree[event.currentTarget.name] = (getChildByName(event.currentTarget.name) as TextInput).text;
			}
			
		}
	}
}