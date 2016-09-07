package com.jd.si.kafkaMonitor.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 系统配置加载
 * Created by lilianglin on 2016/8/26.
 */
public class SystemConfig {
    private static final Log logger = LogFactory.getLog(SystemConfig.class);

    //报警开关 true：打开 false：关闭
    public static Boolean alarmSwitch;
    //报警扫描频率，单位秒
    public static Integer alarmFrequency;
    public static Integer alarmQueueSize;

    //mail配置
    public static String mailUsername;
    public static String mailPassword;
    public static String mailSmtp;
    public static Integer mailPort;
    public static String mailFrom;
    public static String mailTo;

    public static Long lagThreshold;

    static {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("system.properties");
        try {
            Properties properties = new Properties();
            properties.load(in);
            alarmQueueSize = Integer.parseInt(properties.getProperty("alarmQueueSize","1000"));
            alarmFrequency = Integer.parseInt(properties.getProperty("alarmFrequency","60"));
            alarmSwitch = Boolean.valueOf(properties.getProperty("alarmSwitch", "false"));

            mailUsername = properties.getProperty("mailUsername");
            mailPassword = properties.getProperty("mailPassword");
            mailSmtp = properties.getProperty("mailSmtp");
            mailPort = Integer.valueOf(properties.getProperty("mailPort","465"));
            mailFrom = properties.getProperty("mailFrom");
            mailTo = properties.getProperty("mailTo");

            lagThreshold = Long.valueOf(properties.getProperty("lagThreshold","1000"));
        } catch (IOException e) {
            logger.error("properties is not found", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                logger.error("load properties failed", e);
            }
        }
    }
}
