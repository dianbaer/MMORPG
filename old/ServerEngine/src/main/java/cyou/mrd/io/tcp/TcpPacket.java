package cyou.mrd.io.tcp;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.mina.core.buffer.IoBuffer;



import cyou.mrd.util.IoUtil;

public class TcpPacket {
	
	public static final byte[] HEAD = {'U','A'};
	
	private static final Charset utf8 = Charset.forName("utf-8");
	private static final Charset utf16be = Charset.forName("UTF-16BE");
	//private static final CharsetEncoder encoder = utf8.newEncoder();
	//private static final CharsetDecoder decoder = utf8.newDecoder();
	
	short opCode;
	
	IoBuffer data;
	private TcpClient _tcpClient;
	public TcpPacket(short opCode){
		this.opCode = opCode;
		this.data = IoBuffer.allocate(128);
		data.setAutoExpand(true);
		IoUtil.byteOrder(this.data);
	}
	public void setClient(TcpClient tcpClient){
		_tcpClient = tcpClient;
	}
	public TcpClient getClient(){
		return _tcpClient;
	}
	public void clear(){
		_tcpClient = null;
		data.clear();
		data = null;
		opCode = 0;
	}
	public TcpPacket(short opCode,IoBuffer data){
		this.opCode = opCode;
		this.data = data;
		IoUtil.byteOrder(this.data);
	}
	
	public short getOpCode(){
		return opCode;
	}
	
	public void put(byte value){
		data.put(value);
	}
	
	public byte get(){
		return data.get();
	}
	
	public byte getByte(){
		return data.get();
	}
	
	public void put(byte[] value){
		data.putInt(value.length);
		data.put(value);
	}
	
	public void put(byte[] value,int off,int length){
		data.putInt(length);
		data.put(value,off,length);
	}
	

	
	public void putPlain(byte[] value){
		data.put(value);
	}
	
	public void putInts(int[] value){
		data.putShort((short)value.length);
		for(int i=0;i<value.length;i++){
			data.putInt(value[i]);
		}
	}
	public void putShorts(short[] value){
		data.putShort((short)value.length);
		for(int i=0;i<value.length;i++){
			data.putShort(value[i]);
		}
	}
	public byte[] getBytes(){
		int len = data.getInt();
		byte[] ret = new byte[len];
		data.get(ret);
		return ret;
	}
	
	public void putShort(short value){
		data.putShort(value);
	}
	
	public short getShort(){
		return data.getShort();
	}
	
	public void putInt(int value){
		data.putInt(value);
	}
	
	public int getInt(){
		return data.getInt();
	}
	
	public int[] getInts() {
		short len = data.getShort();
		int[] ret = new int[len];
		for (int i = 0; i < len; i++) {
			ret[i] = data.getInt();
		}
		return ret;
	}
	
	public void putLong(long value){
		data.putLong(value);
	}
	
	public long getLong(){
		return data.getLong();
	}
	public float getFloat(){
		return data.getFloat();
		
	}
	public double getDouble(){
		return data.getDouble();
	}
	public void putDouble(double b){
		data.putDouble(b);
	}
	public int get(byte[] bs) {
		IoBuffer ret = data.get(bs, 0, bs.length);
		return ret.remaining();
	}

	public int getUnsignedByte() {
		return data.getUnsigned();
	}

	public int getUnsignedShort() {
		return data.getUnsignedShort();
	}


	public void put(int b) {
		data.put((byte)b);
	}

	public void putShort(int s) {
		data.putShort((short)s);
	}

	public void putString(String s) {
//		byte[] bytes = s.getBytes(utf8);
//		data.putShort((short)bytes.length);
//		data.put(bytes);
        byte[] bytes = s.getBytes(utf8);
		byte[] bytes2 = s.getBytes(utf16be);
		if (bytes.length < bytes2.length) {
		if (bytes.length > 32767) {
			throw new IllegalArgumentException();
		}
		data.putShort((short) bytes.length);
		data.put(bytes);
		} else {
            if (bytes2.length > 32767) {
                throw new IllegalArgumentException();
            }
		    data.putShort((short)(bytes2.length | 0x8000));
		    data.put(bytes2);
		}
	}
	
	public void putUTF(String s) {
	    byte[] bytes = s.getBytes(utf8);
        data.putShort((short)bytes.length);
        data.put(bytes);
	}
	
	public String getString() {
	    int len = data.getShort() & 0xFFFF;
	    if ((len & 0x8000) == 0) {
	        byte[] buf = new byte[len];
	        data.get(buf);
	        return new String(buf, utf8);
	    } else {
	        len &= 0x7FFF;
	        byte[] buf = new byte[len];
            data.get(buf);
            return new String(buf, utf16be);
	    }
	}
	
	public IoBuffer getData(){
		return data;
	}

	public String toString() {
		if (data.array().length < 200) {
			return Arrays.toString(this.data.array());
		} else {
			return "TcpDataLength: " + data.array().length;
		}
	}

}
