package com.jd.si.kafkaMonitor.service;

import com.google.gson.Gson;
import com.jd.si.kafkaMonitor.model.DataTypeEnum;
import com.jd.si.kafkaMonitor.model.DateTypeEnum;
import com.jd.si.kafkaMonitor.model.JVMAttributeEnum;
import com.jd.si.kafkaMonitor.model.KafkaAttributeEnum;
import com.jd.si.kafkaMonitor.dao.MonitorDataDao;
import com.jd.si.kafkaMonitor.jmx.ExecutorUtil;
import com.jd.si.kafkaMonitor.model.MonitorData;
import com.jd.si.kafkaMonitor.model.ShowMonitor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @function 监控数据存取
 * @author 创建人 李良林
 * @date 创建日期 2016-03-30
 */

@Service
public class MonitorDataService extends GenericService{

	private static final Log logger = LogFactory.getLog(MonitorDataService.class);

	@Autowired
	JmxConfigService jmxConfigService;

	private MonitorDataDao monitorDataDao;

	public MonitorDataService() {}

	@Autowired
	public MonitorDataService(MonitorDataDao monitorDataDao) {
		super(monitorDataDao);
		this.monitorDataDao = monitorDataDao;
	}

	/**
	 * 插入监控数据
	 * @param monitorData
	 */
	public void insert(MonitorData monitorData){
		monitorDataDao.insert(monitorData);
	}

	/**
	 * 根据集群和具体某个指标展示图表
	 * @param clusterId
	 * @param monitorKey
	 * @param dateType
	 * @param dataType
	 * @param searchTime
	 * @return
	 */
	public Map<String,ShowMonitor> getMonitorData(String clusterId,final String monitorKey,final String dateType,final String dataType,final String searchTime){
		Map<String,String> paramMap = new HashMap<String, String>(1);
		paramMap.put("cid",clusterId);
		//根据集群ID获取机器ID list
		List<Map<String,String>> jcList = jmxConfigService.getList(paramMap);
		if(CollectionUtils.isEmpty(jcList)){
			return null;
		}
		//多线程获取各个机器的指标
		List<Callable<Map<String,ShowMonitor>>> callables = new ArrayList<Callable<Map<String,ShowMonitor>>>(jcList.size());
		for(final Map<String,String> map : jcList){
			callables.add(new Callable<Map<String,ShowMonitor>>() {
				@Override
				public Map<String,ShowMonitor> call() throws Exception {
					Map<String,ShowMonitor> retMap = new LinkedHashMap<String, ShowMonitor>();
					String pid = String.valueOf(map.get("id"));
					String host = String.valueOf(map.get("host"));
					Map<String,ShowMonitor> showMonitorMap = getMonitorData(pid,dateType,dataType,searchTime);
					if(showMonitorMap.get(monitorKey) != null){
						ShowMonitor showMonitor = showMonitorMap.get(monitorKey);
						showMonitor.setDesc(host + "_" + showMonitor.getDesc());
						retMap.put(pid + "_" + monitorKey,showMonitor);
					}
					return retMap;
				}
			});
		}
		Map<String,ShowMonitor> finalMap = new LinkedHashMap<String, ShowMonitor>();
		try{
			List<Future<Map<String,ShowMonitor>>> futures = ExecutorUtil.getPool().invokeAll(callables);
			if(futures != null && futures.size() > 0){
				for (Future<Map<String,ShowMonitor>> future : futures){
					Map<String, ShowMonitor> itemMap = future.get();
					if(itemMap != null){
						finalMap.putAll(itemMap);
					}
				}
			}
		} catch (InterruptedException e) {
			logger.error("getMonitorData error: " + e);
		}catch (ExecutionException e1){
			logger.error("getMonitorData error: " + e1);
		}
		return finalMap;
	}

