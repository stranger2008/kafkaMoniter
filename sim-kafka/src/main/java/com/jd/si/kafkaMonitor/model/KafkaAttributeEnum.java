package com.jd.si.kafkaMonitor.model;

/**
 * Created by lilianglin on 2016/8/3.
 */
public enum KafkaAttributeEnum{
    MESSAGE_IN_PER_SEC("mips","kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec","MeanRate,OneMinuteRate,FiveMinuteRate,FifteenMinuteRate","所有topic的写入消息速率","消息数/秒"),
    BYTES_IN_PER_SEC("bips","kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec","MeanRate,OneMinuteRate,FiveMinuteRate,FifteenMinuteRate","所有topic的流入数据速率","字节/秒"),
    BYTES_OUT_PER_SEC("bops","kafka.server:type=BrokerTopicMetrics,name=BytesOutPerSec","MeanRate,OneMinuteRate,FiveMinuteRate,FifteenMinuteRate","所有topic的流出数据速率","字节/秒"),
    PRODUCE_REQUEST_PER_SEC("prps","kafka.network:type=RequestMetrics,name=RequestsPerSec,request=Produce","MeanRate,OneMinuteRate,FiveMinuteRate,FifteenMinuteRate","producer的请求速率","请求次数/秒"),
    CONSUMER_REQUEST_PER_SEC("crps","kafka.network:type=RequestMetrics,name=RequestsPerSec,request=FetchConsumer","MeanRate,OneMinuteRate,FiveMinuteRate,FifteenMinuteRate","Fetch-Consumer的请求速率","请求次数/秒"),
    FLOWER_REQUEST_PER_SEC("frps","kafka.network:type=RequestMetrics,name=RequestsPerSec,request=FetchFollower","MeanRate,OneMinuteRate,FiveMinuteRate,FifteenMinuteRate","Fetch-Follower的请求速率","请求次数/秒");

    private String key;
    private String metricName;
    private String attrName;
    private String desc;
    private String unit;

    KafkaAttributeEnum(String key,String metricName,String attrName,String desc,String unit) {
        this.key = key;
        this.desc = desc;
        this.metricName = metricName;
        this.attrName = attrName;
        this.unit = unit;
    }

    public static String getAttr(String attrName,String key){
        KafkaAttributeEnum[] kafkaAttributeEnums = KafkaAttributeEnum.values();
        for(int i=0;i<kafkaAttributeEnums.length;i++){
            if(kafkaAttributeEnums[i].getKey().equals(key)){
                if(attrName.equals("desc")){
                    return kafkaAttributeEnums[i].getDesc();
                }
                if(attrName.equals("unit")){
                    return kafkaAttributeEnums[i].getUnit();
                }
                if(attrName.equals("attrName")){
                    return kafkaAttributeEnums[i].getAttrName();
                }
            }
        }
        return "";
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
