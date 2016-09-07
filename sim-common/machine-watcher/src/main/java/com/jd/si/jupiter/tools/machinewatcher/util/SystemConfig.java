package com.jd.si.jupiter.tools.machinewatcher.util;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;


public class SystemConfig {
    private static Logger logger = Logger.getLogger(SystemConfig.class);
    public static String machineWatcherBakDir;
    public static int machineWatcherBakCount;

    public static long watcherInitialDelay;
    public static long watcherInterval;
    public static long monWriterInitialDelay;
    public static long monWriterInterval;

    public static Integer thriftServicePort;

    static {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("system.properties");
        try {
            logger.error("------load machine-watcher config begin-------");
            Properties properties = new Properties();
            properties.load(in);
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<Object, Object> entry: properties.entrySet()){
                sb.append("\n config > " + entry.getKey() + "=" + entry.getValue());
            }
            sb.append("\nfinish loading config.\n");
            logger.error(sb.toString());
            machineWatcherBakDir = properties.getProperty("machineWatcherBakDir");
            machineWatcherBakCount = Integer.parseInt(properties.getProperty("machineWatcherBakCount"));
            watcherInitialDelay = Long.parseLong(properties.getProperty("watcher_initial_delay"));
            watcherInterval = Long.parseLong(properties.getProperty("watcher_interval"));
            monWriterInitialDelay = Long.parseLong(properties.getProperty("mon_writer_initial_delay"));
            monWriterInterval = Long.parseLong(properties.getProperty("mon_writer_interval"));

            thriftServicePort = Integer.parseInt(properties.getProperty("thriftServicePort"));
            logger.error("------load machine-watcher config end-------");
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
