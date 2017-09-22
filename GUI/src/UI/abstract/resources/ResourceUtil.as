package UI.abstract.resources
{
	

	public class ResourceUtil
	{
		/**取图片集里面的图片**/
		public static var IMAGE:String = "name";
		/**取图片集里面的图片动画集**/
		public static var MC:String = "names";
		public function ResourceUtil()
		{
		}
		/**
		 * 获取图片集里面的图片
		 */
		public static function getResourcePathByXML(xmlPath:String,type:String,name:String):String{
			var str:String = xmlPath.substr(0, xmlPath.lastIndexOf( "." ) )+".atlas?"+type+"="+name;
			return str;
		}
		/**
		 * 获取动画的路径
		 */
		public static function getAnimationURL(category:String, name:String,currAction:int=0):String
		{
			if(category==AnCategory.USER ||  category==AnCategory.MOUNTS){
				
				return category + "/" + name +"_"+currAction+ ".jta";
			}else{
				return category + "/" + name + ".jta";
			}
		}
		/**
		 * 获取动画里面的一张图片(8个方向的动画)
		 */
		public static function getAnimationBitmapData(url:String,type:int,dir:int,frame:int):String{
			var str:String = url.substr(0, url.lastIndexOf( "." ) )+".jtas?type="+type+"&dir="+dir+"&frame="+frame;
			return str;
		}
		/**
		 * 获取动画里面的一张图片(5个方向的动画)
		 */
		public static function getAnimationBitmapData5(url:String,type:int,dir:int,frame:int):String{
			if(dir>5){
				dir = 10-dir;
			}
			var str:String = url.substr(0, url.lastIndexOf( "." ) )+".jtas?type="+type+"&dir="+dir+"&frame="+frame;
			return str;
		}
		/**
		 * 获取动画里面的一张图片(1个方向的动画)
		 */
		public static function getAnimationBitmapData1(url:String,type:int,dir:int,frame:int):String{
			dir = 5;
			var str:String = url.substr(0, url.lastIndexOf( "." ) )+".jtas?type="+type+"&dir="+dir+"&frame="+frame;
			return str;
		}
		/**
		 * 获取地图数据
		 */
		//public static function getMapData(id:int):String{
		//	var url:String = StringUtil.substitute("map/s{0}/s{0}.mpt", id);
		//	return url;
		//}
		/**
		 * 得到显示在场景上的小地图
		 * @param id
		 * @return 
		 * 
		 */		
		//public static function getThumbMap(id:int):String
		//{
		//	return StringUtil.substitute("map/s{0}/s{0}_t.jpg", id);
		//}
		/**
		 * 得到小地图 
		 * @param id
		 * @return 
		 * 
		 */	
		//public static function getMapPiece(id:int, key:String):String
		//{
		//	return StringUtil.substitute("map/s{0}/s{0}_{1}.jpg", id, key);
		//}
	}
}