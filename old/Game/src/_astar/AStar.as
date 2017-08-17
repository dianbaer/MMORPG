package _astar
{
	import flash.geom.Point;
	
	import UI.abstract.resources.AnConst;
	import UI.abstract.utils.CommonPool;
	
	import _45degrees.com.friendsofed.isometric.IsoUtils;
	import _45degrees.com.friendsofed.isometric.Point3D;

	public class AStar
	{
		private static var helpPoint:Point = new Point();
		private static var helpPoint1:Point = new Point();
		private static var helpPos:Point3D = new Point3D();
		
		private var _open:Array;
		private var _closed:Array;
		private var _grid:Grid;
		private var _endNode:Node;
		private var _startNode:Node;
		private var _path:Array;
//		private var _heuristic:Function = manhattan;
//		private var _heuristic:Function = euclidian;
		private var _heuristic:Function = diagonal;
//		private var _heuristic:Function = pointDistance1;
		private var _straightCost:Number = 1.0;
		private var _diagCost:Number = Math.SQRT2;
		private var size:int = 0;
		private var sizeTwo:int = 0;
		private var distance:Number = 0.0;
		private var jisuanTime:int = 0;
		private var endPoint:Point = new Point();
		public function AStar()
		{
		}
		public function clear():void{
			_grid = null;
			_startNode = null;
			_endNode = null;
			_path = null;
			CommonPool.toPoolArray(_open);
			_open = null;
			CommonPool.toPoolArray(_closed);
			_closed = null;
			this.size = 0;
			sizeTwo = 0;
			distance = 0.0;
			jisuanTime = 0;
			endPoint.setTo(0,0);
			
		}
		public function findPath(grid:Grid,size:int):Boolean
		{
			this.size = size;
			sizeTwo = size*2;
			helpPoint.setTo(0,0);
			helpPoint1.setTo(size,size*0.5);
			distance = Point.distance(helpPoint,helpPoint1);
			_grid = grid;
			_open = CommonPool.fromPoolArray();
			_closed = CommonPool.fromPoolArray();
			
			_startNode = _grid.startNode;
			_endNode = _grid.endNode;
			helpPos.setValue(_endNode.x*size,0,_endNode.y*size);
			IsoUtils.isoToScreen(helpPos,endPoint);
			_startNode.g = 0;
			_startNode.h = _heuristic(_startNode);
			_startNode.f = _startNode.g + _startNode.h;
			
			return search();
		}
		
		public function search():Boolean
		{
			var node:Node = _startNode;
			while(node != _endNode)
			{
				var startX:int = Math.max(_grid.minX, node.x - 1);
				var endX:int = Math.min(_grid.maxX, node.x + 1);
				var startY:int = Math.max(_grid.minY, node.y - 1);
				var endY:int = Math.min(_grid.maxY, node.y + 1);
				
				for(var i:int = startX; i <= endX; i++)
				{
					for(var j:int = startY; j <= endY; j++)
					{
						var test:Node = _grid.getNode(i, j);
						//目前没有遮挡，所以这些点不能走的判断，不会生效
						if(test == node || 
						   !test.walkable ||
						   !_grid.getNode(node.x, test.y).walkable ||
						   !_grid.getNode(test.x, node.y).walkable)
						{
							continue;
						}
						
						//var cost:Number = _straightCost;
						//if(!((node.x == test.x) || (node.y == test.y)))
						//{
						//	cost = _diagCost;
						//}
						var cost:Number = getG(node,test);
						jisuanTime++;
						
						var g:Number = node.g + cost * test.costMultiplier;
						var h:Number = _heuristic(test);
						var f:Number = g + h;
						if(isOpen(test) || isClosed(test))
						{
							if(test.f > f)
							{
								test.f = f;
								test.g = g;
								test.h = h;
								test.parent = node;
							}
						}
						else
						{
							test.f = f;
							test.g = g;
							test.h = h;
							test.parent = node;
							_open[_open.length] = test;
						}
					}
				}
				//不理解这个循环有什么用
				//for(var o:int = 0; o < _open.length; o++)
				//{
				//}
				_closed[_closed.length] = node;
				if(_open.length == 0)
				{
					trace("no path found");
					return false;
				}
				_open.sortOn("f", Array.NUMERIC);
				node = _open.shift() as Node;
			}
			buildPath();
			return true;
		}
		private function getG(upNode:Node,arrrie:Node):Number{
			var cost:Number = _straightCost;
			if(!((upNode.x == arrrie.x) || (upNode.y == arrrie.y)))
			{
				cost = _diagCost;
			}
			return cost;
		}
		
		private function getG1(upNode:Node,arrrie:Node):Number{
			var  startX:int = upNode.x-1;
			var  endX:int = upNode.x+1;
			var  startY:int = upNode.y-1;
			var  endY:int = upNode.y+1;
			var a:int = 0;
			var cost:Number;
			var dir:int = 0;
			for(var i:int = startX;i<=endX;i++){
				for(var j:int = startY;j<=endY;j++){
					a++;
					if(i ==arrrie.x && j == arrrie.y){
						if(a == 1){
							AnConst.UP;
							cost = size;
						}else if(a == 2){
							AnConst.LEFT_UP;
							cost = distance;
						}else if(a == 3){
							AnConst.LEFT;
							cost = sizeTwo;
						}else if(a == 4){
							AnConst.RIGHT_UP;
							cost = distance;
						}else if(a == 6){
							AnConst.LEFT_DOWN;
							cost = distance;
						}else if(a == 7){
							AnConst.RIGHT;
							cost = sizeTwo;
						}else if(a == 8){
							AnConst.RIGHT_DOWN;
							cost = distance;
						}else if(a == 9){
							AnConst.DOWN;
							cost = size;
						}
						break;
					}
					
				}
			}
			return cost;
		}
		
		private function buildPath():void
		{
			//trace(jisuanTime);
			//var helpObj:Object = CommonPool.fromPoolObject();
			//helpObj.message = "计算了："+jisuanTime+"次";
			//AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj);
			_path = CommonPool.fromPoolArray();
			var node:Node = _endNode;
			_path[_path.length] = node;
			while(node != _startNode)
			{
				node = node.parent;
				_path.unshift(node);
			}
		}
		
		public function get path():Array
		{
			return _path;
		}
		
		private function isOpen(node:Node):Boolean
		{
			for(var i:int = 0; i < _open.length; i++)
			{
				if(_open[i] == node)
				{
					return true;
				}
			}
			return false;
		}
		
		private function isClosed(node:Node):Boolean
		{
			for(var i:int = 0; i < _closed.length; i++)
			{
				if(_closed[i] == node)
				{
					return true;
				}
			}
			return false;
		}
		
		private function manhattan(node:Node):Number
		{
			return Math.abs(node.x - _endNode.x) * _straightCost + Math.abs(node.y + _endNode.y) * _straightCost;
		}
		
		private function euclidian(node:Node):Number
		{
			var dx:Number = node.x - _endNode.x;
			var dy:Number = node.y - _endNode.y;
			return Math.sqrt(dx * dx + dy * dy) * _straightCost;
		}
		private function pointDistance(node:Node):Number
		{
			helpPos.setValue(node.x*size,0,node.y*size);
			IsoUtils.isoToScreen(helpPos,helpPoint);
			return Point.distance(helpPoint,endPoint);;
		}
		private function pointDistance1(node:Node):Number
		{
			var dx:Number = Math.abs(node.x - _endNode.x);
			var dy:Number = Math.abs(node.y - _endNode.y);
			//是一个正方形，直接走斜线
			if(dx == dy){
				return dx * distance;
			}
			var diag:Number = Math.min(dx, dy);
			var straight:Number = dx + dy;
			
			helpPos.setValue(node.x*size,0,node.y*size);
			IsoUtils.isoToScreen(helpPos,helpPoint);
			
			var radian:Number = Math.atan2(endPoint.y-helpPoint.y,endPoint.x-helpPoint.x);
			var rotation:Number = radian/Math.PI*180.0;
			var cost:int;
			if(rotation < 45 || rotation > -45){
				cost = sizeTwo;
			}else if(rotation > 45 && rotation < 135){
				cost = size;
			}else if(rotation > 135 && rotation < -135){
				cost = sizeTwo;
			}else if(rotation > -135 && rotation < -45){
				cost = size;
			}
			return distance * diag + cost * (straight - 2 * diag);
		}
		private function diagonal(node:Node):Number
		{
			var dx:Number = Math.abs(node.x - _endNode.x);
			var dy:Number = Math.abs(node.y - _endNode.y);
			var diag:Number = Math.min(dx, dy);
			var straight:Number = dx + dy;
			return _diagCost * diag + _straightCost * (straight - 2 * diag);
		}
		
		public function get visited():Array
		{
			return _closed.concat(_open);
		}
	}
}
