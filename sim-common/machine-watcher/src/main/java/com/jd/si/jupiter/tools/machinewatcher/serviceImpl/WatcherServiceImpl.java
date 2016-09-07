package com.jd.si.jupiter.tools.machinewatcher.serviceImpl;

import com.jd.si.jupiter.monitor.machine.thrift.TMonitorBean;
import com.jd.si.jupiter.monitor.machine.thrift.WatcherService;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * monitor接口实现
 * Created by lilianglin on 2016/8/30.
 */
public class WatcherServiceImpl implements WatcherService.Iface {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatcherServiceImpl.class);

    @Override
    public TMonitorBean getMonitorInfo() throws TException {
        try {
            return MonitorUtil.getMonitorInfo();
        } catch (Exception e) {
            LOGGER.error("getMonitorInfo error",e);
            return null;
        }
    }

}
