package com.jd.si.kafkaMonitor.alarm;

import com.jd.si.kafkaMonitor.model.BrokerStateEnum;
import com.jd.si.kafkaMonitor.model.AlarmTypeNum;
import com.jd.si.kafkaMonitor.model.KafkaZkModel;
import com.jd.si.kafkaMonitor.service.ClusterService;
import com.jd.si.kafkaMonitor.service.ZkMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 机器存活报警
 * Created by lilianglin on 2016/8/26.
 */
@Service
public class BrokerAliveAlarm implements AlarmService {

    @Autowired
    ClusterService clusterService;
    @Autowired
    ZkMonitorService zkMonitorService;

    /**
     * 先取出有哪些集群，在用集群的zk获取brokerList
     * 在用数据库中的jmx broker与之对比，找出不存活的broker
     * 不存活定义：zk没有，db有
     */
    @Override
    public void run() {
        List<Map<String, String>> clusterList = clusterService.getList(null);
        if(CollectionUtils.isEmpty(clusterList)){
            return;
        }
        for(Map<String,String> cMap : clusterList){
            String zkhost = "",cname = "";
            if(cMap.get("cname") != null){
                cname = cMap.get("cname").toString();
            }
            if(cMap.get("zkhost") != null){
                zkhost = cMap.get("zkhost").toString();
                List<KafkaZkModel.Broker> brokerList = zkMonitorService.getBrokerList(zkhost);
                if(CollectionUtils.isEmpty(brokerList)){
                    continue;
                }
                AlarmVo alarmVo = new AlarmVo();
                Iterator<KafkaZkModel.Broker> brokerIterator = brokerList.iterator();
                StringBuilder broStr = new StringBuilder();
                boolean alarmFlag = false;
                while(brokerIterator.hasNext()){
                    KafkaZkModel.Broker broker = brokerIterator.next();
                    if(broker.getState() == BrokerStateEnum.DEAD.getValue()){
                        broStr.append(broker.getHost());
                        broStr.append(" ");
                        alarmFlag = true;
                    }
                }
                //没不存活的
                if(!alarmFlag){
                    continue;
                }
                alarmVo.setType(AlarmTypeNum.BROKER_ALIVE.getValue());
                alarmVo.setTitle(AlarmTypeNum.BROKER_ALIVE.getTitle());
                String alarmTemplateStr = AlarmTypeNum.BROKER_ALIVE.getAlarmStr();
                alarmTemplateStr = alarmTemplateStr.replace("#zkhost",cname);
                alarmTemplateStr = alarmTemplateStr.replace("#brokerList",broStr);
                alarmVo.setAlarmStr(alarmTemplateStr);
                //放入本地消费队列
                AlarmQueue.getAlarmQueue().put(alarmVo);
            }
        }

    }

}
