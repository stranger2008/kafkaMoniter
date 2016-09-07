package com.jd.si.kafkaMonitor.service;

import com.google.gson.Gson;
import com.jd.si.kafkaMonitor.model.BrokerStateEnum;
import com.jd.si.kafkaMonitor.common.KafkaHelper;
import com.jd.si.kafkaMonitor.common.ZkClientFactory;
import com.jd.si.kafkaMonitor.jmx.ExecutorUtil;
import com.jd.si.kafkaMonitor.model.KafkaZkModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * kafka zookeeper数据监控
 * Created by lilianglin on 2016/8/19.
 */
@Service
public class ZkMonitorService {

    private static final Log logger = LogFactory.getLog(ZkMonitorService.class);

    public static void main(String[] args) throws Exception {
        String zk = "172.20.113.123:2181,172.20.113.124:2181,172.20.113.125:2181,172.20.113.126:2181,172.20.113.127:2181";
        System.out.println(new ZkMonitorService().getConsumerOffsets(zk,"lf_wx_click_groupby_pintype_worker"));
    }

    //broker list ids path
    private static final String BROKER_IDS_PATH = "/brokers/ids";
    //broker topics path
    private static final String BROKER_TOPICS_PATH = "/brokers/topics";
    //broker consumer path
    private static final String BROKER_CONSUMER_PATH = "/consumers";

    @Autowired
    JmxConfigService jmxConfigService;

    /**
     * 获取kafka zk集群下的broker list
     * 通过数据库里的jmxconfig list和zk上面存活的broker host判断是否存活
     * @param zk
     * @return
     */
    public List<KafkaZkModel.Broker> getBrokerList(String zk){
        if(StringUtils.isBlank(zk)){
            return Collections.EMPTY_LIST;
        }
        //获取jmx_config中的broker list
        List<Map<String,String>> jmxconfigs = jmxConfigService.getList(null);
        Set<String> jmxBrokers = new HashSet<String>();
        if(jmxconfigs != null && jmxconfigs.size() > 0){
            for(Map<String,String> map : jmxconfigs){
                String host;
                if(map.get("host") != null){
                    host = map.get("host").toString();
                    jmxBrokers.add(host);
                }
            }
        }
        //开始从zk获取存活的broker
        List<KafkaZkModel.Broker> brokers = getZkBrokers(zk);
        //融合db和zk上的host
        mergeJmxConfigsZk(jmxBrokers,brokers);
        Collections.sort(brokers);
        return brokers;
    }

    /**
     * 获取zk上的brokers
     * @param zk
     * @return
     */
    public List<KafkaZkModel.Broker> getZkBrokers(String zk){
        List<KafkaZkModel.Broker> brokers = null;
        //开始从zk获取存活的broker
        CuratorFramework client = ZkClientFactory.newClient(zk);
        try {
            client.start();
            List<String> brokerIds = client.getChildren().forPath(BROKER_IDS_PATH);
            if(CollectionUtils.isEmpty(brokerIds)){
                return null;
            }
            brokers = new ArrayList<KafkaZkModel.Broker>(brokerIds.size());
            for(String bid : brokerIds){
                byte[] bytes = client.getData().forPath(BROKER_IDS_PATH + "/" + bid);
                String brokerJson = new String(bytes);
                KafkaZkModel.Broker broker = new Gson().fromJson(brokerJson,KafkaZkModel.Broker.class);
                broker.setId(Integer.parseInt(bid));
                broker.setTimestamp(getDateFormat(broker.getTimestamp()));
                brokers.add(broker);
            }
            return brokers;
        } catch (Exception e) {
            logger.error("get zookeeper broker list error",e);
        }finally {
            CloseableUtils.closeQuietly(client);
        }
        return brokers;
    }

