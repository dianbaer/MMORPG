package UI.abstract.component.interfaces
{
	import flash.events.IEventDispatcher;
	
	import UI.abstract.component.interfaces.IDispose;

	public interface IList extends IEventDispatcher
	{

		/**
		 *  items长度
		 */
		function get length () : int;


		/**
		 *  添加对象到末尾
		 */
		function addItem ( item : Object ) : void;

		/**
		 *  添加对象到指定位置
		 */
		function addItemAt ( item : Object , index : int ) : void;
		
		/**
		 *  添加数组对象到末尾
		 */
		function addItemList ( items : Array ) : void;
		
		/**
		 *  添加数组对象到指定位置
		 */
		function addItemListAt ( items : Array , index : int ) : void;
		
		/**
		 *  获得指定索引位置的对象
		 */
		function getItemAt ( index : int ) : Object;

		/**
		 * 获得对象的索引
		 */
		function getItemIndex ( item : Object ) : int;

		/**
		 * 更新某个对象信息
		 */
//		function itemUpdated ( item : Object , property : Object = null , oldValue : Object = null , newValue : Object = null ) : void;
		function itemUpdated ( item : Object ) : void

		/**
		 * 移出列表所有对象
		 */
		function removeAll () : void;

		/**
		 * 移出列表引对象
		 * return : 被移除的对象
		 */
		function removeItem ( item : Object ) : Object;

		/**
		 * 移出列表指定索引对象
		 * return : 被移除的对象
		 */
		function removeItemAt ( index : int ) : Object;

		/**
		 * 设置一个新的对象替换掉指定位置的对象
		 * return ： 返回被替换的对象
		 */
		function setItemAt ( item : Object , index : int ) : Object;

		/**
		 * 返回数组
		 */
		function toArray () : Array;
		
	}
}
