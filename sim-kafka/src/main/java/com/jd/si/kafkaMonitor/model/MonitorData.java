package com.jd.si.kafkaMonitor.model;

/**
 * 监控数据对象
 * Created by lilianglin on 2016/8/4.
 */
public class MonitorData {

    private Integer id;
    private Integer pid;
    private Integer dateType;
    private Integer dataType;
    private String jsonData;
    private String indate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getDateType() {
        return dateType;
    }

    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getIndate() {
        return indate;
    }

    public void setIndate(String indate) {
        this.indate = indate;
    }

    @Override
    public String toString() {
        return "MonitorData{" +
                "indate='" + indate + '\'' +
                ", jsonData='" + jsonData + '\'' +
                ", dataType=" + dataType +
                ", dateType=" + dateType +
                ", pid=" + pid +
                ", id=" + id +
                '}';
    }
}
