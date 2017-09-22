package cyou.akworld;

import java.util.HashMap;

public class Data {
	/**
	 * 力量
	 */
	public static int STR = 1;
	/**
	 * 敏捷
	 */
	public static int AGI = 2;
	/**
	 * 智力
	 */
	public static int INT = 3;
	//类型=<提升层次，数值>
	private HashMap<Integer, HashMap<Integer, Integer>> map = new HashMap<Integer, HashMap<Integer,Integer>>();
}
