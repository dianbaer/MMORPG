package ak.util;

public interface IRandom {
	/**
	 * 获取百分比
	 * @return
	 */
	public int getProbability();
	/**
	 * 获得开放等级
	 * @return
	 */
	public int getOpenLvl();
	/**
	 * 获得类型
	 * @return
	 */
	public int getType();
}
