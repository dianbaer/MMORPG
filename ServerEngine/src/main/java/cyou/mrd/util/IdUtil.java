package cyou.mrd.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdUtil {

	public static final Logger log = LoggerFactory.getLogger(IdUtil.class);

	private static Map<Integer, String> encode = new HashMap<Integer, String>();

	private static Map<Character, String> decode = new HashMap<Character, String>();

	private static int seed = 13580;

	// 这里大小写字母意义相同
	static {
		encode.put(0, "r");
		encode.put(1, "t");
		encode.put(2, "v");
		encode.put(3, "b");
		encode.put(4, "x");
		encode.put(5, "z");
		encode.put(6, "k");
		encode.put(7, "p");
		encode.put(8, "e");
		encode.put(9, "s");

		decode.put('R', "0");
		decode.put('r', "0");
		decode.put('T', "1");
		decode.put('t', "1");
		decode.put('V', "2");
		decode.put('v', "2");
		decode.put('B', "3");
		decode.put('b', "3");
		decode.put('X', "4");
		decode.put('x', "4");
		decode.put('Z', "5");
		decode.put('z', "5");
		decode.put('K', "6");
		decode.put('k', "6");
		decode.put('P', "7");
		decode.put('p', "7");
		decode.put('E', "8");
		decode.put('e', "8");
		decode.put('S', "9");
		decode.put('s', "9");
	}

	public static String enCode(int id) {
		log.info("[IdUtil]enCode(id:{})", id);
		String ret = "";
		try {
			id += seed;
			id *= 3;
			String key = id + "";
			char[] keys = key.toCharArray();
			StringBuffer sb = new StringBuffer();
			for (char c : keys) {
				String value = encode.get(Integer.parseInt(String.valueOf(c)));
				sb.append(value);
			}
			ret = sb.toString();
			log.info("IdUtil:id:{},enCodeId:{}", id, ret);
		} catch (Exception e) {
			log.error("IdUtil Exception:id:{}", id, e);
		}
		return ret;
	}

	public static int deCode(String value) {
		return deCode(value, true);
	}

	public static int deCode(String value, boolean isAssertEncoded) {
		log.info("[IdUtil]deCode(value:{})", value);
		if (value == null || value.equals("")) {
			log.info("[IdUtil]deCode(value:{} loop:value is error)", value);
			return -1;
		}
		int id = 0;
		try {
			char[] values = value.toCharArray();
			StringBuffer sb = new StringBuffer();
			for (char c : values) {
				String key = decode.get(c);
				if (key == null) {
					return -1;
				}
				sb.append(key);
			}
			int id1 = Integer.parseInt(sb.toString());
			int remainder = id1 % 3;
			if(remainder != 0){
				return -1;
			}
			id = id1 / 3 - seed;
			log.info("[IdUtil]deCode(value:{})->id:{}",value, id);
		} catch (Throwable e) {
			if (isAssertEncoded) {
				log.error("[IdUtil]deCode(value:{})", value, e);
			}
		}
		return id;
	}

	public static void main(String[] args) {
//		int testid = 2;
//		String id = IdUtil.enCode(testid);
//		log.info(testid + "编码后变为 :  " + id);
		log.info("解码后变为 :  " + deCode("xrerk"));
		System.out.println("解码后变为 :  " + deCode("PDA"));
	}

}
