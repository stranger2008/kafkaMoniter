package com.jd.si.jupiter.tools.machinewatcher.util;

import com.jd.si.jupiter.monitor.machine.thrift.TNetUsageBean;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by zhangyun6 on 2014/10/9.
 */
public class NetUsage {
    private static Logger log = Logger.getLogger(NetUsage.class);
    private static NetUsage INSTANCE = new NetUsage();
    private final static double TotalBandwidth = 10000;   //网口带宽,Mbps

    private NetUsage(){

    }

    public static NetUsage getInstance(){
        return INSTANCE;
    }

    /**
     * @Purpose:采集网络带宽使用率
     * @param
     * @return float,网络带宽使用率,小于1
     */
    public List<TNetUsageBean> get() throws Exception {
        //log.info("开始收集网络带宽使用率");
        List<TNetUsageBean> list = new ArrayList<TNetUsageBean>();
        Process pro1=null,pro2=null;
        BufferedReader in1 = null;
        BufferedReader in2 = null;
        Runtime r = Runtime.getRuntime();

        try {
            String command = "cat /proc/net/dev";
            //第一次采集流量数据
            long startTime = System.currentTimeMillis();
            pro1 = r.exec(command);
            in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
            String line = null;
            long inSize1 = 0, outSize1 = 0;
            int count1 =  0;

            HashMap<String,TNetUsageBean> map = new HashMap<String, TNetUsageBean>();
            while((line=in1.readLine()) != null){
                if(++count1 >= 3){
                    line = line.trim();
                    String[] deviceName_value = line.split(":");
                    //System.out.println("deviceName:" + deviceName_value[0]);
                    //System.out.println("deviceValue:" + deviceName_value[1]);
                    String deviceName = deviceName_value[0];
                    String[] value = deviceName_value[1].trim().split("\\s+");
                    //System.out.println("value:" + value[0]);
                    long inSize = Long.parseLong(value[0].trim());
                    long outSize = Long.parseLong(value[8].trim());
                    String commandSpeed = "ethtool " + deviceName + " | grep Speed | awk '{split($0,b,\":\");print b[2]}'";
                    Process pro  = r.exec(new String[] {"/bin/sh", "-c", commandSpeed});
                    BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
                    String str = "";
                    long speed = 0;
                    if((str = in.readLine()) != null){
                        String s = str.replace("Mb/s","").trim();
                        speed = Integer.parseInt(s);
                    }
                    TNetUsageBean netUsageBean = new TNetUsageBean();
                    netUsageBean.setDeviceName(deviceName);
                    netUsageBean.setReceive(inSize);
                    netUsageBean.setTransmit(outSize);
                    netUsageBean.setDeviceSpeed(speed);
                    map.put(deviceName,netUsageBean);
                    in.close();
                    pro.destroy();
                }
            }

            Thread.sleep(1000);

            //第二次采集流量数据
            long endTime = System.currentTimeMillis();
            pro2 = r.exec(command);
            in2 = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
            long inSize2 = 0 ,outSize2 = 0;
            int count2=0;
            while((line=in2.readLine()) != null){
                if(++count2 >= 3){
                    line = line.trim();
                    String[] deviceName_value = line.split(":");
                    String deviceName = deviceName_value[0];
                    String[] value = deviceName_value[1].trim().split("\\s+");
                    long inSize = Long.parseLong(value[0].trim());
                    long outSize = Long.parseLong(value[8].trim());

                    TNetUsageBean netUsageBean = map.get(deviceName);
                    double interval = (double)(endTime - startTime)/1000;//ms转换成s
                    //网口传输速度,单位为bps(bit/s)
                    if(netUsageBean.getDeviceSpeed() > 0){
                        double inRate = (double)(inSize - netUsageBean.getReceive())*8/(1024*1024*interval);
                        netUsageBean.setReceivePS(inRate);
                        //System.out.println("receive(Mbps): " + inRate);
                        netUsageBean.setReceiveUsage(inRate/netUsageBean.getDeviceSpeed());
                        double outRate = (double)(outSize - netUsageBean.getTransmit())*8/(1024*1024*interval);
                        netUsageBean.setTransmitPS(outRate);
                        //System.out.println("transmit(Mbps): " + outRate);
                        netUsageBean.setTransmitUsage(outRate/netUsageBean.getDeviceSpeed());
                    }
                }
            }

            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, TNetUsageBean> entry = (Map.Entry<String, TNetUsageBean>) iter.next();
                list.add(entry.getValue());
            }
        } catch (Exception e) {
            log.error("NetUsage发生Exception. " + e.getMessage());
            System.err.println("NetUsage发生Exception.");
            e.printStackTrace();
        }finally {
            if(in1!=null){
                in1.close();
            }
            if(in2!=null){
                in2.close();
            }
            if(pro1!=null){
                pro1.destroy();
            }
            if(pro2!=null){
                pro2.destroy();
            }
        }
        //netUsage = (float)(Math.round(netUsage*1000000))/1000000;
        return list;
    }



}
