package com.jd.si.kafkaMonitor.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * kafka zookeeper model
 * Created by lilianglin on 2016/8/19.
 */
public class KafkaZkModel {

    //broker model
    public static class Broker implements Comparable<Broker>{
        private Integer id;
        private String host;
        private String port;
        private String version;
        private String jmx_port;
        private String timestamp;
        private Integer state;

        public Broker() {
        }

        public Broker(Integer id, String host, String port, String version, String jmx_port, String timestamp, Integer state) {
            this.id = id;
            this.host = host;
            this.port = port;
            this.version = version;
            this.jmx_port = jmx_port;
            this.timestamp = timestamp;
            this.state = state;
        }

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getJmx_port() {
            return jmx_port;
        }

        public void setJmx_port(String jmx_port) {
            this.jmx_port = jmx_port;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "broker{" +
                    "id=" + id +
                    ", host='" + host + '\'' +
                    ", state='" + state + '\'' +
                    ", port='" + port + '\'' +
                    ", version='" + version + '\'' +
                    ", jmx_port='" + jmx_port + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }

        @Override
        public int compareTo(Broker o) {
            if(o.getId() == null || this.getId() == null){
                return 0;
            }
            return this.getId().compareTo(o.getId());
        }
    }

    //topic model view
    public static class TopicModel implements Comparable<TopicModel>{

        private String topic;
        private Integer partitions;
        private List<String> consumers;
        private Integer replicas;

        public TopicModel() {
        }

        public TopicModel(String topic, Integer partitions, List<String> consumers, Integer replicas) {
            this.topic = topic;
            this.partitions = partitions;
            this.consumers = consumers;
            this.replicas = replicas;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public Integer getPartitions() {
            return partitions;
        }

        public void setPartitions(Integer partitions) {
            this.partitions = partitions;
        }

        public List<String> getConsumers() {
            return consumers;
        }

        public void setConsumers(List<String> consumers) {
            this.consumers = consumers;
        }

        public Integer getReplicas() {
            return replicas;
        }

        public void setReplicas(Integer replicas) {
            this.replicas = replicas;
        }

        @Override
        public int compareTo(TopicModel o) {
            Integer a = o.getConsumers()!=null?o.getConsumers().size() : 0;
            Integer b = this.getConsumers()!=null?this.getConsumers().size() : 0;
            int i = a.compareTo(b);
            if(i == 0){
                return this.getTopic().compareTo(o.getTopic());
            }
            return i;
        }

        @Override
        public String toString() {
            return "Topic{" +
                    "topic='" + topic + '\'' +
                    ", partitions='" + partitions + '\'' +
                    ", consumers=" + consumers +
                    ", replicas=" + replicas +
                    '}';
        }
    }

    //zookeeper上 brokers/topics/[topic]的json格式
    public static class Topic {

        private Integer version;
        private HashMap<String,Integer[]> partitions;

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public HashMap<String,Integer[]> getPartitions() {
            return partitions;
        }

        public void setPartitions(HashMap<String,Integer[]> partitions) {
            this.partitions = partitions;
        }

        @Override
        public String toString() {
            return "Topic{" +
                    "version=" + version +
                    ", partitions=" + partitions +
                    '}';
        }
    }

    //consumer topic对应的offset
    public static class ConsumerTopicOffset {
        private String topic;
        private Integer partition;
        private Long logsize;
        private Long offset;
        private Long lag;
        private String owner;

        public ConsumerTopicOffset() {
        }

        public ConsumerTopicOffset(String topic, Integer partition, Long logsize, Long offset, Long lag, String owner) {
            this.topic = topic;
            this.partition = partition;
            this.logsize = logsize;
            this.offset = offset;
            this.lag = lag;
            this.owner = owner;
        }

        public Integer getPartition() {
            return partition;
        }

        public void setPartition(Integer partition) {
            this.partition = partition;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public Long getLogsize() {
            return logsize;
        }

        public void setLogsize(Long logsize) {
            this.logsize = logsize;
        }

        public Long getOffset() {
            return offset;
        }

        public void setOffset(Long offset) {
            this.offset = offset;
        }

        public Long getLag() {
            return lag;
        }

        public void setLag(Long lag) {
            this.lag = lag;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        @Override
        public String toString() {
            return "CousumerTopicOffset{" +
                    "topic='" + topic + '\'' +
                    ", partition=" + partition +
                    ", logsize=" + logsize +
                    ", offset=" + offset +
                    ", lag=" + lag +
                    ", owner='" + owner + '\'' +
                    '}';
        }
    }

}
