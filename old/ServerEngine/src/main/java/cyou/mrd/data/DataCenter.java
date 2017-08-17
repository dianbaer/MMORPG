package cyou.mrd.data;

import cyou.mrd.Platform;
import cyou.mrd.service.Service;
import cyou.mrd.util.ConfigKeys;

public interface DataCenter extends Service {
	
	public static final String KEY_GAME_BASE = "_" + Platform.getConfiguration().getString(ConfigKeys.GAME_CODE) + "_";

	/**
	 * value TObjectIntMap<session, playerId> session列表
	 */
	public static final String SESSION_LIST = "_key_session_list";

	/**
	 * 获取远程数据
	 * 
	 * @return
	 */
	public Data getData(String key);

	/**
	 * 新增数据
	 * 实现时应该尽量保证保存成功，如果保存失败返回false；
	 * @param data
	 * @return 网络存储方式会出现存储不成功
	 * @throws DataException 
	 */
	public boolean sendNewData(String key, Object data);
	
	/**
	 * 合并数据时，会出现当前数据版本低于远程版本，
	 * 调用方需要手动做合并操作， 一般操作为： 取得新版本数据， 将需要修改的数据重新赋值，再次调用updateData方法
	 * @param key
	 * @param data
	 * @return
	 * @throws DataException
	 */
	public boolean sendData(String key, Data data);

}
