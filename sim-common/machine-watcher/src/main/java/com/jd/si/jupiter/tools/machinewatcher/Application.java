package com.jd.si.jupiter.tools.machinewatcher;

import com.jd.si.jupiter.monitor.machine.thrift.WatcherService;
import com.jd.si.jupiter.tools.machinewatcher.serviceImpl.MachineInitialization;
import com.jd.si.jupiter.tools.machinewatcher.serviceImpl.WatcherServiceImpl;
import com.jd.si.jupiter.tools.machinewatcher.util.SystemConfig;
import com.jd.soa.RPCServer;
import com.jd.soa.RPCServerBuilder;
import com.jd.thriftzookeeper.common.NetUtils;

/**
 * 启动器
 * Created by lilianglin on 2016/8/30.
 */
public class Application {

    public static void main(String[] args) {
        //开始抓取机器性能
        new MachineInitialization().init();
        //启动服务
        startServer();
    }

    /**
     * 启动thrift rpc服务
     */
    private static void startServer(){
        final RPCServer<WatcherService.Iface> server = new RPCServerBuilder<WatcherService.Iface>()
                .setHost(NetUtils.getLocalHost())
                .setPort(SystemConfig.thriftServicePort)
                .setImpl(new WatcherServiceImpl())
                .setUseNameSpace(false)
                .setProtocol(RPCServerBuilder.COMPACT_PROTOCOL)
                .build();
        server.start();
        // 进程结束前停止服务
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    server.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
