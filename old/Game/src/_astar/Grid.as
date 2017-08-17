package _astar
{
	import UI.abstract.utils.CommonPool;

	/**
	 * Holds a two-dimensional array of Nodes methods to manipulate them, start node and end node for finding a path.
	 */
	public class Grid
	{
		private var _startNode:Node;
		private var _endNode:Node;
		private var _nodes:Array;
		
		private var _minX:int;
		private var _maxX:int;
		private var _minY:int;
		private var _maxY:int;
		/**
		 * Constructor.
		 */
		public function Grid(minX:int, maxX:int,minY:int, maxY:int)
		{
			
			
			reset(minX,maxX,minY,maxY);
		}
		public function reset(minX:int, maxX:int,minY:int, maxY:int):Grid{
			_minX = minX;
			_maxX = maxX;
			_minY = minY;
			_maxY = maxY;
			_nodes = CommonPool.fromPoolArray();
			
			for(var i:int = minX; i <= maxX; i++)
			{
				_nodes[i] = CommonPool.fromPoolArray();
				for(var j:int = minY; j <= maxY; j++)
				{
					_nodes[i][j] = Node.fromPool(i, j);
				}
			}
			return this;
		}
		////////////////////////////////////////
		// public methods
		////////////////////////////////////////
		
		public function get maxY():int
		{
			return _maxY;
		}

		public function set maxY(value:int):void
		{
			_maxY = value;
		}

		public function get minY():int
		{
			return _minY;
		}

		public function set minY(value:int):void
		{
			_minY = value;
		}

		public function get maxX():int
		{
			return _maxX;
		}

		public function set maxX(value:int):void
		{
			_maxX = value;
		}

		public function get minX():int
		{
			return _minX;
		}

		public function set minX(value:int):void
		{
			_minX = value;
		}

		/**
		 * Returns the node at the given coords.
		 * @param x The x coord.
		 * @param y The y coord.
		 */
		public function getNode(x:int, y:int):Node
		{
			return _nodes[x][y] as Node;
		}
		
		/**
		 * Sets the node at the given coords as the end node.
		 * @param x The x coord.
		 * @param y The y coord.
		 */
		public function setEndNode(x:int, y:int):void
		{
			_endNode = _nodes[x][y] as Node;
		}
		
		/**
		 * Sets the node at the given coords as the start node.
		 * @param x The x coord.
		 * @param y The y coord.
		 */
		public function setStartNode(x:int, y:int):void
		{
			_startNode = _nodes[x][y] as Node;
		}
		
		/**
		 * Sets the node at the given coords as walkable or not.
		 * @param x The x coord.
		 * @param y The y coord.
		 */
		public function setWalkable(x:int, y:int, value:Boolean):void
		{
			_nodes[x][y].walkable = value;
		}
		
		
		
		////////////////////////////////////////
		// getters / setters
		////////////////////////////////////////
		
		/**
		 * Returns the end node.
		 */
		public function get endNode():Node
		{
			return _endNode;
		}
		
		/**
		 * Returns the number of columns in the grid.
		 */
		/*public function get numCols():int
		{
			return _numCols;
		}*/
		
		/**
		 * Returns the number of rows in the grid.
		 */
		/*public function get numRows():int
		{
			return _numRows;
		}*/
		
		/**
		 * Returns the start node.
		 */
		public function get startNode():Node
		{
			return _startNode;
		}
		public function clear():void{
			_startNode = null;
			_endNode = null;
			if(_nodes != null){
				for(var i:int = minX; i <= maxX; i++)
				{
					for(var j:int = minY; j <= maxY; j++)
					{
						_nodes[i][j].dispose();
					}
					CommonPool.toPoolArray(_nodes[i]);
				}
				CommonPool.toPoolArray(_nodes);
				_nodes = null;
			}
			_minX = 0;
			_maxX = 0;
			_minY = 0;
			_maxY = 0;
		}
	}
}