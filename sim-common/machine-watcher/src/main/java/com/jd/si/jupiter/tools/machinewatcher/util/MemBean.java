package com.jd.si.jupiter.tools.machinewatcher.util;

/**
 * Created by zhangyun6 on 2014/12/30.
 */
public class MemBean {
    private double memUsage; //内存使用率
    private long memTotal; //内存大小

    public double getMemUsage() {
        return memUsage;
    }

    public void setMemUsage(double memUsage) {
        this.memUsage = memUsage;
    }

    public long getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(long memTotal) {
        this.memTotal = memTotal;
    }

    public long getMemUse() {
        return memUse;
    }

    public void setMemUse(long memUse) {
        this.memUse = memUse;
    }

    private long memUse; //已使用内存大小
}
