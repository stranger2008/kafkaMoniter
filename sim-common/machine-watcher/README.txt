一、CPU使用率
cat /proc/stat
输出解释：
CPU 以及CPU0、CPU1、CPU2、CPU3每行的每个参数意思（以第一行为例）为：
参数 解释
user (432661) 从系统启动开始累计到当前时刻，用户态的CPU时间（单位：jiffies） ，不包含 nice值为负进程。1jiffies=0.01秒
nice (13295) 从系统启动开始累计到当前时刻，nice值为负的进程所占用的CPU时间（单位：jiffies）
system (86656) 从系统启动开始累计到当前时刻，核心时间（单位：jiffies）
idle (422145968) 从系统启动开始累计到当前时刻，除硬盘IO等待时间以外其它等待时间（单位：jiffies）
iowait (171474) 从系统启动开始累计到当前时刻，硬盘IO等待时间（单位：jiffies） ，
irq (233) 从系统启动开始累计到当前时刻，硬中断时间（单位：jiffies）
softirq (5346) 从系统启动开始累计到当前时刻，软中断时间（单位：jiffies）

CPU时间=user+system+nice+idle+iowait+irq+softirq
那么CPU利用率的计算方法：可以使用取两个采样点，计算其差值的办法。
计算方式：CPU利用率 = 1- (idle2-idle1)/(cpu2-cpu1)

二、内存使用率
#cat /proc/meminfo
#计算方式：内存使用率 = 1 - MemFree/MemTotal
#计算方式2：http://blog.chinaunix.net/uid-24709751-id-3564801.html

$ free -m
             total       used       free     shared    buffers     cached
Mem:         15901      12702       3198          0        298       2886
-/+ buffers/cache:       9518       6383
Swap:        16383      11793       4590
计算方式：(used-buffers-cached)/total


三、磁盘IO
 iostat -d -x
一秒中有百分之多少的时间用于I/O操作，或者说一秒中有多少时间I/O队列是非空的。如果%util接近100%,表明I/O请求太多,I/O系统已经满负荷，磁盘可能存在瓶颈,一般%util大于70%,I/O压力就比较大，读取速度有较多的wait
计算方式：最大值


四、网络带宽使用率
cat /proc/net/dev
网口带宽：ethtool eth0
计算方式：统计一段时间内Receive和Tramsmit的bytes数的变化，即可获得网口传输速率，再除以网口的带宽就得到带宽的使用率


五、平均负载
top -b -n 1
计算方式：直接读取结果

六、TCP连接数
netstat -n | grep ESTABLISHED | awk '/^tcp/ {++S[$NF]} END {for(a in S) print S[a]}'
计算方式：直接读取结果

七、机器IP
1、程序读取（目前选用）
2、命令（未选用）：ifconfig eth0|sed -n 2p|awk '{ print $2 }'|tr -d 'addr:'

测试：java -cp loadtest-util-1.0-SNAPSHOT-jar-with-dependencies.jar  com.jd.si.jupiter.lt.util.monitor.MonitorUtil

参考网址：http://blog.csdn.net/blue_jjw/article/details/8741000


============================================================================================
------------------------
machine-watcher使用说明 ||
------------------------
主程序：需要监控的服务
agent（主程序）需要使用machine-watcher工具jar包来进行数的获取，再通过主程序的rpc功能对外提供服务
1）引入pom依赖
    <groupId>com.jd.si.jupiter.tools.machinewatcher</groupId>
    <artifactId>machine-watcher</artifactId>
    <version>1.0.0-SNAPSHOT</version>
2）在自己的agent的conf目录下增加配置文件“machine-watcher.properties”，以下参数必须
----->
    # machine watcher conf begin（machine-watcher使用）
    #机器监控日志写入的目录，以及最多多少个日志文件（按写数据的时间戳命名，一次监控一个文件）
    machineWatcherBakDir=/export/servers/si/storm/machineWatcher_data
    machineWatcherBakCount=1440

    # schedule conf：watcher命令执行初始延迟，单位：s
    watcher_initial_delay=15
    #schedule conf：watcher命令执行间隔，单位：s
    watcher_interval=2
    # schedule conf：监控信息写入文件定时任务，初始延迟，单位：s
    mon_writer_initial_delay=15
    #schedule conf：监控信息写入文件定时任务，写间隔，单位：s
    mon_writer_interval=60
----->
3）agent（主程序）中启动machine-watcher，定时获取机器监控信息
    MachineInitialization machineInitialization = new MachineInitialization();
    machineInitialization.init();
    logger.error("machine monitor started!");

4)在agent（主程序）中获取machine-watcher写入内存的机器监控信息
    通过MachineGlobalInstance.getMonitorBean();即可获得当前内存中的监控信息

5）agent（主程序）需要提供thrift rpc服务/其他通信方式，方便展示端获取监控数据。
   以thrift rpc为例，在thrift service接口的实现中获取机器监控信息返回给调用端
   注：
   1、在主程序的thrift接口文件增新增自定义的方法getMachineMonitorInfo
   2、增加自定义机器信息对象MonitorBean和NetUsageBean
参考：si-monitor\sim-storm\storm-watcher-thrift\src\main\thrift\watcher.thrift
----->
    @Override
    public MonitorBean getMachineMonitorInfo() throws TException {
        logger.info("getMonitorInfo return > " + MachineGlobalInstance.getMonitorBean());
        return convertToThriftBean(MachineGlobalInstance.getMonitorBean());
    }


--------------watcher.thrift----------------------
// 服务资源监控信息
struct MonitorBean {
  1: double cpuUsage //CPU使用率
  2: double memUsage //内存使用率
  3: double ioUsage //磁盘IO使用率
  4: list<NetUsageBean> netUsages  //带宽使用率 集合
  5: i32 establishedCount //TCP连接数
  6: double loadAverage1m //load average 1分钟
  7: double loadAverage5m //load average 5分钟
  8: double loadAverage15m //load average 15分钟
  9: i64 refreshTime //刷新时间
  10:string ip //机器IP
  11:i64 memTotal //内存大小
  12:i64 memUse //已使用内存大小
}


//带宽使用率
struct NetUsageBean {
  1: string deviceName //网卡名称
  2: i64 receive //下行数据
  3: i64 transmit //上行数据
  4: double receivePS // 下行数据/秒
  5: double transmitPS // 上行数据/秒
  6: i64 deviceSpeed // 网卡带宽
  7: double receiveUsage // 下行带宽使用率
  8: double transmitUsage // 上行带宽使用率
}