package UI.theme.defaulttheme.dataGrid
{
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.datagrid.IItemRendererGrid;
	import UI.abstract.component.control.panel.Panel;
	import UI.abstract.component.control.text.ALabel;
	import UI.abstract.component.control.text.TextStyle;
	import UI.theme.defaulttheme.list.ListObject;
	import UI.theme.defaulttheme.text.Label;
	import UI.theme.defaulttheme.text.TextInput;
	
	import flash.display.DisplayObject;

	public class DataGridObject extends ListObject implements IItemRendererGrid
	{
		/** 数据对象 **/
		//private var _displayObjList : Array = [];

		private var _widthList : Array      = [];
		
		private var dataGridContainer:Panel;

		public function DataGridObject ()
		{
			super();
			dataGridContainer = new Panel();
			addChild(dataGridContainer);
		}

		public function set widthList ( value : Array ) : void
		{
			_widthList = value;
		}
		override public function forceUpdate():void{
			var panel:Panel;
			while(dataGridContainer.content.numChildren>0){
				var num:int = dataGridContainer.content.numChildren-1;
				panel = dataGridContainer.content.removeChildAt(num) as Panel;
				//如果数据源是一个显示对象，不注销，只移除
				if(data[num] is DisplayObject){
					panel.content.removeChildAt(0);
				}
				panel.dispose();
			}
			
			var len : int = _widthList.length;
			for(var i:int = 0;i<len;i++){
				panel = new Panel();
				dataGridContainer.content.addChild(panel);
				if(_data[i] is String){
					var label:Label = new Label();
					label.text = _data[i];
					panel.content.addChild(label);
				}else if(_data[i] is DisplayObject){
					panel.content.addChild(_data[i]);
				}
			}
			
			//不能小于最小高度
			if(_height<_minHeight){
				height = _minHeight;
			}
			nextDraw();
		}
		override protected function draw():void{
			super.draw();
			if(dataGridContainer){
				dataGridContainer.width = _width;
				dataGridContainer.height = _height;
				for(var i:int = 0;i<dataGridContainer.content.numChildren;i++){
					var panel:Panel = dataGridContainer.content.getChildAt(i) as Panel;
					panel.width = _widthList[i]*_width;
					panel.height = _height;
					if ( i == 0 )
						panel.x = 0; 
					else
						panel.x = dataGridContainer.content.getChildAt(i-1).x + dataGridContainer.content.getChildAt(i-1).width;
					var child:DisplayObject = panel.content.getChildAt(0);
					
					if(child is ALabel){
						child.y = (panel.height - (child as ALabel).textFieldHeight) >> 1;
					}else{
						child.y = (panel.height - child.height) >> 1;
					}
					switch ( _align )
					{
						case TextStyle.CENTER:
							if(child is ALabel){
								child.x = (panel.width - (child as ALabel).textFieldWidth) >> 1;
							}else{
								child.x = (panel.width - child.width) >> 1;
							}
							break;
						case TextStyle.LEFT:
							if(child is ALabel){
								child.x = 0;
							}else{
								child.x = 0;
							}
							
							break;
						case TextStyle.RIGHT:
							if(child is ALabel){
								child.x = panel.width - (child as ALabel).textFieldWidth;
							}else{
								child.x = panel.width - child.width;
							}
							break;
					}
					
				}
			}
		}
		/*override public function set data ( value : Object ) : void
		{
			if ( !value )
				return;
			var arr : Array = value as Array;
			if ( !arr )
				return;
			if ( !_data )
				_data = [];
			var len : int = _widthList.length;
			var dataOld : Object;
			var dataNew : Object;
			var item : DisplayObject;

			// 列数改变移出多余的
			if ( _displayObjList.length > len )
			{
				for ( var j : int = len ; j < _displayObjList.length ; j++ )
				{
					if ( _displayObjList[ j ] is UIComponent )
						UIComponent( _displayObjList[ j ] ).dispose();
					else if ( _displayObjList[ j ] is DisplayObject )
						this.removeChild( _displayObjList[ j ] as DisplayObject );
				}
			}

			// 创建对象
			for ( var i : int = 0 ; i < len ; i++ )
			{
				dataOld = _data[ i ];
				dataNew = arr[ i ];
				if ( dataOld != dataNew )
				{
					if ( dataOld is String && dataNew is String )
					{
						if ( _displayObjList[ i ] )
						{
							_displayObjList[ i ].text = dataNew
							continue;
						}
					}

					// 移出老的
					if ( dataOld is UIComponent )
						UIComponent( dataOld ).dispose();
					else if ( dataOld is DisplayObject )
						this.removeChild( dataOld as DisplayObject );

					// 创建新的
					if ( dataNew is String )
					{
						item = new Label( "" );
						Label( item ).enabled = true;
						Label( item ).text = dataNew.toString();
						Label( item ).align = _align;
						this.addChild( item );
					}
					else if ( dataNew is DisplayObject )
					{
						item = dataNew as DisplayObject;
						this.addChild( item );
						if ( !isFix && item.height > height )
							height = item.height;
					}

					_displayObjList[ i ] = item;
				}
			}

			var displayObj : DisplayObject;
			var w : int;
			var totalW : int;
			// 排列位置
			for ( i = 0 ; i < _displayObjList.length ; i++ )
			{
				w = _widthList[ i ];
				displayObj = _displayObjList[ i ];

				if ( displayObj.width > w )
					displayObj.width = w;
				if ( isFix )
					displayObj.height = height;
				if ( displayObj is TextInput )
					TextInput( displayObj ).setSize( w , height );

				switch ( _align )
				{
					case TextStyle.CENTER:
						displayObj.x = totalW + ( ( w - displayObj.width ) >> 1 );
						displayObj.y = ( height - displayObj.height ) >> 1;
						break;
					case TextStyle.LEFT:
						displayObj.x = totalW;
						displayObj.y = ( height - displayObj.height ) >> 1;
						break;
					case TextStyle.RIGHT:
						displayObj.x = totalW + w - displayObj.width;
						displayObj.y = ( height - displayObj.height ) >> 1;
						break;
					default:

						break;
				}
				totalW += w;
			}
			_data = value;
		}*/

		override public function set align ( value : String ) : void
		{
			_align = value;
		}

		override public function dispose () : void
		{
			var panel:Panel;
			while(dataGridContainer.content.numChildren>0){
				var num:int = dataGridContainer.content.numChildren-1;
				panel = dataGridContainer.content.removeChildAt(num) as Panel;
				//如果数据源是一个显示对象，不注销，只移除
				if(data[num] is DisplayObject){
					panel.content.removeChildAt(0);
				}
				panel.dispose();
			}
			dataGridContainer = null;
			//_displayObjList.length = 0;
			//_displayObjList = null;
			_widthList = null;
			super.dispose();
		}
	}
}
