package UI.theme.defaulttheme.window
{
	import UI.App;
	import UI.abstract.component.event.WindowEvent;
	import UI.theme.defaulttheme.button.Button;
	import UI.theme.defaulttheme.text.Label;

	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.MouseEvent;

	public class HintWindow extends Window
	{
		/** 提示内容 **/
		private var _contentText : Label;

		/** 遮罩 **/
		private var _mask : Sprite;

		private var _yesBtn : Button;

		private var _noBtn : Button;

		private var _canelBtn : Button;

		public function HintWindow ()
		{
			super();
			_mask = new Sprite();
			App.event.addEvent( App.stage , Event.RESIZE , onResize );
		}

		/**
		 * 设置提示信息
		 * @param text : 提示内容
		 * @param title : 提示标题
		 * @param yes : 确定按钮 传null为不显示
		 * @param no : 否按钮
		 * @param canel : 取消按钮
		 * @param isMask : 是否显示遮罩
		 */
		public function setInfo ( text : String , title : String = "提示" , yes : String = "确定" , no : String = "否" , canel : String = "取消" , isMask : Boolean =
								  true ) : void
		{
			if ( !_contentText )
			{
				_contentText = new Label();
				_contentText.y = 30;
				addChild( _contentText );
			}
			_contentText.text = text;
			this.text = title;

			var arr : Array  = [];
			var gapBtn : int = 100
			if ( yes )
			{
				if ( !_yesBtn )
				{
					_yesBtn = new Button();
					App.event.addEvent( _yesBtn , MouseEvent.CLICK , onYesClick );
				}
				_yesBtn.text = yes;
				addChild( _yesBtn );
				_yesBtn.update();
				arr.push( _yesBtn );
			}

			if ( no )
			{
				if ( !_noBtn )
				{
					_noBtn = new Button();
					App.event.addEvent( _noBtn , MouseEvent.CLICK , onNoClick );
				}
				_noBtn.text = no;
				addChild( _noBtn );
				_noBtn.update();
				arr.push( _noBtn );
			}

			if ( canel )
			{
				if ( !_canelBtn )
				{
					_canelBtn = new Button();
					App.event.addEvent( _canelBtn , MouseEvent.CLICK , onCanelClick );
				}
				_canelBtn.text = canel;
				addChild( _canelBtn );
				_canelBtn.update();
				arr.push( _canelBtn );
			}

			
			var maxW : int = 0;
			for ( var i : int = 0 ; i < arr.length ; i++ )
				maxW += i > 0 ? arr[ i ].width + gapBtn : arr[ i ].width;

			//计算最大宽度 应为多少
			maxW = Math.max( maxW , _contentText.width , 200 );

			var len : int = arr.length;
			for ( i = 0 ; i < len ; i++ )
			{
				arr[ i ].y = _contentText.y + _contentText.height + 40;
				arr[ i ].x = ( i + 1 ) / ( len + 1 ) * maxW - arr[ i ].width / 2;
			}

			setSize( contentX * 2 + Math.max( _content.superWidth , maxW ) , contentY * 2 + _content.superHeight + _contentText.y );
			_contentText.x = ( width - _contentText.width - contentX * 2 ) >> 1;
			App.ui.center( this );
			App.ui.root.addChildAt( this , 0 );

			if ( isMask )
			{
				_mask.graphics.clear();
				_mask.graphics.beginFill( 0 , 0.7 );
				_mask.graphics.drawRect( 0 , 0 , App.stage.stageWidth , App.stage.stageHeight );
				App.ui.root.addChildAt( _mask , 0 );
			}
		}

		private function onYesClick ( e : MouseEvent ) : void
		{
			parent && parent.removeChild( this );
			_mask.parent && _mask.parent.removeChild( _mask );
			dispatchEvent( new WindowEvent( WindowEvent.YES ) );
		}

		private function onNoClick ( e : MouseEvent ) : void
		{
			parent && parent.removeChild( this );
			_mask.parent && _mask.parent.removeChild( _mask );
			dispatchEvent( new WindowEvent( WindowEvent.NO ) );
		}

		private function onCanelClick ( e : MouseEvent ) : void
		{
			parent && parent.removeChild( this );
			_mask.parent && _mask.parent.removeChild( _mask );
			dispatchEvent( new WindowEvent( WindowEvent.CANEL ) );
		}

		private function onResize ( e : Event ) : void
		{
			if ( this.parent )
				App.ui.center( this );

			if ( _mask.parent )
			{
				_mask.graphics.clear();
				_mask.graphics.beginFill( 0 , 0.7 );
				_mask.graphics.drawRect( 0 , 0 , App.stage.stageWidth , App.stage.stageHeight );
			}
		}

//		/**
//		 * 添加子对象frea
//		 */
//		override public function addChild ( child : DisplayObject ) : DisplayObject
//		{
//		}
//
//		override public function addChildAt ( child : DisplayObject , index : int ) : DisplayObject
//		{
//		}
	}
}
