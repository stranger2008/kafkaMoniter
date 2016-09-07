package com.jd.si.kafkaMonitor.model;

/**
 * 报警类型
 * Created by lilianglin on 2016/8/26.
 */
public enum AlarmTypeNum {

    BROKER_ALIVE(1,"Kakfa Broker存活报警","#zkhost机房一批机器不存活【#brokerList】"),
    CONSUMER_LAG(2,"Kafka Topic消费延迟报警","#zkhost机房，Topic：#topicName，Consumer Group：#consumerGrp，Lag消费延迟：#lagNum，超过指定阈值：#thresholdNum");

    private int value;
    private String title;
    private String alarmStr;

    AlarmTypeNum(int value,String title,String alarmStr){
        this.value = value;
        this.title = title;
        this.alarmStr = alarmStr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlarmStr() {
        return alarmStr;
    }

    public void setAlarmStr(String alarmStr) {
        this.alarmStr = alarmStr;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
