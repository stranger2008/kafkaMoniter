package com.jd.si.jupiter.tools.machinewatcher.serviceImpl;


import com.jd.si.jupiter.monitor.machine.thrift.TMonitorBean;
import com.jd.si.jupiter.monitor.machine.thrift.TNetUsageBean;
import com.jd.si.jupiter.tools.machinewatcher.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by zhangyun6 on 2014/10/10.
 */
public class MonitorUtil {
    private static final Logger LOG = LoggerFactory.getLogger(MonitorUtil.class);

    public static TMonitorBean getMonitorInfo() throws Exception {
        TMonitorBean monitorBean = new TMonitorBean();
        monitorBean.setCpuUsage(CpuUsage.getInstance().get());
        monitorBean.setMemUsage(MemUsage.getInstance().get().getMemUsage());
        monitorBean.setMemTotal(MemUsage.getInstance().get().getMemTotal());
        monitorBean.setMemUse(MemUsage.getInstance().get().getMemUse());
        monitorBean.setNetUsages(NetUsage.getInstance().get());
        monitorBean.setIoUsage(IoUsage.getInstance().get());
        double[] la = LoadAverage.getInstance().get();
        monitorBean.setLoadAverage1m(la[0]);
        monitorBean.setLoadAverage5m(la[1]);
        monitorBean.setLoadAverage15m(la[2]);
        monitorBean.setEstablishedCount(EstablishedCount.getInstance().get());
        monitorBean.setIp(NetWorkUtil.getLocalHost());
        monitorBean.setRefreshTime(System.currentTimeMillis());
        return monitorBean;
    }

    public static String getAddress() throws SocketException {
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        String address = null;
        while (e.hasMoreElements()) {
            List<InterfaceAddress> lsit = e.nextElement().getInterfaceAddresses();
            for (InterfaceAddress interfaceAddress : lsit) {
                if (interfaceAddress.getAddress() instanceof Inet4Address) {
                    String host = interfaceAddress.getAddress().getHostAddress();
                    if (host.startsWith("192.168") || host.startsWith("10.12")) {
                        address = host;
                    } else if (host.equals("127.0.0.1")) {
                        continue;
                    } else {
                        return host;
                    }
                }
            }
        }
        return address;
    }


    public static void main(String[] args) throws Exception {
        while (true){
            TMonitorBean monitorBean = MonitorUtil.getMonitorInfo();
            printMonitorBean(monitorBean);
            Thread.sleep(5000);
        }
    }


    public static void printMonitorBean(TMonitorBean monitorBean){
        System.out.println("****** ");
        System.out.println("CpuUsage:" + monitorBean.getCpuUsage());
        System.out.println("MemUsage:" + monitorBean.getMemUsage());
        System.out.println("IoUsage:" + monitorBean.getIoUsage());
        //DecimalFormat df=(DecimalFormat) NumberFormat.getInstance();
        //df.setMaximumFractionDigits(6);
        //System.out.println("NetUsage:" + df.format(monitorBean.getNetUsage()));
        printNetUsages(monitorBean.getNetUsages());
        System.out.println("EstablishedCount:" + monitorBean.getEstablishedCount());
        System.out.println("LoadAverage1m:" + monitorBean.getLoadAverage1m());
        System.out.println("LoadAverage5m:" + monitorBean.getLoadAverage5m());
        System.out.println("LoadAverage15m:" + monitorBean.getLoadAverage15m());
        System.out.println("Ip:" + monitorBean.getIp());
        System.out.println("RefreshTime:" + monitorBean.getRefreshTime());
        System.out.println("****** ");
    }


    public static void printNetUsages(List<TNetUsageBean> list){
        try {
            StringBuffer sb = new StringBuffer();
            for(TNetUsageBean netUsageBean: list){
                sb.append("Net Device Name:"+netUsageBean.getDeviceName() + " | ");
                sb.append("Receive:" + netUsageBean.getReceive() + " | ");
                sb.append("Transmit:" + netUsageBean.getTransmit() + " | ");
                sb.append("DeviceSpeed:" + netUsageBean.getDeviceSpeed() + " | ");
                sb.append("ReceiveUsage:" + netUsageBean.getReceiveUsage() + " | ");
                sb.append("TransmitUsage:"+netUsageBean.getTransmitUsage());
                sb.append("\n");
            }
            System.out.println("NetUsages:");
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
