package UI.theme.defaulttheme.tree
{
	import UI.abstract.component.control.tree.TreeData;
	import UI.theme.defaulttheme.Skin;
	import UI.theme.defaulttheme.button.CheckBox;
	import UI.theme.defaulttheme.list.ListObject;
	import UI.theme.defaulttheme.text.Label;

	public class TreeObject extends ListObject
	{
		protected var icon : CheckBox;

		protected var iconWidth : int       = 20

		protected var iconHeight : int      = 20

		/** 每个等级需要填补的距离 **/
		protected var _padding : int        = 25

		/** 图标和文字的距离 **/
		protected var _gapIconToLabel : int = 5

		protected var _treeLabel : Label;

		public function TreeObject ()
		{
			super();
		}

		/*override public function set data ( value : Object ) : void
		{
			_data = value;
			
			nextDraw();
		}*/
		override public function forceUpdate():void{
			var treeData : TreeData = _data as TreeData
			if ( treeData )
			{
				if ( !_treeLabel )
				{
					_treeLabel = new Label();
					_treeLabel.enabled = true;
					_treeLabel.mouseChildren = false;
					_treeLabel.mouseEnabled = false;
					addChild( _treeLabel );
				}
				_treeLabel.text = treeData.text;
				
				if ( treeData.canExpand )
				{
					//有子项
					if ( !icon )
					{
						icon = new CheckBox( Skin.TREE_BUTTON_EXPAND + "," + Skin.TREE_BUTTON_SHRINK );
						icon.setSize( iconWidth , iconHeight );
						icon.mouseChildren = false;
						icon.mouseEnabled = false;
						addChild( icon );
					}
					if ( treeData.opened )
						icon.selected = true;
					else
						icon.selected = false;
					icon.visible = true;
				}
				else
				{
					//无子项
					if ( icon )
						icon.visible = false;
				}
			}
			//不能小于最小高度
			if(_height<_minHeight){
				height = _minHeight;
			}
			nextDraw();
		}
		override protected function draw () : void
		{
			super.draw();
			if ( _data )
			{
				var level : int         = TreeData( _data ).level;
				var canExpand : Boolean = TreeData( _data ).canExpand;

				if ( canExpand )
				{
					icon.x = _padding * level;
					icon.y = ( height - iconHeight ) >> 1;
					_treeLabel.x = icon.x + iconWidth + _gapIconToLabel;
					_treeLabel.y = ( height - _treeLabel.textFieldHeight ) >> 1;
				}
				else
				{
					_treeLabel.x = _padding * level;
					_treeLabel.y = ( height - _treeLabel.textFieldHeight ) >> 1;
				}
			}
		}
		override public function dispose () : void
		{
			icon = null;
			_treeLabel = null;
			super.dispose();
		}
	}
}