    /**
     * 融合db和zk上的host
     * @param jmxBrokers
     * @param brokers
     */
    private void mergeJmxConfigsZk(Set<String> jmxBrokers,List<KafkaZkModel.Broker> brokers){
        if(CollectionUtils.isEmpty(jmxBrokers) || CollectionUtils.isEmpty(brokers)){
            return;
        }
        for(KafkaZkModel.Broker broker : brokers){
            if(jmxBrokers.contains(broker.getHost())){
                //zk有，db有
                broker.setState(BrokerStateEnum.ALIVE.getValue());
            }else{
                //zk有，db没有
                broker.setState(BrokerStateEnum.UNCONTROL.getValue());
            }
        }
        //zk没有，db有，不存活挂了
        for(String jmxHost : jmxBrokers){
            boolean find = false;
            for(KafkaZkModel.Broker broker : brokers){
                if(broker.getHost().equals(jmxHost)){
                    find = true;
                }
            }
            if(!find){
                KafkaZkModel.Broker broker = new KafkaZkModel.Broker();
                broker.setId(-1);
                broker.setHost(jmxHost);
                broker.setState(BrokerStateEnum.DEAD.getValue());
                brokers.add(broker);
            }
        }
    }

    /**
     * 获取zk集群的topic list
     * @param zk
     * @return
     */
    public List<KafkaZkModel.TopicModel> getTopicList(String zk){
        if(StringUtils.isBlank(zk)){
            return Collections.EMPTY_LIST;
        }
        CuratorFramework client = ZkClientFactory.newClient(zk);
        try {
            client.start();
            List<String> topics = client.getChildren().forPath(BROKER_TOPICS_PATH);
            if(CollectionUtils.isEmpty(topics)){
                return null;
            }
            Map<String,List<String>> topic2ConsumerMap = topicNameToConsumerPathMap(client);
            List<KafkaZkModel.TopicModel> topicList = new ArrayList<KafkaZkModel.TopicModel>(topics.size());
            for(String topic_name : topics){
                KafkaZkModel.TopicModel topic = new KafkaZkModel.TopicModel();
                topic.setTopic(topic_name);
                //获取分区数
                List<String> topic_partitions = client.getChildren().forPath(BROKER_TOPICS_PATH  + "/" + topic_name + "/partitions");
                if(topic_partitions != null && topic_partitions.size() > 0){
                    topic.setPartitions(topic_partitions.size());
                }
                //获取topic总副本数
                byte[] bytes = client.getData().forPath(BROKER_TOPICS_PATH + "/" + topic_name);
                String topicJson = new String(bytes);
                KafkaZkModel.Topic _topic = new Gson().fromJson(topicJson,KafkaZkModel.Topic.class);
                topic.setReplicas(getReplicas(_topic));

                //设置consumer消费者
                topic.setConsumers(topic2ConsumerMap.get(topic_name));

                //设置topic的sum of partition offsets
//                if(topic2ConsumerMap.get(topic_name) != null){
//                    List<String> consumer_grps = topic2ConsumerMap.get(topic_name);
//                    for(String consumer_grp : consumer_grps){
//                        List<String> offset_tpc_childs = client.getChildren().forPath(consumer_grp);
//                        if(offset_tpc_childs != null && offset_tpc_childs.size() > 0){
//                            for(String offset_tpc_partition : offset_tpc_childs){
//                                byte[] offset_tpc_partition_bytes = client.getData().forPath(consumer_grp + "/" + offset_tpc_partition);
//                                Integer __offset = Integer.parseInt(new String(offset_tpc_partition_bytes));
//                                System.out.println(__offset);
//                            }
//                        }
//                    }
//                }

                topicList.add(topic);
            }
            Collections.sort(topicList);
            return topicList;
        } catch (Exception e) {
            logger.error("get zookeeper broker list error",e);
        }finally {
            CloseableUtils.closeQuietly(client);
        }
        return null;
    }

