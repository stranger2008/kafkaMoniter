package com.jd.si.kafkaMonitor.model;

/**
 * 监控数据类型
 * Created by lilianglin on 2016/8/4.
 */
public enum DataTypeEnum {

    JVM(1),KAFKA(2);

    private int value;

    DataTypeEnum(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
