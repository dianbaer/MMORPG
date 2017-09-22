package _45degrees.com.friendsofed.isometric
{
	import flash.display.Sprite;
	import flash.geom.Point;

	public class IsoObject extends Sprite
	{
		//这个对象的一个唯一值，用来排序
		public static var thingIdNum:int = 1;
		//id
		public var thingId:int;
		public var sortId:int;
		protected var _position:Point3D;
		protected var _size:Number;
		//protected var _walkable:Boolean = false;
		//protected var _vx:Number = 0;
		//protected var _vy:Number = 0;
		//protected var _vz:Number = 0;
		
		// a more accurate version of 1.2247...
		public static const Y_CORRECT:Number = Math.cos(-Math.PI / 6) * Math.SQRT2;
		public var isDispose:Boolean = false;
		/**
		 * 是否可交互，true是，false否
		 */
		public var interactive:Boolean = true;
		private static var helpPoint:Point = new Point();
		public function IsoObject(size:Number)
		{
			
			_position = new Point3D();
			updateScreenPosition();
			sortId = IsoObject.thingIdNum++;
			reset(size);
		}
		
		/**
		 * Converts current 3d position to a screen position 
		 * and places this display object at that position.
		 */
		protected function updateScreenPosition():void
		{
			IsoUtils.isoToScreen(_position,helpPoint);
			super.x = helpPoint.x;
			super.y = helpPoint.y;
		}
		
		/**
		 * String representation of this object.
		 */
		/*
		override public function toString():String
		{
			return "[IsoObject (x:" + _position.x + ", y:" + _position.y + ", z:" + _position.z + ")]";
		}
		*/
		/**
		 * Sets / gets the x position in 3D space.
		 */
		override public function set x(value:Number):void
		{
			_position.x = value;
			updateScreenPosition();
		}
		override public function get x():Number
		{
			return _position.x;
		}
		
		/**
		 * Sets / gets the y position in 3D space.
		 */
		override public function set y(value:Number):void
		{
			_position.y = value;
			updateScreenPosition();
		}
		override public function get y():Number
		{
			return _position.y;
		}
		
		/**
		 * Sets / gets the z position in 3D space.
		 */
		override public function set z(value:Number):void
		{
			_position.z = value;
			updateScreenPosition();
		}
		override public function get z():Number
		{
			return _position.z;
		}
		
		
		/**
		 * 设置position，注意传进来的对象的数据被保存，对象其实没被保存
		 */
		public function set position(value:Point3D):void
		{
			//_position = value;
			_position.x = value.x;
			_position.y = value.y;
			_position.z = value.z;
			updateScreenPosition();
		}
		public function get position():Point3D
		{
			return _position;
		}
		
		/**
		 * Returns the transformed 3D depth of this object.
		 */ 
		public function get depth():Number
		{
			return (_position.x + _position.z) * .866 - _position.y * .707;
		}
		
		/**
		 * Indicates whether the space occupied by this object can be occupied by another object.
		 */
		/*
		public function set walkable(value:Boolean):void
		{
			_walkable = value;
		}
		public function get walkable():Boolean
		{
			return _walkable;
		}
		*/
		/**
		 * Returns the size of this object.
		 */
		public function get size():Number
		{
			return _size;
		}
		
		/**
		 * Returns the square area on the x-z plane that this object takes up.
		 */
		/*
		public function get rect():Rectangle
		{
			return new Rectangle(x - size / 2, z - size / 2, size, size);
		}
		*/
		/**
		 * Sets / gets the velocity on the x axis.
		 */
		/*
		public function set vx(value:Number):void
		{
			_vx = value;
		}
		public function get vx():Number
		{
			return _vx;
		}
		*/
		/**
		 * Sets / gets the velocity on the y axis.
		 */
		/*
		public function set vy(value:Number):void
		{
			_vy = value;
		}
		public function get vy():Number
		{
			return _vy;
		}
		*/
		/**
		 * Sets / gets the velocity on the z axis.
		 */
		/*
		public function set vz(value:Number):void
		{
			_vz = value;
		}
		public function get vz():Number
		{
			return _vz;
		}
		*/
		public static function interpolate(arrivePoint:Point,startPoint:Point,moveDistance:Number,endPoint:Point = null):Point{
			var radian:Number = Math.atan2(arrivePoint.y-startPoint.y,arrivePoint.x-startPoint.x);
			
			if(endPoint == null) endPoint = new Point();
			endPoint.x = Math.cos(radian)*moveDistance+startPoint.x;
			endPoint.y = Math.sin(radian)*moveDistance+startPoint.y;
			return endPoint;
		}
		protected function interpolateWithArrvie(arrivePoint:Point,startPoint:Point, moveDistance:Number,endPoint:Point = null):Point{
			var radian:Number = Math.atan2(arrivePoint.y-startPoint.y,arrivePoint.x-startPoint.x);
			if(endPoint == null) endPoint = new Point();
			endPoint.x = Math.cos(radian)*moveDistance+arrivePoint.x;
			endPoint.y = Math.sin(radian)*moveDistance+arrivePoint.y;
			return endPoint;
		}
		public function reset(size:Number):IsoObject{
			isDispose = false;
			_size = size;
			return this;
		}
		public function dispose():void{
			_position.x = 0;
			_position.y = 0;
			_position.z = 0;
			thingId = 0;
			_size = 0;
			super.x = 0;
			super.y = 0;
			//这个不能重置
			//interactive = true;
			isDispose = true;
		}
	}
}