	/**
	 * 根据前台传入的机器id、日期类型、信息类型和时间维度查询监控数据
	 * @param pid
	 * @param dateType
	 * @param dataType
	 * @param searchTime
	 * @return
	 */
	public Map<String,ShowMonitor> getMonitorData(String pid,String dateType,String dataType,String searchTime){
		Map<String,String> map = new HashMap<String, String>(4);
		map.put("pid",pid);
		map.put("dateType",dateType);
		map.put("dataType",dataType);
		map.put("searchTime",getSearchTime(dateType,searchTime));
		List<Map<String,String>> list = monitorDataDao.getList(map);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		Map<String,ShowMonitor> resultMap = new HashMap<String,ShowMonitor>();
		List<String> dateList = new ArrayList<String>(list.size());
		Map<String,List<String>> resListMap = new HashMap<String, List<String>>();
		for(Map<String,String> _map : list){
			String jsonData = "",indate = "";
			if(_map.get("jsonData") != null){
				jsonData = _map.get("jsonData").toString();
			}
			if(_map.get("indate") != null){
				indate = String.valueOf(_map.get("indate"));
			}
			dateList.add(getIndate(indate));
			if(StringUtils.isBlank(jsonData)){
				continue;
			}
			setKeyListMap(jsonData,resListMap);
		}
		for (Map.Entry<String, List<String>> entry : resListMap.entrySet()) {
			List<String> value = entry.getValue();
			String key = entry.getKey();
			ShowMonitor showMonitor = new ShowMonitor();
			showMonitor.setDataList(value);
			showMonitor.setDateList(dateList);
			if(dataType.equals(String.valueOf(DataTypeEnum.JVM.getValue()))){
				showMonitor.setDesc(JVMAttributeEnum.getAttr("desc", key));
				showMonitor.setUnit(JVMAttributeEnum.getAttr("unit",key));
			}else if(dataType.equals(String.valueOf(DataTypeEnum.KAFKA.getValue()))){
				showMonitor.setDesc(KafkaAttributeEnum.getAttr("desc",key));
				showMonitor.setUnit(KafkaAttributeEnum.getAttr("unit",key));
			}
			resultMap.put(key,showMonitor);
		}
		//设置多属性值
		setAttr(resultMap);
		return resultMap;
	}

	/**
	 * 设置多属性值
	 * @param resultMap
	 */
	public void setAttr(Map<String,ShowMonitor> resultMap){
		for (Map.Entry<String, ShowMonitor> entry : resultMap.entrySet()) {
			ShowMonitor value = entry.getValue();
			List<String> dataList = value.getDataList();
			String key = entry.getKey();
			//获取属性名称
			String attrName = KafkaAttributeEnum.getAttr("attrName", key);
			String attrs[] = attrName.split(",");
			Map<String,List<String>> attrMap = new HashMap<String, List<String>>();
			for(int i=0;i<attrs.length;i++){
				attrMap.put(attrs[i], getAttrValueList(i, dataList));
			}
			value.setManyAttrMap(attrMap);
		}
	}

	/**
	 * 按照数据库中的数据位置提取数据
	 * @param pos
	 * @param dataList
	 * @return
	 */
	public List<String> getAttrValueList(int pos,List<String> dataList){
		if(CollectionUtils.isEmpty(dataList)){
			return Collections.EMPTY_LIST;
		}
		List<String> attrValueList = new ArrayList<String>();
		for(String val : dataList){
			String vals[] = val.split(",");
			for(int i=0;i<vals.length;i++){
				if(i == pos){
					attrValueList.add(vals[i]);
				}
			}
		}
		return attrValueList;
	}


	public void setKeyListMap(String jsonData,Map<String,List<String>> resListMap){
		Gson gson = new Gson();
		Map<String,String> jsonMap = gson.fromJson(jsonData,HashMap.class);
		if(jsonMap == null){
			return;
		}
		for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
			String value = entry.getValue();
			String key = entry.getKey();
			List<String> list;
			if(resListMap.get(key) == null){
				list = new ArrayList<String>();
			}else{
				list = resListMap.get(key);
			}
			list.add(value);
			resListMap.put(key,list);
		}
	}

	public String getIndate(String indate){
		SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd HH:mm") ;        // 实例化模板对象
		Date d = null ;
		try{
			d = sp.parse(indate) ;   // 将给定的字符串中的日期提取出来
		}catch(Exception e){            // 如果提供的字符串格式有错误，则进行异常处理
			e.printStackTrace() ;       // 打印异常信息
		}
		String _st = sdf1.format(d);
		return _st;
	}

	public String getSearchTime(String dateType,String searchTime){
		String _st;
		Integer stInt = Integer.parseInt(searchTime);
		Calendar cal=Calendar.getInstance();
		SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(dateType.equals(String.valueOf(DateTypeEnum.MINUTES.getValue()))){
			cal.add(Calendar.MINUTE,-stInt);
		}
		if(dateType.equals(String.valueOf(DateTypeEnum.HOURS.getValue()))){
			cal.add(Calendar.HOUR,-stInt);
		}
		if (dateType.equals(String.valueOf(DateTypeEnum.DAYS.getValue()))){
			cal.add(Calendar.DATE,-stInt);
		}
		Date d=cal.getTime();
		_st = sp.format(d);
		return _st;
	}

}

