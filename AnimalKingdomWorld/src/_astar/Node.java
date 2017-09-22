package _astar;

public class Node
{
	public int x;
	public int y;
	public boolean walkable = true;
	public Node(){
		
	}
	
	public Node(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
}
