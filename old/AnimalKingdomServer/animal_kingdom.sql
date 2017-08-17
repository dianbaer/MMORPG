# MySQL-Front 5.1  (Build 4.13)

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;


# Host: 10.6.34.123    Database: animal_kingdom
# ------------------------------------------------------
# Server version 5.0.87-log

#
# Source for table account
#

DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` int(11) NOT NULL auto_increment,
  `mid` varchar(255) default '',
  `imoney` int(11) NOT NULL default '0',
  `rewardDollar` int(11) NOT NULL default '0',
  `createTime` datetime default NULL,
  `lastLoginTime` datetime default NULL,
  `usedDollar` int(11) NOT NULL default '0',
  `compensateDollar` int(11) NOT NULL default '0',
  `initDollar` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `mid` (`mid`)
) ENGINE=InnoDB AUTO_INCREMENT=4690990 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

#
# Source for table account_sns
#

DROP TABLE IF EXISTS `account_sns`;
CREATE TABLE `account_sns` (
  `Id` int(11) NOT NULL default '0',
  `cyouId` varchar(255) default '',
  `faceBookId` varchar(255) default '',
  `kaixinId` varchar(255) default '',
  `qqId` varchar(255) default NULL,
  `weiboId` varchar(255) default NULL,
  `renrenId` varchar(255) default NULL,
  `twitterId` varchar(255) default NULL,
  PRIMARY KEY  (`Id`),
  KEY `cyouId` (`cyouId`(20)),
  KEY `faceBookId` (`faceBookId`(20)),
  KEY `weiboId` (`weiboId`(20)),
  KEY `kaixinId` (`kaixinId`(20)),
  KEY `qqId` (`qqId`(20)),
  KEY `renrenId` (`renrenId`(20)),
  KEY `twitterId` (`twitterId`(20))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Source for table charge
#

DROP TABLE IF EXISTS `charge`;
CREATE TABLE `charge` (
  `id` int(11) NOT NULL auto_increment,
  `serialnumber` varchar(255) default NULL,
  `accountid` varchar(255) default NULL,
  `playerid` varchar(255) default NULL,
  `bundleid` varchar(255) default NULL,
  `result` int(11) default NULL,
  `productid` varchar(255) default NULL,
  `errorcode` int(11) default NULL,
  `serverreceiveclient` int(11) default NULL,
  `serversendworld` int(11) default NULL,
  `worldreceiveserver` int(11) default NULL,
  `worldsendbilling` int(11) default NULL,
  `worldreceivebilling` int(11) default NULL,
  `worldsendserver` int(11) default NULL,
  `serverreceiveworld` int(11) default NULL,
  `finishtime` timestamp NULL default NULL,
  `requesttime` timestamp NULL default NULL,
  `receipt` text,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1405879 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

#
# Source for table chargeworld
#

DROP TABLE IF EXISTS `chargeworld`;
CREATE TABLE `chargeworld` (
  `id` int(11) NOT NULL auto_increment,
  `serialnumber` varchar(255) default NULL,
  `accountid` int(11) default NULL,
  `playerid` int(11) default NULL,
  `errorcode` int(11) default NULL,
  `worldreceiveserver` int(11) default NULL,
  `worldsendbilling` int(11) default NULL,
  `worldreceivebilling` int(11) default NULL,
  `worldsendserver` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1148261 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

#
# Source for table friend_home
#

DROP TABLE IF EXISTS `friend_home`;
CREATE TABLE `friend_home` (
  `id` int(11) NOT NULL auto_increment,
  `playerId` int(11) NOT NULL default '0',
  `friendId` int(11) NOT NULL default '0',
  `clearTimes` int(11) NOT NULL default '0',
  `clearDay` int(11) NOT NULL default '0',
  `transportTimes` int(11) NOT NULL default '0',
  `transportDay` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
  KEY `playerId` (`playerId`)
) ENGINE=InnoDB AUTO_INCREMENT=405443 DEFAULT CHARSET=utf8;

#
# Source for table friends
#

DROP TABLE IF EXISTS `friends`;
CREATE TABLE `friends` (
  `player_id` int(11) NOT NULL default '0',
  `friends` blob,
  PRIMARY KEY  (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

#
# Source for table keyword
#

DROP TABLE IF EXISTS `keyword`;
CREATE TABLE `keyword` (
  `Id` int(11) NOT NULL auto_increment,
  `specialid` int(11) NOT NULL default '0',
  `word` varchar(255) character set utf8 default NULL,
  PRIMARY KEY  (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=347 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

#
# Source for table mail
#

DROP TABLE IF EXISTS `mail`;
CREATE TABLE `mail` (
  `Id` int(11) NOT NULL auto_increment,
  `sourceid` int(11) NOT NULL default '0',
  `destid` int(11) NOT NULL default '0',
  `sourcename` varchar(255) character set utf8 default NULL,
  `posttime` datetime default NULL,
  `expiration_time` datetime default NULL,
  `content` text character set utf8 NOT NULL,
  `status` int(11) NOT NULL default '0',
  `type` int(11) NOT NULL default '0',
  `useType` int(11) NOT NULL default '0',
  `sourceicon` varchar(255) collate utf8_bin default NULL,
  `sourcelevel` int(11) NOT NULL default '0',
  `exist` int(11) NOT NULL default '0',
  PRIMARY KEY  (`Id`),
  KEY `destid` (`destid`)
) ENGINE=MyISAM AUTO_INCREMENT=3187577 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

#
# Source for table player
#

DROP TABLE IF EXISTS `player`;
CREATE TABLE `player` (
  `id` int(11) NOT NULL auto_increment,
  `accountId` int(11) NOT NULL default '0',
  `cyouId` varchar(255) default NULL,
  `moveHousePassword` varchar(255) default NULL,
  `housePasswordCreateTime` datetime default NULL,
  `name` varchar(255) default '',
  `icon` varchar(255) default NULL,
  `level` int(11) NOT NULL default '0',
  `money` int(11) NOT NULL default '0',
  `openblock` int(11) NOT NULL default '0',
  `building` blob,
  `star` int(11) NOT NULL default '1',
  `lang` int(11) NOT NULL default '1',
  `exist` int(11) NOT NULL default '0',
  `rabit0Number` int(11) NOT NULL default '0',
  `rabit1Number` int(11) NOT NULL default '0',
  `catsInfo` varchar(255) default NULL,
  `battleWinTimes` int(11) NOT NULL default '0',
  `battleTimes` int(11) NOT NULL default '0',
  `pool` mediumtext,
  `items` varchar(511) default NULL,
  `housePassword` varchar(255) default NULL,
  `quest` blob,
  `constellation` int(11) NOT NULL default '1',
  PRIMARY KEY  (`id`),
  KEY `accountId` (`accountId`),
  KEY `name` (`name`(12)),
  KEY `level` (`level`)
) ENGINE=InnoDB AUTO_INCREMENT=4679137 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

#
# Source for table player_transport
#

DROP TABLE IF EXISTS `player_transport`;
CREATE TABLE `player_transport` (
  `id` int(11) NOT NULL auto_increment,
  `playerId` int(11) NOT NULL default '0',
  `friendId` int(11) NOT NULL default '0',
  `friendName` varchar(255) NOT NULL default '',
  `transTime` date NOT NULL default '0000-00-00',
  `exist` tinyint(1) NOT NULL default '0',
  `icon` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `playerId` (`playerId`)
) ENGINE=InnoDB AUTO_INCREMENT=407690 DEFAULT CHARSET=utf8;

/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
