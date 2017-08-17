<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>客户端模拟器</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
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

<body>
	<div class="top">客户端模拟器 请输入协议内容</div>
	<br />
	<button id="login">login</button>
	<button id="onlineList">onlineList</button>
	<button id="gameprocess">gameprocess</button>
	<button id="friendList">friendList</button>
	<button id="friendHome">friendHome</button>
	<br /><br />
	<input id="SRC"/>
	<br />
	<br />
	<button id="send">send</button>
	<br />
	<br />
	<textarea id="DH" cols="75" rows="6">返回值</textarea>

	<script type="text/javascript">
	$("#login").click(function() {
		SRC.value = '{"opcode":1, "data":{"userName":"pipi", "password":111, "token":"", "loginType":0, "version":1}}';
	});
	$("#onlineList").click(function() {
		SRC.value = '{"opcode":3, "data":{"type":"0"}}';
	});
	$("#gameprocess").click(function() {
		SRC.value = '{"opcode":5, "data":{"buildList" : [{"id":"1", "level":1},{"id":"2", "level":1}]}}';
	});
	$("#friendList").click(function() {
		SRC.value = '{"opcode":7, "data":{}}'
	});
	$("#friendHome").click(function() {
		SRC.value = '{"opcode":9, "data":{"friendId":"1"}}';
	});

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
</html>
