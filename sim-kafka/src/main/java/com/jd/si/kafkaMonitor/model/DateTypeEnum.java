package com.jd.si.kafkaMonitor.model;

/**
 * 监控日期类型
 * Created by lilianglin on 2016/8/4.
 */
public enum DateTypeEnum {

    MINUTES(1),HOURS(2),DAYS(3);

    private int value;

    DateTypeEnum(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
