package UI.abstract.resources.item
{
	import flash.utils.ByteArray;
	
	import UI.abstract.resources.ResourceManager;
	import UI.abstract.resources.ResourceUtil;
	import UI.abstract.utils.CommonPool;

	public class LoadObj
	{
		public var url : String;

		/** 扩展类型 **/
		public var extension : String;

		/** 所有回调函数等列表 **/
		private var list : Array              = [];

		private var res : ResourceManager;
		
		private var _isComplete:Boolean = false;
		
		/**二级制**/
		public var byteArray:ByteArray;
		
		public function LoadObj (  res : ResourceManager )
		{
			
			reset(res);
		}
		public function reset(res : ResourceManager):LoadObj{
			this.res = res;
			return this;
		}
		public function push ( obj : Object ) : void
		{
			//没有有onComplete也放入数组，回收obj
			//if(obj.onComplete){
				list[list.length] = obj;
			//}
		}
		
		/**
		 * 取消加载回调函数
		 * 返回true 代表列表已经没有回调函数
		 */
		public function canel ( fun : Function ) : Boolean
		{
			for ( var i : int = 0; i < list.length; i++ )
			{
				if ( list[i].onComplete == fun )
				{
					var obj:Object = list.splice( i, 1 );
					CommonPool.toPoolObject(obj);
					break;
				}
			}
			if ( list.length == 0 )
				return true; 
			else
				return false;
		}

		/**
		 * 完成回调
		 */
		public function onComplete () : void
		{
			_isComplete = true;
			for ( var i : int = 0 ; i < list.length ; i++ )
			{
				//加载jta的某一个图片时，其实不要jta这个文件回调，所以jta的文件回调函数是空
				if ( list[ i ].onComplete ){
					if(list[ i ].propName){
						var imageAtlasResource:ImageAtlasResource = res.getResource( ResourceManager.unFormatResourceName( url ) ) as ImageAtlasResource;
						switch(list[ i ].propName){
							case ResourceUtil.IMAGE:
								if(res.getResource( ResourceManager.unFormatResourceName( url+"?"+ResourceUtil.IMAGE+"="+list[ i ].prop ))){
									list[ i ].onComplete.apply( null , [ res.getResource( ResourceManager.unFormatResourceName( url+"?"+ResourceUtil.IMAGE+"="+list[ i ].prop )) ] );
								}else{
									list[ i ].onComplete.apply( null , [ imageAtlasResource.getTexture(list[ i ].prop) ] );
								}
								
								break;
							case ResourceUtil.MC:
								if(res.getResource( ResourceManager.unFormatResourceName( url+"?"+ResourceUtil.MC+"="+list[ i ].prop ))){
									list[ i ].onComplete.apply( null , [ res.getResource( ResourceManager.unFormatResourceName( url+"?"+ResourceUtil.MC+"="+list[ i ].prop )) ] );
								}else{
									list[ i ].onComplete.apply( null , [ imageAtlasResource.getTextures(list[ i ].prop) ] );
								}
								break;
							default:
								list[ i ].onComplete.apply( null , [ null ] );
								break;
						}
						
					}else{
						list[ i ].onComplete.apply( null , [ res.getResource( ResourceManager.unFormatResourceName( url ) ) ] );
					}
				}
				CommonPool.toPoolObject(list[ i ]);	
			}
			list.length = 0;
		}


		/**
		 * 加载错误回调
		 */
		public function onField () : void
		{
			_isComplete = true;
			for ( var i : int = 0 ; i < list.length ; i++ )
			{
				if ( list[ i ].onField )
					list[ i ].onField.apply();
				CommonPool.toPoolObject(list[ i ]);
			}
			list.length = 0;
		}

		public function dispose () : void
		{
			url = null;
			extension = null;
			for ( var i : int = 0 ; i < list.length ; i++ )
			{
				CommonPool.toPoolObject(list[ i ]);
			}
			list.length = 0;
			
			res = null;
			_isComplete = false;
			if(byteArray){
				byteArray.clear();
				byteArray = null;
			}
			toPool(this);
		}
		/**
		 * 是否处于完成回调状态，如果是外部就不调用这个类的方法
		 */
		public function get isComplete():Boolean
		{
			return _isComplete;
		}
		private static var sLoadObjPool:Vector.<LoadObj> = new <LoadObj>[];
		
		/** @private */
		public static function fromPool(res : ResourceManager):LoadObj
		{
			if (sLoadObjPool.length) return sLoadObjPool.pop().reset(res);
			else return new LoadObj(res);
		}
		
		/** @private */
		public static function toPool(loadObj:LoadObj):void
		{
			
			sLoadObjPool[sLoadObjPool.length] = loadObj;
		}
	}
}
