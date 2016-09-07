CREATE DATABASE /*!32312 IF NOT EXISTS*/si_kafka_monitor /*!40100 DEFAULT CHARACTER SET utf8 */;

USE si_kafka_monitor;

/*Table structure for table km_cluster */

DROP TABLE IF EXISTS km_cluster;

CREATE TABLE km_cluster (
  id int(11) NOT NULL AUTO_INCREMENT,
  cname varchar(100) NOT NULL COMMENT 'zk集群名称',
  zkhost varchar(300) DEFAULT NULL COMMENT 'zk集群host list',
  cdesc varchar(300) DEFAULT NULL COMMENT 'zk集群描述',
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;


/*Table structure for table km_jmx_config */

DROP TABLE IF EXISTS km_jmx_config;

CREATE TABLE km_jmx_config (
  id int(11) NOT NULL AUTO_INCREMENT,
  cid int(11) DEFAULT NULL COMMENT 'zk集群ID',
  host varchar(20) DEFAULT NULL COMMENT '机器IP',
  port varchar(10) DEFAULT NULL COMMENT '机器jmx端口号',
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;


/*Table structure for table km_monitor_data */

DROP TABLE IF EXISTS km_monitor_data;

CREATE TABLE km_monitor_data (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  pid tinyint(4) NOT NULL COMMENT 'kafka进程ID',
  dateType tinyint(4) NOT NULL COMMENT '1:分钟 2：小时 3：天',
  dataType tinyint(4) NOT NULL COMMENT '1：jvm 2：kafka',
  jsonData varchar(1000) NOT NULL COMMENT 'json格式数据内容',
  indate datetime NOT NULL COMMENT '录入时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=62992 DEFAULT CHARSET=utf8;