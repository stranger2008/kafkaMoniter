package com.jd.si.jupiter.tools.machinewatcher;

import com.jd.si.jupiter.monitor.machine.thrift.TMonitorBean;
import com.jd.si.jupiter.monitor.machine.thrift.WatcherService;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * 机器性能数据获取client
 * Created by lilianglin on 2016/8/30.
 */
public class MachineWatcherClient {

    public static final int SERVER_PORT = 6066;
    public static final int TIMEOUT = 2000;

    /**
     * 获取机器性能监控指标
     * @param ip
     * @return
     */
    public static TMonitorBean getMachineInfo(String ip) {
        return getMachineInfo(ip,SERVER_PORT);
    }

    public static TMonitorBean getMachineInfo(String ip,int port) {
        if(StringUtils.isBlank(ip) || port == 0){
            return null;
        }
        TTransport transport = null;
        try {
            transport = new TFramedTransport(new TSocket(ip, SERVER_PORT, TIMEOUT));
            // 协议要和服务端一致
            TProtocol protocol = new TCompactProtocol(transport);
            WatcherService.Client client = new WatcherService.Client(
                    protocol);
            transport.open();
            return client.getMonitorInfo();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            if (null != transport) {
                transport.close();
            }
        }
        return null;
    }



}
