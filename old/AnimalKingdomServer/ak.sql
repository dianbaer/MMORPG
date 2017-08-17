-- MySQL dump 10.13  Distrib 5.5.19, for Win32 (x86)
--
-- Host: localhost    Database: animal_kingdom
-- ------------------------------------------------------
-- Server version	5.5.19

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account` ( -- 账户表,记录平台及充值相关数据
  `id` int(11) NOT NULL AUTO_INCREMENT, -- 账户ID，主键自增
  `mid` varchar(64) DEFAULT '', -- 平台ID
  `imoney` int(11) NOT NULL DEFAULT '0', -- 充值金额
  `rewardDollar` int(11) NOT NULL DEFAULT '0', -- 游戏内获得
  `createTime` datetime DEFAULT NULL, -- 创建账号时间
  `lastLoginTime` datetime DEFAULT NULL, -- 最后一次登录时间
  `usedDollar` int(11) NOT NULL DEFAULT '0', -- 已使用
  `compensateDollar` int(11) NOT NULL DEFAULT '0', -- 补偿获得
  `initDollar` int(11) NOT NULL DEFAULT '0', -- 初始获得
  PRIMARY KEY (`id`),
  KEY `mid` (`mid`),
  unique(`mid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_sns`
--
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_sns` ( -- 账户关联SNS信息
  `Id` int(11) NOT NULL DEFAULT '0', -- 同账户ID，主键
  `cyouId` varchar(64) DEFAULT '',
  `faceBookId` varchar(64) DEFAULT '',
  `kaixinId` varchar(64) DEFAULT '',
  `qqId` varchar(64) DEFAULT NULL,
  `weiboId` varchar(64) DEFAULT NULL,
  `renrenId` varchar(64) DEFAULT NULL,
  `twitterId` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `cyouId` (`cyouId`(20)),
  KEY `faceBookId` (`faceBookId`(20)),
  KEY `weiboId` (`weiboId`(20)),
  KEY `kaixinId` (`kaixinId`(20)),
  KEY `qqId` (`qqId`(20)),
  KEY `renrenId` (`renrenId`(20)),
  KEY `twitterId` (`twitterId`(20))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_sns`
--

LOCK TABLES `account_sns` WRITE;
/*!40000 ALTER TABLE `account_sns` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_sns` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `charge`
--
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `charge` ( -- 账单支付(gameserver服使用)
  `id` int(11) NOT NULL AUTO_INCREMENT, -- 账单ID，主键，自增
  `serialnumber` varchar(255) DEFAULT NULL, -- 账单号，由系统自动生成(gameserver生成)
  `accountid` varchar(64) DEFAULT NULL, -- 对应账户ID，与account账户ID对应
  `playerid` varchar(64) DEFAULT NULL, -- 对应玩家ID，与player玩家ID对应
  `bundleid` varchar(64) DEFAULT NULL, -- 游戏应用ID，由系统提供标识游戏使用
  `result` int(11) DEFAULT NULL, -- 最终计算的花费金额
  `productid` varchar(64) DEFAULT NULL, -- 商品ID
  `errorcode` int(11) DEFAULT NULL, -- 错误码(用于定位具体原因，gameserver服处理过程中)
  `serverreceiveclient` int(11) DEFAULT NULL, -- 用于记录账单当前状态，下同 gameserver服务器收到客户端付费请求
  `serversendworld` int(11) DEFAULT NULL, -- gameserver服务器请求world服务器
  `worldreceiveserver` int(11) DEFAULT NULL,-- 此字段未使用，相应的状态已分拆到具体的处理服对应的表中
  `worldsendbilling` int(11) DEFAULT NULL, -- 此字段未使用，同上
  `worldreceivebilling` int(11) DEFAULT NULL, -- 此字段未使用，同上
  `worldsendserver` int(11) DEFAULT NULL, -- 此字段未使用，同上
  `serverreceiveworld` int(11) DEFAULT NULL, -- gameserver服务器接收到world服的返回
  `finishtime` timestamp NULL DEFAULT NULL,  -- 最终完成时间
  `requesttime` timestamp NULL DEFAULT NULL, -- 客户端请求时间
  `receipt` text, -- Base64编码的单据信息，用于最终向苹果平台发送
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `charge`
--

LOCK TABLES `charge` WRITE;
/*!40000 ALTER TABLE `charge` DISABLE KEYS */;
/*!40000 ALTER TABLE `charge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chargeworld`
--
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chargeworld` ( -- 账单支付(world服使用)
  `id` int(11) NOT NULL AUTO_INCREMENT, -- 账单ID，主键，自增
  `serialnumber` varchar(32) DEFAULT NULL, -- 来自gameserver的账单号
  `accountid` int(11) DEFAULT NULL,-- 对应账户ID
  `playerid` int(11) DEFAULT NULL, -- 对应玩家ID
  `errorcode` int(11) DEFAULT NULL, -- 错误码(用于定位具体原因，在world服的处理中)
  `worldreceiveserver` int(11) DEFAULT NULL, -- 账单跟踪状态，下同，1表示world服收到gameserver请求
  `worldsendbilling` int(11) DEFAULT NULL,-- world服向billingserver发送请求
  `worldreceivebilling` int(11) DEFAULT NULL, -- world服收到billingserver的返回
  `worldsendserver` int(11) DEFAULT NULL, -- world服向gameserver发送返回
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chargeworld`
--

LOCK TABLES `chargeworld` WRITE;
/*!40000 ALTER TABLE `chargeworld` DISABLE KEYS */;
/*!40000 ALTER TABLE `chargeworld` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friend_home`
--
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `friend_home` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` int(11) NOT NULL DEFAULT '0', -- 玩家自己ID
  `friendId` int(11) NOT NULL DEFAULT '0', -- 好友的ID
  `clearTimes` int(11) NOT NULL DEFAULT '0', -- 打扫次数，每天最多3次
  `clearDay` int(11) NOT NULL DEFAULT '0', -- 打扫时间
  `transportTimes` int(11) NOT NULL DEFAULT '0', -- 气球次数，每天最多1次
  `transportDay` int(11) NOT NULL DEFAULT '0', -- 气球传送时间
  `waterTimes` int(11) NOT NULL DEFAULT '0', -- 浇水次数，每天最多1次
  `waterDay` int(11) NOT NULL DEFAULT '0', -- 浇水日期
  PRIMARY KEY (`id`),
  KEY `playerId` (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend_home`
--

LOCK TABLES `friend_home` WRITE;
/*!40000 ALTER TABLE `friend_home` DISABLE KEYS */;
/*!40000 ALTER TABLE `friend_home` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friends`
--
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `friends` (
  `player_id` int(11) NOT NULL DEFAULT '0', -- 游戏内玩家ID，主键，同player表的id
  `friends` blob, -- 对应的好友列表
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friends`
--

LOCK TABLES `friends` WRITE;
/*!40000 ALTER TABLE `friends` DISABLE KEYS */;
/*!40000 ALTER TABLE `friends` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `keyword`
--
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keyword` ( -- 关键字列表，初始存入缓存，根据游戏玩家的加入实时更新
  `Id` int(11) NOT NULL AUTO_INCREMENT, -- 主键ID，自增
  `specialid` int(11) NOT NULL DEFAULT '0',
  `word` varchar(255) CHARACTER SET utf8 DEFAULT NULL, -- 关键字，实际用做缓存的KEY，根据客户端提交的内容直接定位
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `keyword`
--

LOCK TABLES `keyword` WRITE;
/*!40000 ALTER TABLE `keyword` DISABLE KEYS */;
/*!40000 ALTER TABLE `keyword` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mail`
--
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mail` ( -- 邮件
  `Id` int(11) NOT NULL AUTO_INCREMENT, -- 邮件ID，主键，自增
  `sourceid` int(11) NOT NULL DEFAULT '0',
  `destid` int(11) NOT NULL DEFAULT '0', -- 接收方ID，如果为玩家则与player表中的id对应
  `sourcename` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `posttime` datetime DEFAULT NULL,
  `expiration_time` datetime DEFAULT NULL,
  `content` text CHARACTER SET utf8 NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL DEFAULT '0',
  `useType` int(11) NOT NULL DEFAULT '0',
  `sourceicon` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `sourcelevel` int(11) NOT NULL DEFAULT '0',
  `exist` int(11) NOT NULL DEFAULT '0', -- 
  PRIMARY KEY (`Id`),
  KEY `destid` (`destid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
--
-- Dumping data for table `mail`
--

LOCK TABLES `mail` WRITE;
/*!40000 ALTER TABLE `mail` DISABLE KEYS */;
/*!40000 ALTER TABLE `mail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player`
--
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player` ( -- 游戏内玩家表
  `id` int(11) NOT NULL AUTO_INCREMENT, -- 玩家ID，主键，自增
  `accountId` int(11) NOT NULL DEFAULT '0', -- 所属账户ID，与账户表ID关联对应
  `cyouId` varchar(32) DEFAULT NULL, -- 
  `moveHousePassword` varchar(12) DEFAULT NULL,
  `housePasswordCreateTime` datetime DEFAULT NULL,
  `name` varchar(32) DEFAULT '', -- 玩家名称
  `icon` varchar(12) DEFAULT NULL, -- 玩家头像
  `level` int(11) NOT NULL DEFAULT '0', -- 当前玩家等级
  `money` int(11) NOT NULL DEFAULT '0', -- 当前金币
  `openBlock` int(11) NOT NULL DEFAULT '0', -- 
  `openBlock4` int(11) NOT NULL DEFAULT '0',
  `openBlock3` int(11) NOT NULL DEFAULT '0',
  `openBlock2` int(11) NOT NULL DEFAULT '0',
  `building` blob,
  `building2` blob,
  `building3` blob,
  `building4` blob,
  `star` int(11) NOT NULL DEFAULT '1', -- 玩家星数（经验值）
  `lang` int(11) NOT NULL DEFAULT '1', -- 前端界面语言
  `exist` int(11) NOT NULL DEFAULT '0', -- 该玩家是否存在 0不存在 1存在
  `rabit0Number` int(11) NOT NULL DEFAULT '0',
  `rabit0Number4` int(11) NOT NULL DEFAULT '0',
  `rabit0Number3` int(11) NOT NULL DEFAULT '0',
  `rabit0Number2` int(11) NOT NULL DEFAULT '0',
  `rabit1Number` int(11) NOT NULL DEFAULT '0',
  `rabit1Number4` int(11) NOT NULL DEFAULT '0',
  `rabit1Number3` int(11) NOT NULL DEFAULT '0',
  `rabit1Number2` int(11) NOT NULL DEFAULT '0',
  `catsInfo` varchar(255) DEFAULT NULL,
  `battleWinTimes` int(11) NOT NULL DEFAULT '0', -- 战斗胜利次数
  `battleTimes` int(11) NOT NULL DEFAULT '0', -- 战斗次数
  `pool` mediumtext, -- 玩家属性池，存储一些常用信息，格式为键值对，一行一条数据
  `items` varchar(511) DEFAULT NULL,
  `housePassword` varchar(12) DEFAULT NULL,
  `quest` blob,
  `quest2` blob,
  `quest3` blob,
  `quest4` blob,
  `constellation` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `accountId` (`accountId`),
  KEY `name` (`name`(12)),
  KEY `level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player`
--

LOCK TABLES `player` WRITE;
/*!40000 ALTER TABLE `player` DISABLE KEYS */;
/*!40000 ALTER TABLE `player` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player_transport`
--
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `player_transport` ( -- 玩家传送表（与好友间传送小动物）
  `id` int(11) NOT NULL AUTO_INCREMENT, -- 主键，ID，自增
  `playerId` int(11) NOT NULL DEFAULT '0', -- 玩家ID，与player表的主键ID对应
  `friendId` int(11) NOT NULL DEFAULT '0', -- 
  `friendName` varchar(32) NOT NULL DEFAULT '',
  `transTime` date NOT NULL DEFAULT '0000-00-00',
  `exist` tinyint(1) NOT NULL DEFAULT '0', -- 是否存在 1是 0否
  `icon` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `playerId` (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player_transport`
--

LOCK TABLES `player_transport` WRITE;
/*!40000 ALTER TABLE `player_transport` DISABLE KEYS */;
/*!40000 ALTER TABLE `player_transport` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-09-25 15:55:00

-- billing database ---

CREATE TABLE order_appstore ( -- 最终账单支付信息(Billing服务器使用)
  `id` int(11) NOT NULL AUTO_INCREMENT, -- 账单ID，主键，自增
  `accountid` int(11) NOT NULL, -- 账户ID(gameserver)
  `serialnumber` bigint, -- 账单号(gameserver负责生成)
  `transactionid` bigint, -- 传输的账单号ID，
  `bundleid` varchar(255), -- 对应游戏应用ID(每个gameserver唯一)
  `productid` varchar(255), -- 商品ID
  `receipt` varchar(255), -- Base64编码的单据信息(用于向苹果平台传输的必须数据，接收验证返回的结果也是receipt格式后再进行游戏内验证)
  `imoney` int(11), -- 所需金额
  `createtime` datetime DEFAULT NULL, -- 创建时间(以billingserver收到处理的时间)
  `finishtime` datetime DEFAULT NULL, -- 处理完成时间
  `receiveworld` int(11),-- 表示接收到world发来的请求
  `requestapp` int(11), -- 去苹果apple平台请求
  `receiveapp` int(11), -- 接收到苹果apple平台返回
  `responseworld` int(11),-- 表示正常响应world的输出
  `errorcode` int(11), -- 错误码输出(用于定位具体的原因，用于billingserver服的处理中)
  `gamecode` varchar(255), -- gameserver的游戏标识
  `playerid` int(11), -- 玩家ID(gameserver)
  `gameserverid` int(11), -- gameserver服务器ID，
  `httpcode` int(11), -- 苹果apple平台返回的http状态码
  `status` int(11), -- 苹果apple平台返回结果中的状态
  `isoffline` int(11), -- 离线关闭 1是 0否?
  `istest` int(11), -- 是否是测试环境
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------- 以下为新版兔村添加或修改 ---------------------------------

-- 新版针对邮件表修改
alter table mail add column templateId int unsigned default '0' after content;
alter table mail add column rich int unsigned default '0' after exist;
alter table mail add column raceId tinyint unsigned default '0' after rich;
alter table mail add column download tinyint unsigned default '0' after raceId;

-- 新版Player表修改
alter table player add column raceId int unsigned default '0' after constellation;
alter table player add column rich int unsigned default '0' after raceId;
alter table player add column stone int unsigned default '0' after rich;
alter table player add column food int unsigned default '0' after stone;
alter table player add column woods int unsigned default '0' after food;
alter table player add column exp int unsigned default '0' after woods;
alter table player add column love int unsigned default '0' after exp;
alter table player add column lastLoginTime datetime after love;
-- 修改兵力存储范围
alter table player change catsInfo catsInfo text NULL;
-- 修改背包物品存储范围
alter table player change items items text NULL;
-- 添加种族索引，方便日后统计
alter table player add index raceId(raceId);
-- player_sns表
CREATE TABLE `player_sns` (
  `player_id` int(11) NOT NULL COMMENT '玩家的ID，主键，非自增，与玩家表中的ID对应',
  `help_count` tinyint(4) DEFAULT '0' COMMENT '帮助好友浇水次数，用户第一次初始化此记录，初始为0，以后为更新操作，每天有上限限制。',
  `last_help_time` datetime DEFAULT NULL COMMENT '最后一次帮助好友浇水更新日期，年月日格式。',
  `tree_grade` tinyint(4) DEFAULT '0' COMMENT '当前摇钱树等级，由用户上传获取，可能不实时，计算时使用。需检查合理等级区间。',
  `visit_count` tinyint(4) DEFAULT '0' COMMENT '好友浇水次数，初始为0或冷却CD结束后清0，达到条件即为可收获状态',
  `last_harvest_time` int(11) DEFAULT '0' COMMENT '最后一次收获时间，玩家成功收获后更新的时间戮，初始为0表示',
  `send_count` tinyint(4) DEFAULT '0' COMMENT '每天的爱心发信次数，初始为0，每天重置为0',
  `last_send_time` datetime DEFAULT NULL COMMENT '当天爱心发信日期，年月日格式',
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- user_opt_log表
CREATE TABLE `user_opt_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` int(11) NOT NULL COMMENT '玩家的ID',
  `type` tinyint(4) NOT NULL COMMENT '日志类型\r\n1. 系统奖励\r\n  其他类型待定\r\n',
  `type_id` int(11) NOT NULL COMMENT '日志类型对表的id',
  `status` tinyint(4) NOT NULL COMMENT '日志状态\r\n1. 服务端产生\r\n2. 客户端成功响应并处理\r\n',
  `content` text NOT NULL COMMENT '日志内容，根据不同的类型记录对应的内容',
  `add_time` datetime NOT NULL COMMENT '日志添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- friend_home表增加发送爱心次数与发送爱心时间
alter table friend_home add column sendTimes int unsigned NOT NULL after waterDay;
alter table friend_home add column sendDay int unsigned NOT NULL after sendTimes;

-- 增加玩家互动的体力值（交互需要消耗）
alter table player_sns add column active_count smallint unsigned after last_harvest_time;
alter table player_sns add column last_active_time int unsigned after active_count;

-- 摇钱树功能相关
alter table player_sns add column tree_x smallint unsigned after last_help_time;
alter table player_sns add column tree_y smallint unsigned after tree_x;
alter table player_sns add column visit_record text after visit_count;
alter table friend_home drop column waterTimes;
alter table friend_home drop column waterDay;
alter table player_sns add column tree_status tinyint unsigned after tree_grade;
alter table player_sns drop column last_help_time;
alter table player_sns add column last_help_day int unsigned after help_count;
alter table player_sns drop column last_send_time;
alter table player_sns add column last_send_day int unsigned after send_count;
-- 增加系统公告表
CREATE TABLE `notice` (
  `notice_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '公告id',
  `content` text COMMENT '公告内容',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 增加市场表
CREATE TABLE `market` (
  `player_id` int(10) unsigned NOT NULL DEFAULT '0',
  `pos` smallint(5) unsigned NOT NULL DEFAULT '0',
  `status` tinyint(3) unsigned DEFAULT NULL,
  `item_id` int(10) unsigned DEFAULT NULL,
  `item_num` int(10) unsigned DEFAULT NULL,
  `price` int(10) unsigned DEFAULT NULL,
  `add_time` int(10) unsigned DEFAULT NULL,
  `expire_time` int(10) unsigned DEFAULT NULL,
  `recycle_time` int(10) unsigned DEFAULT NULL,
  `buy_player_id` int(10) unsigned DEFAULT NULL,
  `buy_player_name` varchar(255) DEFAULT NULL,
  `buy_player_icon` varchar(255) DEFAULT NULL,
  `buy_player_rich` int(10) unsigned DEFAULT NULL,
  `sell_player_name` varchar(255) DEFAULT NULL,
  `sell_player_icon` varchar(255) DEFAULT NULL,
  `sell_player_rich` int(11) DEFAULT NULL,
  `sell_player_lvl` int(11) DEFAULT NULL,
  `sell_player_raceid` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`player_id`,`pos`),
  KEY `item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 增加市场交易日志表
create table market_log(
	log_id int unsigned auto_increment,
	type tinyint unsigned,
	pos smallint unsigned,
	item_id int unsigned,
	item_num int unsigned,
	price int unsigned,
	sell_player_id int unsigned,
	buy_player_id int unsigned,
	add_time datetime,
	primary key(log_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 增加田地状态和房屋状态
alter table friend_home add column fieldState smallint unsigned NOT NULL DEFAULT '0' after sendDay;
alter table friend_home add column fieldDay int unsigned NOT NULL DEFAULT '0' after fieldState;
alter table friend_home add column houseState smallint unsigned NOT NULL DEFAULT '0' after fieldDay;
alter table friend_home add column houseDay int unsigned NOT NULL DEFAULT '0' after houseState;
alter table friend_home add column npcId int unsigned NOT NULL DEFAULT '0' after houseDay;
-- 增加排行榜表
CREATE TABLE `rank` (
  `rank_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint(3) unsigned NOT NULL,
  `last_rank` tinyint(3) unsigned NOT NULL,
  `now_rank` tinyint(3) unsigned NOT NULL,
  `value` int(10) unsigned NOT NULL,
  `player_id` int(11) NOT NULL,
  `rank_day` int(11) NOT NULL,
  `player_name` varchar(255),
  `player_lvl` int(11) NOT NULL,
  PRIMARY KEY (`rank_id`),
  KEY `player_id` (`player_id`)
) ENGINE=InnoDB AUTO_INCREMENT=601 DEFAULT CHARSET=utf8;
-- 增加player表字段，为了排行榜的功能
alter table player add column lastSynchInfoTime datetime after lastLoginTime;
alter table player add column allCatCount int unsigned DEFAULT '0' after lastSynchInfoTime;
alter table player add column allBattleStarCount int unsigned DEFAULT '0' after allCatCount;
-- 贸易表
CREATE TABLE `trade` (
  `player_id` int(11) NOT NULL,
  `pos` tinyint(4) NOT NULL,
  `trade_id` int(11) DEFAULT NULL,
  `box_id` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `item_id` int(11) DEFAULT NULL,
  `item_num` int(11) DEFAULT NULL,
  `help_player_id` int(11) DEFAULT NULL,
  `help_player_name` varchar(255) DEFAULT NULL,
  `help_player_icon` varchar(255) DEFAULT NULL,
  `help_player_rich` int(11) DEFAULT NULL,
  PRIMARY KEY (`player_id`,`pos`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 增加客户端发来的json数据
alter table player add column jsonData mediumtext after allBattleStarCount;
-- 邮件增加参数
alter table mail add column param text CHARACTER SET utf8 after download;
alter table mail add column awardId int unsigned DEFAULT '0' after param;
alter table player_sns add column first_visit tinyint unsigned DEFAULT '0' after last_send_day;