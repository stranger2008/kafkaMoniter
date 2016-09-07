package com.jd.si.kafkaMonitor.model;

/**
 * jmx连接配置
 * Created by lilianglin on 2016/8/8.
 */
public class JmxConfig {

    private int id;
    private int cid;
    private String host;
    private String port;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "JmxConfig{" +
                "id=" + id +
                "cid=" + cid +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
