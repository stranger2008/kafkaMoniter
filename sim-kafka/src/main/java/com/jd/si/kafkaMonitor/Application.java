package com.jd.si.kafkaMonitor;

import com.jd.si.kafkaMonitor.alarm.AlarmWorker;
import com.jd.si.kafkaMonitor.common.SystemConfig;
import com.jd.si.kafkaMonitor.model.DateTypeEnum;
import com.jd.si.kafkaMonitor.jmx.ThreadPoolRunner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时从指定JMX获取数据
 * Created by lilianglin on 2016/8/2.
 */
public class Application extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
        //分钟
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ThreadPoolRunner.run(DateTypeEnum.MINUTES.getValue());
            }
        }, 1, 60, TimeUnit.SECONDS);
        //小时
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ThreadPoolRunner.run(DateTypeEnum.HOURS.getValue());
            }
        }, 1, 60 * 60, TimeUnit.SECONDS);
        //天
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ThreadPoolRunner.run(DateTypeEnum.DAYS.getValue());
            }
        }, 1, 60 * 60 * 24, TimeUnit.SECONDS);

        //扫描报警，一分钟一次
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                AlarmWorker.run();
            }
        }, 1, SystemConfig.alarmFrequency, TimeUnit.SECONDS);
    }


}
