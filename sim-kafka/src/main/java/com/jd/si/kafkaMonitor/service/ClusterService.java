package com.jd.si.kafkaMonitor.service;

import com.jd.si.kafkaMonitor.model.DataTypeEnum;
import com.jd.si.kafkaMonitor.model.JVMAttributeEnum;
import com.jd.si.kafkaMonitor.model.KafkaAttributeEnum;
import com.jd.si.kafkaMonitor.dao.ClusterDao;
import com.jd.si.kafkaMonitor.model.Cluster;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @function 集群管理service
 * @author 创建人 李良林
 * @date 创建日期 2016-03-30
 */

@Service
public class ClusterService extends GenericService{

	private static final Log logger = LogFactory.getLog(ClusterService.class);

	private ClusterDao clusterDao;

	public ClusterService() {}

	@Autowired
	public ClusterService(ClusterDao clusterDao) {
		super(clusterDao);
		this.clusterDao = clusterDao;
	}

	public Cluster get(int id){
		return this.clusterDao.getByPk(id);
	}

	/**
	 * 添加集群
	 * @param cluster
	 * @return
	 */
	public boolean insert(Cluster cluster){
		if(cluster == null){
			return false;
		}
		String cname = cluster.getCname();
		String cdesc = cluster.getCdesc();
		String zkhost = cluster.getZkhost();
		if(StringUtils.isEmpty(cname) || cname.length() > 100){
			return false;
		}
		if(StringUtils.isNotBlank(cdesc) && cdesc.length() > 300){
			return false;
		}
		if(StringUtils.isNotBlank(zkhost) && zkhost.length() > 300){
			return false;
		}
		try{
			return clusterDao.insert(cluster);
		}catch (Exception e){
			logger.error("集群添加异常",e);
		}
		return false;
	}

	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public boolean delete(int id){
		try {
			clusterDao.delete(id);
			return true;
		} catch (Exception e) {
			logger.error("delete cluster error",e);
			return false;
		}
	}

	/**
	 * 获取所有指标属性，包括jvm、kafka
	 * @return
	 */
	public Map<Integer,Map<String,String>> getMoniterAttr(){
		Map<Integer,Map<String,String>> map = new HashMap<Integer, Map<String, String>>(2);
		//获取jvm指标属性
		JVMAttributeEnum[] jvmAttributeEnums = JVMAttributeEnum.values();
		Map<String,String> jvmMap = new LinkedHashMap<String, String>(jvmAttributeEnums.length);
		for(int i=0;i<jvmAttributeEnums.length;i++){
			jvmMap.put(jvmAttributeEnums[i].getKey(),jvmAttributeEnums[i].getDesc());
		}
		map.put(DataTypeEnum.JVM.getValue(),jvmMap);
		//获取kafka指标属性
		KafkaAttributeEnum[] kafkaAttributeEnums = KafkaAttributeEnum.values();
		Map<String,String> kafkaMap = new LinkedHashMap<String, String>(kafkaAttributeEnums.length);
		for(int i=0;i<kafkaAttributeEnums.length;i++){
			kafkaMap.put(kafkaAttributeEnums[i].getKey(),kafkaAttributeEnums[i].getDesc());
		}
		map.put(DataTypeEnum.KAFKA.getValue(),kafkaMap);
		return map;
	}

}

