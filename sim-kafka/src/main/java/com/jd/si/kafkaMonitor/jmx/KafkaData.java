package com.jd.si.kafkaMonitor.jmx;

import com.google.gson.Gson;
import com.jd.si.kafkaMonitor.model.DataTypeEnum;
import com.jd.si.kafkaMonitor.model.KafkaAttributeEnum;

import javax.management.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取kafka jmx数据
 * Created by lilianglin on 2016/8/3.
 */
public class KafkaData implements MonitorDataInterface {

    @Override
    public String getJsonData(MBeanServerConnection mbs) {
        try {
            Gson gson = new Gson();
            KafkaAttributeEnum[] kaes = KafkaAttributeEnum.values();
            int kafkaLen = kaes.length;
            Map<String,String> map = new HashMap<String, String>(kafkaLen);
            for(int i=0;i<kafkaLen;i++){
                ObjectName countObj = new ObjectName(kaes[i].getMetricName());
                String attrName = kaes[i].getAttrName();
                String attr[] = attrName.split(",");
                if(attr.length == 0){
                    continue;
                }
                StringBuilder sb = new StringBuilder();
                for(int j=0;j<attr.length;j++){
                    Object attrValue = mbs.getAttribute(countObj, attr[j]);
                    sb.append(getValueByType(attrValue));
                    if(j != attr.length - 1){
                        sb.append(",");
                    }
                }
                map.put(kaes[i].getKey(),sb.toString());
            }
            return gson.toJson(map);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ReflectionException e) {
            e.printStackTrace();
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据对象的数据类型做处理
     * @param obj
     * @return
     */
    private String getValueByType(Object obj){
        if(obj instanceof Double){
            Double val = (Double)obj;
            return Double.toString(val);
        }
        return "";
    }

    @Override
    public int getDataType() {
        return DataTypeEnum.KAFKA.getValue();
    }

}
