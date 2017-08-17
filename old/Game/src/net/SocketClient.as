package net
{
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.IOErrorEvent;
	import flash.events.ProgressEvent;
	import flash.events.SecurityErrorEvent;
	import flash.net.Socket;
	import flash.utils.ByteArray;
	
	public class SocketClient extends EventDispatcher
	{
		
		private var socket:Socket;
		private var buf:ByteArrayBuffer = new ByteArrayBuffer();
		private static var helpArray:Array = new Array();
		public function SocketClient()
		{
			super();
		}
		public function resetConnect(server:String=null,port:int=0):void{
			socket = new Socket();
			socket.addEventListener(IOErrorEvent.IO_ERROR,Net_Error);
			socket.addEventListener(Event.CLOSE, Net_Error);
			socket.addEventListener(Event.CONNECT,Net_Connect);
			socket.addEventListener(SecurityErrorEvent.SECURITY_ERROR,Net_Error);
			socket.addEventListener(ProgressEvent.SOCKET_DATA,Net_Data);
			socket.connect(server,port);
		}
		public function clear():void{
			socket.removeEventListener(IOErrorEvent.IO_ERROR,Net_Error);
			socket.removeEventListener(Event.CLOSE, Net_Error);
			socket.removeEventListener(Event.CONNECT,Net_Connect);
			socket.removeEventListener(SecurityErrorEvent.SECURITY_ERROR,Net_Error);
			socket.removeEventListener(ProgressEvent.SOCKET_DATA,Net_Data);
			socket = null;
			buf.clear();
		}
		private function Net_Error(evt:Event):void
		{
			dispatchEvent(new CodeEvent(CodeEvent.DISCONNECT,null));
		}
		private function Net_Connect(evt:Event):void
		{
			dispatchEvent(new CodeEvent(CodeEvent.CONNECT,null));
			
		}
		public function send(tcpPacket:TcpPacket):void{
			if(!socket.connected){
				return;
			}
			var byte:ByteArray = ByteArrayBuffer.fromPool();
			byte.writeByte(ByteArrayBuffer.BEGINCHAR1);
			byte.writeByte(ByteArrayBuffer.BEGINCHAR2);
			byte.writeInt(tcpPacket.body.length+ByteArrayBuffer.LENGTH);
			byte.position = byte.length;
			byte.writeBytes(tcpPacket.body);
			byte.position = 0;
			//trace(byte.readByte());
			///trace(byte.readByte());
			//trace(byte.readInt());
			
			
			//byte.position = 0;
			tcpPacket.dispose();
			socket.writeBytes(byte,0,byte.length);
			socket.flush();
			ByteArrayBuffer.toPool(byte);
		}
		//private var num:int = 0;
		private function Net_Data(evt:ProgressEvent):void
		{
			
			var ba:ByteArray = ByteArrayBuffer.fromPool();
			
			socket.readBytes(ba,0,evt.bytesTotal);
			if(buf.pushByteArray(ba)){
				ByteArrayBuffer.toPool(ba);
			}
			
			/*if(num == 0 || num == 1){
				num++;
				return;
			}
			num = 0;*/
			buf.getPacketList(helpArray);
			
			for each(ba in helpArray)
				handler(ba);
				
			helpArray.length = 0;
		}
		protected function handler(ba:ByteArray):void
		{
			ba.position = 0;
			var opcode:int = ba.readShort();
			//trace(opcode);
			var event:CodeEvent = new CodeEvent(opcode.toString(),ba);
			this.dispatchEvent(event);
			event.dispose();
		}
		
	}
}