package UI.abstract.component.control
{
	import UI.abstract.component.control.base.UIComponent;

	/**
	 * 接受拖动对象
	 */
	public interface IDrop
	{

		/**
		 * 是否能可以接受
		 */
		function canDrop ( dragObject : UIComponent ) : Boolean;

		/**
		 * 接受拖动对象
		 */
		function drop ( dragObject : UIComponent ) : void;

	}
}
