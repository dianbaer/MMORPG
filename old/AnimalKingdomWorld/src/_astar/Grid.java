package _astar;

import java.util.HashMap;

public class Grid {
	private Node _startNode;
	private Node _endNode;
	private HashMap<String, Node> _nodes;

	private int _minX;
	private int _maxX;
	private int _minY;
	private int _maxY;
	public Grid(int minX, int maxX, int minY,int maxY)
	{
		
		_minX = minX;
		_maxX = maxX;
		_minY = minY;
		_maxY = maxY;
		_nodes = new HashMap<String, Node>();
		
		for(int i = minX; i <= maxX; i++)
		{
			
			for(int j = minY; j <= maxY; j++)
			{
				_nodes.put(i+"_"+j, new Node(i, j));
			}
		}
	}
	public int get_minX() {
		return _minX;
	}
	public void set_minX(int _minX) {
		this._minX = _minX;
	}
	public int get_maxX() {
		return _maxX;
	}
	public void set_maxX(int _maxX) {
		this._maxX = _maxX;
	}
	public int get_minY() {
		return _minY;
	}
	public void set_minY(int _minY) {
		this._minY = _minY;
	}
	public int get_maxY() {
		return _maxY;
	}
	public void set_maxY(int _maxY) {
		this._maxY = _maxY;
	}
	
	public Node getNode(int x,int y)
	{
		return _nodes.get(x+"_"+y);
	}
	
	
	public void setEndNode(int x,int y)
	{
		_endNode = _nodes.get(x+"_"+y);
	}
	
	
	public void setStartNode(int x, int y)
	{
		_startNode = _nodes.get(x+"_"+y);
	}
	
	
	public void setWalkable(int x, int y, boolean value)
	{
		_nodes.get(x+"_"+y).walkable = value;
	}
	public Node getEndNode()
	{
		return _endNode;
	}
	
	
	public Node getStartNode()
	{
		return _startNode;
	}
	public void clear(){
		_startNode = null;
		_endNode = null;
		_nodes.clear();
		_nodes = null;
	}
}
