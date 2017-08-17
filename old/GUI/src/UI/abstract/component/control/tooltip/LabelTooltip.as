package UI.abstract.component.control.tooltip
{
	import UI.component.control.text.Label;

	public class LabelTooltip extends BaseTooltip
	{
		private var label:Label;
		public function LabelTooltip()
		{
			super();
			label = new Label();
			addChild(label);
			enabled = false;
		}
		override public function set data(data:Object):void{
			label.text = data.toString();
		}
		override public function dispose():void{
			label = null;
			super.dispose();
		}
	}
}