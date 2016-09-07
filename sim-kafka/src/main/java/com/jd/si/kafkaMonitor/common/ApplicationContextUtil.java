package com.jd.si.kafkaMonitor.common;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 获取spring ApplicationContext
 * Created by lilianglin on 2016/8/4.
 */
public class ApplicationContextUtil {

    private static ApplicationContext ac;

    static{
        ac = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    public static ApplicationContext get(){
        return ac;
    }

}
