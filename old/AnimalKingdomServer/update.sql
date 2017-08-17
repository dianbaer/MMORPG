USE `animal_kingdom`;
ALTER TABLE `animal_kingdom`.`player`
  CHANGE COLUMN `icon` `icon` varchar(12) NULL DEFAULT NULL;
ALTER TABLE `animal_kingdom`.`player`
  CHANGE COLUMN `name` `name` varchar(32) DEFAULT '';
ALTER TABLE `animal_kingdom`.`player`
  CHANGE COLUMN `cyouId` `cyouId` varchar(32) NULL DEFAULT NULL;
ALTER TABLE `animal_kingdom`.`player`
  CHANGE COLUMN `moveHousePassword` `moveHousePassword` varchar(12) NULL DEFAULT NULL;
ALTER TABLE `animal_kingdom`.`player`
  CHANGE COLUMN `housePassword` `housePassword` varchar(12) NULL DEFAULT NULL;
ALTER TABLE `animal_kingdom`.`player_transport`
  CHANGE COLUMN `friendName` `friendName` varchar(32) NOT NULL DEFAULT '';
ALTER TABLE `animal_kingdom`.`chargeworld`
  CHANGE COLUMN `serialnumber` `serialnumber` varchar(32) NULL DEFAULT NULL;
ALTER TABLE `animal_kingdom`.`charge`
  CHANGE COLUMN `accountid` `accountid` varchar(64) NULL DEFAULT NULL;
ALTER TABLE `animal_kingdom`.`charge`
  CHANGE COLUMN `playerid` `playerid` varchar(64) NULL DEFAULT NULL;
ALTER TABLE `animal_kingdom`.`charge`
  CHANGE COLUMN `bundleid` `bundleid` varchar(64) NULL DEFAULT NULL;
ALTER TABLE `animal_kingdom`.`charge`
  CHANGE COLUMN `productid` `productid` varchar(64) NULL DEFAULT NULL;
ALTER TABLE `animal_kingdom`.`account_sns`
  CHANGE COLUMN `twitterId` `twitterId` varchar(64) NULL DEFAULT NULL;
ALTER TABLE `animal_kingdom`.`account_sns`
  CHANGE COLUMN `renrenId` `renrenId` varchar(64) NULL DEFAULT NULL;
ALTER TABLE `animal_kingdom`.`account_sns`
  CHANGE COLUMN `weiboId` `weiboId` varchar(64) NULL DEFAULT NULL;
ALTER TABLE `animal_kingdom`.`account_sns`
  CHANGE COLUMN `qqId` `qqId` varchar(64) NULL DEFAULT NULL;
ALTER TABLE `animal_kingdom`.`account_sns`
  CHANGE COLUMN `kaixinId` `kaixinId` varchar(64) NULL DEFAULT '';
ALTER TABLE `animal_kingdom`.`account_sns`
  CHANGE COLUMN `faceBookId` `faceBookId` varchar(64) NULL DEFAULT '';
ALTER TABLE `animal_kingdom`.`account_sns`
  CHANGE COLUMN `cyouId` `cyouId` varchar(64) NULL DEFAULT '';
ALTER TABLE `animal_kingdom`.`account`
  CHANGE COLUMN `mid` `mid` varchar(64) NULL DEFAULT '';

#################### udpate 20130131 则增加开岛支持
USE `animal_kingdom`;
ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `building4` blob NULL AFTER `building`;
ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `building3` blob NULL AFTER `building`;
ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `building2` blob NULL AFTER `building`;

ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `quest4` blob NULL AFTER `quest`;
ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `quest3` blob NULL AFTER `quest`;
ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `quest2` blob NULL AFTER `quest`;

ALTER TABLE `animal_kingdom`.`player`
  CHANGE COLUMN `openblock` `openBlock` int(11) NOT NULL DEFAULT '0';
  
ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `openBlock2` int(11) NOT NULL DEFAULT 0 AFTER `openBlock`;
ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `openBlock3` int(11) NOT NULL DEFAULT 0 AFTER `openBlock`;
ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `openBlock4` int(11) NOT NULL DEFAULT 0 AFTER `openBlock`;

ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `rabit0Number2` int(11) NOT NULL DEFAULT 0 AFTER `rabit0Number`;
ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `rabit0Number3` int(11) NOT NULL DEFAULT 0 AFTER `rabit0Number`;
ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `rabit0Number4` int(11) NOT NULL DEFAULT 0 AFTER `rabit0Number`;

ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `rabit1Number2` int(11) NOT NULL DEFAULT 0 AFTER `rabit1Number`;
ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `rabit1Number3` int(11) NOT NULL DEFAULT 0 AFTER `rabit1Number`;
ALTER TABLE `animal_kingdom`.`player`
  ADD COLUMN `rabit1Number45` int(11) NOT NULL DEFAULT 0 AFTER `rabit1Number`;

--20130121 传说没有见索引
ALTER TABLE `animal_kingdom`.`friend_home`
  ADD INDEX (`playerId`);

