package com.jd.si.kafkaMonitor.dao;

import com.jd.si.kafkaMonitor.model.MonitorData;

/**
 * kafka broker 配置读取
 * Created by lilianglin on 2016/3/30.
 */
public interface MonitorDataDao extends GenericDao{

    public void insert(MonitorData monitorData);

}
