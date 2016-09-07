package com.jd.si.kafkaMonitor.common;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 创建zk链接
 * Created by lilianglin on 2016/8/19.
 */
public class ZkClientFactory {

    public static CuratorFramework newClient(String ipAddr) {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        return CuratorFrameworkFactory.newClient(ipAddr, retryPolicy);
    }

}
