# MMORPG

[![Build Status](https://travis-ci.org/dianbaer/MMORPG.svg?branch=master)](https://travis-ci.org/dianbaer/MMORPG)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c06f3b523cdd4d78af544e73c270349c)](https://www.codacy.com/app/232365732/MMORPG?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dianbaer/MMORPG&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)


# MMORPG是一个可以用来学习ARPG核心战斗的项目

### 答疑、问题反馈QQ群：537982451

>体验地址：https://mmorpg.threecss.com

## 简介（大家通过学习，可以理解mmorpg的核心思路）

>1、含有mmorpg所有战斗、场景、技能、ai、广播等核心代码。

>2、服务器有很高的稳定性、安全性、效率、承载量，借鉴了天龙服务器的技术架构。魔兽世界的体验和交互设计思路，服务器拥有完整的客户端逻辑，对网络延迟处理的非常优秀，在外网服务器运行半年多的时间。

>3、客户端有很高的渲染效率，经过2次深度优化，在网页上可以跑满60帧，手机上跑40-60帧。

>4、客户端与服务器的交互品质达到一个业界很高的水准，在100毫秒以内，每秒20个心跳。战斗与交互体验非常优秀，可以达到国内较高的水准。

>5、服务器与客户端交互，技能连技能，技能取消技能，都使用了一种可以叫做“弱同步”的机制，这是为了交互设计体验而设计的一种机制。

## 核心功能

>技能

	伤害技能
	buff技能
	闪现
	大跳
	治疗技能
	冲锋
	召唤图腾
	被技能影响(击飞效果)
	无敌斩
	
>Buff

	对速度的影响(add、del)
	眩晕
	持续类型伤害
	不能移动
	冰箱
	变羊
	持续类型治疗
	减伤,加伤技能
	法术反射

>其他

	一个线程多个场景
	区域广播，每个轮训一次性计算，后续不在增加重复计算，极大的提高效率
	优秀的线程安全设计，切换场景先进入离开队列，主线程先拿在放入进入场景队列。
	45°等角视图，3d坐标系映射至2d坐标系。
	

## 打版本

	服务器：

		ant
	
	客户端：
	
		使用flash builder发布网页版或手机Android版
		
## 推荐环境：

>快捷部署 https://github.com/dianbaer/deployment-server

	jdk-8u121

	apache-tomcat-8.5.12

	CentOS-7-1611

	
## 发布项目：
	
>一、服务器：

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
	
>二、客户端

	1、将dist/asset与dist/webgame考入web容器（httpd、nginx等），例如
	
		/home/tomcat/webgame
		
		/home/tomcat/webgame/asset
		
		修改asset/config.js参数
		
			"ip":"172.27.108.76",--------------场景服务器ip
			"port":7005,-----------------------场景服务器端口
	
