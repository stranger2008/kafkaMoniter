package com.jd.si.jupiter.tools.machinewatcher.ruannable;

import com.jd.si.jupiter.monitor.machine.thrift.TMonitorBean;
import com.jd.si.jupiter.tools.machinewatcher.serviceImpl.MonitorUtil;
import com.jd.si.jupiter.tools.machinewatcher.model.MachineGlobalInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyun6 on 2014/12/19.
 */
public class MachineWatcher  implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(MachineWatcher.class);

    @Override
    public void run() {
        try {
            TMonitorBean monitorBean = MonitorUtil.getMonitorInfo();
            MachineGlobalInstance.setMonitorBean(monitorBean);
            //MonitorUtil.printMonitorBean(monitorBean);
            //writeFile(monitorBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
