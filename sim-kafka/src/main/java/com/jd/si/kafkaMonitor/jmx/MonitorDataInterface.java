package com.jd.si.kafkaMonitor.jmx;

import javax.management.MBeanServerConnection;
import java.util.Map;

/**
 * 获取监控数据
 * Created by lilianglin on 2016/8/3.
 */
public interface MonitorDataInterface {

    public String getJsonData(MBeanServerConnection mbs);

    public int getDataType();

}
