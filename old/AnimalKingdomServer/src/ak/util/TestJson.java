/**
 * TestJson.java
 * ak.util
 *
 *   version  date      	author
 * ──────────────────────────────────
 *    1.0	 2013年12月2日 		shiwei2006
 *
 * Copyright (c) 2013, www.cyou-inc.com All Rights Reserved.
*/

package ak.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ClassName:TestJson
 * ClassDescription:  The Class Description
 *
 * @author   shiwei2006
 * @Date	 2013年12月2日		下午9:45:28
 * @version  1.0
 */
public class TestJson {

	public static void main(String[] args) {

		String jsonStr = "{'opcode':1060, 'data':{'client':[1,2,3,4,5]}}";
		JSONObject json = JSONObject.fromObject(jsonStr);
		JSONObject data = json.getJSONObject("data");
		if(data.containsKey("client")){
			JSONArray client = data.getJSONArray("client");
			for(int i = 0; i < client.size(); i++){
				System.out.println(client.getInt(i));
			}
			System.out.println(client);
		}
	}

}

