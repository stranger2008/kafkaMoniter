package com.jd.si.jupiter.tools.machinewatcher.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by zhangyun6 on 2014/10/9.
 */
public class IoUsage {
    private static Logger log = Logger.getLogger(IoUsage.class);
    private static IoUsage INSTANCE = new IoUsage();

    private IoUsage(){

    }

    public static IoUsage getInstance(){
        return INSTANCE;
    }

    /**
     * @Purpose:采集磁盘IO使用率
     * @param
     * @return float,磁盘IO使用率,小于1
     */
    public double get() throws Exception {
        //log.info("开始收集磁盘IO使用率");
        double ioUsage = 0.0;
        Process pro = null;
        BufferedReader in = null;
        Runtime r = Runtime.getRuntime();
        try {
            String command = "iostat -d -x";
            pro = r.exec(command);
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = null;
            int count =  0;
            while((line=in.readLine()) != null){
                if(++count >= 4){
//                  log.info(line);
                    String[] temp = line.split("\\s+");
                    if(temp.length > 1){
                        double util =  Double.parseDouble(temp[temp.length-1]);
                        ioUsage = (ioUsage>util)?ioUsage:util;
                    }
                }
            }
            if(ioUsage > 0){
                //log.info("本节点磁盘IO使用率为: " + ioUsage);
                ioUsage /= 100;
            }

        } catch (Exception e) {
            log.error("IoUsage发生Exception. " + e.getMessage());
            System.err.println("IoUsage发生Exception.");
            e.printStackTrace();
        }finally {
            if(in!=null){
                in.close();
            }
            if(pro!=null){
                pro.destroy();
            }
        }
        return ioUsage;
    }
}
