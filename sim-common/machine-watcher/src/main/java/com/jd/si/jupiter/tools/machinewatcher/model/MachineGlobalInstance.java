package com.jd.si.jupiter.tools.machinewatcher.model;


import com.jd.si.jupiter.monitor.machine.thrift.TMonitorBean;

/**
 * Created by zhangyun6 on 2014/12/18.
 */
public class MachineGlobalInstance {
    private static TMonitorBean monitorBean;
    public static TMonitorBean getMonitorBean() {
        return monitorBean;
    }
    public synchronized  static  void setMonitorBean(TMonitorBean monitorB) {
        monitorBean = monitorB;
    }
}
