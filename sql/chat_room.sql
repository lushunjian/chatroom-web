/*
Navicat MySQL Data Transfer

Source Server         : mydb
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : chat_room

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2019-01-09 21:48:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for friend
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_account` varchar(12) NOT NULL,
  `friend_account` varchar(12) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of friend
-- ----------------------------
INSERT INTO `friend` VALUES ('1', '111', '123');
INSERT INTO `friend` VALUES ('2', '111', '222');
INSERT INTO `friend` VALUES ('3', '111', '333');
INSERT INTO `friend` VALUES ('4', '123', '111');
INSERT INTO `friend` VALUES ('5', '222', '111');
INSERT INTO `friend` VALUES ('6', '333', '111');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(10) DEFAULT '' COMMENT '昵称',
  `user_account` varchar(12) NOT NULL COMMENT '账号',
  `user_password` varchar(10) NOT NULL COMMENT '密码',
  `user_describe` varchar(30) DEFAULT '' COMMENT '描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account` (`user_account`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '张三', '123', '123', '');
INSERT INTO `user` VALUES ('2', '张柯', '111', '111', '测试');
INSERT INTO `user` VALUES ('4', '张三风', '222', '222', '123');
INSERT INTO `user` VALUES ('6', '李四', '333', '333', '333');
INSERT INTO `user` VALUES ('8', '小四', '444', '444', '444');
INSERT INTO `user` VALUES ('9', '王五', '555', '555', '555');
INSERT INTO `user` VALUES ('10', '赵六', '666', '666', '666');
INSERT INTO `user` VALUES ('11', '无论', '777', '777', '111');
INSERT INTO `user` VALUES ('12', '测试用户', '999', '999', '123');
