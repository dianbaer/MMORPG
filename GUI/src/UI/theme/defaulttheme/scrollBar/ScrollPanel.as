package UI.theme.defaulttheme.scrollBar
{
	import UI.abstract.component.control.panel.Panel;
	import UI.abstract.component.control.scrollBar.AScrollPanel;

	public class ScrollPanel extends AScrollPanel
	{
		public function ScrollPanel ()
		{
			super();
			panel = new Panel();
			isShowAlways = false;
			isShowVScrollbar = true;
			tick = 10;
		}

		/**
		 * @private
		 */
		override public function set isShowHScrollbar ( value : Boolean ) : void
		{
			if(super.isShowHScrollbar == value){
				return;
			}
			super.isShowHScrollbar = value;
			if ( value )
			{
				if ( !hScrollbar )
				{
					hScrollbar = new HScrollBar();
					hScrollbar.trackTick = hScrollbar.tick * 5;
				}
			}
			else
			{
				hScrollbar = null;
			}
			nextDraw()
		}

		/**
		 * @private
		 */
		override public function set isShowVScrollbar ( value : Boolean ) : void
		{
			if(super.isShowVScrollbar == value){
				return;
			}
			super.isShowVScrollbar = value;
			if ( value )
			{
				if ( !vScrollbar )
				{
					vScrollbar = new VScrollBar();
					vScrollbar.trackTick = vScrollbar.tick * 5;
				}
			}
			else
			{
				vScrollbar = null;
			}
			nextDraw()
		}
	}
}
