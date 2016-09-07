package com.jd.si.kafkaMonitor.alarm;

/**
 * 报警消息对象
 * Created by lilianglin on 2016/8/26.
 */
public class AlarmVo {

    private Integer type;
    private String title;
    private String alarmStr;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAlarmStr() {
        return alarmStr;
    }

    public void setAlarmStr(String alarmStr) {
        this.alarmStr = alarmStr;
    }

    public AlarmVo() {
    }

    public AlarmVo(Integer type, String title, String alarmStr) {
        this.type = type;
        this.title = title;
        this.alarmStr = alarmStr;
    }

    @Override
    public String toString() {
        return "AlarmVo{" +
                "type=" + type +
                ", title='" + title + '\'' +
                ", alarmStr='" + alarmStr + '\'' +
                '}';
    }
}
