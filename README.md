# threecss-mmorpg

[![Build Status](https://travis-ci.org/dianbaer/threecss-mmorpg.svg?branch=master)](https://travis-ci.org/dianbaer/threecss-mmorpg)

threecss-mmorpg是基于ThreeCSS分布式框架开发的一款通用型的MMORPG项目

体验地址：https://mmorpg.threecss.com

目前old文件夹含有mmorpg所有战斗、场景、技能、ai、广播等核心代码。

打版本

	服务器：

		ant
	
	客户端：
	
		使用flash builder发布网页版或手机Android版

	
发布项目：
	
一、服务器：

	1、将dist/asset考入服务器，例如：
	
		/home/asset

	2、将dist/allWorld世界服务器考入服务器，例如：
	
		/home/allWord
		
		修改/home/allWord/custom.properties配置文件
		
		执行./start.sh
		
	3、将dist/world场景服务器考入服务器，例如：
		
		/home/world
		
		修改/home/world/custom.properties配置文件
		
		执行./start.sh
		
	4、将dist/cross跨域策略分发服务器考入服务器，例如：
	
		/home/cross
		
		执行./start.sh
	
二、客户端

	1、将dist/asset与dist/webgame考入web容器（httpd、nginx等），例如
	
		/var/www/html/webgame
		
		/var/www/html/asset
		
		修改asset/config.js参数
		
			"ip":"172.27.108.76",--------------场景服务器ip
			"port":7005,-----------------------场景服务器端口
	