    /**
     * 创建topic名称和消费者组的对应关系
     * @return
     */
    public Map<String,List<String>> topicNameToConsumerPathMap(final CuratorFramework client) throws Exception {
        final Map<String,List<String>> topicMap = new HashMap<String, List<String>>();
        List<String> consumers_grps = client.getChildren().forPath(BROKER_CONSUMER_PATH);
        List<Callable<Boolean>> callables = new ArrayList<Callable<Boolean>>();
        if(consumers_grps != null && consumers_grps.size() > 0){
            for(final String consumer_grp_name : consumers_grps) {
                callables.add(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        String consumer_group_offsets = BROKER_CONSUMER_PATH + "/" + consumer_grp_name + "/offsets";
                        Stat stat = client.checkExists().forPath(consumer_group_offsets);
                        if (stat == null) {
                            return true;
                        }
                        List<String> offsets_topic = client.getChildren().forPath(consumer_group_offsets);
                        if(offsets_topic != null && offsets_topic.size() > 0){
                            for(String offset_tpc : offsets_topic){
                                List<String> consumersPath;
                                if(topicMap.get(offset_tpc) == null){
                                    consumersPath = new ArrayList<String>();
                                    topicMap.put(offset_tpc,consumersPath);
                                }else{
                                    consumersPath = topicMap.get(offset_tpc);
                                }
                                consumersPath.add(consumer_grp_name);
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
        return topicMap;
    }

    /**
     * 获取zk所有的消费组
     * @param zk
     * @return
     */
    public List<String> getConsumerGroupList(String zk){
        if(StringUtils.isBlank(zk)){
            return Collections.EMPTY_LIST;
        }
        CuratorFramework client = ZkClientFactory.newClient(zk);
        try {
            client.start();
            List<String> consumers = client.getChildren().forPath(BROKER_CONSUMER_PATH);
            Collections.sort(consumers);
            return consumers;
        } catch (Exception e) {
            logger.error("get zookeeper consumers list error",e);
        }finally {
            CloseableUtils.closeQuietly(client);
        }
        return null;
    }

    /**
     * 根据zk和consumer组名获取logsize、offset、lag
     * logsize：根据topic、partition直接从kafka服务获取
     * offset：直接从zookeeper /consumer/[consumer_grp]/offsets获取
     * lag：logsize - offset
     * @param zk
     * @param consumer
     * @return
     */
    public List<KafkaZkModel.ConsumerTopicOffset> getConsumerOffsets(String zk,String consumer){
        if(StringUtils.isBlank(zk) || StringUtils.isBlank(consumer)){
            return null;
        }
        List<KafkaZkModel.ConsumerTopicOffset> finalTopicOffsets = new ArrayList<KafkaZkModel.ConsumerTopicOffset>();
        CuratorFramework client = ZkClientFactory.newClient(zk);
        try {
            client.start();
            //获取broker list的第一个
            List<KafkaZkModel.Broker> brokers = getZkBrokers(zk);
            String host;
            Integer port;
            if(CollectionUtils.isEmpty(brokers)){
                return null;
            }
            KafkaZkModel.Broker broker = brokers.get(0);
            host = broker.getHost();
            port = Integer.parseInt(broker.getPort());
            //获取消费组下的topic
            String consumer_offsets_path = BROKER_CONSUMER_PATH + "/" + consumer + "/offsets";
            Stat cop_stat = client.checkExists().forPath(consumer_offsets_path);
            if(cop_stat == null){
                return finalTopicOffsets;
            }
            List<String> topics = client.getChildren().forPath(consumer_offsets_path);
            if(CollectionUtils.isEmpty(topics)){
                return null;
            }
            Map<String,List<KafkaZkModel.ConsumerTopicOffset>> _tempMap = new HashMap<String, List<KafkaZkModel.ConsumerTopicOffset>>(topics.size());
            for(String topic_name : topics){
                List<String> topic_partitions = client.getChildren().forPath(BROKER_CONSUMER_PATH + "/" + consumer + "/offsets/" + topic_name);
                if(CollectionUtils.isEmpty(topic_partitions)){
                    continue;
                }
                List<KafkaZkModel.ConsumerTopicOffset> _tempList = new ArrayList<KafkaZkModel.ConsumerTopicOffset>();
                for(String partition : topic_partitions){
                    //获取分区下的offsets
                    byte[] bytes = client.getData().forPath(BROKER_CONSUMER_PATH + "/" + consumer + "/offsets/" + topic_name + "/" + partition);
                    String ofs_str = new String(bytes);
                    Long partition_offset = Long.valueOf(ofs_str);
                    KafkaZkModel.ConsumerTopicOffset consumerTopicOffset = new KafkaZkModel.ConsumerTopicOffset();
                    consumerTopicOffset.setOffset(partition_offset);
                    consumerTopicOffset.setPartition(Integer.parseInt(partition));
                    //获取owner
                    String owner_path = BROKER_CONSUMER_PATH + "/" + consumer + "/owners/" + topic_name + "/" + partition;
                    Stat owner_stat = client.checkExists().forPath(owner_path);
                    if(owner_stat == null){
                        consumerTopicOffset.setOwner("");
                    }else{
                        byte[] owner_bytes = client.getData().forPath(owner_path);
                        String owner = new String(owner_bytes);
                        consumerTopicOffset.setOwner(owner);
                    }
                    //获取logsize
                    Long logsize = KafkaHelper.getLogSize(host, port, topic_name, Integer.parseInt(partition));
                    consumerTopicOffset.setLogsize(logsize);
                    //设置topic名称
                    consumerTopicOffset.setTopic(topic_name);
                    //设置延迟lag
                    Long lag = logsize - partition_offset;
                    consumerTopicOffset.setLag(lag);
                    _tempList.add(consumerTopicOffset);
                }
                _tempMap.put(topic_name,_tempList);
            }
            //构造前台显示的结构
            for(Map.Entry<String,List<KafkaZkModel.ConsumerTopicOffset>> entry : _tempMap.entrySet()){
                String topic_name = entry.getKey();
                List<KafkaZkModel.ConsumerTopicOffset> __ctos = entry.getValue();
                KafkaZkModel.ConsumerTopicOffset consumerTopicOffset = new KafkaZkModel.ConsumerTopicOffset();
                Long _logsize = 0L,_lag = 0L,_offset = 0L;
                Iterator<KafkaZkModel.ConsumerTopicOffset> topicOffsetIterator = __ctos.iterator();
                while(topicOffsetIterator.hasNext()){
                    KafkaZkModel.ConsumerTopicOffset cto = topicOffsetIterator.next();
                    _logsize += cto.getLogsize();
                    _lag += cto.getLag();
                    _offset += cto.getOffset();
                    cto.setTopic("");
                }
                consumerTopicOffset.setLag(_lag);
                consumerTopicOffset.setLogsize(_logsize);
                consumerTopicOffset.setOffset(_offset);
                consumerTopicOffset.setTopic(topic_name);
                finalTopicOffsets.add(consumerTopicOffset);
                finalTopicOffsets.addAll(__ctos);
            }
            return finalTopicOffsets;
        } catch (Exception e) {
            logger.error("get zookeeper consumers list error",e);
        }finally {
            CloseableUtils.closeQuietly(client);
        }
        return null;
    }

    /**
     * 获取topic的broker副本数
     * @param _topic
     * @return
     */
    private Integer getReplicas(KafkaZkModel.Topic _topic){
        if(_topic == null){
            return 0;
        }
        HashMap<String,Integer[]> partitions = _topic.getPartitions();
        if(partitions == null){
            return 0;
        }
        Set<Integer> replicas = new HashSet<Integer>();
        for(Map.Entry<String,Integer[]> map : partitions.entrySet()){
            Integer[] values = map.getValue();
            for(int i=0;i<values.length;i++){
                replicas.add(values[i]);
            }
        }
        return replicas.size();
    }

    /**
     * 数字转字符串日期
     * @param timestamp
     * @return
     */
    private String getDateFormat(String timestamp){
        Date date = new Date(Long.valueOf(timestamp));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        return format.format(c1.getTime());
    }

}
