package com.jd.si.kafkaMonitor.dao;

import com.jd.si.kafkaMonitor.model.JmxConfig;

import java.util.List;

/**
 * kafka broker 配置读取
 * Created by lilianglin on 2016/3/30.
 */
public interface JmxConfigDao extends GenericDao{

    public boolean insertBatch(List<JmxConfig> jmxConfigList);

}
