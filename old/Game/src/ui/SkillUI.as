package ui
{
	import flash.display.BitmapData;
	import flash.display.BitmapDataChannel;
	import flash.display.Shape;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.ui.Keyboard;
	
	import UI.abstract.component.control.container.Box;
	import UI.abstract.component.control.grid.GridData;
	import UI.abstract.component.event.DragEvent;
	import UI.abstract.utils.CommonPool;
	import UI.theme.defaulttheme.Grid;
	
	import _45degrees.com.friendsofed.isometric.ActivityThing;
	
	import proxy.PlayerProxy;
	
	public class SkillUI extends Box
	{
		private static var helpPoint:Point = new Point(0,0);
		private static var helpRectangle:Rectangle = new Rectangle(0,0,W,H);
		private var gridArray:Array = new Array();
		private var keyArrayValue:Array = [Keyboard.NUMBER_1,Keyboard.NUMBER_2,Keyboard.NUMBER_3,Keyboard.NUMBER_4,Keyboard.Q,Keyboard.E,Keyboard.R,Keyboard.F,Keyboard.Z];
		private var keyArrayShow:Array = ["1","2","3","4","Q","E","R","F","Z"];
		private static var W:int = 32;
		private static var H:int = 32;
		public static var arrCache:Vector.<BitmapData>;
		private var target:ActivityThing;
		public function SkillUI()
		{
			super();
			drawMask();
			for(var i:int = 0;i<keyArrayShow.length;i++){
				var grid : SkillGrid = new SkillGrid();
				grid.setSize( 36, 36 );
				grid.x = i*40;
				grid.y = 0;
				addChild( grid );
				gridArray[i] = grid;
				grid.addEventListener(MouseEvent.MOUSE_DOWN,onClick);
			}
			
			//addEventListener(DragEvent.DRAG_COMPLETE,onGridDragComplete);
			
		}
		private function onClick(event:MouseEvent):void{
			if(GlobalData.inStory){
				return;
			}
			var grid : SkillGrid = event.currentTarget as SkillGrid;
			if(grid.data == null){
				return;
			}
			//取消技能
			if(grid.isCancelSkill){
				var proxy1:PlayerProxy = AppFacade.getInstance().retrieveProxy(PlayerProxy.NAME) as PlayerProxy;
				proxy1.cancelBuff(grid.buffData["id"]);
				return;
			}
			if(grid.isInCD){
				var helpObj:Object = CommonPool.fromPoolObject();
				helpObj.message = "技能正在cd";
				AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj);
				return;
			}
			var helpObj1:Object = CommonPool.fromPoolObject();
			helpObj1.skillId = grid.data.data["id"];
			AppFacade.getInstance().sendNotification(NotiConst.USE_SKILL,helpObj1);
		}
		public function drawMask():void{
			arrCache = new Vector.<BitmapData>;
			var x:uint, y:uint;
			var oShape:Shape = new Shape();
			oShape.graphics.beginFill(0xCC0000, 1);
			oShape.graphics.drawRect(0, 0, W, H);
			oShape.graphics.endFill();
			oShape.graphics.lineStyle(2, 0x000000, 1);
			for(x=W/2; x<W; x++){
				drawLine(x, 0,oShape);
				
			}
			for(y=0; y<H; y++){
				drawLine(W, y,oShape);
				
			}
			
			for(x=0; x<W; x++){
				drawLine(W-x, H,oShape);
				
			}
			
			for(y=0; y<H; y++){
				drawLine(0, H-y,oShape);
				
			}
			
			for(x=0; x<W/2; x++){
				drawLine(x, 0,oShape);
				
			}
		}
		private function drawLine(x:uint, y:uint,oShape:Shape):void
		{
			oShape.graphics.moveTo(W/2, H/2);
			oShape.graphics.lineTo(x, y);		
			
			var bmd:BitmapData = new BitmapData(W, H, true);
			bmd.draw(oShape);
			
			bmd.copyChannel(bmd, helpRectangle, helpPoint, BitmapDataChannel.RED, BitmapDataChannel.ALPHA);
			arrCache[arrCache.length] = bmd;
		}
		public function setSkill(skillArray:Array):void{
			if(skillArray.length  == 0){
				return;
			}
			for(var i:int = 0;i<gridArray.length;i++){
				var grid : SkillGrid = gridArray[i];
				if(skillArray[i] != null && skillArray[i] != 0){
					var skill:Object = GlobalData.skillData[skillArray[i]];
					if(skill){
						var gridData:GridData = new GridData();
						gridData.data = skill;
						gridData.num = keyArrayShow[i];
						gridData.imageUrl = "skill/"+skill["icon"];
						grid.data = gridData;
						//设置冰箱的buffdata
						if(skill["buffId"] != 0){
							var buffData:Object = GlobalData.buffData[skill["buffId"]];
							if(buffData && buffData["type"] == BuffInfo.ICE_BOX){
								grid.buffData = buffData;
							}
						}
						//加入时间轴
						grid.addToJuggler();
					}
					
				}
			}
		}
		public function setTarget(target:ActivityThing):void{
			this.target = target;
			for(var i:int = 0;i<gridArray.length;i++){
				var grid : SkillGrid = gridArray[i];
				grid.setTarget(target);
			}
		}
		public function enterCD(skillData:Object):void{
			for(var i:int = 0;i<gridArray.length;i++){
				var grid : SkillGrid = gridArray[i];
				if(grid.data){
					if(grid.data.data == skillData && grid.data.data["cd"] != 0){
						grid.enterCD(false);
					}else{
						grid.enterCD(true);
					}
				}
			}
		}
		public function exitCD():void{
			for(var i:int = 0;i<gridArray.length;i++){
				var grid : SkillGrid = gridArray[i];
				if(grid.data){
					grid.exitCD();
				}
			}
		}
		public function useSkill(num:int):int{
			var index:int = keyArrayValue.indexOf(num);
			if(index == -1){
				return -1;
			}else{
				var grid : SkillGrid = gridArray[index];
				if(grid.data == null){
					return -1;
				}
				//取消技能
				if(grid.isCancelSkill){
					var proxy1:PlayerProxy = AppFacade.getInstance().retrieveProxy(PlayerProxy.NAME) as PlayerProxy;
					proxy1.cancelBuff(grid.buffData["id"]);
					return -1;
				}
				if(grid.isInCD){
					var helpObj:Object = CommonPool.fromPoolObject();
					helpObj.message = "技能正在cd";
					AppFacade.getInstance().sendNotification(NotiConst.ADD_ERROR_MESSAGE,helpObj);
					return -1;
				}else{
					return grid.data.data["id"];
				}
			}
		}
		private function onGridDragComplete(e : DragEvent):void{
			var grid1 : Grid = e.dragTarget as Grid;
			var grid2 : Grid = e.dropTarget as Grid;
			if(grid2 != null){
				var obj1 : GridData = grid1.data;
				var num1:String = obj1.num;
				var obj2 : GridData = grid2.data;
				obj1.num = obj2.num;
				obj2.num = num1;
				grid1.data = obj2;
				grid2.data = obj1;
			}
		}
		override public function get width():Number{
			return getAllChildrenSize().x;
		}
		override public function get height():Number{
			return getAllChildrenSize().y;
		}
	}
}