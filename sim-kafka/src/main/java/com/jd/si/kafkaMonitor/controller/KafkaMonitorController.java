package com.jd.si.kafkaMonitor.controller;

import com.google.gson.Gson;
import com.jd.si.kafkaMonitor.common.KafkaHelper;
import com.jd.si.kafkaMonitor.model.Cluster;
import com.jd.si.kafkaMonitor.model.KafkaZkModel;
import com.jd.si.kafkaMonitor.model.ShowMonitor;
import com.jd.si.kafkaMonitor.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * behavior服务监控
 * Created by lilianglin on 2015/12/24.
 */

@Controller
public class KafkaMonitorController {

    @Autowired
    JmxConfigService jmxConfigService;
    @Autowired
    MonitorDataService monitorDataService;
    @Autowired
    ClusterService clusterService;
    @Autowired
    ZkMonitorService zkMonitorService;
    @Autowired
    MachineWatchService machineWatchService;
    Gson gson = new Gson();

    @RequestMapping("/serverList")
    public @ResponseBody String getJmxConfig(){
        List<Map<String,String>> list = jmxConfigService.getList(null);
        Gson gson = new Gson();
        return getStringUtf8(gson.toJson(list));
    }

    @RequestMapping("/showMonitor")
    public @ResponseBody String showMonitorData(String pid,String dateType,String dataType,String searchTime){
        Map<String,ShowMonitor> map = monitorDataService.getMonitorData(pid,dateType,dataType,searchTime);
        return getStringUtf8(gson.toJson(map));
    }

    @RequestMapping("/showClusterMonitor")
    public @ResponseBody String showClusterMonitor(String clusterId,String monitorKey,String dateType,String dataType,String searchTime){
        Map<String,ShowMonitor> map = monitorDataService.getMonitorData(clusterId,monitorKey,dateType,dataType,searchTime);
        return getStringUtf8(gson.toJson(map));
    }

    @RequestMapping("/delServer")
    public @ResponseBody String deleteServer(int id){
        boolean flag = jmxConfigService.delete(id);
        return String.valueOf(flag);
    }

    @RequestMapping("/addServerInfo")
    public @ResponseBody String addServerInfo(String jmxConf,String cid){
        boolean ret = jmxConfigService.insert(jmxConf,cid);
        return String.valueOf(ret);
    }

    @RequestMapping("/addCluster")
    public @ResponseBody String addCluster(String cname,String zkhost,String cdesc){
        Cluster cluster = new Cluster();
        cluster.setCname(cname);
        cluster.setZkhost(zkhost);
        cluster.setCdesc(cdesc);
        boolean ret = clusterService.insert(cluster);
        return String.valueOf(ret);
    }

    @RequestMapping("/clusterList")
    public @ResponseBody String clusterList(){
        List<Map<String,String>> list = clusterService.getList(null);
        return getStringUtf8(gson.toJson(list));
    }

    @RequestMapping("/delCluster")
    public @ResponseBody String deleteCluster(int id){
        boolean flag = clusterService.delete(id);
        return String.valueOf(flag);
    }

    @RequestMapping("/getMonitorAttr")
     public @ResponseBody String getMonitorAttr(){
        return getStringUtf8(gson.toJson(clusterService.getMoniterAttr()));
    }

    @RequestMapping("/getBrokerList")
      public @ResponseBody String getBrokerList(String zk){
        return getStringUtf8(gson.toJson(zkMonitorService.getBrokerList(zk)));
    }

    @RequestMapping("/getTopicList")
    public @ResponseBody String getTopicList(String zk){
        return getStringUtf8(gson.toJson(zkMonitorService.getTopicList(zk)));
    }

    @RequestMapping("/getConsumerList")
    public @ResponseBody String getConsumerList(String zk){
        return getStringUtf8(gson.toJson(zkMonitorService.getConsumerGroupList(zk)));
    }

    @RequestMapping("/getConsumerDetail")
    public @ResponseBody String getConsumerDetail(String zk,String consumer_grp){
        return getStringUtf8(gson.toJson(zkMonitorService.getConsumerOffsets(zk, consumer_grp)));
    }

    @RequestMapping("/createTopic")
    public @ResponseBody String createTopic(String zk,String topic_name,String partitions,String replicas){
        return String.valueOf(KafkaHelper.createTopic(zk, topic_name, partitions, replicas));
    }

    @RequestMapping("/deleteTopic")
    public @ResponseBody String deleteTopic(String zk,String topic_name){
        return String.valueOf(KafkaHelper.deleteTopic(zk, topic_name));
    }

    @RequestMapping("/addPartitions")
    public @ResponseBody String addPartitions(String zk,String topic_name,int numPartitions){
        return String.valueOf(KafkaHelper.addPartitions(zk, topic_name, numPartitions));
    }

    @RequestMapping("/getMachineInfo")
    public @ResponseBody String getMachineInfo(String cid){
        return String.valueOf(gson.toJson(machineWatchService.getMachineMonitor(cid)));
    }

    @RequestMapping("/getCluster")
    public @ResponseBody String getCluster(int id){
        return getStringUtf8(gson.toJson(clusterService.get(id)));
    }

    public String getStringUtf8(String str){
        try {
            return new String(str.getBytes("utf-8"),"iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

}
