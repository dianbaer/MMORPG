package cyou.mrd.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.XMLConfiguration;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Utils {
	private static final Logger log = LoggerFactory.getLogger(Utils.class);

	private static final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
	

	public static String getHourString(Date date) {
		if (date == null) {
			return "";
		}
		return hourFormat.format(date);
	}

	public static String getDayString(Date date) {
		if (date == null) {
			return "";
		}
		return dayFormat.format(date);
	}

	public static String getDateString(Date date) {
		if (date == null) {
			return "";
		}
		return format.format(date);
	}
	
	//精确到10分钟
	public static String getMinuteString(Date date) {
		if (date == null) {
			return "";
		}
		String s = format.format(date);
		return s.substring(0,15) + "0:00";
	}


	private static final DecimalFormat percentFormat = new DecimalFormat("####.#");

	public static String formatValue(int value) {
		return String.valueOf(Math.abs(value));
	}

	public static String formatValue(float value) {
		return formatFloat(Math.abs(value));
	}

	public static String formatValue(String value) {
		return value;
	}

	public static String formatPercent(float p) {
		return percentFormat.format(Math.abs(p)) + "%";
	}

	public static String formatFloat(double p) {
		return percentFormat.format(Math.abs(p));
	}

	// 读取txt文件
	public static List<String> getLinesFormTXT(String szFileName) {
		log.info("[TEXT] read({})", szFileName);
		List<String> content = new ArrayList<String>();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(szFileName);
		} catch (FileNotFoundException e) {
			log.info("txt file pattern fail[{}]", szFileName);
			log.error("FileNotFoundException", e);
		}

		if (fis != null) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(fis, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				log.error("[txt] UnsupportedEncodingException", e);
			}
			try {
				// 跳过第1,2行
				br.readLine();
				br.readLine();
				String line;
				while (br.ready()) {
					line = br.readLine();
					if (line != null) {
						// #有注释
						if (line.startsWith("#"))
							continue;
						content.add(line);
						// log.info(line);
					}
				}
			} catch (IOException e) {
				log.error("IOException", e);
			} catch (Throwable t) {
				log.error("[TEXT] read error", t);
			}
		}

		// 关闭数据流
		if (fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
				log.error("IOException", e);
			}
		}
		log.info("[getLinesFormTXT] return[size({}) content(...{})]", content.size(), content.size() > 0 ? content.get(content.size() - 1)
				: "[none]");
		return content;
	}

	// 加载属性文件
	public Properties loadconfig(String filePath) {
		Properties propertie = null;
		try {
			FileInputStream inputFile = new FileInputStream(filePath);
			propertie = new Properties();
			propertie.load(inputFile);
			inputFile.close();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.info("[loadconfig] return[propertiesize[{}]]", propertie.size());
		return propertie;
	}

	// xml操作
	public static Document getDocument(InputStream r) throws Exception {
		SAXReader reader = new SAXReader();
		return reader.read(r);
	}

	public static Document getDocument(Reader r) throws Exception {
		SAXReader reader = new SAXReader();
		return reader.read(r);
	}

	public static void saveDocument(Document doc, Writer w) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter writer = new XMLWriter(w, format);
		try {
			writer.write(doc);
		} catch (IOException e) {
			log.error("IOException", e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 验证玩家姓名是否符合规则
	 * 
	 * @param name
	 * @return
	 */
	public static CheckNameState checkUserName(String name) {
		CheckNameState ret = new CheckNameState();
		ret.errorCode = 0;
		if (name == null) {
			ret.errorCode = 20;
		}
		if (name.indexOf('%') != -1 || name.indexOf('\'') != -1 || name.indexOf('\"') != -1 || name.indexOf('$') != -1) {
			ret.errorCode = 21;
		}

		String t = name.replaceAll("[^0-9A-Za-z\\d\\u0000-\\uFFFF]*" , "");
		if (t.length() != name.length()) {
			ret.errorCode = 21;
		}

		if (name.length() < 2) {
			ret.errorCode = 22;
		}
		if (name.length() > 18) {
			ret.errorCode = 23;
		}

		if (ret.errorCode == 0) {
			ret.sucess = true;
		}
		return ret;
	}

	public static class CheckNameState {
		public boolean sucess;
		public int errorCode;
	}

	private static final String PLACEHOLDER_START = "${";

	public static void resolvePlaceHolders(XMLConfiguration conf) {
		Iterator<?> iterator = conf.getKeys();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String value = resolvePlaceHolder(conf.getString(key));
			conf.setProperty(key, value);
		}
	}

	/**
	 * Handles interpolation processing for a single property.
	 * 
	 * @param property
	 *            The property value to be processed for interpolation.
	 * @return The (possibly) interpolated property value.
	 */
	private static String resolvePlaceHolder(String property) {
		if (property.indexOf(PLACEHOLDER_START) < 0) {
			return property;
		}
		StringBuffer buff = new StringBuffer();
		char[] chars = property.toCharArray();
		for (int pos = 0; pos < chars.length; pos++) {
			if (chars[pos] == '$') {
				// peek ahead
				if (chars[pos + 1] == '{') {
					// we have a placeholder, spin forward till we find the end
					String systemPropertyName = "";
					int x = pos + 2;
					for (; x < chars.length && chars[x] != '}'; x++) {
						systemPropertyName += chars[x];
						// if we reach the end of the string w/o finding the
						// matching end, that is an exception
						if (x == chars.length - 1) {
							throw new IllegalArgumentException("unmatched placeholder start [" + property + "]");
						}
					}
					String systemProperty = extractFromSystem(systemPropertyName);
					buff.append(systemProperty == null ? "" : systemProperty);
					pos = x + 1;
					// make sure spinning forward did not put us past the end of
					// the buffer...
					if (pos >= chars.length) {
						break;
					}
				}
			}
			buff.append(chars[pos]);
		}

		String rtn = buff.toString();

		if (rtn == null) {
			log.info("[resolvePlaceHolder] return[null]");
			return null;
		} else {
			rtn = rtn.trim();
			String retStr = rtn.length() == 0 ? null : rtn;
			log.info("[resolvePlaceHolder] return[retStr[{}]]", retStr);
			return retStr;
		}
	}

	private static String extractFromSystem(String systemPropertyName) {
		try {
			return System.getProperty(systemPropertyName);
		} catch (Throwable t) {
			return null;
		}
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
//		System.loadLibrary("EncodeLib_JNI");
//		byte[] newby = new byte[] { 51, 43, 80, 48, 53, 52, 42, -77, 50, 43, 80, 53, -25, 49, 47, -77, -86, -87, 75, -77, -48, 50, -79,
//				-51, -109, -44, 54, 49, 76, -49, 44, -53, 85, 49, 75, -85, 72, -73, -77, 77, -55, -49, 72, -45, 53, -80, 80, -52, -53, -81,
//				75, 83, 82, 80, 84, -49, 84, -78, 73, -53, 43, -87, 77, -87, 77, 44, -48, 86, 81, -77, 49, 72, -51, -49, 40, -43, -12, 7, 0 };
//		byte[] newbb = new byte[newby.length * 2];
//		int j = HTTPEncodeUtil.Decode(newby, newby.length, newbb, 100000);
//		String str2 = new String(newbb);＂"”“""”“"”“”“""''"""‘’“
//		System.out.println(j + "--" + str2);
//<<<<<<< .mine
		String name = "☪➹☃☂❦❧✲❈✿ fyh αγμνξ с很难卢卡斯的肌肤愛上對方阿斯頓發垃圾課поонгбам ㄕㄔㄍㄎㄏ ǎěǐǐòǒ たせねはひ ソヌハフヘホ“〈》～々＂「〉   ⒋㈢㈣㈥㈦㈦㈧⒐ⅥⅦ  ÷∏∠∪⊙≌∽－ ┯┟┯┰万太亿毫吉百拍 ☆※→◎◆℃¤↑︿＠＆＃■№★♂♀";
		byte[] b1 = new byte[]{(byte) 0xf0, (byte) 0x9f,(byte) 0x8c, (byte)0x90};
		byte[] b2 = new byte[]{(byte) 0xf0, (byte) 0xAf,(byte) 0xAF, (byte)0x91};
		byte[] b3 = new byte[]{(byte) 0xf0, (byte) 0x9f,(byte) 0x8E, (byte)0x92};
		String t1 = new String(b1, "UTF-8");
		String t2 = new String(b2, "UTF-8");
		String t3 = new String(b3, "UTF-8");
		name += t1;
		name += t2;
		name += t3;
		String t = name.replaceAll("[^\\u0000-\\uFFFF]*" , "");
		System.out.println(name.equals(t));
		System.out.println(name);
		System.out.println(t);
//		String t1 = "～";
//		for(char ch : t1.toCharArray()) {
//			Character c = new Character(ch);
////			System.out.println("ch: " + c.to());
//			byte[] b = (""+ch).getBytes();
//			for(byte bit : b) {
//				System.out.printf("%x \n ", bit);
//			}
//		}
//		
//		byte[] b2 = new byte[]{(byte) 0xf0, (byte) 0x9f,(byte) 0x8c, (byte)0x99};
//		String t2 = new String(b2, "UTF-8");
////		String t2 = "☪";
//		System.out.println(t2);
		
		int codePoint = t2.codePointCount(0, t2.length());
		for(int i = 0; i < codePoint; ++i){
			int cp = t2.codePointAt(i);
			System.out.printf("Unicode = \\u %x  , v=%d\n", cp, cp);
		}
//		
//		
//		for(char ch : t2.toCharArray()) {
//			System.out.println(ch);
//			Character c = new Character(ch);
//			System.out.println("ch: " + c.toString());
//			byte[] b = (""+ch).getBytes();
//			for(byte bit : b) {
//				System.out.printf("%x \n ", bit);
//			}
//		}
		
//		String mid = "7c:58:d7:98".replaceAll(":", "");
//		System.out.println(mid);
//=======
////		String name = "asdlkaasd支持愛上對方阿阿斯頓。，１２３啊會計師０－１發的折哦slkdjf哈子1232_☪➹☃☂❦❧✲❈✿--12**(*(*^&*%&^^%#12313";
////		String t = name.replaceAll("[^0-9A-Za-z\\d\\u4E00-\\u9FA5]*" , "");
////		System.out.println(name.equals(t));
////		System.out.println(name);
//		System.out.println(getMinuteString(new Date()));
//>>>>>>> .r11456
	}
		
	

	public static boolean isNumber(String str) {
		if (str == null || str.length() == 0) {
			return false;
		} else {
			char[] chars = str.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				if (chars[i] < '0' || chars[i] > '9') {
					return false;
				}
			}
			return true;
		}
	}

	public static String getIp(HttpServletRequest request) {
		// We look if the request is forwarded
		// If it is not call the older function.
		String ip = request.getHeader("X-Pounded-For");

		if (ip != null) {
			return ip;
		}

		ip = request.getHeader("x-forwarded-for");

		if (ip == null) {
			return request.getRemoteAddr();
		} else {
			// Process the IP to keep the last IP (real ip of the computer on
			// the net)
			StringTokenizer tokenizer = new StringTokenizer(ip, ",");

			// Ignore all tokens, except the last one
			for (int i = 0; i < tokenizer.countTokens() - 1; i++) {
				tokenizer.nextElement();
			}

			ip = tokenizer.nextToken().trim();

			if (ip.equals("")) {
				ip = null;
			}
		}

		// If the ip is still null, we put 0.0.0.0 to avoid null values
		if (ip == null) {
			ip = "0.0.0.0";
		}

		return ip;
	}
}
