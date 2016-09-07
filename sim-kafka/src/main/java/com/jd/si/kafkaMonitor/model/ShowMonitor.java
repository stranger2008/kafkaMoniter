package com.jd.si.kafkaMonitor.model;

import java.util.List;
import java.util.Map;

/**
 * 图标展示对象
 * Created by lilianglin on 2016/8/5.
 */
public class ShowMonitor {

    private List<String> dataList;
    private List<String> dateList;
    private Map<String,List<String>> manyAttrMap;
    private String desc;
    private String unit;

    public Map<String, List<String>> getManyAttrMap() {
        return manyAttrMap;
    }

    public void setManyAttrMap(Map<String, List<String>> manyAttrMap) {
        this.manyAttrMap = manyAttrMap;
    }

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    public List<String> getDateList() {
        return dateList;
    }

    public void setDateList(List<String> dateList) {
        this.dateList = dateList;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "ShowMonitor{" +
                "dataList=" + dataList +
                ", dateList=" + dateList +
                ", manyAttrMap=" + manyAttrMap +
                ", title='" + unit + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
