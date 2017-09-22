package cyou.akworld;

import _astar.Node;

public class AIWalk {
	public static Node getNextNode(int x,int y,int startX,int startY,int endX,int endY){
		
		int num = (int)(Math.random()*8);
		Node node =null;
		switch(num){
		case 0:
			node = oneWalk(x,y,startX,startY,endX,endY);
			break;
		case 1:
			node = twoWalk(x,y,startX,startY,endX,endY);
			break;
		case 2:
			node = threeWalk(x,y,startX,startY,endX,endY);
			break;
		case 3:
			node = fourWalk(x,y,startX,startY,endX,endY);
			break;
		case 4:
			node = fiveWalk(x,y,startX,startY,endX,endY);
			break;
		case 5:
			node = sixWalk(x,y,startX,startY,endX,endY);
			break;
		case 6:
			node = sevenWalk(x,y,startX,startY,endX,endY);
			break;
		case 7:
			node = eightWalk(x,y,startX,startY,endX,endY);
			break;
		}
		return node;
	}
	public static Node oneWalk(int x,int y,int startX,int startY,int endX,int endY){
		Node node = new Node();
		if(x-1>=startX && x-1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x-1;
			node.y = y-1;
		}else if(x>=startX && x <= endX && y-1>=startY && y-1 <= endY){
			node.x = x;
			node.y = y-1;
		}else if(x+1>=startX && x+1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x+1;
			node.y = y-1;
		}else if(x-1>=startX && x-1 <= endX && y>=startY && y <= endY){
			node.x = x-1;
			node.y = y;
		}else if(x-1>=startX && x-1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x-1;
			node.y = y+1;
		}else if(x+1>=startX && x+1 <= endX && y>=startY && y <= endY){
			node.x = x+1;
			node.y = y;
		}else if(x>=startX && x <= endX && y+1>=startY && y+1 <= endY){
			node.x = x;
			node.y = y+1;
		}else if(x+1>=startX && x+1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x+1;
			node.y = y+1;
		}
		return node;
	}
	public static Node twoWalk(int x,int y,int startX,int startY,int endX,int endY){
		Node node = new Node();
		if(x>=startX && x <= endX && y-1>=startY && y-1 <= endY){
			node.x = x;
			node.y = y-1;
		}else if(x+1>=startX && x+1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x+1;
			node.y = y-1;
		}else if(x-1>=startX && x-1 <= endX && y>=startY && y <= endY){
			node.x = x-1;
			node.y = y;
		}else if(x-1>=startX && x-1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x-1;
			node.y = y+1;
		}else if(x+1>=startX && x+1 <= endX && y>=startY && y <= endY){
			node.x = x+1;
			node.y = y;
		}else if(x>=startX && x <= endX && y+1>=startY && y+1 <= endY){
			node.x = x;
			node.y = y+1;
		}else if(x+1>=startX && x+1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x+1;
			node.y = y+1;
		}else if(x-1>=startX && x-1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x-1;
			node.y = y-1;
		}
		return node;
	}
	public static Node threeWalk(int x,int y,int startX,int startY,int endX,int endY){
		Node node = new Node();
		if(x+1>=startX && x+1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x+1;
			node.y = y-1;
		}else if(x-1>=startX && x-1 <= endX && y>=startY && y <= endY){
			node.x = x-1;
			node.y = y;
		}else if(x-1>=startX && x-1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x-1;
			node.y = y+1;
		}else if(x+1>=startX && x+1 <= endX && y>=startY && y <= endY){
			node.x = x+1;
			node.y = y;
		}else if(x>=startX && x <= endX && y+1>=startY && y+1 <= endY){
			node.x = x;
			node.y = y+1;
		}else if(x+1>=startX && x+1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x+1;
			node.y = y+1;
		}else if(x-1>=startX && x-1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x-1;
			node.y = y-1;
		}else if(x>=startX && x <= endX && y-1>=startY && y-1 <= endY){
			node.x = x;
			node.y = y-1;
		}
		return node;
	}
	public static Node fourWalk(int x,int y,int startX,int startY,int endX,int endY){
		Node node = new Node();
		if(x-1>=startX && x-1 <= endX && y>=startY && y <= endY){
			node.x = x-1;
			node.y = y;
		}else if(x-1>=startX && x-1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x-1;
			node.y = y+1;
		}else if(x+1>=startX && x+1 <= endX && y>=startY && y <= endY){
			node.x = x+1;
			node.y = y;
		}else if(x>=startX && x <= endX && y+1>=startY && y+1 <= endY){
			node.x = x;
			node.y = y+1;
		}else if(x+1>=startX && x+1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x+1;
			node.y = y+1;
		}else if(x-1>=startX && x-1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x-1;
			node.y = y-1;
		}else if(x>=startX && x <= endX && y-1>=startY && y-1 <= endY){
			node.x = x;
			node.y = y-1;
		}else if(x+1>=startX && x+1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x+1;
			node.y = y-1;
		}
		return node;
	}
	public static Node fiveWalk(int x,int y,int startX,int startY,int endX,int endY){
		Node node = new Node();
		if(x-1>=startX && x-1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x-1;
			node.y = y+1;
		}else if(x+1>=startX && x+1 <= endX && y>=startY && y <= endY){
			node.x = x+1;
			node.y = y;
		}else if(x>=startX && x <= endX && y+1>=startY && y+1 <= endY){
			node.x = x;
			node.y = y+1;
		}else if(x+1>=startX && x+1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x+1;
			node.y = y+1;
		}else if(x-1>=startX && x-1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x-1;
			node.y = y-1;
		}else if(x>=startX && x <= endX && y-1>=startY && y-1 <= endY){
			node.x = x;
			node.y = y-1;
		}else if(x+1>=startX && x+1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x+1;
			node.y = y-1;
		}else if(x-1>=startX && x-1 <= endX && y>=startY && y <= endY){
			node.x = x-1;
			node.y = y;
		}
		return node;
	}
	public static Node sixWalk(int x,int y,int startX,int startY,int endX,int endY){
		Node node = new Node();
		if(x+1>=startX && x+1 <= endX && y>=startY && y <= endY){
			node.x = x+1;
			node.y = y;
		}else if(x>=startX && x <= endX && y+1>=startY && y+1 <= endY){
			node.x = x;
			node.y = y+1;
		}else if(x+1>=startX && x+1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x+1;
			node.y = y+1;
		}else if(x-1>=startX && x-1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x-1;
			node.y = y-1;
		}else if(x>=startX && x <= endX && y-1>=startY && y-1 <= endY){
			node.x = x;
			node.y = y-1;
		}else if(x+1>=startX && x+1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x+1;
			node.y = y-1;
		}else if(x-1>=startX && x-1 <= endX && y>=startY && y <= endY){
			node.x = x-1;
			node.y = y;
		}else if(x-1>=startX && x-1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x-1;
			node.y = y+1;
		}
		return node;
	}
	public static Node sevenWalk(int x,int y,int startX,int startY,int endX,int endY){
		Node node = new Node();
		if(x>=startX && x <= endX && y+1>=startY && y+1 <= endY){
			node.x = x;
			node.y = y+1;
		}else if(x+1>=startX && x+1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x+1;
			node.y = y+1;
		}else if(x-1>=startX && x-1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x-1;
			node.y = y-1;
		}else if(x>=startX && x <= endX && y-1>=startY && y-1 <= endY){
			node.x = x;
			node.y = y-1;
		}else if(x+1>=startX && x+1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x+1;
			node.y = y-1;
		}else if(x-1>=startX && x-1 <= endX && y>=startY && y <= endY){
			node.x = x-1;
			node.y = y;
		}else if(x-1>=startX && x-1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x-1;
			node.y = y+1;
		}else if(x+1>=startX && x+1 <= endX && y>=startY && y <= endY){
			node.x = x+1;
			node.y = y;
		}
		return node;
	}
	public static Node eightWalk(int x,int y,int startX,int startY,int endX,int endY){
		Node node = new Node();
		if(x+1>=startX && x+1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x+1;
			node.y = y+1;
		}else if(x-1>=startX && x-1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x-1;
			node.y = y-1;
		}else if(x>=startX && x <= endX && y-1>=startY && y-1 <= endY){
			node.x = x;
			node.y = y-1;
		}else if(x+1>=startX && x+1 <= endX && y-1>=startY && y-1 <= endY){
			node.x = x+1;
			node.y = y-1;
		}else if(x-1>=startX && x-1 <= endX && y>=startY && y <= endY){
			node.x = x-1;
			node.y = y;
		}else if(x-1>=startX && x-1 <= endX && y+1>=startY && y+1 <= endY){
			node.x = x-1;
			node.y = y+1;
		}else if(x+1>=startX && x+1 <= endX && y>=startY && y <= endY){
			node.x = x+1;
			node.y = y;
		}else if(x>=startX && x <= endX && y+1>=startY && y+1 <= endY){
			node.x = x;
			node.y = y+1;
		}
		return node;
	}
}
