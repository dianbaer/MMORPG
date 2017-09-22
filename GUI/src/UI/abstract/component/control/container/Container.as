package UI.abstract.component.control.container
{
	import UI.App;
	import UI.abstract.component.control.base.UIActiveCompent;
	import UI.abstract.component.control.base.UIComponent;
	import UI.abstract.manager.LogManager;
	
	import flash.display.DisplayObject;
	import flash.geom.Point;
	import flash.utils.getQualifiedClassName;
	
	
	/**
	 * 没有背景的容器
	 */
	public class Container extends UIActiveCompent
	{
		
		
		private var mChildren:Vector.<DisplayObject>;
		
		
		public function Container()
		{
			super();
			mChildren = new Vector.<DisplayObject>();
		}
		
		
		override public function dispose():void
		{
			for (var i:int=mChildren.length-1; i>=0; --i){
				var obj : DisplayObject = removeChildAt(i);
				if(obj is UIComponent){
					(obj as UIComponent).dispose();
				}
				
			}
			if(mChildren.length>0){
				App.log.info( "有子对象没移除："+super.numChildren, LogManager.LOG_WARN , getQualifiedClassName( this ) , "dispose");
			}
			mChildren = null;
			super.dispose();
		}
		
		
		override public function addChild(child:DisplayObject):DisplayObject
		{
			addChildAt(child, numChildren);
			return child;
		}
		
		
		override public function addChildAt(child:DisplayObject, index:int):DisplayObject
		{
			var numChildren:int = mChildren.length; 
			
			if (index >= 0 && index <= numChildren)
			{
				if(child.parent){
					child.parent.removeChild(child);
				}
				
				
				if (index == numChildren) mChildren.push(child);
				else                      mChildren.splice(index, 0, child);
				
				super.addChildAt(child,index);
				
				
				return child;
			}
			else
			{
				throw new RangeError("Invalid child index");
			}
		}
		
		
		override public function removeChild(child:DisplayObject/*, dispose:Boolean=false*/):DisplayObject
		{
			var childIndex:int = getChildIndex(child);
			if (childIndex != -1) removeChildAt(childIndex/*, dispose*/);
			return child;
		}
		
		
		override public function removeChildAt(index:int/*, dispose:Boolean=false*/):DisplayObject
		{
			if (index >= 0 && index < numChildren)
			{
				var child:DisplayObject = mChildren[index];
				super.removeChildAt(index);
				
				index = mChildren.indexOf(child); 
				if (index >= 0) mChildren.splice(index, 1); 
				/*if (dispose) child.dispose();*/
				
				return child;
			}
			else
			{
				throw new RangeError("Invalid child index");
			}
		}
		
		
		override public function removeChildren(beginIndex:int=0, endIndex:int=-1/*, dispose:Boolean=false*/):void
		{
			if (endIndex < 0 || endIndex >= numChildren) 
				endIndex = numChildren - 1;
			
			for (var i:int=beginIndex; i<=endIndex; ++i)
				removeChildAt(beginIndex/*, dispose*/);
		}
		
		
		override public function getChildAt(index:int):DisplayObject
		{
			if (index >= 0 && index < numChildren)
				return mChildren[index];
			else
				throw new RangeError("Invalid child index");
		}
		
		
		override public function getChildByName(name:String):DisplayObject
		{
			var numChildren:int = mChildren.length;
			for (var i:int=0; i<numChildren; ++i)
				if (mChildren[i].name == name) return mChildren[i];
			
			return null;
		}
		
		
		override public function getChildIndex(child:DisplayObject):int
		{
			return mChildren.indexOf(child);
		}
		
		
		override public function setChildIndex(child:DisplayObject, index:int):void
		{
			var oldIndex:int = getChildIndex(child);
			if (oldIndex == -1) throw new ArgumentError("Not a child of this container");
			mChildren.splice(oldIndex, 1);
			mChildren.splice(index, 0, child);
			super.setChildIndex(child,index);
		}
		
		
		override public function swapChildren(child1:DisplayObject, child2:DisplayObject):void
		{
			var index1:int = getChildIndex(child1);
			var index2:int = getChildIndex(child2);
			if (index1 == -1 || index2 == -1) throw new ArgumentError("Not a child of this container");
			swapChildrenAt(index1, index2);
		}
		
		
		override public function swapChildrenAt(index1:int, index2:int):void
		{
			var child1:DisplayObject = getChildAt(index1);
			var child2:DisplayObject = getChildAt(index2);
			mChildren[index1] = child2;
			mChildren[index2] = child1;
			super.swapChildrenAt(index1,index2);
		}
		
		
		override public function contains(child:DisplayObject):Boolean
		{
			while (child)
			{
				if (child == this) return true;
				else child = child.parent;
			}
			return false;
		}
		/***
		 * 获取所有子对象一共的大小（基于正x,y坐标）
		 */
		public function getAllChildrenSize(size:Point=null):Point
		{
			if (size == null) size = new Point();
			
			
			if (numChildren == 0)
			{
				//size.setTo(0,0);
				size.x = 0;
				size.y = 0;
				
				return size;
			}
			else if (numChildren == 1)
			{
				//size.setTo(mChildren[0].x+mChildren[0].width,mChildren[0].y+mChildren[0].height);
				size.x = mChildren[0].x+mChildren[0].width;
				size.y = mChildren[0].y+mChildren[0].height;
				return size;
			}
			else
			{
				var maxWidth:Number = -Number.MAX_VALUE;
				var maxHeight:Number = -Number.MAX_VALUE;
				
				for (var i:int=0; i<numChildren; ++i)
				{
					
					maxWidth = maxWidth > mChildren[i].x+mChildren[i].width ? maxWidth : mChildren[i].x+mChildren[i].width;
					
					maxHeight = maxHeight > mChildren[i].y+mChildren[i].height ? maxHeight : mChildren[i].y+mChildren[i].height;
				}
				//size.setTo(maxWidth,maxHeight);
				size.x = maxWidth;
				size.y = maxHeight;
				return size;
			}                
		}
		
		override public function get numChildren():int { return mChildren.length; }        
	}
}
