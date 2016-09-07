package com.jd.si.kafkaMonitor.model;

/**
 * Created by lilianglin on 2016/8/3.
 */
public enum JVMAttributeEnum {
    JVM_CPUUSAGE("cpu","cpu使用率","%"),
    JVM_THREADCOUNT("tct","当前活跃线程数","个"),
    JVM_LOADCLASSCOUNT("lcc","当前加载类数","个"),
    JVM_HEAPSIZEUSED("hsu","堆内存使用情况","MB");

    private String key;
    private String desc;
    private String unit;

    JVMAttributeEnum(String key,String desc, String unit) {
        this.key = key;
        this.desc = desc;
        this.unit = unit;
    }

    public static String getAttr(String attrName,String key){
        JVMAttributeEnum[] jvmAttributeEnums = JVMAttributeEnum.values();
        for(int i=0;i<jvmAttributeEnums.length;i++){
            if(jvmAttributeEnums[i].getKey().equals(key)){
                if(attrName.equals("desc")){
                    return jvmAttributeEnums[i].getDesc();
                }
                if(attrName.equals("unit")){
                    return jvmAttributeEnums[i].getUnit();
                }
            }
        }
        return "";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
