package com.cyou.mrd;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import cyou.mrd.Platform;
import cyou.mrd.account.Account;
import cyou.mrd.service.PlayerService;

import ak.gm.GMCode;
import ak.mail.IAkMailService;
import ak.mail.MailEx;
import ak.notice.AkNoticeService;
import ak.player.PlayerEx;
import ak.player.PlayerServiceEx;

public  class  GMNetLibJni {
	private native int gmn_init(short ctrlid);  //初始化
	private native int gmn_connect(byte[] host, int hostlen,int port, int reqid); //连接
	private native int gmn_getReqID(); //得到请求ID
	private native int gmn_pushBuff(int reqid,byte[] buff,int len); //发送请求
	private native int gmn_sendGmMsg(int reqid,int packetid, int exflag, int timeout); //请求，并得到请求的长度
	private native byte[] gmn_getresult(int reqid);  //得到请求的内容
	private native byte[] gmn_popBuff(int reqid,int len);  //pop出固定长度的内容
	private native int gmn_release(int reqid);  //释放请求的ID
	private native int gmn_close(int reqid);  //释放请求的ID 
	private native int gmn_recvGmMsg(); // 取一条消息
	private native int gmn_getPacketID(); // 取消息ID
	private native int gmn_getExFlag(); // 取扩展标识
	
	private static final int PACKET_GM_REQUEST = 2;
	private static final int PACKET_GM_RESPONSE = 3;
	private static final int PACKET_GM_HEARTBEAT = 4;
	private static final int PACKET_GM_REGISTER = 6;
	
	private boolean recv_ping = true;
	private boolean is_register = false;
	private long appkey;
	private String gid;
	private int cid;
	private String ip;
	private int port;
	
	private long op_appkey;
	private String op_gid;
	private int op_cid;
	private int op_code;
	
	public GMNetLibJni(String ip, int port, long appkey, String gid, int cid)
	{
		this.appkey = appkey;
		this.gid = gid;
		this.cid = cid;
		this.ip = ip;
		this.port = port;
		this.op_appkey = 0l;
		this.op_gid = "";
		this.op_cid = 0;
		this.op_code = 0;
	}
	
	static {
		System.loadLibrary("GSNetLib");
	}
	
	public int init(short ctrlid)
	{
		return gmn_init(ctrlid);
	}
	
	public int connect(String host, int port, int reqid)
	{
		return gmn_connect(host.getBytes(), host.length(), port, reqid);
	}
	
	public int getReqID()
	{
		return gmn_getReqID();
	}
	
	public int getExFlag()
	{
		return gmn_getExFlag();
	}
	public int pushBuff(int reqid,byte[] buff,int len)
	{
		return gmn_pushBuff(reqid,buff,len);
	}
	
	public int sendGmMsg(int reqid, int packetid, int exflag, int timeout)
	{
		return gmn_sendGmMsg(reqid, packetid, exflag, timeout);
	}
	
	public int recvGmMsg()
	{
		int ret = gmn_recvGmMsg();
		if (ret < 0)
		{
			return -1;
		}
		if (ret == 1)
		{
			this.op_appkey = popLong(0);
			this.op_cid = popInt(0);
			this.op_gid = popFixedString(0);
			this.op_code = popInt(0);
		}
		return ret;
	}
	
	public ByteBuffer getresult(int reqid, int bufflen)
	{
		byte[] _ret = gmn_getresult(reqid);
		ByteBuffer _bb = ByteBuffer.allocate(bufflen);
		_bb.put(_ret);
		return _bb;
	}
	
	public int release(int reqid)
	{
		return gmn_release(reqid);
	}
	
	public int close(int reqid)
	{
		return gmn_close(reqid);
	}
	
	//--------------------------------------------------------------------------------
	//PUSH扩展接口: 一个short
	public int pushShort(int reqid,short _data)
	{
		byte[] bytes = ByteConvert.shortToBytes(_data);
		gmn_pushBuff(reqid, bytes, 4);
		return _data;
	}
	
	//PUSH扩展接口: 一个整数
	public int pushInt(int reqid,int _data)
	{
		byte[] bytes = ByteConvert.intToBytes(_data);
		gmn_pushBuff(reqid, bytes, 4);
		return _data;
	}
	
	//PUSH pid
	public long pushProjectID(int reqid,long _data)
	{
		byte[] bytes = ByteConvert.longToBytes(_data);
		gmn_pushBuff(reqid, bytes, 8);
		return _data;
	}
	
	//PUSH扩展接口： 一个字符串
	public int pushString(int reqid,String _data)
	{
		byte[] _context = _data.getBytes();
		int _len = _context.length;
		pushInt(reqid,_len);
		gmn_pushBuff(reqid, _context, _len);
		return _data.length();
	}
	
