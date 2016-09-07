package com.jd.si.jupiter.tools.machinewatcher.serviceImpl;

import com.jd.si.jupiter.tools.machinewatcher.ruannable.MachineWatcher;
import com.jd.si.jupiter.tools.machinewatcher.ruannable.MachineWatcherFileWriter;
import com.jd.si.jupiter.tools.machinewatcher.util.SystemConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 监控数据定时器
 */
public class MachineInitialization {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(MachineInitialization.class);

	public void init() {
        //定时把机器的性能指标写到内存的一个对象上
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(new MachineWatcher(), SystemConfig.watcherInitialDelay, SystemConfig.watcherInterval, TimeUnit.SECONDS);//本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务

        //定时把那个已经有指标的对象写入到文件中
        ScheduledExecutorService sesWrite = Executors.newSingleThreadScheduledExecutor();
        sesWrite.scheduleWithFixedDelay(new MachineWatcherFileWriter(), SystemConfig.watcherInitialDelay, SystemConfig.monWriterInterval, TimeUnit.SECONDS);
        LOG.info("****************machine-watcher startup success****************");

    }
}
