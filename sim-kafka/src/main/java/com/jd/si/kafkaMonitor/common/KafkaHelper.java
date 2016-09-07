package com.jd.si.kafkaMonitor.common;

import java.util.*;

import kafka.admin.AdminUtils;
import kafka.api.OffsetRequest;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.BrokerEndPoint;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.TopicMetadataResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 获取某个topic、某个partition的logsize
 * Created by lilianglin on 2016/8/23.
 */
public class KafkaHelper {

    private static final Log logger = LogFactory.getLog(KafkaHelper.class);

    public static void main(String[] args) {
//        long i = KafkaHelper.getLogSize("172.20.114.33",9092,"wg_wx.000000",45);
//        System.out.println(i);
//        KafkaHelper.createTopic("172.20.113.123:2181,172.20.113.124:2181,172.20.113.125:2181,172.20.113.126:2181,172.20.113.127:2181","leantest","10","3");
        KafkaHelper.deleteTopic("172.20.113.123:2181,172.20.113.124:2181,172.20.113.125:2181,172.20.113.126:2181,172.20.113.127:2181","leantest22");

    }

    /**
     * 添加分区数
     * @param zkhost
     * @param topic_name
     * @param numPartitions 增加到多少  numPartitions = oldPartitions + newNum
     * @return
     */
    public static boolean addPartitions(String zkhost,String topic_name,int numPartitions){
        try {
            ZkUtils zkUtils = getZkUtils(zkhost);
            AdminUtils.addPartitions(zkUtils, topic_name, numPartitions, "", true, null);
            zkUtils.close();
        } catch (Exception e) {
            logger.error("addPartitions topic error",e);
            return false;
        }
        return true;
    }

    /**
     * 删除topic
     * 根据broker的配置（delete.topic.enable=true/false）来决定删除策略
     * false：在删除topic的时候无法删除，会打上一个你将删除该topic的标记
     * true：集群自动将那些标记删除的topic删除掉，对应的log.dirs目录下的topic目录和数据也会被删除。属性设置为true之后，你就能成功删除你想要删除的topic。
     * @param zkhost
     * @param topic_name
     * @return
     */
    public static boolean deleteTopic(String zkhost,String topic_name){
        try {
            ZkUtils zkUtils = getZkUtils(zkhost);
            AdminUtils.deleteTopic(zkUtils,topic_name);
            zkUtils.close();
        } catch (Exception e) {
            logger.error("delete topic error",e);
            return false;
        }
        return true;
    }

    /**
     * 创建tipic
     * @param zkhost zk地址
     * @param topic_name topic名称
     * @param partitions 分区数
     * @param replicas 副本数
     * @return
     */
    public static boolean createTopic(String zkhost,String topic_name,String partitions,String replicas){
        if(StringUtils.isBlank(zkhost) || StringUtils.isBlank(topic_name) || StringUtils.isBlank(partitions) || StringUtils.isBlank(replicas)){
            return false;
        }
        try {
            ZkUtils zkUtils = getZkUtils(zkhost);
            Properties topicConfig = new Properties();
            AdminUtils.createTopic(zkUtils, topic_name, Integer.parseInt(partitions), Integer.parseInt(replicas), topicConfig, null);
            zkUtils.close();
        } catch (NumberFormatException e) {
           logger.error("create topic error",e);
            return false;
        } catch (ZkInterruptedException e) {
            logger.error("create topic error",e);
            return false;
        }
        return true;
    }

    /**
     * 获取zkUtils对象
     * @param zkhost
     * @return
     */
    private static ZkUtils getZkUtils(String zkhost){
        if(StringUtils.isBlank(zkhost)){
            return null;
        }
        int sessionTimeoutMs = 10 * 1000;
        int connectionTimeoutMs = 8 * 1000;
        // Note: You must initialize the ZkClient with ZKStringSerializer.  If you don't, then
        // createTopic() will only seem to work (it will return without error).  The topic will exist in
        // only ZooKeeper and will be returned when listing topics, but Kafka itself does not create the
        // topic.
        ZkClient zkClient = new ZkClient(zkhost, sessionTimeoutMs, connectionTimeoutMs, ZKStringSerializer$.MODULE$);
        boolean isSecureKafkaCluster = false;
        ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zkhost), isSecureKafkaCluster);
        return zkUtils;
    }

    /**
     * 获取kafka logSize
     * @param host
     * @param port
     * @param topic
     * @param partition
     * @return
     */
    public static long getLogSize(String host,int port,String topic,int partition){
        String clientName = "Client_" + topic + "_" + partition;
        BrokerEndPoint leaderBroker = getLeaderBroker(host, port, topic, partition);
        String reaHost = null;
        if (leaderBroker != null) {
            reaHost = leaderBroker.host();
        }else {
            logger.error("Partition of Host is not find");
            return 0;
        }
        SimpleConsumer simpleConsumer = new SimpleConsumer(reaHost, port, 10000, 64*1024, clientName);
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
        requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(OffsetRequest.LatestTime(), 1));
        kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(requestInfo, OffsetRequest.CurrentVersion(), clientName);
        OffsetResponse response = simpleConsumer.getOffsetsBefore(request);
        if (response.hasError()) {
            System.out.println("Error fetching data Offset , Reason: " + response.errorCode(topic, partition) );
            return 0;
        }
        long[] offsets = response.offsets(topic, partition);
        return offsets[0];
    }

    /**
     * 获取broker ID
     * @param host
     * @param port
     * @param topic
     * @param partition
     * @return
     */
    public static Integer getBrokerId(String host,int port,String topic,int partition){
        BrokerEndPoint leaderBroker = getLeaderBroker(host, port, topic, partition);
        if (leaderBroker != null) {
            return leaderBroker.id();
        }
        return null;
    }
    /**
     * 获取leaderBroker
     * @param host
     * @param port
     * @param topic
     * @param partition
     * @return
     */
    private static BrokerEndPoint getLeaderBroker(String host,int port,String topic,int partition){
        String clientName = "Client_Leader_LookUp";
        SimpleConsumer consumer = null;
        PartitionMetadata partitionMetaData = null;
        try {
            consumer = new SimpleConsumer(host, port, 10000, 64*1024, clientName);
            List<String> topics = new ArrayList<String>();
            topics.add(topic);
            TopicMetadataRequest request = new TopicMetadataRequest(topics);
            TopicMetadataResponse reponse = consumer.send(request);
            List<TopicMetadata> topicMetadataList = reponse.topicsMetadata();
            for(TopicMetadata topicMetadata : topicMetadataList){
                for(PartitionMetadata metadata : topicMetadata.partitionsMetadata()){
                    if (metadata.partitionId() == partition) {
                        partitionMetaData = metadata;
                        break;
                    }
                }
            }
            if (partitionMetaData != null) {
                return partitionMetaData.leader();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
