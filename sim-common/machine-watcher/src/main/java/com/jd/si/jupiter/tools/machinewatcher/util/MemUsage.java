package com.jd.si.jupiter.tools.machinewatcher.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * Created by zhangyun6 on 2014/10/9.
 */
public class MemUsage{

    private static Logger log = Logger.getLogger(MemUsage.class);
    private static MemUsage INSTANCE = new MemUsage();

    private MemUsage(){

    }

    public static MemUsage getInstance(){
        return INSTANCE;
    }

    /**
     * Purpose:采集内存使用率
     * @param
     * @return float,内存使用率,小于1
     */
    public MemBean get() throws Exception {
        //log.info("开始收集memory使用率");
        MemBean memBean = new MemBean();
        double memUsage = 0.0f;
        Process pro = null;
        BufferedReader in = null;
        Runtime r = Runtime.getRuntime();
        try {
            String command = "free -m";
            pro = r.exec(command);
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = null;
            long total=0;
            long use=0;
            while((line=in.readLine()) != null){
                //log.info(line);
                String[] memInfo = line.split("\\s+");
                if(memInfo[0].startsWith("Mem")){
                    total = Long.parseLong(memInfo[1]);
                    long used = Long.parseLong(memInfo[2]);
                    long buffers = Long.parseLong(memInfo[5]);
                    long cached = Long.parseLong(memInfo[6]);
                    use = used-buffers-cached;
                    memUsage = (double)use/(double)total;
                    break;
                }
            }
            memBean.setMemTotal(total);
            memBean.setMemUse(use);
            memBean.setMemUsage(memUsage);

        } catch (Exception e) {
            log.error("MemUsage发生Exception. " + e.getMessage());
            System.err.println("MemUsage发生Exception.");
            e.printStackTrace();
        }finally {
            if(in!=null){
                in.close();
            }
            if(pro!=null){
                pro.destroy();
            }
        }
        return memBean;
    }

    public MemBean get_bak() throws Exception {
        //log.info("开始收集memory使用率");
        MemBean memBean = new MemBean();
        double memUsage = 0.0f;
        Process pro = null;
        BufferedReader in = null;
        Runtime r = Runtime.getRuntime();
        try {
            String command = "cat /proc/meminfo";
            pro = r.exec(command);
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = null;
            int count = 0;
            long totalMem = 0, freeMem = 0;
            while((line=in.readLine()) != null){
                //log.info(line);
                String[] memInfo = line.split("\\s+");
                if(memInfo[0].startsWith("MemTotal")){
                    totalMem = Long.parseLong(memInfo[1]);
                }
                if(memInfo[0].startsWith("MemFree")){
                    freeMem = Long.parseLong(memInfo[1]);
                }
                memUsage = 1- (double)freeMem/(double)totalMem;
                //log.info("本节点内存使用率为: " + memUsage);
                if(++count == 2){
                    break;
                }
            }
            memBean.setMemTotal(totalMem);
            memBean.setMemUse(totalMem-freeMem);
            memBean.setMemUsage(memUsage);

        } catch (Exception e) {
            log.error("MemUsage发生Exception. " + e.getMessage());
            System.err.println("MemUsage发生Exception.");
            e.printStackTrace();
        }finally {
            if(in!=null){
                in.close();
            }
            if(pro!=null){
                pro.destroy();
            }
        }
        return memBean;
    }
}
