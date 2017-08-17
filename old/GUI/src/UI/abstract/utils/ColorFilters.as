package UI.abstract.utils
{
	import flash.filters.ColorMatrixFilter;
	import flash.filters.GlowFilter;

	
	public class ColorFilters
	{
		/**
		 * 字体样式
		 * 参数说明（光晕颜色 , 透明度, 水平模糊量, 垂直模糊量, 光晕的强度, 应用滤镜的次数(也就是品质), 指定发光是否为内侧发光, 是否具有挖空效果 ）
		 * 有需求的自己往后加（ 记得加颜色注释 ）
		 */		
		// 黑色
		public static var colour_Black:GlowFilter = new GlowFilter( 0x000000,1,2,2,10,1,false,false );
		
		public static var color_Green:GlowFilter = new GlowFilter( 0x00FF00,0.3,4,4,10,1,false,false );
		
		//紫色
		public static var color_Purple:GlowFilter = new GlowFilter( 0xFF00FC,0.3,4,4,10,1,false,false );
		
		//红色
		public static var color_Red:GlowFilter = new GlowFilter( 0xFF0000,0.3,4,4,10,1,false,false );
		
		
		private static var filter:ColorMatrixFilter;
		private static var redFilter:ColorMatrixFilter;
		private static var rLum:Number=0.2225;
		private static var gLum:Number=0.7169;
		private static var bLum:Number=0.0606;
		private static var bwMatrix:Array=[rLum, gLum, bLum, 0, 0, rLum, gLum, bLum, 0, 0, rLum, gLum, bLum, 0, 0, 0, 0, 0, 1, 0];
		private static var redMatrix:Array=[1,0,0,0,0,0,0.1,0,0,0,0,0,0.1,0,0,0,0,0,1,0];
		/**
		 * 黑白滤镜
		 */
		public static function get BWFilter():ColorMatrixFilter
		{
			if (!filter)
			{
				filter=new ColorMatrixFilter(bwMatrix);
			}
			return filter;
		}
		/**
		 * 红色滤镜
		 * 耐久度
		 * **/
		public static function get RedFilter():ColorMatrixFilter{
			if (!redFilter){
				redFilter=new ColorMatrixFilter(redMatrix);
			}
			return redFilter;
		}
		public function ColorFilters()
		{
		}
	}
}