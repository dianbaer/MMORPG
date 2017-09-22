package UI.abstract.component.data
{
	import UI.App;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.component.control.dropDownMenu.MenuData;
	import UI.abstract.component.control.tree.TreeData;
	import UI.abstract.component.event.DataChangeEvent;
	
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;

	public class DataProvider extends EventDispatcher
	{
		/**
		 * 所有数据
		 */
		protected var data : Array = [];

		public function DataProvider ( value : Array = null )
		{
			if ( value == null )
				data = [];
			else
				data = value;
		}

		/**
		 *  长度
		 */
		public function get length () : uint
		{
//			if ( !data ) return 0;
			return data.length;
		}


		/**
		 * 增加一项到某个位置
		 */
		public function addItemAt ( item : Object , index : uint ) : void
		{
			if ( !checkIndex( index , data.length ) )
				return;
			dispatchPreChangeEvent( DataChangeType.ADD , [ item ] , index , index );
			data.splice( index , 0 , item );
			dispatchChangeEvent( DataChangeType.ADD , [ item ] , index , index );
		}

		/**
		 * 在末尾增加一项
		 */
		public function addItem ( item : Object ) : void
		{
			dispatchPreChangeEvent( DataChangeType.ADD , [ item ] , data.length , data.length );
			data.push( item );
			dispatchChangeEvent( DataChangeType.ADD , [ item ] , data.length - 1 , data.length - 1 );
		}

		/**
		 * 增加数组中所有元素到index处
		 */
		public function addItemsAt ( items : Array , index : uint ) : void
		{
			if ( !checkIndex( index , data.length ) )
				return;
			var arr : Array = items.concat();
			dispatchPreChangeEvent( DataChangeType.ADD , arr , index , index + arr.length - 1 );
			data.splice.apply( data , [ index , 0 ].concat( items ) );
			dispatchChangeEvent( DataChangeType.ADD , arr , index , index + arr.length - 1 );
		}

		/**
		 * 末尾添加数组所有元素
		 */
		public function addItems ( items : Array ) : void
		{
			addItemsAt( items , data.length );
		}

		/**
		 * 获得第index项
		 */
		public function getItemAt ( index : uint ) : Object
		{
//			checkIndex( index , data.length - 1 );
			return data[ index ];
		}

		/**
		 * 获得item对象index
		 */
		public function getItemIndex ( item : Object ) : int
		{
			return data.indexOf( item );
		}

		/**
		 * 移出第index项
		 */
		public function removeItemAt ( index : uint ) : Object
		{
			if ( !checkIndex( index , data.length - 1 ) )
				return null;
			dispatchPreChangeEvent( DataChangeType.REMOVE , data.slice( index , index + 1 ) , index , index );
			var arr : Array = data.splice( index , 1 );
			dispatchChangeEvent( DataChangeType.REMOVE , arr , index , index );
			return arr[ 0 ];
		}

		/**
		 * 移出item项
		 */
		public function removeItem ( item : Object ) : Object
		{
			var index : int = getItemIndex( item );
			if ( index != -1 )
			{
				return removeItemAt( index );
			}
			return null;
		}

		/**
		 * 移出所有项
		 */
		public function removeAll () : void
		{
			var arr : Array = data.concat();

			dispatchPreChangeEvent( DataChangeType.REMOVE_ALL , arr , 0 , arr.length );
			data.length = 0;
			dispatchChangeEvent( DataChangeType.REMOVE_ALL , data , 0 , data.length );
		}

		/**
		 * 把oldItem替换为newItem
		 */
		public function replaceItem ( newItem : Object , oldItem : Object ) : Object
		{
			var index : int = getItemIndex( oldItem );
			if ( index != -1 )
			{
				return replaceItemAt( newItem , index );
			}
			return null;
		}

		/**
		 * 把第index项替换为newItem
		 */
		public function replaceItemAt ( newItem : Object , index : uint ) : Object
		{
			if ( !checkIndex( index , data.length - 1 ) )
				return null;
			var arr : Array = [ data[ index ] ];
			dispatchPreChangeEvent( DataChangeType.REPLACE , arr , index , index );
			data[ index ] = newItem;
			dispatchChangeEvent( DataChangeType.REPLACE , arr , index , index );
			return arr[ 0 ];
		}

		/**
		 * 获得属性fieldName 为 value的值
		 */
		public function getItemAtByField ( fieldName : String , value : * ) : Object
		{
			var obj : Object = null;
			for ( var i : int = 0 ; i < data.length ; i++ )
			{
				if ( data[ i ][ fieldName ] == value )
				{
					obj = data[ i ];
					break;
				}
			}
			return obj;
		}

		/**
		 * 获得属性fieldName的值为value的项 返回第一个
		 */
		public function getItemIndexByField ( fieldName : String , value : * ) : int
		{
			return data.indexOf( getItemAtByField( fieldName , value ) );
		}

		/**
		 * 数组排序 同数组参数
		 */
		public function sort ( sortArgs : Array ) : *
		{
			dispatchPreChangeEvent( DataChangeType.SORT , data.concat() , 0 , data.length - 1 );
			var returnValue : Array = data.sort.apply( data , sortArgs );
			dispatchChangeEvent( DataChangeType.SORT , data.concat() , 0 , data.length - 1 );
			return returnValue;
		}

		/**
		 * 数组排序 同数组参数
		 */
		public function sortOn ( sortArgs : Array ) : *
		{
			dispatchPreChangeEvent( DataChangeType.SORT , data.concat() , 0 , data.length - 1 );
			var returnValue : Array = data.sortOn.apply( data , sortArgs );
			dispatchChangeEvent( DataChangeType.SORT , data.concat() , 0 , data.length - 1 );
			return returnValue;
		}

		/**
		 * 复制数据集
		 */
		public function clone () : DataProvider
		{
			return new DataProvider( data.concat() );
		}

		/**
		 * 返回数据副本
		 */
		public function toArray () : Array
		{
			return data.concat();
		}

		/**
		 * 卸载
		 */
		public function dispose () : void
		{
			for(var i:int = 0;i<data.length;i++){
				//list里面有可能含有对象
				if(data[i] is UIComponent){
					(data[i] as UIComponent).dispose();
					data[i] = null;
				}
				//datagrid里面数组里面可能含有对象
				if(data[i] is Array){
					for(var j:int = 0;j<data[i].length;j++){
						if(data[i][j] is UIComponent){
							(data[i][j] as UIComponent).dispose();
						}
					}
					data[i].length = 0;
					data[i] = null;
				}
				//如果是tree
				if(data[i] is TreeData){
					(data[i] as TreeData).dispose();
					data[i] = null;
				}
				//如果是menuData
				if(data[i] is MenuData){
					(data[i] as MenuData).dispose();
					data[i] = null;
				}
			}
			data.length = 0;
			data = null;
			App.event.removeEventByObj( this );
		}

		/**
		 * 格式化为字符串形式
		 */
		override public function toString () : String
		{
			return "DataProvider [" + data.join( " , " ) + "]";
		}

		protected function checkIndex ( index : int , maximum : int ) : Boolean
		{
			if ( index > maximum || index < 0 )
				return false
			else
				return true;
		}

		/**
		 * 更新数据集到显示
		 */
		public function update () : void
		{
			dispatchChangeEvent( DataChangeType.UPDATE , null , 0 , 0 );
		}

		protected function dispatchChangeEvent ( evtType : String , items : Array , startIndex : int , endIndex : int ) : void
		{
			dispatchEvent( new DataChangeEvent( DataChangeEvent.DATA_CHANGE , evtType , items , startIndex , endIndex ) );
		}

		protected function dispatchPreChangeEvent ( evtType : String , items : Array , startIndex : int , endIndex : int ) : void
		{
			dispatchEvent( new DataChangeEvent( DataChangeEvent.PRE_DATA_CHANGE , evtType , items , startIndex , endIndex ) );
		}
	}
}
