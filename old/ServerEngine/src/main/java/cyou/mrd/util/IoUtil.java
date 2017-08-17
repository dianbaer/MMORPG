package cyou.mrd.util;

import org.apache.mina.core.buffer.IoBuffer;



public class IoUtil {

	/**
	 * 封装了协议层中字节存储为高位还是低位优先
	 * @see java.nio.ByteOrder
	 * @param data
	 */
	public static void byteOrder(IoBuffer data) {
//		data.order(ByteOrder.LITTLE_ENDIAN); //C++大小端问题；
	}

}
