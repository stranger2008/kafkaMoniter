package com.jd.si.kafkaMonitor.model;

/**
 * broker类型
 * Created by lilianglin on 2016/8/4.
 */
public enum BrokerStateEnum {

    //活着的，死了，未在控制范围内
    ALIVE(1),DEAD(2),UNCONTROL(3);

    private int value;

    BrokerStateEnum(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
