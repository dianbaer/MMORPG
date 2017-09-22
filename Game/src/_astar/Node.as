package _astar
{
	/**
	 * Represents a specific node evaluated as part of a pathfinding algorithm.
	 */
	public class Node
	{
		public var x:int;
		public var y:int;
		public var f:Number;
		public var g:Number;
		public var h:Number;
		public var walkable:Boolean = true;
		public var parent:Node;
		public var costMultiplier:Number = 1.0;
		
		public function Node(x:int, y:int)
		{
			
			reset(x,y);
		}
		public function reset(x:int, y:int):Node{
			this.x = x;
			this.y = y;
			return this;
		}
		public function dispose():void{
			x = 0;
			y = 0;
			f = 0;
			g = 0;
			h = 0;
			walkable = true;
			parent = null;
			costMultiplier = 1.0;
			toPool(this);
		}
		private static var sNodePool:Vector.<Node> = new <Node>[];
		
		/** @private */
		public static function fromPool(x:int, y:int):Node
		{
			if (sNodePool.length) return sNodePool.pop().reset(x,y);
			else return new Node(x,y);
		}
		
		/** @private */
		public static function toPool(node:Node):void
		{
			sNodePool[sNodePool.length] = node;
		}
	}
}