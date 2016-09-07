package com.jd.si.jupiter.tools.machinewatcher.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by zhangyun6 on 2014/10/9.
 */
public class LoadAverage {
    private static Logger log = Logger.getLogger(LoadAverage.class);
    private static LoadAverage INSTANCE = new LoadAverage();

    private LoadAverage(){

    }

    public static LoadAverage getInstance(){
        return INSTANCE;
    }
    public  double[] get111() throws Exception {
        double[] result = new double[3];
        Process pro = null;
        BufferedReader in = null;
        try {
            Runtime rt = Runtime.getRuntime();
            pro = rt.exec("top -b -n 1");// call "top" command in linux  定时、反复调用时为什么没有关闭进程。。。。？？？？
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String str = null;
            while ((str = in.readLine()) != null) {
                String s1 = str.substring(str.lastIndexOf(":")+1);
                String[] arr1 = s1.split(",");
                for(int i=0; i<arr1.length;i++){
                    result[i] = Double.parseDouble(arr1[i].trim());
                }
                break;
            }
        }catch(Exception e){
            log.error("LoadAverage发生Exception. " + e.getMessage());
            System.err.println("LoadAverage发生Exception.");
            e.printStackTrace();
        }finally {
            if(in!=null){
                in.close();
            }
            if(pro!=null){
                pro.destroy();
            }
        }
        return result;
    }

    public  double[] get() throws Exception {
        double[] result = new double[3];
        Process pro = null;
        BufferedReader in = null;
        try {
            Runtime rt = Runtime.getRuntime();
            pro = rt.exec("uptime");// call "uptime" command in linux
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String str = null;
            while ((str = in.readLine()) != null) {
                String s1 = str.substring(str.lastIndexOf(":")+1);
                String[] arr1 = s1.split(",");
                for(int i=0; i<arr1.length;i++){
                    result[i] = Double.parseDouble(arr1[i].trim());
                }
                break;
            }
        }catch(Exception e){
            log.error("LoadAverage发生Exception. " + e.getMessage());
            System.err.println("LoadAverage发生Exception.");
            e.printStackTrace();
        }finally {
            if(in!=null){
                in.close();
            }
            if(pro!=null){
                pro.destroy();
            }
        }
        return result;
    }

    //uptime
}
