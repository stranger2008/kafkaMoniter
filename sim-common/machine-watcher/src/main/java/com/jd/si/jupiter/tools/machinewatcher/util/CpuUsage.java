package com.jd.si.jupiter.tools.machinewatcher.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by zhangyun6 on 2014/10/9.
 */
public class CpuUsage {

    private static Logger log = Logger.getLogger(CpuUsage.class);
    private static CpuUsage INSTANCE = new CpuUsage();

    private CpuUsage(){

    }

    public static CpuUsage getInstance(){
        return INSTANCE;
    }

    /**
     * Purpose:采集CPU使用率
     * @param
     * @return float,CPU使用率,小于1
     */
    public double get() throws Exception {
        //log.info("开始收集cpu使用率");
        double cpuUsage = 0;
        Process pro1=null,pro2=null;
        BufferedReader in1=null;
        BufferedReader in2=null;
        Runtime r = Runtime.getRuntime();
        try {
            String command = "cat /proc/stat";
            //第一次采集CPU时间
            long startTime = System.currentTimeMillis();
            pro1 = r.exec(command);
            in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
            String line = null;
            long idleCpuTime1 = 0, totalCpuTime1 = 0;   //分别为系统启动后空闲的CPU时间和总的CPU时间
            while((line=in1.readLine()) != null){
                if(line.startsWith("cpu")){
                    line = line.trim();
                    //log.info(line);
                    String[] temp = line.split("\\s+");
                    idleCpuTime1 = Long.parseLong(temp[4]);
                    for(String s : temp){
                        if(!s.equals("cpu")){
                            totalCpuTime1 += Long.parseLong(s);
                        }
                    }
                    //log.info("IdleCpuTime: " + idleCpuTime1 + ", " + "TotalCpuTime" + totalCpuTime1);
                    //System.out.println("IdleCpuTime: " + idleCpuTime1 + ", " + "TotalCpuTime:" + totalCpuTime1);
                    break;
                }
            }

            Thread.sleep(100);
            //第二次采集CPU时间
            long endTime = System.currentTimeMillis();
            pro2 = r.exec(command);
            in2 = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
            long idleCpuTime2 = 0, totalCpuTime2 = 0;   //分别为系统启动后空闲的CPU时间和总的CPU时间
            while((line=in2.readLine()) != null){
                if(line.startsWith("cpu")){
                    line = line.trim();
                    //log.info(line);
                    String[] temp = line.split("\\s+");
                    idleCpuTime2 = Long.parseLong(temp[4]);
                    for(String s : temp){
                        if(!s.equals("cpu")){
                            totalCpuTime2 += Long.parseLong(s);
                        }
                    }
                    //log.info("IdleCpuTime: " + idleCpuTime2 + ", " + "TotalCpuTime" + totalCpuTime2);
                    //System.out.println("IdleCpuTime: " + idleCpuTime2 + ", " + "TotalCpuTime:" + totalCpuTime2);
                    break;
                }
            }
            if(idleCpuTime1 != 0 && totalCpuTime1 !=0 && idleCpuTime2 != 0 && totalCpuTime2 !=0){
                cpuUsage = 1 - (double)(idleCpuTime2 - idleCpuTime1)/(double)(totalCpuTime2 - totalCpuTime1);
                //log.info("本节点CPU使用率为: " + cpuUsage);
            }
        } catch (Exception e) {
            log.error("CpuUsage发生Exception. " + e.getMessage());
            System.err.println("CpuUsage发生Exception.");
            e.printStackTrace();
        }finally {
            if(in1!=null){
                in1.close();
            }
            if(in2!=null){
                in2.close();
            }
            if(pro1!=null){
                pro1.destroy();
            }
            if(pro2!=null){
                pro2.destroy();
            }
        }
        return cpuUsage;
    }


    /**
     * 通过top命令获取，每次获取结果没有差异
     * @return
     * @throws Exception
     */
    public  double getCpuUsage() throws Exception {
        double cpuUsed = 0;
        double idleUsed = 0.0;
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec("top -b -n 1");// call "top" command in linux
        BufferedReader in = null;
        in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String str = null;
        int linecount = 0;

        while ((str = in.readLine()) != null) {
            linecount++;
            if (linecount == 3) {
                String[] s = str.split("%");
                String idlestr = s[3];
                String idlestr1[] = idlestr.split(" ");
                idleUsed = Double.parseDouble(idlestr1[idlestr1.length-1]);
                   //System.out.println("IdleUsed:XXXXXXXXXXXX"+idleUsed);
                cpuUsed = 100-idleUsed;
                break;
            }

        }
        return cpuUsed;
    }

}