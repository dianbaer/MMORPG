<%@ page session="false" language="java" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>客户端模拟器</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<script type="text/javascript" src="jquery-1.6.4.js"></script>

<style type="text/css">
body {
	text-align: center;
	width: 960px;
	height: 960px;
}

.top {
	border: 2px sloid #000000;
	font-weight: bold;
	font-size: 30;
	color: #000880;
	width: 960px;
	height: 40px;
	background-color: #888888;
	text-align: center;
	padding-top: 5px;
}

#SRC {
	width: 800px;
	height: 100px;
	background-color: #FFFFFF;
	color: #000000;
	border-color: #000000;
	broder-size: 3px;
}

#DH {
	width: 800px;
	height: 200px;
	background-color: #FFFFFF;
	color: #000000;
	border-color: #000000;
	broder-size: 3px;
	autoline: true;
}

#reg {
	width: 200px;
	height: 40px;
}
</style>
</head>


<script type="text/javascript">
	/* object to string */
	function obj2str(o) {
		var r = [], i, j = 0, len;
		if (o == null) {
			return o;
		}
		if (typeof o == 'string') {
			return '"' + o + '"';
		}
		if (typeof o == 'object') {
			if (!o.sort) {
				r[j++] = '{';
				for (i in o) {
					r[j++] = '"';
					r[j++] = i;
					r[j++] = '":';
					r[j++] = obj2str(o[i]);
					r[j++] = ',';
				}
				//可能的空对象
				//r[r[j-1] == '{' ? j:j-1]='}';
				r[j - 1] = '}';
			} else {
				r[j++] = '[';
				for (i = 0, len = o.length; i < len; ++i) {
					r[j++] = obj2str(o[i]);
					r[j++] = ',';
				}
				//可能的空数组
				r[len == 0 ? j : j - 1] = ']';
			}
			return r.join('');
		}
		return o.toString();
	}

	jQuery(function($) {
		$("#send").click(function() {
			DH.value = "";
			var send = SRC.value;
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "s",
				data : send,
				dataType : 'json',
				success : function(result) {
					DH.value = obj2str(result);
				}
			});
		});
	});
</script>

<body>
	<div class="top">客户端模拟器 请输入协议内容</div>
	<br />
	<button id="login" title="登录, 如果是新用户则自动注册">login</button>
	<button id="logout" title="登出">logout</button>
	<button id="upload" title="上传进度">上传进度</button>
	<button id="download" title="恢复进度">恢复进度</button>
	<br />
	<br />
	<button id="friendSearch" title="搜索好友">friendSearch</button>
<!-- 	<button id="recommendFriend" title="推荐好友">推荐好友</button> -->
	<button id="randomFriend" title="随机好友">随机好友</button>
<!-- 	<button id="specialFriend" title="特色好友">特色好友</button> -->
	<button id="friendAdd" title="新增好友">friendAdd</button>
	<button id="friendDel" title="删除好友">friendDel</button>
	<button id="friendList" title="好友列表">friendList</button>
	<button id="friendHome" title="访问好友家, 下发建筑列表">friendHome</button>
	<br /><br />
	<button id="clearFriendHome" title="打扫好友家">clearFriendHome</button>
<!-- 	<button id="waFriendHome" title="好友家挖宝藏">waaa!</button> -->
	<button id="transFriendHome" title="好友家小动物观光团">运送小动物</button>
	<br />
	<br>
	<button id="mailSend">发送邮件</button>
	<button id="mailList">邮件列表</button>
	<button id="mailDel">删除邮件</button>
	<br />
	
	<br />
<!-- 	<button id="nickname" title="修改昵称">nickname</button> -->
	<button id="ICON" title="设置头像和昵称">Icon&Nickname</button>
	<button id="snsbingding" title="SNS绑定">snsbingding</button>
	<button id="snsLoadFriend">导入sns好友</button>
	<br />
	<br />
	<button id="SetHousePassword">设置搬家密码</button>
	<button id="MoveHouse">搬家</button>
	<br /><br />
	<button id="Dollar" title="同步美元">sync Dollar</button>
	<button id="log" title="上传日志">上传日志</button>
	<br /><br /> 
<!-- 	<button id="shopList">商店列表</button> -->
	<button id="charge">付费验证</button>
	<button id="imoneylist">商品列表</button>
	<br>
	<select id = "language">
		<option value="1" selected>请选择语言(默认为中文)</option>
		<option value="1">中文</option>
		<option value="2">英文</option>
		<option value="3">俄语</option>
		<option value="4">韩语</option>
		<option value="5">法语</option>
		<option value="6">西班牙</option>
		<option value="7">希腊</option>
		<option value="8">外语8</option>
		<option value="9">外语9</option>
		<option value="10">外语10</option>
		<option value="11">外语11</option>
		<option value="12">外语12</option>
		<option value="13">外语13</option>
	</select>
	<br /><br />
	<textarea id="SRC"></textarea>
	<br />
	<br />
	<button id="send">send</button>
	<br />
	
	<br />
	<textarea id="DH" cols="75" rows="6">返回值</textarea>
