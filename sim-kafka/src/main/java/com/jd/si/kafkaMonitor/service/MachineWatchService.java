package com.jd.si.kafkaMonitor.service;

import com.jd.si.jupiter.monitor.machine.thrift.TMonitorBean;
import com.jd.si.jupiter.tools.machinewatcher.MachineWatcherClient;
import com.jd.si.kafkaMonitor.jmx.ExecutorUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * 机器监控
 * Created by lilianglin on 2016/8/30.
 */
@Service
public class MachineWatchService {

    private static final Log logger = LogFactory.getLog(MachineWatchService.class);

    @Autowired
    JmxConfigService jmxConfigService;

    /**
     * 获取机器本身性能指标数据
     * @return
     */
    public List<TMonitorBean> getMachineMonitor(String cid){
        if(StringUtils.isBlank(cid)){
            return Collections.EMPTY_LIST;
        }
        Map<String,String> paramMap = new HashMap<String, String>(1);
        paramMap.put("cid",cid);
        List<Map<String,String>> jmxConfs = jmxConfigService.getList(paramMap);
        if(CollectionUtils.isEmpty(jmxConfs)){
            return Collections.EMPTY_LIST;
        }
        final List<TMonitorBean> monitorBeanList = new ArrayList<TMonitorBean>(jmxConfs.size());
        List<Callable<Boolean>> callables = new ArrayList<Callable<Boolean>>();
        for(final Map<String,String> map : jmxConfs){
            callables.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    if(map.get("host") != null){
                        String host = map.get("host").toString();
                        TMonitorBean monitorBean = MachineWatcherClient.getMachineInfo(host);
                        monitorBeanList.add(monitorBean);
                    }
                    return true;
                }
            });
        }
        try{
            ExecutorUtil.getPool().invokeAll(callables);
        } catch (InterruptedException e) {
            logger.error("getMachineMonitor error: " + e);
        }
        return monitorBeanList;
    }

    public static void main(String[] args) {
        TMonitorBean monitorBean = MachineWatcherClient.getMachineInfo("192.168.157.131");
        System.out.println(monitorBean);
    }

}
