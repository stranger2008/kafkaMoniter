package com.jd.si.jupiter.tools.machinewatcher.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by zhangyun6 on 2014/10/9.
 */
public class EstablishedCount {
    private static Logger log = Logger.getLogger(EstablishedCount.class);
    private static EstablishedCount INSTANCE = new EstablishedCount();

    private EstablishedCount(){

    }

    public static EstablishedCount getInstance(){
        return INSTANCE;
    }
    public  int get() throws Exception {
        int result = 0;
        Process p = null;
        BufferedReader in = null;
        try {
            Runtime rt = Runtime.getRuntime();
            p = rt.exec(new String[] {"/bin/sh", "-c", "netstat -n | grep ESTABLISHED | awk '/^tcp/ {++S[$NF]} END {for(a in S) print S[a]}'"});
            //Process p = rt.exec("netstat -n | grep ESTABLISHED | awk \'/^tcp/ {++S[$NF]} END {for(a in S) print S[a]}\'");// call "top" command in linux

            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String str = null;
            while ((str = in.readLine()) != null) {
                String s = str.trim();
                result = Integer.parseInt(s);
                break;
            }
        }catch(Exception e){
            log.error("EstablishedCount发生Exception. " + e.getMessage());
            System.err.println("EstablishedCount发生Exception.");
            e.printStackTrace();
        }finally {
            if(in!=null){
                in.close();
            }
            if(p!=null){
                p.destroy();
            }
        }
        return result;
    }

}