	//push gameid
	public int pushGameID(int reqid,String _data)
	{
		int len = _data.length();
		for (int i = len; i < 8; i++)
		{
			_data = _data + "\0";
		}
		byte[] _context = _data.getBytes();
		gmn_pushBuff(reqid, _context, 8);
		return len;
	}
	//--------------------------------------------------------------------------------
	//POP扩展接口： 一个short
	public short popShort(int reqid)
	{
		byte[] bytes = gmn_popBuff(reqid, 2);
		return ByteConvert.bytesToShort(bytes);
	}
	
	//POP扩展接口: 一个整数
	public int popInt(int reqid)
	{
		byte[] bytes = gmn_popBuff(reqid, 4);
		return ByteConvert.bytesToInt(bytes);
	}
	
	//POP扩展接口: 一个整数
	public long popLong(int reqid)
	{
		byte[] bytes = gmn_popBuff(reqid, 8);
		return ByteConvert.bytesToLong(bytes);
	}
	
	//POP扩展接口： 一个字符串
	public String popString(int reqid)
	{
		int _len = popInt(reqid);
		byte[] bytes = gmn_popBuff(reqid, _len);
		String _ret = "";
		try {
			_ret = new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			_ret = new String(bytes);
		}
		return _ret;
	}
	//POP扩展接口： 一个字符串
	public String popFixedString(int reqid)
	{
		byte[] bytes = gmn_popBuff(reqid, 8);
		String _ret = new String(bytes);
		_ret = _ret.replaceAll("\\W", "\0");
		int len = _ret.indexOf("\0");
		if (len > 0)
		{
			_ret = _ret.substring(0, len);
		}
		return _ret;
	}
	
	// 初始化GM工具
	public int initGmTool()
	{
		int _ret = init((short) 12);
		if (_ret < 0)
		{
			System.out.println("init GMNetLibJni error!");
			return -1;
		}
		_ret = connect(this.ip, this.port, 0);
		if (_ret < 0)
		{
			System.out.println("connetct:" + this.ip + ":" + this.port +"  error!");
			return -1;
		}
		return 0;
	}
	
	// 获取消息ID
	public int getPacketID()
	{
		return gmn_getPacketID();
	}
	
	// 发送消息
	public int sendPacket(int packetid, int exflag, String content)
	{
		System.out.println("返回GMtool"+content);
		pushProjectID(0, this.appkey);
		pushInt(0, this.cid);
		pushGameID(0, this.gid);
		pushInt(0, this.op_code);
		pushString(0, content);
		sendGmMsg(0, packetid, exflag,0);
		release(0);
		return 0;
	}
	
