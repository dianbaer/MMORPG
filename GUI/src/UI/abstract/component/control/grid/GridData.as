package UI.abstract.component.control.grid
{

	public class GridData
	{
		/** 格子基础图像地址 **/
		public var imageUrl : String;

		/** 数量 **/
		public var num : String;
		public var data:Object;

		/**
		 * 复制数据
		 */
		public function clone (gridData:GridData = null) : GridData
		{
			if(!gridData) gridData = new GridData();
			gridData.imageUrl = imageUrl;
			gridData.num = num;
			gridData.data = data;
			return gridData;
		}

		/**
		 * 还原为初始化状态
		 */
		public function clear () : void
		{
			imageUrl = null;
			num = null;
			data = null;
		}
		/**
		 * 是否有数据可以拖动
		 */
		public function isDrog () : Boolean
		{
			return imageUrl != null && data != null;
		}
	}
}
