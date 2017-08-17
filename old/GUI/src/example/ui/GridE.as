package example.ui
{
	import UI.App;
	import UI.abstract.component.control.container.Container;
	import UI.abstract.component.control.grid.GridData;
	import UI.abstract.component.control.layout.VLayout;
	import UI.abstract.component.event.DragEvent;
	import UI.theme.defaulttheme.Grid;
	import UI.theme.defaulttheme.text.TextInput;
	
	import flash.display.Sprite;
	import flash.events.FocusEvent;
	
	public class GridE extends Sprite
	{
		private var layout:VLayout = new VLayout(10,10,3);
		private var array:Array = new Array();
		public function GridE()
		{
			super();
			
			var box8 : Container = new Container();
			box8.x = 50
			box8.y = 50
			addChild(box8);
			var i : int;
			var j : int;
			/** Grid **/
			var grid : Grid;
			for ( i = 0; i < 3; i++ )
			{
				for ( j = 0; j < 3; j++ )
				{
					grid = new Grid();
					grid.setSize( 40, 40 );
					//grid.x = i*45;
					//grid.y = j*45;
					array.push(grid);
					box8.addChild( grid );
					if ( i == 0 && j == 0 )
					{
						grid.imageUrl = "image.png";
						grid.num = 2;
					}
					if ( i == 0 && j == 2 )
					{
						grid.imageUrl = "scale9.png"; 
						grid.num = 99;
					}
				}
			}
			layout.itemArray = array;
			layout.updateDisplayList();
			App.event.addEvent( box8, DragEvent.DRAG_COMPLETE, onGridDragComplete );
			
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
			textInput.name = "gapImageToGrid";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 60;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "isShowZero";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 90;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "imageUrl";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 120;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "num";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 150;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
		}
		private function onFocusIn(event:FocusEvent):void{
			
		}
		private function onFocusOut(event:FocusEvent):void{
			if(event.currentTarget.name == "gapH" || event.currentTarget.name == "gapW"){
				//win.set9Gap(int((getChildByName("gapW") as TextInput).text),int((getChildByName("gapH") as TextInput).text));
			}
			else{
				for(var i:int = 0;i<array.length;i++){
					array[i][event.currentTarget.name] = (getChildByName(event.currentTarget.name) as TextInput).text;
				}
				layout.updateDisplayList();
			}
			
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
				grid1.num = grid1.num - 1;
			}
		}
	}
}