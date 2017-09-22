package net
{
	import flash.utils.ByteArray;

	public class TcpPacket
	{
		private var _code:String;
		private var _body:ByteArray;
		public function TcpPacket(code:String)
		{
			reset(code);
			
		}

		public function get body():ByteArray
		{
			return _body;
		}
		public function reset(code:String):TcpPacket{
			_code = code;
			_body = ByteArrayBuffer.fromPool();
			_body.writeShort(int(_code));
			return this;
		}
		public function dispose():void{
			ByteArrayBuffer.toPool(_body);
			_body = null;
			_code = null;
			toPool(this);
		}
		public function writeInt(num:int):void{
			_body.writeInt(num);
		}
		public function writeUTF(str:String):void{
			_body.writeUTF(str);
		}
		public function writeByte(num:int):void{
			_body.writeByte(num);
		}
		public function writeUTFBytes(str:String):void{
			_body.writeUTFBytes(str);
		}
		public function writeFloat(num:Number):void{
			_body.writeFloat(num);
		}
		public function writeDouble(num:Number):void{
			_body.writeDouble(num);
		}
		private static var sTcpPacketPool:Vector.<TcpPacket> = new <TcpPacket>[];
		
		/** @private */
		public static function fromPool(code:String):TcpPacket
		{
			if (sTcpPacketPool.length) return sTcpPacketPool.pop().reset(code) as TcpPacket;
			else return new TcpPacket(code);
		}
		
		/** @private */
		public static function toPool(tcpPacket:TcpPacket):void
		{
			sTcpPacketPool[sTcpPacketPool.length] = tcpPacket;
		}
	}
}