</body>
	<script type="text/javascript">
		function getTime() {
			return new Date().getTime()/1000;
		}
		var bValueDic = {};

		function getSrcValue(meta) {
			var ret = bValueDic[meta][bValueDic[meta + "_count"]
					% bValueDic[meta + "_len"]];
			bValueDic[meta + "_count"]++;
			return ret;
		}
		
		function setBValueDic(metaName, values) {
			bValueDic[metaName] = values;
			bValueDic[metaName + "_count"] = 0;
			bValueDic[metaName + "_len"] = values.length;
			$("#"+metaName).click(function() {
				SRC.value = getSrcValue(metaName);
				DH.value = "";
			});
		}
		//注意这里的参数数组, 最后一位不要带","; ie中会认为多一个元素
		setBValueDic("login",
				[
				 //loginType: 0是首次登录. 1是选择回档. 2是选择重新玩
		//		'{"opcode":1, "data":{"id":123, "mid":"123456", "loginType":"mid","version":1,"player":{"Exp":0,"Level":0,"Money":0,"RabitNumber":0,"Cats":[{"count":1,"id":28}],"BattleTimes":0,"BattleWinTimes":0,"Woods":0},"Buildings":[]}}'
		//		,'{"opcode":1, "data":{"id":123,"mid":"123456", "loginType":"renren", "token":"xxxxxxxxxxxxxxxxxxxxxx","snsId":100086,"player":{"Exp":0,"Level":0,"Money":0,"RabitNumber":0,"Cats":[{"count":1,"id":28}],"BattleTimes":0,"BattleWinTimes":0,"Woods":0},"Buildings":[]}}'
		//		,'{"opcode":1, "data":{"id":0,"mid":"2222", "loginType":"qq", "token":"xxxxxxxxxxxxxxxxxxxxxx","snsId":10000,"player":{"Exp":0,"Level":0,"Money":0,"RabitNumber":0,"Cats":[{"count":1,"id":28}],"BattleTimes":0,"BattleWinTimes":0,"Woods":0},"Buildings":[]}}'
		//		'{"opcode":1, "data":{"mid":"10000", "area":"其它","country":"中国","device":"iPhone3","deviceSystem":"iOS 4.x","downloadType":"AppStore","networkType":"WIFI","prisonBreak","是","operator":"中国移动"}}'
		//		,'{"opcode":1, "data":{"id":83,"mid":"10000", "player":{"id":83,"Dollar":200,"Exp":0,"Level":1,"Money":50000,"Energy":100, "RabitNumber":6,"Cats":[{"count":0,"id":12}],"BattleTimes":0,"BattleWinTimes":0,"Woods":100,"OpenBlock":0, "Items":"7=10,8=20"},"Buildings":[{"x":7,"y":9,"ID":48,"State":0,"StateTime":0,"CutTimes":0,"GrowTimes":0},{"x":12,"y":37,"ID":58,"State":0,"StateTime":0,"CutTimes":0,"GrowTimes":0},{"x":20,"y":33,"ID":83,"State":0,"StateTime":0,"CutTimes":0,"GrowTimes":0},{"x":20,"y":32,"ID":83,"State":0,"StateTime":0,"CutTimes":0,"GrowTimes":0}],"ScreenPosX":0,"ScreenPosY":0}}}'
				'{"opcode":1, "data":{"mid":"30000","loginType":0, "area":"其它","country":"中国","device":"iPhone3","deviceSystem":"iOS 4.x","downloadType":"AppStore","networkType":"WIFI","prisonBreak":"1","operator":"中国移动"}}'
				,'{"opcode":1, "data":{"mid":"30000","loginType":1, "area":"其它","country":"中国","device":"iPhone3","deviceSystem":"iOS 4.x","downloadType":"AppStore","networkType":"WIFI","prisonBreak":"1","operator":"中国移动"}}'
				]
		);
		setBValueDic("logout",
				[
				'{"opcode":3, "data":{}}'
				]
		);
		setBValueDic("nickname",
				[
				'{"opcode":5, "data":{name:"ddd"}}',
				'{"opcode":5, "data":{name:"hhh"}}',
				'{"opcode":5, "data":{name:"汉字"}}'
				]
		);
		setBValueDic("onlineList",
				[
				'{"opcode":41, "data":{"type":"0"}}'
				]
		);
		setBValueDic("friendList",
				[
				'{"opcode":51, "data":{}}'
				]
		); 
		setBValueDic("friendSearch",
				[
				'{"opcode":59, "data":{"name":"pipi"}}'
				,'{"opcode":59, "data":{"name":"24045"}}'
				]
		); 
		
		setBValueDic("recommendFriend",
				[
				 '{"opcode":1018, "data":{"level":10, start:1}}'
				]
		); 
		setBValueDic("randomFriend",
				[
				 '{"opcode":1026, "data":{"level":10}}'
				]
		); 
		setBValueDic("specialFriend",
				[
				 '{"opcode":1022, "data":{}}' 
				]
		); 
		setBValueDic("friendAdd",
				[
				 '{"opcode":53, "data":{"id":"3694317"}}'
				 ,'{"opcode":53, "data":{"id":"30"}}'
				]
		); 
		setBValueDic("friendDel",
				[
				'{"opcode":79,"data":{"mailId":13}}'
				]
		); 
		setBValueDic("friendHome",
				[
				 '{"opcode":57, "data":{"friendId":"2"}}'
				, '{"opcode":57, "data":{"friendId":"30"}}'
				]
		);  
		setBValueDic("mailSend",
				[
				'{"opcode":71,"data":{"destId":1,"useType":1, "content":"1111abcdef"}}',
				'{"opcode":71,"data":{"destId":2,"useType":2,"content":"22222给id=2的玩家发送邮件"}}',
				'{"opcode":71,"data":{"destId":3,"useType":0,"content":"3333中文"}}',
				]
		);  
		setBValueDic("mailList",
				[
				'{"opcode":73,"data":{"type":0,"page":0}}',
				'{"opcode":73,"data":{"type":1,"page":0}}'
				]
		); 
		setBValueDic("mailDel",
				[
				'{"opcode":75,"data":{"mailIds":[{"mailId":30},{"mailId":31}]}}'
				]
		); 
		setBValueDic("clearFriendHome",
				[
				'{"opcode":1002,"data":{"friendId":3694317}}'
				]
		); 
		setBValueDic("waFriendHome",
				[
				'{"opcode":1004,"data":{"friendId":2}}'
				]
		);
		setBValueDic("transFriendHome",
				[
				'{"opcode":1008,"data":{"friendId":3694317}}'
				]
		);
		setBValueDic("Dollar",
				[
				'{"opcode":1010,"data":{"Dollar":10, quickBuilding:12, "items":[{"id"=1}, {"id"=2}]}}'
				]
		);
		setBValueDic("ICON",
				[
				'{"opcode":1012,"data":{"icon":"3", "nickname":"ranran"}}',
				'{"opcode":1012,"data":{"icon":"4", "nickname":"keke"}}',
				'{"opcode":1012,"data":{"icon":"5", "nickname":"genggeng"}}'
				]
		);
		setBValueDic("snsbingding",
				[
				'{"opcode":91,"data":{"type":"weibo","id":"123","token":"fdsaevveionvljfieofd"}}'
				]
		);
		setBValueDic("snsLoadFriend",
				[
				'{"opcode":93,"data":{"snsType":"weibo"}}'
				]
		);
		setBValueDic("SetHousePassword",
				[
				'{"opcode":1014,"data":{"pwd":"abcd1234"}}'
				]
		);
		setBValueDic("MoveHouse",
				[
				'{"opcode":1016,"data":{"id":123, "pwd":"abcd1234"}}'
				]
		);
		setBValueDic("upload",
				[
				'{"data":{"Buildings":[{"ID":36,"State":16,"StateTime":64,"flip":0,"x":49,"y":66},{"ID":21,"State":5,"StateTime":-2147141888,"effect":50,"flip":0,"x":67,"y":49},{"ID":56,"State":15,"flip":0,"x":65,"y":51},{"ID":56,"State":15,"flip":0,"x":66,"y":51},{"ID":56,"State":15,"flip":0,"x":67,"y":51},{"ID":56,"State":15,"flip":1,"x":69,"y":49},{"ID":56,"State":15,"flip":1,"x":69,"y":50},{"ID":134,"State":15,"flip":0,"x":63,"y":57},{"ID":143,"State":16,"StateTime":9929,"flip":0,"x":68,"y":54},{"ID":130,"State":16,"StateTime":97226,"flip":1,"x":72,"y":74},{"ID":100,"State":4,"flip":1,"x":50,"y":82},{"ID":106,"State":15,"StateTime":97682,"flip":0,"havestTimes":3,"isHarvest":1,"x":74,"y":50},{"ID":106,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":77,"y":49},{"ID":106,"State":15,"StateTime":97682,"flip":0,"havestTimes":3,"isHarvest":1,"x":78,"y":58},{"ID":106,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":78,"y":53},{"ID":106,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":50,"y":71},{"ID":106,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":50,"y":74},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":56,"y":80},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":56,"y":86},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":63,"y":90},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":75,"y":81},{"ID":106,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":3,"isHarvest":1,"x":76,"y":62},{"ID":106,"State":15,"StateTime":96552,"flip":0,"havestTimes":3,"isHarvest":1,"x":72,"y":62},{"ID":106,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":70,"y":63},{"ID":106,"State":15,"StateTime":96456,"flip":0,"havestTimes":4,"isHarvest":1,"x":76,"y":68},{"ID":106,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":62,"y":75},{"ID":106,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":56,"y":75},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":33,"y":57},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":33,"y":54},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":39,"y":60},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":34,"y":62},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":41,"y":54},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":46,"y":61},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":46,"y":55},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":43,"y":47},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":39,"y":41},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":39,"y":37},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":36,"y":44},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":42,"y":41},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":40,"y":34},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":35,"y":53},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":41,"y":60},{"ID":77,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":59,"y":69},{"ID":77,"State":15,"StateTime":96456,"flip":0,"havestTimes":4,"isHarvest":1,"x":67,"y":70},{"ID":77,"State":15,"StateTime":96456,"flip":0,"havestTimes":4,"isHarvest":1,"x":64,"y":72},{"ID":77,"State":15,"StateTime":457,"flip":0,"havestTimes":3,"isHarvest":1,"x":70,"y":77},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":53,"y":90},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":49,"y":86},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":57,"y":91},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":61,"y":92},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":52,"y":87},{"ID":147,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":49,"y":79},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":51,"y":80},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":50,"y":91},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":53,"y":92},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":50,"y":94},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":63,"y":95},{"ID":84,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":55,"y":51},{"ID":87,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":68,"y":63},{"ID":89,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":10,"x":55,"y":76},{"ID":89,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":10,"x":59,"y":84},{"ID":89,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":57,"y":49},{"ID":89,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":10,"x":41,"y":56},{"ID":111,"State":4,"flip":0,"x":85,"y":32},{"ID":108,"State":4,"flip":0,"x":85,"y":38},{"ID":55,"State":4,"flip":0,"x":88,"y":38},{"ID":55,"State":4,"flip":1,"x":83,"y":38},{"ID":92,"State":4,"flip":0,"x":91,"y":36},{"ID":92,"State":4,"flip":0,"x":91,"y":33},{"ID":114,"State":15,"flip":0,"x":84,"y":101},{"ID":114,"State":15,"flip":0,"x":84,"y":102},{"ID":114,"State":15,"flip":0,"x":87,"y":109},{"ID":114,"State":15,"flip":0,"x":84,"y":107},{"ID":114,"State":15,"flip":0,"x":84,"y":109},{"ID":114,"State":15,"flip":0,"x":84,"y":104},{"ID":114,"State":15,"flip":0,"x":86,"y":109},{"ID":114,"State":15,"flip":0,"x":85,"y":109},{"ID":114,"State":15,"flip":0,"x":84,"y":103},{"ID":114,"State":15,"flip":0,"x":84,"y":105},{"ID":114,"State":15,"flip":0,"x":84,"y":106},{"ID":114,"State":15,"flip":0,"x":84,"y":108},{"ID":114,"State":15,"flip":0,"x":88,"y":109},{"ID":114,"State":15,"flip":0,"x":89,"y":109},{"ID":114,"State":15,"flip":0,"x":90,"y":109},{"ID":114,"State":15,"flip":0,"x":91,"y":109},{"ID":114,"State":15,"flip":0,"x":92,"y":109},{"ID":114,"State":15,"flip":0,"x":93,"y":109},{"ID":114,"State":15,"flip":0,"x":93,"y":108},{"ID":114,"State":15,"flip":0,"x":93,"y":107},{"ID":114,"State":15,"flip":0,"x":93,"y":106},{"ID":114,"State":15,"flip":0,"x":93,"y":105},{"ID":114,"State":15,"flip":0,"x":87,"y":97},{"ID":114,"State":15,"flip":0,"x":93,"y":104},{"ID":114,"State":15,"flip":0,"x":93,"y":101},{"ID":114,"State":15,"flip":0,"x":88,"y":97},{"ID":114,"State":15,"flip":0,"x":93,"y":100},{"ID":114,"State":15,"flip":0,"x":93,"y":99},{"ID":114,"State":15,"flip":0,"x":84,"y":100},{"ID":114,"State":15,"flip":0,"x":84,"y":99},{"ID":114,"State":15,"flip":0,"x":84,"y":98},{"ID":114,"State":15,"flip":0,"x":84,"y":97},{"ID":114,"State":15,"flip":0,"x":85,"y":97},{"ID":114,"State":15,"flip":0,"x":86,"y":97},{"ID":114,"State":15,"flip":0,"x":89,"y":97},{"ID":114,"State":15,"flip":0,"x":90,"y":97},{"ID":114,"State":15,"flip":0,"x":91,"y":97},{"ID":114,"State":15,"flip":0,"x":92,"y":97},{"ID":114,"State":15,"flip":0,"x":93,"y":97},{"ID":114,"State":15,"flip":0,"x":93,"y":98},{"ID":140,"State":4,"flip":1,"x":87,"y":101},{"ID":70,"State":4,"flip":0,"x":68,"y":40},{"ID":15,"State":4,"flip":0,"x":70,"y":34},{"ID":49,"State":4,"flip":0,"x":88,"y":59},{"ID":24,"State":4,"flip":0,"x":85,"y":106},{"ID":25,"State":4,"flip":0,"x":85,"y":104},{"ID":26,"State":4,"flip":0,"x":87,"y":106},{"ID":23,"State":4,"flip":0,"x":90,"y":98},{"ID":23,"State":4,"flip":0,"x":90,"y":106},{"ID":115,"State":4,"flip":1,"x":93,"y":102},{"ID":63,"State":4,"flip":0,"x":85,"y":85},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":78,"y":34},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":82,"y":34},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":91,"y":44},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":86,"y":47},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":93,"y":56},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":91,"y":49},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":82,"y":60},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":83,"y":55},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":86,"y":51},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":90,"y":66},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":86,"y":68},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":92,"y":79},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":83,"y":75},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":82,"y":71},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":82,"y":78},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":94,"y":86},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":82,"y":89},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":94,"y":69},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":89,"y":69},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":86,"y":75},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":74,"y":38},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":75,"y":34},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":86,"y":43},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":94,"y":59},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":94,"y":52},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":85,"y":56},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":84,"y":60},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":83,"y":69},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":75,"y":109},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":69,"y":111},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":66,"y":109},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":90,"y":42},{"ID":10,"State":4,"flip":0,"x":86,"y":71},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":88,"y":79},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":85,"y":80},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":81,"y":85},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":50,"y":18},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":55,"y":19},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":61,"y":22},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":55,"y":23},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":50,"y":23},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":61,"y":29},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":62,"y":26},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":48,"y":31},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":53,"y":31},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":54,"y":27},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":61,"y":32},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":60,"y":35},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":51,"y":34},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":62,"y":43},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":53,"y":44},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":48,"y":25},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":51,"y":22},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":60,"y":19},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":57,"y":22},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":62,"y":30},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":57,"y":30},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":61,"y":28},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":62,"y":21},{"ID":1,"State":15,"StateTime":-2147141888,"flip":0,"x":59,"y":49},{"ID":2,"State":15,"StateTime":-2147141888,"flip":0,"x":62,"y":49},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":67,"y":34},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":64,"y":36},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":65,"y":40},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":66,"y":37},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":32,"y":66},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":37,"y":66},{"ID":139,"State":4,"flip":0,"x":71,"y":102},{"ID":6,"State":4,"StateTime":64,"flip":0,"x":58,"y":102},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":53,"y":103},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":52,"y":99},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":59,"y":98},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":51,"y":106},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":59,"y":110},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":61,"y":110},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":118,"y":98},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":126,"y":100},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":123,"y":105},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":119,"y":107},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":117,"y":109},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":114,"y":96},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":121,"y":99},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":115,"y":97},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":121,"y":106},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":125,"y":105},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":126,"y":102},{"ID":84,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":116,"y":99},{"ID":84,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":117,"y":103},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":118,"y":101},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":115,"y":103},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":113,"y":97},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":119,"y":109},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":116,"y":111},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":120,"y":101},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":122,"y":101},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":116,"y":101},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":111,"y":116},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":114,"y":114},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":113,"y":109},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":100,"y":106},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":107,"y":101},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":107,"y":97},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":106,"y":107},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":103,"y":109},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":100,"y":97},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":109,"y":101},{"ID":147,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":99,"y":110},{"ID":35,"State":4,"flip":0,"x":102,"y":101},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":38,"y":88},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":38,"y":84},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":43,"y":87},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":42,"y":91},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":36,"y":89},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":33,"y":82},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":37,"y":93},{"ID":89,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":42,"y":83},{"ID":89,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":41,"y":84},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":17,"y":65},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":25,"y":70},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":25,"y":67},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":22,"y":71},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":20,"y":73},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":24,"y":82},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":19,"y":78},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":26,"y":88},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":32,"y":92},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":19,"y":81},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":17,"y":86},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":25,"y":91},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":31,"y":94},{"ID":92,"State":4,"flip":0,"x":19,"y":91},{"ID":92,"State":4,"flip":0,"x":19,"y":82},{"ID":92,"State":4,"flip":0,"x":21,"y":83},{"ID":92,"State":4,"flip":0,"x":28,"y":88},{"ID":100,"State":4,"flip":1,"x":23,"y":84},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":67,"y":117},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":75,"y":122},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":78,"y":118},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":71,"y":122},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":66,"y":123},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":72,"y":125},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":66,"y":113},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":77,"y":126},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":69,"y":115},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":66,"y":119},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":78,"y":123},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":68,"y":125},{"ID":84,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":73,"y":118},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":80,"y":132},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":87,"y":135},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":87,"y":131},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":80,"y":128},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":77,"y":133},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":74,"y":128},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":70,"y":128},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":81,"y":135},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":91,"y":134},{"ID":89,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":83,"y":129},{"ID":89,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":75,"y":127},{"ID":89,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":67,"y":122},{"ID":89,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":73,"y":132},{"ID":89,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":85,"y":136},{"ID":89,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":83,"y":133},{"ID":89,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":89,"y":134},{"ID":89,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":81,"y":129},{"ID":89,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":80,"y":125},{"ID":92,"State":4,"flip":0,"x":83,"y":120},{"ID":92,"State":4,"flip":0,"x":90,"y":123},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":108,"y":88},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":106,"y":92},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":101,"y":84},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":99,"y":90},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":107,"y":90},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":101,"y":90},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":0,"x":104,"y":89},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":103,"y":77},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":99,"y":66},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":98,"y":75},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":100,"y":72},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":106,"y":50},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":102,"y":53},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":102,"y":60},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":99,"y":56},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":97,"y":50},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":109,"y":53},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":101,"y":48},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":104,"y":51},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":27,"y":51},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":24,"y":58},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":19,"y":57},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":27,"y":55},{"ID":106,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":21,"y":61},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":34,"y":50},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":88,"y":17},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":81,"y":26},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":89,"y":23},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":92,"y":21},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":95,"y":28},{"ID":44,"State":4,"flip":0,"x":71,"y":83},{"ID":8,"State":15,"StateTime":-2147141888,"TrainCardId":0,"TrainTime":-10.0,"flip":1,"x":49,"y":52},{"ID":43,"State":4,"StateTime":64,"flip":1,"x":67,"y":102},{"ID":93,"State":4,"flip":0,"x":66,"y":105},{"ID":92,"State":4,"flip":0,"x":67,"y":100},{"ID":84,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":74,"y":90},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":73,"y":100},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":68,"y":95},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":78,"y":94},{"ID":61,"State":4,"flip":1,"x":35,"y":74},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":33,"y":72},{"ID":77,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":4,"isHarvest":1,"x":38,"y":64},{"ID":45,"State":4,"flip":0,"x":53,"y":38},{"ID":86,"State":4,"StateTime":-2147141888,"flip":1,"havestTimes":100,"x":77,"y":108},{"ID":86,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":54,"y":102},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":58,"y":85},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":76,"y":101},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":33,"y":75},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":42,"y":65},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":34,"y":47},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":43,"y":39},{"ID":87,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":74,"y":42},{"ID":84,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":63,"y":85},{"ID":84,"State":15,"StateTime":-2147141888,"flip":1,"havestTimes":100,"x":62,"y":79},{"ID":84,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":79,"y":100},{"ID":84,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":91,"y":90},{"ID":84,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":90,"y":62},{"ID":85,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":79,"y":42},{"ID":85,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":46,"y":37},{"ID":85,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":37,"y":43},{"ID":85,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":40,"y":51},{"ID":85,"State":15,"StateTime":973,"flip":0,"havestTimes":3,"x":71,"y":68},{"ID":88,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":40,"y":77},{"ID":88,"State":4,"StateTime":-2147141888,"flip":1,"havestTimes":100,"x":40,"y":69},{"ID":88,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":46,"y":57},{"ID":88,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":95,"x":66,"y":53},{"ID":88,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":58,"y":45},{"ID":88,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":83,"y":63},{"ID":88,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":92,"y":75},{"ID":88,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":88,"y":93},{"ID":86,"State":15,"StateTime":-2147141888,"flip":0,"havestTimes":80,"x":53,"y":62},{"ID":88,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":88,"y":81},{"ID":88,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":94,"y":97},{"ID":88,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":59,"y":104},{"ID":88,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":50,"y":93},{"ID":88,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":34,"y":55},{"ID":88,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":54,"y":34},{"ID":88,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":71,"y":35},{"ID":88,"State":4,"StateTime":-2147141888,"flip":0,"havestTimes":100,"x":93,"y":39},{"ID":134,"State":15,"flip":0,"x":58,"y":56},{"ID":2,"State":15,"StateTime":-2147141888,"flip":0,"x":59,"y":52},{"ID":1,"State":15,"StateTime":-2147141888,"flip":0,"x":49,"y":61},{"ID":1,"State":15,"StateTime":-2147141888,"flip":0,"x":49,"y":49},{"ID":134,"State":15,"flip":0,"x":57,"y":56},{"ID":83,"State":15,"flip":0,"x":52,"y":50},{"ID":83,"State":15,"flip":0,"x":52,"y":49},{"ID":108,"State":15,"flip":0,"x":64,"y":54},{"ID":62,"State":16,"StateTime":97222,"effect":6,"flip":0,"x":59,"y":57},{"ID":18,"State":10,"StateTime":1334049280,"effect":50,"flip":0,"x":65,"y":49},{"ID":148,"State":15,"flip":0,"x":61,"y":52},{"ID":148,"State":15,"flip":0,"x":68,"y":48},{"ID":148,"State":15,"flip":0,"x":69,"y":48},{"ID":148,"State":15,"flip":0,"x":70,"y":48},{"ID":148,"State":15,"flip":0,"x":70,"y":49},{"ID":148,"State":15,"flip":0,"x":70,"y":50},{"ID":148,"State":15,"flip":0,"x":70,"y":51},{"ID":29,"State":15,"flip":0,"x":58,"y":54},{"ID":29,"State":15,"flip":0,"x":58,"y":55},{"ID":29,"State":15,"flip":0,"x":58,"y":56},{"ID":29,"State":15,"flip":0,"x":58,"y":57},{"ID":29,"State":15,"flip":0,"x":58,"y":58},{"ID":29,"State":15,"flip":0,"x":58,"y":59},{"ID":29,"State":15,"flip":0,"x":58,"y":60},{"ID":29,"State":15,"flip":0,"x":59,"y":56},{"ID":29,"State":15,"flip":0,"x":60,"y":56},{"ID":29,"State":15,"flip":0,"x":61,"y":56},{"ID":29,"State":15,"flip":0,"x":62,"y":56},{"ID":29,"State":15,"flip":0,"x":63,"y":56},{"ID":29,"State":15,"flip":0,"x":63,"y":55},{"ID":29,"State":15,"flip":0,"x":63,"y":54},{"ID":29,"State":15,"flip":0,"x":64,"y":54},{"ID":29,"State":15,"flip":0,"x":65,"y":54},{"ID":29,"State":15,"flip":0,"x":66,"y":54},{"ID":29,"State":15,"flip":0,"x":67,"y":54},{"ID":29,"State":15,"flip":0,"x":68,"y":54},{"ID":29,"State":15,"flip":0,"x":68,"y":53},{"ID":29,"State":15,"flip":0,"x":68,"y":52},{"ID":29,"State":15,"flip":0,"x":68,"y":51},{"ID":29,"State":15,"flip":0,"x":68,"y":50},{"ID":148,"State":15,"flip":0,"x":89,"y":98},{"ID":148,"State":15,"flip":0,"x":89,"y":107},{"ID":148,"State":15,"flip":0,"x":89,"y":106},{"ID":148,"State":15,"flip":0,"x":89,"y":105},{"ID":148,"State":15,"flip":0,"x":90,"y":105},{"ID":148,"State":15,"flip":0,"x":87,"y":98},{"ID":148,"State":15,"flip":0,"x":88,"y":105},{"ID":148,"State":15,"flip":0,"x":87,"y":105},{"ID":148,"State":15,"flip":0,"x":87,"y":104},{"ID":148,"State":15,"flip":0,"x":87,"y":103},{"ID":148,"State":15,"flip":0,"x":86,"y":103},{"ID":148,"State":15,"flip":0,"x":85,"y":103},{"ID":148,"State":15,"flip":0,"x":85,"y":102},{"ID":148,"State":15,"flip":0,"x":88,"y":98},{"ID":31,"State":15,"flip":0,"x":38,"y":74},{"ID":31,"State":15,"flip":0,"x":41,"y":66},{"ID":31,"State":15,"flip":0,"x":42,"y":71},{"ID":31,"State":15,"flip":0,"x":38,"y":72},{"ID":31,"State":15,"flip":0,"x":39,"y":72},{"ID":31,"State":15,"flip":0,"x":40,"y":72},{"ID":31,"State":15,"flip":0,"x":41,"y":72},{"ID":31,"State":15,"flip":0,"x":42,"y":72},{"ID":31,"State":15,"flip":0,"x":42,"y":73},{"ID":31,"State":15,"flip":0,"x":41,"y":73},{"ID":31,"State":15,"flip":0,"x":40,"y":73},{"ID":31,"State":15,"flip":0,"x":40,"y":74},{"ID":31,"State":15,"flip":0,"x":40,"y":75},{"ID":31,"State":15,"flip":0,"x":40,"y":76},{"ID":31,"State":15,"flip":0,"x":43,"y":73},{"ID":31,"State":15,"flip":0,"x":44,"y":73},{"ID":31,"State":15,"flip":0,"x":42,"y":70},{"ID":31,"State":15,"flip":0,"x":42,"y":69},{"ID":29,"State":15,"flip":0,"x":63,"y":53},{"ID":29,"State":15,"flip":0,"x":62,"y":54},{"ID":29,"State":15,"flip":0,"x":63,"y":52},{"ID":135,"x":0,"y":32},{"ID":135,"x":0,"y":48},{"ID":135,"x":0,"y":64},{"ID":135,"x":0,"y":80},{"ID":135,"x":0,"y":96},{"ID":135,"x":0,"y":112},{"ID":135,"x":0,"y":128},{"ID":135,"x":0,"y":144},{"ID":135,"x":16,"y":0},{"ID":135,"x":16,"y":96},{"ID":135,"x":16,"y":112},{"ID":135,"x":16,"y":128},{"ID":135,"x":16,"y":144},{"ID":135,"x":32,"y":0},{"ID":135,"x":32,"y":16},{"ID":135,"x":32,"y":96},{"ID":135,"x":32,"y":112},{"ID":135,"x":32,"y":128},{"ID":135,"x":32,"y":144},{"ID":135,"x":48,"y":0},{"ID":135,"x":48,"y":112},{"ID":135,"x":48,"y":128},{"ID":135,"x":48,"y":144},{"ID":135,"x":64,"y":0},{"ID":135,"x":64,"y":144},{"ID":135,"x":80,"y":0},{"ID":135,"x":80,"y":144},{"ID":135,"x":96,"y":0},{"ID":135,"x":96,"y":16},{"ID":135,"x":96,"y":32},{"ID":135,"x":96,"y":128},{"ID":135,"x":96,"y":144},{"ID":135,"x":112,"y":0},{"ID":135,"x":112,"y":16},{"ID":135,"x":112,"y":32},{"ID":135,"x":112,"y":48},{"ID":135,"x":112,"y":64},{"ID":135,"x":112,"y":80},{"ID":135,"x":112,"y":128},{"ID":135,"x":112,"y":144},{"ID":135,"x":128,"y":0},{"ID":135,"x":128,"y":16},{"ID":135,"x":128,"y":32},{"ID":135,"x":128,"y":48},{"ID":135,"x":128,"y":64},{"ID":135,"x":128,"y":80},{"ID":135,"x":128,"y":96},{"ID":135,"x":128,"y":112},{"ID":135,"x":128,"y":128},{"ID":135,"x":128,"y":144},{"ID":135,"x":144,"y":0},{"ID":135,"x":144,"y":16},{"ID":135,"x":144,"y":32},{"ID":135,"x":144,"y":48},{"ID":135,"x":144,"y":64},{"ID":135,"x":144,"y":80},{"ID":135,"x":144,"y":96},{"ID":135,"x":144,"y":112},{"ID":135,"x":144,"y":128},{"ID":135,"x":144,"y":144},{"ID":135,"x":16,"y":16},{"ID":135,"x":0,"y":16},{"ID":135,"x":112,"y":112},{"ID":135,"x":96,"y":112},{"ID":135,"x":112,"y":96},{"ID":135,"x":16,"y":80},{"ID":135,"x":16,"y":64},{"ID":135,"x":64,"y":128},{"ID":135,"x":80,"y":128},{"ID":135,"x":80,"y":112},{"ID":135,"x":96,"y":64},{"ID":135,"x":96,"y":48},{"ID":135,"x":16,"y":48},{"ID":135,"x":16,"y":32},{"ID":135,"x":48,"y":16},{"ID":135,"x":64,"y":16},{"ID":135,"x":80,"y":16},{"ID":135,"x":32,"y":80},{"ID":135,"x":64,"y":112},{"ID":135,"x":96,"y":96},{"ID":135,"x":96,"y":80},{"ID":135,"x":48,"y":96},{"ID":135,"x":64,"y":96},{"ID":135,"x":80,"y":96},{"ID":135,"x":80,"y":80},{"ID":135,"x":64,"y":80},{"ID":135,"x":32,"y":64},{"ID":135,"x":80,"y":64},{"ID":135,"x":80,"y":48},{"ID":135,"x":80,"y":32},{"ID":135,"x":64,"y":32},{"ID":135,"x":48,"y":80},{"ID":135,"x":32,"y":48},{"ID":135,"x":32,"y":32},{"ID":135,"x":48,"y":32},{"ID":135,"x":48,"y":64}],"LocalTime":'+getTime()+',"Mission":[{"ID":2,"State":99},{"ID":3,"State":99},{"ID":4,"State":99},{"ID":5,"State":99},{"ID":8,"State":99},{"ID":9,"State":99},{"Condition":0,"ID":10,"State":2},{"Condition":0,"ID":11,"State":1},{"Condition":0,"ID":12,"State":1},{"Condition":0,"ID":13,"State":1},{"Condition":0,"ID":14,"State":1},{"Condition":0,"ID":15,"State":1},{"Condition":0,"ID":16,"State":1},{"Condition":0,"ID":17,"State":1},{"Condition":0,"ID":18,"State":1},{"Condition":0,"ID":19,"State":1},{"Condition":0,"ID":20,"State":1},{"Condition":0,"ID":21,"State":1},{"Condition":0,"ID":22,"State":1},{"Condition":0,"ID":23,"State":1},{"Condition":0,"ID":24,"State":1},{"Condition":0,"ID":25,"State":1},{"Condition":0,"ID":26,"State":1},{"Condition":0,"ID":27,"State":1},{"Condition":0,"ID":28,"State":1}],"player":{"BattleTimes":0,"BattleWinTimes":0,"Cats":[{"count":0,"id":9},{"count":0,"id":12},{"count":0,"id":21},{"count":1,"id":27},{"count":0,"id":28}],"Dollar":31,"Energy":25,"Exp":13,"Food":40,"Items":"8=33,18=10,19=14,21=5,38=6,49=4,62=9,79=1,83=25,","Level":20,"Money":715889,"Nickname":"123123","OpenBlock":2,"Rabit0Number":5,"Rabit1Number":3,"Woods":720,"icon":1,"id":3084029,"lang":1,"star":1,"verify":{"BuyDollar":0,"CompensateDollar":0,"InitDollar":20,"RemainDollar":1,"RewardDollar":0,"UseDollar":19}},"save_time":'+getTime()+'},"opcode":1020}'
				]
		);
		setBValueDic("download",
				[
				'{"opcode":1024,"data":{}}'
				]
		);
		setBValueDic("log",
				[
				'{"opcode":101,"data":{"goods":[{"time":1234234, "name":"aaa","usage":"xxx","amount":48}, {"time":1234234, "name":"bbb","usage":"yyy","amount":55}], "game":[{"time":1234234, "name":"aaa"},{"time":1234234, "name":"xxx"}]}}'
				]
		);
		setBValueDic("charge",
				[
				'{"opcode":95,"data":{"bid":"com.cyou.iapfortest","pid":"com.cyou.product.test1","receipt":"ewoJInNpZ25hdHVyZSIgPSAiQXBaU0kxaVZHMUNCdTJnc0JFTmo3Z25PbkszM1BqVjJMckZqalQ2dmlHVlN4MVEwUXZZc1loNUV1UnFob1JhVGJyZGd1R2FWUTRkSis1UEN5TkplQXpZSURrY0I5SlBUb3JZZ2VRVi9WU2Z4MWE2TXhiOWlKVm9aQTFNLy8wS3hlL2c5TC9NNWg4K2d5VmZIZmUya2xURlp5Rnh2VC8vSTJwM2EyVXV5UHUzREFBQURWekNDQTFNd2dnSTdvQU1DQVFJQ0NHVVVrVTNaV0FTMU1BMEdDU3FHU0liM0RRRUJCUVVBTUg4eEN6QUpCZ05WQkFZVEFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pTQkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNaU0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVURXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2FWUjFibVZ6SUZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1FYVjBhRzl5YVhSNU1CNFhEVEE1TURZeE5USXlNRFUxTmxvWERURTBNRFl4TkRJeU1EVTFObG93WkRFak1DRUdBMVVFQXd3YVVIVnlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV05oZEdVeEd6QVpCZ05WQkFzTUVrRndjR3hsSUdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3YkdVZ1NXNWpMakVMTUFrR0ExVUVCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNclJqRjJjdDRJclNkaVRDaGFJMGc4cHd2L2NtSHM4cC9Sd1YvcnQvOTFYS1ZoTmw0WElCaW1LalFRTmZnSHNEczZ5anUrK0RyS0pFN3VLc3BoTWRkS1lmRkU1ckdYc0FkQkVqQndSSXhleFRldngzSExFRkdBdDFtb0t4NTA5ZGh4dGlJZERnSnYyWWFWczQ5QjB1SnZOZHk2U01xTk5MSHNETHpEUzlvWkhBZ01CQUFHamNqQndNQXdHQTFVZEV3RUIvd1FDTUFBd0h3WURWUjBqQkJnd0ZvQVVOaDNvNHAyQzBnRVl0VEpyRHRkREM1RllRem93RGdZRFZSMFBBUUgvQkFRREFnZUFNQjBHQTFVZERnUVdCQlNwZzRQeUdVakZQaEpYQ0JUTXphTittVjhrOVRBUUJnb3Foa2lHOTJOa0JnVUJCQUlGQURBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQUVhU2JQanRtTjRDL0lCM1FFcEszMlJ4YWNDRFhkVlhBZVZSZVM1RmFaeGMrdDg4cFFQOTNCaUF4dmRXLzNlVFNNR1k1RmJlQVlMM2V0cVA1Z204d3JGb2pYMGlreVZSU3RRKy9BUTBLRWp0cUIwN2tMczlRVWU4Y3pSOFVHZmRNMUV1bVYvVWd2RGQ0TndOWXhMUU1nNFdUUWZna1FRVnk4R1had1ZIZ2JFL1VDNlk3MDUzcEdYQms1MU5QTTN3b3hoZDNnU1JMdlhqK2xvSHNTdGNURXFlOXBCRHBtRzUrc2s0dHcrR0szR01lRU41LytlMVFUOW5wL0tsMW5qK2FCdzdDMHhzeTBiRm5hQWQxY1NTNnhkb3J5L0NVdk02Z3RLc21uT09kcVRlc2JwMGJzOHNuNldxczBDOWRnY3hSSHVPTVoydG04bnBMVW03YXJnT1N6UT09IjsKCSJwdXJjaGFzZS1pbmZvIiA9ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNREV5TFRBeUxUSTFJREF4T2pVek9qQXhJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkltOXlhV2RwYm1Gc0xYUnlZVzV6WVdOMGFXOXVMV2xrSWlBOUlDSXhNREF3TURBd01ESTVNek0zT1RBNUlqc0tDU0ppZG5KeklpQTlJQ0l4TGpBaU93b0pJblJ5WVc1ellXTjBhVzl1TFdsa0lpQTlJQ0l4TURBd01EQXdNREk1TXpNM09UQTVJanNLQ1NKeGRXRnVkR2wwZVNJZ1BTQWlNU0k3Q2draWIzSnBaMmx1WVd3dGNIVnlZMmhoYzJVdFpHRjBaUzF0Y3lJZ1BTQWlNVE16TURFMk16VTRNVGN5TnlJN0Nna2ljSEp2WkhWamRDMXBaQ0lnUFNBaVkyOXRMbU41YjNVdWNISnZaSFZqZEM1MFpYTjBNU0k3Q2draWFYUmxiUzFwWkNJZ1BTQWlOVEEwTWpZNU9ERXlJanNLQ1NKaWFXUWlJRDBnSW1OdmJTNWplVzkxTG5SbGMzUXVhVzVoY0hCa1pXMXZJanNLQ1NKd2RYSmphR0Z6WlMxa1lYUmxMVzF6SWlBOUlDSXhNek13TVRZek5UZ3hOekkzSWpzS0NTSndkWEpqYUdGelpTMWtZWFJsSWlBOUlDSXlNREV5TFRBeUxUSTFJREE1T2pVek9qQXhJRVYwWXk5SFRWUWlPd29KSW5CMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNREV5TFRBeUxUSTFJREF4T2pVek9qQXhJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkltOXlhV2RwYm1Gc0xYQjFjbU5vWVhObExXUmhkR1VpSUQwZ0lqSXdNVEl0TURJdE1qVWdNRGs2TlRNNk1ERWdSWFJqTDBkTlZDSTdDbjA9IjsKCSJlbnZpcm9ubWVudCIgPSAiU2FuZGJveCI7CgkicG9kIiA9ICIxMDAiOwoJInNpZ25pbmctc3RhdHVzIiA9ICIwIjsKfQ=="}}'
				]
		);
		setBValueDic("shopList",
				[
				'{"opcode":1007,"data":{"type":2}}'
				]
		);
		setBValueDic("imoneylist",
				[
				'{"opcode":97,"data":{ "bid":"com.cyou.mrd.animalkingdom"}}'
				]
		);
	</script>
	<script type="text/javascript">
		$("#language").change(function() {
			SRC.value = '{"opcode":107,"data":{"language":'+$("select option:selected").val()+'}}';
		});
	</script>
</html>
