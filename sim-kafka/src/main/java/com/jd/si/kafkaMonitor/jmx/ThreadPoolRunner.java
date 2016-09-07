package com.jd.si.kafkaMonitor.jmx;

import com.jd.si.kafkaMonitor.common.ApplicationContextUtil;
import com.jd.si.kafkaMonitor.model.DateTypeEnum;
import com.jd.si.kafkaMonitor.model.MbConnVo;
import com.jd.si.kafkaMonitor.model.MonitorData;
import com.jd.si.kafkaMonitor.service.JmxConfigService;
import com.jd.si.kafkaMonitor.service.MonitorDataService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import javax.management.MBeanServerConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 多线程jmx抓取监控数据
 * Created by lilianglin on 2016/8/3.
 */
public class ThreadPoolRunner {

    private static final Log logger = LogFactory.getLog(ThreadPoolRunner.class);

    private static List<MonitorDataInterface> monitorDataInterfaces;
    private static ApplicationContext ac;
    private static JmxConfigService jmxConfigService;
    private static MonitorDataService monitorDataService;

    static {
        ac = ApplicationContextUtil.get();
        jmxConfigService = (JmxConfigService)ac.getBean("jmxConfigService");
        jmxConfigService.setConnections();
        monitorDataService = (MonitorDataService)ac.getBean("monitorDataService");

        monitorDataInterfaces = new ArrayList<MonitorDataInterface>();
        monitorDataInterfaces.add(new JvmData());
        monitorDataInterfaces.add(new KafkaData());
    }

    public static void run(final int dateType){
        List<MbConnVo> mbConnVoList = jmxConfigService.getMbConn();
        if(CollectionUtils.isEmpty(mbConnVoList)){
            logger.error("jmx连接配置为空");
            return;
        }
        List<Callable<Boolean>> callables = new ArrayList<Callable<Boolean>>(mbConnVoList.size());
        for(final MbConnVo mbConnVo : mbConnVoList){
            callables.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return insertDb(mbConnVo,dateType);
                }
            });
        }
        try {
            ExecutorUtil.getPool().invokeAll(callables);
        } catch (InterruptedException e) {
            logger.error("pool.invokeAll error",e);
        }
    }

    public static Boolean insertDb(MbConnVo mbConnVo,int dateType){
        String id = mbConnVo.getId();
        MBeanServerConnection connection = mbConnVo.getConnection();
        for(MonitorDataInterface monitorDataInterface : monitorDataInterfaces){
            String jsonData = monitorDataInterface.getJsonData(connection);
            int dataType = monitorDataInterface.getDataType();
            MonitorData monitorData = new MonitorData();
            monitorData.setPid(Integer.parseInt(id));
            monitorData.setDataType(dataType);
            monitorData.setJsonData(jsonData);
            monitorData.setDateType(dateType);
            logger.info(monitorData);
            monitorDataService.insert(monitorData);
        }
        return true;
    }

    public static void main(String[] args) {
        ThreadPoolRunner.run(DateTypeEnum.MINUTES.getValue());
    }

}
