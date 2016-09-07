package com.jd.si.kafkaMonitor.model;

/**
 * 集群VO
 * Created by lilianglin on 2016/8/17.
 */
public class Cluster {

    private int id;
    private String cname;
    private String zkhost;
    private String cdesc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCdesc() {
        return cdesc;
    }

    public void setCdesc(String cdesc) {
        this.cdesc = cdesc;
    }

    public String getZkhost() {
        return zkhost;
    }

    public void setZkhost(String zkhost) {
        this.zkhost = zkhost;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "id=" + id +
                ", cname='" + cname + '\'' +
                ", zkhost='" + zkhost + '\'' +
                ", cdesc='" + cdesc + '\'' +
                '}';
    }
}
