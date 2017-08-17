package net
{
	import flash.utils.ByteArray;
	
	public class ByteArrayBuffer
	{
		//头字节1
		public static const BEGINCHAR1:int = 85; //U
		//头字节2
		public static const BEGINCHAR2:int = 65;  //A
		//头字节与长度一共的长度
		public static const LENGTH:int = 6;
		
		//缓冲区
		private var buf:ByteArray;
		
		public function clear():void{
			if(buf != null){
				toPool(buf);
				buf = null;
			}
		}
		public function pushByteArray(ba:ByteArray):Boolean
		{
			if(buf == null)
			{
				buf = ba;
				return false;
			}	
			else
			{
				buf.position = buf.length;
				buf.writeBytes(ba);
				buf.position = 0;
				
				return true;
			}
			
		}
		public function getPacketList(array:Array = null):Array
		{
			if(array == null) array = new Array();
			
			this.splitAnimal(array);
			return array;			
		}
		private function splitAnimal(rt_arr:Array):void
		{
			if(!buf) return;
			
			var tmp:ByteArray;
			while(true)
			{
				if(buf.length>LENGTH){
					
					var tou1:int = buf.readByte();
					var tou2:int = buf.readByte();
					if(tou1 == BEGINCHAR1 && tou2 == BEGINCHAR2){
						var len:int = buf.readInt();
						if(buf.bytesAvailable>=(len-LENGTH)){  //去掉head以及len一共6个字节
							
							tmp = fromPool();
							tmp.writeBytes(buf,LENGTH,len-LENGTH);
							rt_arr[rt_arr.length] = tmp;
							if(buf.bytesAvailable == (len-LENGTH)){
								toPool(buf);
								buf = null;
								break;
							}else{
								var t:ByteArray = fromPool();
								t.writeBytes(buf,len,buf.length-len);
								toPool(buf);
								buf = t;
								buf.position = 0;
							}
						}else{
							buf.position = 0;
							break;
						}
					}else{
						toPool(buf);
						buf = null;
						break;
					}
				}else{
					break;
				}
			}
			
		}
		//二进制池
		private static var _typeArrayPool : Vector.<ByteArray> = new Vector.<ByteArray>();
		public static function fromPool ( ) : ByteArray
		{
			var byteArray : ByteArray;
			if ( _typeArrayPool.length )
			{
				byteArray = _typeArrayPool.pop();
			}
			else
				byteArray = new ByteArray();
			return byteArray;
		}
		
		
		public static function toPool ( byteArray : ByteArray ) : void
		{
			byteArray.clear();
			_typeArrayPool[_typeArrayPool.length] = byteArray;
		}
		
		
		public static function clearPool () : void
		{
			_typeArrayPool.length = 0;
		}
		
	}
}