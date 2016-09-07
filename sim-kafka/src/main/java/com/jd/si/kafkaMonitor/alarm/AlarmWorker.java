package com.jd.si.kafkaMonitor.alarm;

import com.jd.si.kafkaMonitor.common.ApplicationContextUtil;
import com.jd.si.kafkaMonitor.common.SystemConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 报警启动器
 * Created by lilianglin on 2016/8/26.
 */
public class AlarmWorker {

    static List<AlarmService> alarmServiceList = new ArrayList<AlarmService>();

    static{
        BrokerAliveAlarm brokerAliveAlarm = (BrokerAliveAlarm)ApplicationContextUtil.get().getBean("brokerAliveAlarm");
        ConsumerLagAlarm consumerLagAlarm = (ConsumerLagAlarm)ApplicationContextUtil.get().getBean("consumerLagAlarm");
        alarmServiceList.add(brokerAliveAlarm);
        alarmServiceList.add(consumerLagAlarm);
    }

    public static void main(String[] args) {
        run();
    }

    public static void run(){
        //开关
        if(!SystemConfig.alarmSwitch){
            return;
        }
        for(AlarmService alarmService : alarmServiceList){
            alarmService.run();
        }
    }

}
