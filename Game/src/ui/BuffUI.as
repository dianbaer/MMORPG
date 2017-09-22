package ui
{
	
	
	public class BuffUI extends Box implements IAnimatable
	{
		private var target:ActivityThing;
		private var deBuffList:Array = new Array();
		private var buffList:Array = new Array();
		public function BuffUI()
		{
			super();
			
			
		}
		public function setTarget(target:ActivityThing):void{
			this.target = target;
			JugglerManager.fourJuggler.add(this);
		}
		public function advanceTime(time:Number):void{
			onFrame();
		}
		private function onFrame():void{
			if(target.buffIsChange){
				refreshBuff();
				target.buffIsChange = false;
			}
		}
		public function refreshBuff():void{
			
			for each ( var buffArray : BuffArray in target.buffDict )
			{
				if(buffArray.buffArray.length > 0){
					for(var i:int = 0;i<buffArray.buffArray.length;i++){
						var buffInfo:BuffInfo = buffArray.buffArray[i];
						var isHave:Boolean = false;
						var arrayList:Array;
						if(buffInfo.buffData["isEnemy"] == 1){
							arrayList = deBuffList;
						}else{
							arrayList = buffList;
						}
						for(var j:int = 0;j<arrayList.length;j++){
							var buffInfo1:BuffInfo = arrayList[j];
							if(buffInfo1.masterId == buffInfo.masterId && buffInfo1.buffData["id"] == buffInfo.buffData["id"]){
								if(buffInfo.surplusTime != 0){
									buffInfo1.surplusTime = buffInfo.surplusTime;
									//这里也清0
									buffInfo.surplusTime = 0;
								}
								isHave = true;
								break;
							}
						}
						if(!isHave){
							//把这个任务的buff时间清0，主要是为了区分
							arrayList.push(buffInfo.clone());
							buffInfo.surplusTime = 0;
						}
					}
				}
			}
			
			for(var m:int = 0;m<deBuffList.length;m++){
				var isHave:Boolean = false;
				var buffInfo:BuffInfo = deBuffList[m];
				var buffArray : BuffArray = target.buffDict[buffInfo.buffData["type"]];
				if(buffArray && buffArray.buffArray.length > 0){
					for(var n:int = 0;n<buffArray.buffArray.length;n++){
						var buffInfo1:BuffInfo = buffArray.buffArray[n];
						if(buffInfo1.masterId == buffInfo.masterId && buffInfo1.buffData["id"] == buffInfo.buffData["id"]){
							isHave = true;
							break;
						}
					}
				}
					
				if(!isHave){
					deBuffList.spite(m);
					buffInfo.dispose();
					m--;
				}
			}
			for(var mm:int = 0;mm<buffList.length;mm++){
				var isHave:Boolean = false;
				var buffInfo:BuffInfo = buffList[mm];
				var buffArray : BuffArray = target.buffDict[buffInfo.buffData["type"]];
				if(buffArray && buffArray.buffArray.length > 0){
					for(var nn:int = 0;nn<buffArray.buffArray.length;nn++){
						var buffInfo1:BuffInfo = buffArray.buffArray[nn];
						if(buffInfo1.masterId == buffInfo.masterId && buffInfo1.buffData["id"] == buffInfo.buffData["id"]){
							isHave = true;
							break;
						}
					}
				}
					
				if(!isHave){
					buffList.spite(mm);
					buffInfo.dispose();
					mm--;
				}
			}
			resetBuffItem();
		}
		private function resetBuffItem():void{
			
		}
		override public function get width():Number{
			return getAllChildrenSize().x;
		}
		override public function get height():Number{
			return getAllChildrenSize().y;
		}
	}
}