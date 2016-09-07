package com.jd.si.kafkaMonitor.alarm;

import com.jd.si.kafkaMonitor.common.SystemConfig;
import com.jd.si.kafkaMonitor.jmx.ExecutorUtil;
import com.jd.si.kafkaMonitor.model.AlarmTypeNum;
import com.jd.si.kafkaMonitor.model.KafkaZkModel;
import com.jd.si.kafkaMonitor.service.ClusterService;
import com.jd.si.kafkaMonitor.service.ZkMonitorService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 消费延迟报警
 * Created by lilianglin on 2016/8/26.
 */
@Service
public class ConsumerLagAlarm implements AlarmService {

    private static final Log logger = LogFactory.getLog(ConsumerLagAlarm.class);

    @Autowired
    ClusterService clusterService;
    @Autowired
    ZkMonitorService zkMonitorService;

    @Override
    public void run() {
        List<Map<String, String>> clusterList = clusterService.getList(null);
        if(CollectionUtils.isEmpty(clusterList)){
            return;
        }
        final List<LagAlarmVo> lagAlarmVos = new ArrayList<LagAlarmVo>();
        //lag阈值
        final Long lagThreshold = SystemConfig.lagThreshold;
        List<Callable<Boolean>> callables = new ArrayList<Callable<Boolean>>();
        for(Map<String,String> cMap : clusterList){
            final String zkhost = cMap.get("zkhost"),cname = cMap.get("cname");
            if(StringUtils.isBlank(zkhost) || StringUtils.isBlank(cname)){
                continue;
            }
            //获取消费者组
            List<String> consumerList = zkMonitorService.getConsumerGroupList(zkhost);
            for(final String consumerName : consumerList){
                callables.add(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        //获取consumer消费偏移信息
                        List<KafkaZkModel.ConsumerTopicOffset> offsets = zkMonitorService.getConsumerOffsets(zkhost,consumerName);
                        if(CollectionUtils.isEmpty(offsets)){
                            return false;
                        }
                        //remove掉子分区的对象
                        removeTopicNameIsNull(offsets);
                        for(KafkaZkModel.ConsumerTopicOffset offset : offsets){
                            String topic_name = offset.getTopic();
                            Long lag = offset.getLag();
                            //lag超过指定阈值的话，报警
                            if(lag > lagThreshold){
                                lagAlarmVos.add(new LagAlarmVo(cname,topic_name,consumerName,lag,lagThreshold));
                            }
                        }
                        return true;
                    }
                });
            }
        }

        try{
            ExecutorUtil.getPool().invokeAll(callables);
        } catch (InterruptedException e) {
            logger.error("getMonitorData error: " + e);
        }
        //发送消息
        sendMsg(lagAlarmVos);
    }

    /**
     * 发送消息
     * @param lagAlarmVos
     */
    public void sendMsg(List<LagAlarmVo> lagAlarmVos){
        if(CollectionUtils.isEmpty(lagAlarmVos)){
            return;
        }
        String title = AlarmTypeNum.CONSUMER_LAG.getTitle();
        StringBuilder alarmStrSb = new StringBuilder();
        for(LagAlarmVo lagAlarmVo : lagAlarmVos){
            String alarmStr = AlarmTypeNum.CONSUMER_LAG.getAlarmStr();
            //#zkhost机房，Topic：#topicName，Consumer Group：#consumerGrp，Lag消费延迟：#lagNum，超过指定阈值：#thresholdNum
            alarmStr = alarmStr.replace("#zkhost",lagAlarmVo.getZkhost());
            alarmStr = alarmStr.replace("#topicName",lagAlarmVo.getTopicName());
            alarmStr = alarmStr.replace("#consumerGrp",lagAlarmVo.getConsumerGrp());
            alarmStr = alarmStr.replace("#lagNum",String.valueOf(lagAlarmVo.getLagNum()));
            alarmStr = alarmStr.replace("#thresholdNum",String.valueOf(lagAlarmVo.getThresholdNum()));
            alarmStrSb.append(alarmStr);
            alarmStrSb.append("<br/>");
        }
        AlarmQueue.getAlarmQueue().put(new AlarmVo(AlarmTypeNum.CONSUMER_LAG.getValue(),title,alarmStrSb.toString()));
    }

    /**
     * remove掉topic_name等于空的，即获取统计lag值的对象
     * @param offsets
     */
    private void removeTopicNameIsNull(List<KafkaZkModel.ConsumerTopicOffset> offsets){
        if(CollectionUtils.isEmpty(offsets)){
            return;
        }
        Iterator<KafkaZkModel.ConsumerTopicOffset> offsetIterator = offsets.iterator();
        while(offsetIterator.hasNext()){
            KafkaZkModel.ConsumerTopicOffset topicOffset = offsetIterator.next();
            if(topicOffset.getTopic().equals("")){
                offsetIterator.remove();
            }
        }
    }

    static class LagAlarmVo {
        private String zkhost;
        private String topicName;
        private String consumerGrp;
        private Long lagNum;
        private Long thresholdNum;

        public LagAlarmVo(String zkhost, String topicName, String consumerGrp, Long lagNum, Long thresholdNum) {
            this.zkhost = zkhost;
            this.topicName = topicName;
            this.consumerGrp = consumerGrp;
            this.lagNum = lagNum;
            this.thresholdNum = thresholdNum;
        }

        public String getZkhost() {
            return zkhost;
        }

        public void setZkhost(String zkhost) {
            this.zkhost = zkhost;
        }

        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }

        public String getConsumerGrp() {
            return consumerGrp;
        }

        public void setConsumerGrp(String consumerGrp) {
            this.consumerGrp = consumerGrp;
        }

        public Long getLagNum() {
            return lagNum;
        }

        public void setLagNum(Long lagNum) {
            this.lagNum = lagNum;
        }

        public Long getThresholdNum() {
            return thresholdNum;
        }

        public void setThresholdNum(Long thresholdNum) {
            this.thresholdNum = thresholdNum;
        }
    }

}


