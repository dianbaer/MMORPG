package com.cyou.mrd;

public class ByteConvert {
	public static byte[] longToBytes(long n) { 
		byte[] b = new byte[8]; 
		b[0] = (byte) (n & 0xff);  
		b[1] = (byte) (n >> 8  & 0xff); 
		b[2] = (byte) (n >> 16 & 0xff); 
		b[3] = (byte) (n >> 24 & 0xff); 
		b[4] = (byte) (n >> 32 & 0xff); 
		b[5] = (byte) (n >> 40 & 0xff); 
		b[6] = (byte) (n >> 48 & 0xff); 
		b[7] = (byte) (n >> 56 & 0xff); 
		return b; 
	} 

	public static void longToBytes( long n, byte[] array, int offset ){ 
		array[0+offset] = (byte) (n & 0xff);  
		array[1+offset] = (byte) (n >> 8 & 0xff); 
		array[2+offset] = (byte) (n >> 16 & 0xff); 
		array[3+offset] = (byte) (n >> 24 & 0xff); 
		array[4+offset] = (byte) (n >> 32 & 0xff); 
		array[5+offset] = (byte) (n >> 40 & 0xff); 
		array[6+offset] = (byte) (n >> 48 & 0xff); 
		array[7+offset] = (byte) (n >> 56 & 0xff); 
	} 

	public static long bytesToLong( byte[] array ){  
		return ((((long) array[ 0] & 0xff) << 56) 
		| (((long) array[ 1] & 0xff) << 48) 
		| (((long) array[ 2] & 0xff) << 40) 
		| (((long) array[ 3] & 0xff) << 32) 
		| (((long) array[ 4] & 0xff) << 24) 
		| (((long) array[ 5] & 0xff) << 16) 
		| (((long) array[ 6] & 0xff) << 8) 
		| (((long) array[ 7] & 0xff) << 0));
	} 

		public static byte[] intToBytes(int n) { 
		byte[] b = new byte[4]; 
		b[0] = (byte) (n & 0xff);  
		b[1] = (byte) (n >> 8 & 0xff); 
		b[2] = (byte) (n >> 16 & 0xff); 
		b[3] = (byte) (n >> 24 & 0xff); 
		return b; 
	} 

	public static void intToBytes( int n, byte[] array, int offset ){ 
		array[offset] = (byte) (n & 0xff);  
		array[1+offset] = (byte) (n >> 8 & 0xff); 
		array[2+offset] = (byte) (n >> 16 & 0xff); 
		array[3+offset] = (byte) (n >> 24 & 0xff);
	}
	 
	public static int bytesToInt(byte b[]) { 
		return	b[0] & 0xff 
		| ((b[1] & 0xff) << 8) 
		| ((b[2] & 0xff) << 16) 
		| ((b[3] & 0xff) << 24); 
	} 
	  
	public static byte[] uintToBytes( long n )
	{  
		byte[] b = new byte[4]; 
		b[0] = (byte) (n & 0xff);  
		b[1] = (byte) (n >> 8 & 0xff); 
		b[2] = (byte) (n >> 16 & 0xff); 
		b[3] = (byte) (n >> 24 & 0xff); 
		return b; 
	} 
	  
	public static void uintToBytes( long n, byte[] array, int offset ){ 
		array[offset] = (byte) (n );  
		array[1+offset] = (byte) (n >> 8 & 0xff); 
		array[2+offset] = (byte) (n >> 16 & 0xff); 
		array[3+offset]	= (byte) (n >> 24 & 0xff); 
	} 
	  
	public static long bytesToUint(byte[] array) {  
		return ((long) (array[0] & 0xff))  
		| ((long) (array[1] & 0xff)) << 8  
		| ((long) (array[2] & 0xff)) << 16  
		| ((long) (array[3] & 0xff)) << 24;  
	} 
	  
	  
	public static byte[] shortToBytes(short n) { 
		byte[] b = new byte[2];  
		b[0] = (byte) ( n& 0xff); 
		b[1] = (byte) ((n >> 8) & 0xff); 
		return b; 
	}  

	public static void shortToBytes(short n, byte[] array, int offset ) {
		array[offset] = (byte) ( n & 0xff); 
		array[offset+1] = (byte) ((n >> 8) & 0xff); 
	} 

	public static short bytesToShort(byte[] b){ 
		return (short)( b[0] & 0xff  |(b[1] & 0xff) << 8 ); 
	}

	
	public static byte[] ushortToBytes(int n) { 
		byte[] b = new byte[2];  
		b[0] = (byte) ( n & 0xff); 
		b[1] = (byte) ((n >> 8) & 0xff); 
		return b; 
	}

	public static void ushortToBytes(int n, byte[] array, int offset ) { 
		array[offset] = (byte) ( n & 0xff); 
		array[offset+1] = (byte) ((n >> 8) & 0xff); 
	} 
	  
	public static int bytesToUshort(byte b[]) { 
		return b[0] & 0xff | (b[1] & 0xff) << 8; 
	}

	public static byte[] ubyteToBytes( int n ){ 
		byte[] b = new byte[1]; 
		b[0] = (byte) (n & 0xff); 
		return b; 
	} 

	public static void ubyteToBytes( int n, byte[] array, int offset ){ 
		array[0] = (byte) (n & 0xff);
	} 

}
