package com.jd.si.kafkaMonitor.jmx;

import com.jd.si.kafkaMonitor.common.Const;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lilianglin on 2016/8/22.
 */
public class ExecutorUtil {

    private static final Log logger = LogFactory.getLog(ExecutorUtil.class);

    private static ExecutorService pool;

    static {
        try {
            pool = Executors.newFixedThreadPool(Const.poolThreadCountMax);
        } catch (Exception e) {
            logger.error("ThreadPoolRunner newFixedThreadPool init failed ! ", e);
        }
    }

    public static ExecutorService getPool(){
        return pool;
    }

}
