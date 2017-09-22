package example.ui
{
	import UI.abstract.component.control.image.Image;
	import UI.abstract.component.control.text.TextStyle;
	import UI.abstract.component.data.DataProvider;
	import UI.theme.defaulttheme.dataGrid.DataGrid;
	import UI.theme.defaulttheme.text.TextInput;
	
	import flash.display.Sprite;
	import flash.events.FocusEvent;
	
	public class DataGridE extends Sprite
	{
		private var dg : DataGrid;
		public function DataGridE()
		{
			super();
			var arr : Array = [];
			var i : int;
			var j : int;
			
			arr = [];
			for ( i = 0 ; i < 20 ; i++ )
				for ( j = 0 ; j < 3 ; j++ )
				{
					if ( !arr[ i ] )
						arr[ i ] = [];
					if ( j == 0 && i == 0 )
					{
						var image11 : Image = new Image();
						image11.url = "image.png";
						image11.setSize( 50 , 10 );
						arr[ i ].push( image11 );
						continue;
					}
					if ( j == 1 && i == 0 )
					{
						var image22 : Image = new Image();
						image22.url = "image.png";
						image22.setSize( 600 , 100 );
						arr[ i ].push( image22 );
						continue;
					}
					
					arr[ i ].push( ( i + j ).toString() +"aaaaa");
				}
			var dgData : DataProvider = new DataProvider( arr );
			dg         = new DataGrid();
			dg.x = 50;
			dg.y = 50;
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
			addChild( dg );
			
			
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
			
			var textInput:TextInput = new TextInput();
			textInput.name = "titleHeight";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 150;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "gapToContent";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 180;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "isShowTitle";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 210;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
			
			var textInput:TextInput = new TextInput();
			textInput.name = "align";
			textInput.addEventListener(FocusEvent.FOCUS_IN,onFocusIn);
			textInput.addEventListener(FocusEvent.FOCUS_OUT,onFocusOut);
			textInput.x = 500;
			textInput.y = 240;
			textInput.width = 100;
			textInput.height = 20;
			addChild(textInput);
		}
		private function onFocusIn(event:FocusEvent):void{
			
		}
		private function onFocusOut(event:FocusEvent):void{
			if(event.currentTarget.name == "gapH" || event.currentTarget.name == "gapW"){
				dg.set9Gap(int((getChildByName("gapW") as TextInput).text),int((getChildByName("gapH") as TextInput).text));
			}else if(event.currentTarget.name == "imageWidth"){
				//imm.width = int((getChildByName(event.currentTarget.name) as TextInput).text);
			}else if(event.currentTarget.name == "imageHeight"){
				//imm.height = int((getChildByName(event.currentTarget.name) as TextInput).text);
			}
			else{
				
				dg[event.currentTarget.name] = (getChildByName(event.currentTarget.name) as TextInput).text;
			}
			
		}
	}
}