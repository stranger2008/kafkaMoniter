/*
 JD Si Tools Service API
 Version    : 1.0
 Author     : liliangin@jd.com
 Owner      : si-infra@jd.com
 All Rights Reserved by jd.com 2014
 Last Update:
*/

namespace java com.jd.si.jupiter.monitor.machine.thrift

//带宽使用率
struct TNetUsageBean {
  1: string deviceName //网卡名称
  2: i64 receive //下行数据
  3: i64 transmit //上行数据
  4: double receivePS // 下行数据/秒
  5: double transmitPS // 上行数据/秒
  6: i64 deviceSpeed // 网卡带宽
  7: double receiveUsage // 下行带宽使用率
  8: double transmitUsage // 上行带宽使用率
}

// 服务资源监控信息
struct TMonitorBean {
  1: double cpuUsage //CPU使用率
  2: double memUsage //内存使用率
  3: double ioUsage //磁盘IO使用率
  4: list<TNetUsageBean> netUsages  //带宽使用率 集合
  5: i32 establishedCount //TCP连接数
  6: double loadAverage1m //load average 1分钟
  7: double loadAverage5m //load average 5分钟
  8: double loadAverage15m //load average 15分钟
  9: i64 refreshTime //刷新时间
  10:string ip //机器IP
  11:i64 memTotal //内存大小
  12:i64 memUse //已使用内存大小
}

service WatcherService {
	// 获取机器信息（cpu、内存、负载等）
	TMonitorBean getMonitorInfo()
}