package com.jd.si.kafkaMonitor.dao;

import com.jd.si.kafkaMonitor.model.Cluster;

/**
 * kafka集群管理
 * Created by lilianglin on 2016/3/30.
 */
public interface ClusterDao extends GenericDao<Cluster>{

    public boolean insert(Cluster cluster);

}
