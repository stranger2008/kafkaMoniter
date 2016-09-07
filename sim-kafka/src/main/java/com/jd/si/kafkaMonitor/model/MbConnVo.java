package com.jd.si.kafkaMonitor.model;

import javax.management.MBeanServerConnection;

/**
 * jmx连接对象
 * Created by lilianglin on 2016/8/2.
 */
public class MbConnVo {

    private String id;
    private MBeanServerConnection connection;

    public MbConnVo() {
    }

    public MbConnVo(String id, MBeanServerConnection connection) {
        this.id = id;
        this.connection = connection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MBeanServerConnection getConnection() {
        return connection;
    }

    public void setConnection(MBeanServerConnection connection) {
        this.connection = connection;
    }

    @Override
    public String toString() {
        return "MbConnVo{" +
                "id='" + id + '\'' +
                ", connection=" + connection +
                '}';
    }
}
