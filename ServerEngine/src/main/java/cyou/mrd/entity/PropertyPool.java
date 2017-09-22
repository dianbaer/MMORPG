package cyou.mrd.entity;

import java.io.BufferedReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cyou.mrd.util.NumberUtil;

/**
 * 属性池。
 */
public class PropertyPool implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	// 变量表
    protected ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<String, String>();
	
	/**
	 * 深度复制。
	 * @return
	 */
	@Override
	public PropertyPool clone() {
		PropertyPool ret = new PropertyPool();
		ret.properties.putAll(properties);
		return ret;
	}
	
	/**
	 * 从存储的字符串中恢复。
	 * 每行格式为：变量名=变量值
	 * @param data
	 */
	public void parse(String data) throws Exception {
		properties.clear();
		BufferedReader br = new BufferedReader(new StringReader(data));
		String line;
		while ((line = br.readLine()) != null) {
			int pos = line.indexOf('=');
			if (pos == -1) {
				continue;
			}
			String varName = line.substring(0, pos);
			String varValue = line.substring(pos + 1);
			properties.put(varName, varValue);
		}
	}
	
	/**
	 * 转换为存储格式。
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		Iterator<String> itor = properties.keySet().iterator();
		while (itor.hasNext()) {
		    if (buf.length() > 0) {
		        buf.append("\n");
		    }
		    String key = itor.next();
		    String value = properties.get(key);
		    buf.append(key);
		    buf.append("=");
		    buf.append(value);
		}
		return buf.toString();
	}
	
	public void remove(String varName){
		properties.remove(varName);
	}
	
	/**
	 * 判断两个列表是否完全相同。
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof PropertyPool)) {
			return false;
		}
		Map<String, String> tbl1 = properties;
		Map<String, String> tbl2 = ((PropertyPool)o).properties;
		if (tbl1.size() != tbl2.size()) {
		    return false;
		}
		Iterator<String> itor = tbl1.keySet().iterator();
		while (itor.hasNext()) {
		    String key = itor.next();
		    if (!tbl1.get(key).equals(tbl2.get(key))) {
		        return false;
		    }
		}
		return true;
	}
	
	public int changeValue(String varName,int v){
		int oldV = getInt(varName,0);
		oldV += v;
		setInt(varName,oldV);
		return oldV;
	}
	
	/**
	 * 取得一个整数变量的值，缺省值为0.
	 * @param varName
	 * @return
	 */
	public int getInt(String varName) {
	    try {
	        return NumberUtil.parseInt(properties.get(varName));
	    } catch (Exception e) {
	        return 0;
	    }
	}
	
	/**
	 * 取得一个整数变量的值，缺省值为defaultValue.
	 */
	public int getInt(String varName, int defaultValue) {
		try {
			return NumberUtil.parseInt(properties.get(varName));
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * 取得一个字符串变量的值，缺省值为空串。
	 * @param varName
	 * @return
	 */
	public String getString(String varName) {
	    String ret = properties.get(varName);
	    if (ret == null) {
	        ret = "";
	    }
	    return ret;
	}
	
	public long setLong(String varName, long value){
		properties.put(varName, String.valueOf(value));
		return value;
	}
	
	public long getLong(String varName,long defaultValue){
		try {
			return Long.parseLong(properties.get(varName));
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * 设置一个整数变量的值。如果该变量还不存在，则创建。
	 * @param varName
	 * @param value
	 */
	public int setInt(String varName, int value) {
	    properties.put(varName, String.valueOf(value));
	    return value;
	}

    /**
     * 设置一个字符串变量的值。如果该变量还不存在，则创建。
     * @param varName
     * @param value
     */
    public void setString(String varName, String value) {
        properties.put(varName, value);
    }
    
    /**
     * 根据提供的属性前缀，删除所有以此前缀打头的属性
     * @param namePrefix
     */
    public void deleteByPrefix(String namePrefix){
    	ConcurrentHashMap<String, String> new_properties = new ConcurrentHashMap<String, String>();
        
        Iterator<String> it = properties.keySet().iterator();
        
        while(it.hasNext()){
            String key = it.next();
            
            if(key.startsWith(namePrefix)){
                continue;
            }
            
            new_properties.put(key, properties.get(key));
        }
        
        properties = new_properties;
    }
}

