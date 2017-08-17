package _45degrees.com.friendsofed.isometric
{
	import flash.display.Sprite;
	import flash.text.TextField;
	import flash.utils.Dictionary;
	
	import UI.abstract.utils.CommonPool;

	public class IsoWorld extends Sprite
	{
		public var _floor:Sprite;
		//所有事物的数组
		public var _objects:Array;
		//玩家的数据（可交互）
		public var _playerObjects:Array;
		//其他事物的数组(不可交互)
		private var _otherObjects:Array;
		
		private var _world:Sprite;
		private var _text:Sprite;
		public var _path:Sprite;
		public var thingDict:Dictionary;
		
		public function IsoWorld()
		{
			_path = new Sprite();
			addChild(_path);
			
			_floor = new Sprite();
			addChild(_floor);
			
			_world = new Sprite();
			addChild(_world);
			_text = new Sprite();
			addChild(_text);
			
			
			_objects = new Array();
			_playerObjects = new Array();
			_otherObjects = new Array();
			thingDict = new Dictionary();
			
		}
		
		public function addChildToWorld(child:IsoObject):void
		{
			_world.addChild(child);
			if(child.interactive){
				_playerObjects[_playerObjects.length] = child;
				thingDict[child.thingId] = child;
			}else{
				_otherObjects[_otherObjects.length] = child;
				thingDict[child.thingId] = child;
			}
			_objects[_objects.length] = child;
			
		}
		public function addChildToText(text:TextField):void{
			_text.addChild(text);
		}
		public function addChildToFloor(child:IsoObject):void
		{
			_floor.addChild(child);
		}
		public function removeChildToWorld(child:IsoObject):void{
			_world.removeChild(child);
			var i:int = 0;
			for(i = 0;i<_objects.length;i++){
				if(_objects[i] == child){
					_objects.splice(i,1);
					break;
				}
			}
			if(child.interactive){
				for(i = 0;i<_playerObjects.length;i++){
					if(_playerObjects[i] == child){
						_playerObjects.splice(i,1);
						break;
					}
				}
				//这里比较危险，IsoObject里面最后把thingId置空了，一定要保证这个thingId被置空后不被使用
				delete thingDict[child.thingId];
			}else{
				for(i = 0;i<_otherObjects.length;i++){
					if(_otherObjects[i] == child){
						_otherObjects.splice(i,1);
						break;
					}
				}
				//这里比较危险，IsoObject里面最后把thingId置空了，一定要保证这个thingId被置空后不被使用
				delete thingDict[child.thingId];
			}
			
		}
		public function sort():void
		{
			_objects.sortOn(["depth","sortId"], [Array.NUMERIC,Array.NUMERIC/*|Array.DESCENDING*/]);
			//这里也要排序的(因为碰撞检测会用到这个数组)
			_playerObjects.sortOn(["depth","sortId"], [Array.NUMERIC,Array.NUMERIC/*|Array.DESCENDING*/]);
			for(var i:int = 0; i < _objects.length; i++)
			{
				_world.setChildIndex(_objects[i], i);
			}
		}
		/*
		public function canMove(obj:IsoObject):Boolean
		{
			var rect:Rectangle = obj.rect;
			rect.offset(obj.vx, obj.vz);
			for(var i:int = 0; i < _objects.length; i++)
			{
				var objB:IsoObject = _objects[i] as IsoObject;
				if(obj != objB && !objB.walkable && rect.intersects(objB.rect))
				{
					return false;
				}
			}
			return true;
		}
		*/
		public function dispose():void{
			_path.graphics.clear();
			if(_floor.numChildren> 0){
				_floor.removeChildren(0,_floor.numChildren-1);
			}
			var text:TextField;
			for(var j:int = 0;j<_text.numChildren;j++){
				text = _text.removeChildAt(0) as TextField;
				CommonPool.toPoolText(text);
			}
			/*
			if(_text.numChildren > 0){
				_text.removeChildren(0,_text.numChildren-1);
			}
			*/
			var length:int = _objects.length;
			for(var i:int = length-1; i >= 0; i--)
			{
				//可能调用dispose时，会把其他的删除了，判断一下（寻路特效）
				if(_objects[i]){
					_objects[i].dispose();
				}
			}
			x = 0;
			y = 0;
			if(parent){
				parent.removeChild(this);
			}
			
		}
		
	}
}