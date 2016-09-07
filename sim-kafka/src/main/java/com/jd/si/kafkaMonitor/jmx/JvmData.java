package com.jd.si.kafkaMonitor.jmx;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.lang.management.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.jd.si.kafkaMonitor.model.DataTypeEnum;
import com.jd.si.kafkaMonitor.model.JVMAttributeEnum;
import com.sun.management.OperatingSystemMXBean;

/**
 * 获取JVM的jmx数据
 * Created by lilianglin on 2016/8/3.
 */
public class JvmData implements MonitorDataInterface {

    @Override
    public String getJsonData(MBeanServerConnection mbs){
        //获取远程memorymxbean
        MemoryMXBean memBean = null;
        //获取远程opretingsystemmxbean
        OperatingSystemMXBean opMXbean = null;
        //线程数
        ThreadMXBean threadMXBean = null;
        //类加载数量
        ClassLoadingMXBean classLoadingMXBean = null;
        try {
            memBean= ManagementFactory.newPlatformMXBeanProxy(mbs, ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
            opMXbean = ManagementFactory.newPlatformMXBeanProxy(mbs, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
            threadMXBean = ManagementFactory.newPlatformMXBeanProxy(mbs, ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
            classLoadingMXBean = ManagementFactory.newPlatformMXBeanProxy(mbs, ManagementFactory.CLASS_LOADING_MXBEAN_NAME, ClassLoadingMXBean.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //cpu使用率
        double cpuUsage = getCpuUsage(opMXbean);
        //当前线程数
        int threadCount = threadMXBean.getThreadCount();
        //当前加载类数量
        int loadClassCount = classLoadingMXBean.getLoadedClassCount();
        MemoryUsage heap = memBean.getHeapMemoryUsage();
        //堆使用的大小，单位MB
        long heapSizeUsed = heap.getUsed() / 1024 / 1024;
        //封装成map
        Map<String,String> map = new HashMap<String, String>();
        map.put(JVMAttributeEnum.JVM_HEAPSIZEUSED.getKey(),String.valueOf(heapSizeUsed));
        map.put(JVMAttributeEnum.JVM_THREADCOUNT.getKey(),String.valueOf(threadCount));
        map.put(JVMAttributeEnum.JVM_LOADCLASSCOUNT.getKey(),String.valueOf(loadClassCount));
        map.put(JVMAttributeEnum.JVM_CPUUSAGE.getKey(),String.valueOf(cpuUsage));
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    @Override
    public int getDataType() {
        return DataTypeEnum.JVM.getValue();
    }

    /**
     * 获取cpu使用率
     * @param opMXbean
     * @return
     */
    private double getCpuUsage(OperatingSystemMXBean opMXbean){
        long nanoBefore = System.nanoTime();
        long cpuBefore = opMXbean.getProcessCpuTime();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long cpuAfter = opMXbean.getProcessCpuTime();
        long nanoAfter = System.nanoTime();
        double percent = ((cpuAfter-cpuBefore)*100F)/ (nanoAfter-nanoBefore) / opMXbean.getAvailableProcessors() ;
        double pp = (double)((int)(percent * 100)) / 100;
        return pp;
    }
}
