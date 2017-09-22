package UI.abstract.component.control.datagrid
{
	import UI.abstract.component.control.button.ALabelImageButton;
	import UI.abstract.component.control.list.AList;
	
	import flash.events.Event;
	
	/**
	 * 多列列表 需要先设置列数
	 */
	public class ADataGrid extends AList
	{
		/** 标题按钮类 **/
		protected var  _titleClass : Class;
		
		/** 标题高度 **/
		protected var  _titleHeight : int;
		
		/** 是否显示标题 **/
		protected var _isShowTitle : Boolean = true;
		
		/** 列数 **/
		protected var _column : int;
		
		/** 每列表宽度 **/
		protected var  _titleWidthList : Array = [];
		
		/**每列的文字**/
		protected var  _titleTextList : Array = [];
		
		/** 标题按钮列表 **/
		protected var  _titleList : Vector.<ALabelImageButton> = new Vector.<ALabelImageButton>();
		
		protected var _gapToContent : int = 0;
		
		public function ADataGrid()
		{
			super();
			
		}
		
		/** 标题和内容距离 **/
		public function get gapToContent():int
		{
			return _gapToContent;
		}

		/**
		 * @private
		 */
		public function set gapToContent(value:int):void
		{
			if(_gapToContent == value){
				return;
			}
			_gapToContent = value;
			nextDraw();
		}

		override protected function draw () : void
		{
			super.draw();
			
			if(_isShowTitle){
				_scrollBar.y = _scrollToTop + _titleHeight + _gapToContent;
				
				_scrollBar.setSize( _width - _scrollToTop*2, _height - _scrollToTop*2 - _titleHeight - _gapToContent);
				
			}
			
			var i : int;
			
			for ( i = 0; i < _titleList.length; i++ )
			{
				
				_titleList[i].height = _titleHeight; 
				_titleList[i].width = _titleWidthList[i]*_container.width;
				if(i == _titleList.length -1){
					//_titleList[i].width = _titleWidthList[i]*_container.width + _scrollBar.vScrollWidth;
				}
				_titleList[i].text = _titleTextList[i];
				//_titleList[i].update();
				if ( i == 0 )
					_titleList[i].x = _scrollToTop; 
				else
					_titleList[i].x = _titleList[i - 1].x + _titleList[i - 1].width; 
				
				_titleList[i].y = _scrollToTop; 
				if(_isShowTitle){
					_titleList[i].visible = true;
				}else{
					_titleList[i].visible = false;
				}
			}
			
			
			
			
		}
		override protected function onScrollBarChange ( e : Event ) : void
		{
			super.onScrollBarChange(e);
			for ( var i : int = 0; i < _titleList.length; i++ )
			{
				_titleList[i].width = _titleWidthList[i]*_container.width;
				if(i == _titleList.length -1){
					//_titleList[i].width = _titleWidthList[i]*_container.width + _scrollBar.vScrollWidth;
				}
				if ( i == 0 )
					_titleList[i].x = _scrollToTop; 
				else
					_titleList[i].x = _titleList[i - 1].x + _titleList[i - 1].width; 
			}
		}

		/** 是否显示标题 **/
		public function get isShowTitle():Boolean
		{
			return _isShowTitle;
		}
		
		/**
		 * @private
		 */
		public function set isShowTitle(value:Boolean):void
		{
			if(_isShowTitle == value){
				return;
			}
			_isShowTitle = value;
			nextDraw();
		}
		
		/** 标题高度 **/
		public function get titleHeight():int
		{
			return _titleHeight;
		}
		
		/**
		 * @private
		 */
		public function set titleHeight(value:int):void
		{
			if ( _titleHeight == value ) return;
			_titleHeight = value;
			nextDraw();
		}
		
		/** 设置index列的宽度 **/
		public function setTitleWidth( index : int, value : Number ) : void
		{
			//_titleList[index].width = value;  
			_titleWidthList[index] = value;
			if ( container is DataGridItemContainer )
				DataGridItemContainer( container ).widthList = _titleWidthList;
			nextDraw();
		}
		
		/** 设置index列的文本内容 **/
		public function setTitleText( index : int, value : String ) : void
		{
			_titleTextList[index] = value;
			//_titleList[index].text = value;  
			nextDraw();
		}
		
		/** 标题按钮类 **/
		public function get TitleClass():Class
		{
			return _titleClass;
		}
		
		/**
		 * @private
		 */
		public function set TitleClass(value:Class):void
		{
			if(_titleClass == value){
				return;
			}
			_titleClass = value;
		}

		/** 列数 **/
		public function get column():int
		{
			return _column;
		}

		/**
		 * @private
		 */
		public function set column(value:int):void
		{
			if(_column == value){
				return;
			}
			_column = value;
			var len : int = _titleList.length;
			
			var i : int = 0;
			var btn : ALabelImageButton;
			//var title : DataGridTitle;
			
			if ( value < len  )
			{
				for ( i = value; i < len; i++ )
					_titleList[i].dispose();
				_titleList.splice( i, len );
				/*_titleWidthList.splice( i, len );
				_titleTextList.splice( i, len );*/
			}
			else
			{ 
				for ( i = len; i < value; i++ )
				{
					//title = new DataGridTitle();
					btn = new TitleClass();
					//title.button = btn;
					_titleList.push(btn);
					this.addChild(btn);
				}
			}
			nextDraw();
		}
		
		override public function dispose () : void
		{
			var len : int = _titleList.length
			for ( var i : int = 0; i < len; i++ )
				_titleList[i].dispose();
			_titleList.length = 0;
			_titleList = null;
			_titleWidthList.length = 0;
			_titleWidthList = null;
			_titleTextList.length = 0;
			_titleTextList = null;
			_titleClass = null;
			super.dispose();
		}
	}
}