	// 主tick
	public int tick()
	{
		// 发送注册信息
		sendPacket(6, 0,"register");
		int recv_ret;
		int packetid;
		int exflag;
		long last_ping_time = System.currentTimeMillis() / 1000;
		//接收消息
		while(true)
		{
			long current_time =  System.currentTimeMillis() / 1000;
			if (current_time - last_ping_time >= 60)// 1分钟ping一次
			{
				last_ping_time = current_time;
				if (this.recv_ping)
				{
					sendPacket(4, 0,"ping");
					this.recv_ping = false;
				}
				else
				{
					release(0);
					close(0);
					if (this.connect(this.ip, this.port, 0) < 0)
					{
						System.out.println("connetct:" + this.ip + ":" + this.port +"  error!");
						return -1;
					}
					this.is_register = false;
				}
				if (!this.is_register)
				{
					sendPacket(6,0, "register");
					this.recv_ping = true;
				}
			}
			
			recv_ret = recvGmMsg();
			if (recv_ret == -1)
			{
				// 接收过程出错了
				continue;
			}
			else if (recv_ret == 0)
			{
				// 继续接收
				continue;
			}
			else if (recv_ret == 1)
			{
				packetid = getPacketID();
				exflag = getExFlag();
				switch(packetid)
				{
					case PACKET_GM_HEARTBEAT:
					{
						// 等待下一次发送ping包
						this.recv_ping = true;
						release(0);
						break;
					}
					case PACKET_GM_REGISTER:
					{
						String request = popString(0);
						//System.out.println("recv register!" +  "content:" + request);
						this.is_register=  true;
						release(0);
						break;
					}
					case PACKET_GM_RESPONSE:
					{
						String request = popString(0);
						// 调用处理函数,阻塞
						this.process_msg(this.op_appkey,this.op_cid,this.op_gid,this.op_code,exflag,request);
						release(0);
						break;
					}
					default:
					{
						break;
					}
				}
			}
		}
	}
	
	
	/* 处理客户端发来的消息
	 * appkey: billing获得的appkey，是个唯一值
	 * cid: 渠道ID
	 * gid： 分区ID
	 * opcode：协议
	 * content：内容
	 * exflag：不用关心，sendPacket的时候带上就可以
	 * sendPacket的时候把content改为要给gmtool回复的内容即可
	 */
	public void process_msg(long appkey, int cid, String gid, int opcode, int exflag,String content)
	{
		System.out.println("GMTool发送来:"+content);
		JSONObject receiveJson;
		JSONObject sendJson = new JSONObject();
		try {
			receiveJson = JSONObject.fromObject(content);
		} catch (Throwable e) {
			sendJson.put("error", true);
			sendJson.put("success", false);
			this.sendPacket(3,exflag,sendJson.toString());
			return;
		}
		JSONArray roleList;
		IAkMailService<MailEx> mailService;
		switch (opcode) {
		//发送公告
		case GMCode.SEND_NOTICE:
			if(!receiveJson.containsKey("content") || receiveJson.getString("content") == "" || receiveJson.getString("content") == null){
				sendJson.put("error", true);
				sendJson.put("success", false);
				this.sendPacket(3,exflag,sendJson.toString());
				return;
			}
			try {
				AkNoticeService service = Platform.getAppContext().get(AkNoticeService.class);
				service.sendSystemNotice(receiveJson.getString("content"));
				sendJson.put("error", false);
				sendJson.put("success", true);
				this.sendPacket(3,exflag,sendJson.toString());
			} catch (Throwable e) {
				sendJson.put("error", false);
				sendJson.put("success", false);
				this.sendPacket(3,exflag,sendJson.toString());
			}
			
			break;
		//目前所有人是发给所有在线用户
		case GMCode.SEND_MAIL:
			if(!receiveJson.containsKey("content") ||
					!receiveJson.containsKey("title") ||
					!receiveJson.containsKey("all") ||
					!receiveJson.containsKey("roleList")){
				sendJson.put("error", true);
				sendJson.put("success", false);
				this.sendPacket(3,exflag,sendJson.toString());
				return;
			}
			content = receiveJson.getString("content");
			String title = receiveJson.getString("title");
			boolean all = receiveJson.getBoolean("all");
			roleList = receiveJson.getJSONArray("roleList");
			mailService = Platform.getAppContext().get(IAkMailService.class);
			if(all){
				try {
					mailService.SendSystemNoticeToAll(content, 0,0);
					sendJson.put("error", false);
					sendJson.put("success", true);
					this.sendPacket(3,exflag,sendJson.toString());
				} catch (Throwable e) {
					sendJson.put("error", false);
					sendJson.put("success", false);
					this.sendPacket(3,exflag,sendJson.toString());
				}
				
			}else{
				Iterator<JSONObject> roleListIterator = roleList.iterator();
				JSONArray faultArray = new JSONArray();
				while (roleListIterator.hasNext()) {
					JSONObject role = roleListIterator.next();
					if(!role.containsKey("roleName")){
						sendJson.put("error", true);
						sendJson.put("success", false);
						this.sendPacket(3,exflag,sendJson.toString());
						return;
					}
					String roleName = role.getString("roleName");
					int roleId = Integer.parseInt(roleName);
					try {
						mailService.sendSystemMailHaveAward(roleId, 0, content,0);
					} catch (Throwable e) {
						
						JSONObject fault = new JSONObject();
						fault.put("roleName", roleName);
						faultArray.add(fault);
					}
					
				}
				sendJson.put("error", false);
				sendJson.put("roleList", faultArray);
				this.sendPacket(3,exflag,sendJson.toString());
			}
			break;
		
		case GMCode.SEND_ITEM:
			if(!receiveJson.containsKey("itemList") ||
					!receiveJson.containsKey("type") ||
					!receiveJson.containsKey("roleList")){
				sendJson.put("error", true);
				sendJson.put("success", false);
				this.sendPacket(3,exflag,sendJson.toString());
				return;
			}
			if(receiveJson.containsKey("content")){
				content = receiveJson.getString("content");
			}else{
				content = "系统发送的道具";
			}
			
			JSONArray itemList = receiveJson.getJSONArray("itemList");
			int type = receiveJson.getInt("type");
			roleList = receiveJson.getJSONArray("roleList");
			String itemListStr = "";
			for(int i = 0;i<itemList.size();i++){
				JSONObject item = (JSONObject)itemList.get(i);
				if(i == itemList.size()-1){
					itemListStr += item.getInt("itemId")+"="+item.getInt("itemCount");
				}else{
					itemListStr += item.getInt("itemId")+"="+item.getInt("itemCount")+",";
				}
				
			}
			JSONObject jsObj = new JSONObject();
			jsObj.put("award", itemListStr);
			mailService = Platform.getAppContext().get(IAkMailService.class);
			Iterator<JSONObject> roleListIterator = roleList.iterator();
			JSONArray faultArray = new JSONArray();
			while (roleListIterator.hasNext()) {
				JSONObject role = roleListIterator.next();
				if(!role.containsKey("roleName")){
					sendJson.put("error", true);
					sendJson.put("success", false);
					this.sendPacket(3,exflag,sendJson.toString());
					return;
				}
				String roleName = role.getString("roleName");
				int roleId = Integer.parseInt(roleName);
				try {
					mailService.sendSystemMailHaveGoods(roleId, jsObj.toString(), content);
				} catch (Throwable e) {
					for(int i = 0;i<itemList.size();i++){
						JSONObject item = (JSONObject)itemList.get(i);
						JSONObject fault = new JSONObject();
						fault.put("roleName", roleName);
						fault.put("itemId", item.getInt("itemId"));
						fault.put("itemCount", item.getInt("itemCount"));
						faultArray.add(fault);
					}
					
				}
				
			}
			sendJson.put("error", false);
			sendJson.put("roleList", faultArray);
			this.sendPacket(3,exflag,sendJson.toString());
			
			break;
		case GMCode.SEARCH_PLAYER:
			if(!receiveJson.containsKey("accountId")){
				sendJson.put("error", true);
				sendJson.put("success", false);
				this.sendPacket(3,exflag,sendJson.toString());
				return;
			}
			int accountId = receiveJson.getInt("accountId");
			
			Account account = Platform.getEntityManager().find(Account.class, accountId);
			if(account == null){
				sendJson.put("error", false);
				sendJson.put("roleList", new JSONArray());
				this.sendPacket(3,exflag,sendJson.toString());
				return;
			}
			PlayerServiceEx playerService = (PlayerServiceEx) Platform.getAppContext().get(PlayerService.class);
			PlayerEx player = playerService.getPlayerByCachaAndDb(account);
			if(player == null){
				sendJson.put("error", false);
				sendJson.put("roleList", new JSONArray());
				this.sendPacket(3,exflag,sendJson.toString());
				return;
			}
			JSONObject playerJson = new JSONObject();
			playerJson.put("roleId", player.getId());
			playerJson.put("roleName", player.getName());
			playerJson.put("serverId", player.getLoginServerId());
			playerJson.put("serverName", player.getLoginServerId());
			playerJson.put("level", player.getLevel());
			playerJson.put("money", player.getAccount().getRemainDollar());
			playerJson.put("gold", player.getMoney());
			playerJson.put("lastLoginTime", player.getLastLoginTime());
			JSONArray playerList = new JSONArray();
			playerList.add(playerJson);
			sendJson.put("error", false);
			sendJson.put("roleList", playerList);
			this.sendPacket(3,exflag,sendJson.toString());
			break;
		default:
			sendJson.put("error", true);
			sendJson.put("success", false);
			this.sendPacket(3,exflag,sendJson.toString());
			break;
		}
		
	}
	public byte[] gbk2utf8(String chenese) {     char c[] = chenese.toCharArray();     byte[] fullByte = new byte[3 * c.length];     for (int i = 0; i < c.length; i++) {         int m = (int) c[i];         String word = Integer.toBinaryString(m);          StringBuffer sb = new StringBuffer();         int len = 16 - word.length();         for (int j = 0; j < len; j++) {             sb.append("0");         }         sb.append(word);         sb.insert(0, "1110");         sb.insert(8, "10");         sb.insert(16, "10");          String s1 = sb.substring(0, 8);         String s2 = sb.substring(8, 16);         String s3 = sb.substring(16);          byte b0 = Integer.valueOf(s1, 2).byteValue();         byte b1 = Integer.valueOf(s2, 2).byteValue();         byte b2 = Integer.valueOf(s3, 2).byteValue();         byte[] bf = new byte[3];         bf[0] = b0;         fullByte[i * 3] = bf[0];         bf[1] = b1;         fullByte[i * 3 + 1] = bf[1];         bf[2] = b2;         fullByte[i * 3 + 2] = bf[2];      }     return fullByte; }

	public static void main(String[] args)
	{	
		GMNetLibJni _jni = new GMNetLibJni("219.232.242.229", 8090,Long.parseLong("1389243086150"),"1",2001);
		if (_jni.initGmTool() < 0)
		{
			System.out.println("init error,may be cann't connect syscenter");
			return;
		}
		while (true)
		{
			int ret = _jni.tick();
			System.out.println("tick ret:" + ret);
		}
	
	}

}

