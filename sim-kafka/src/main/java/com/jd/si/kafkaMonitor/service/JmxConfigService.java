package com.jd.si.kafkaMonitor.service;

import com.jd.si.kafkaMonitor.model.DateTypeEnum;
import com.jd.si.kafkaMonitor.dao.JmxConfigDao;
import com.jd.si.kafkaMonitor.jmx.ThreadPoolRunner;
import com.jd.si.kafkaMonitor.model.JmxConfig;
import com.jd.si.kafkaMonitor.model.MbConnVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.*;

/**
 * @function kafka broker 配置读取
 * @author 创建人 李良林
 * @date 创建日期 2016-03-30
 */

@Service
public class JmxConfigService extends GenericService{

	private static final Log logger = LogFactory.getLog(JmxConfigService.class);

	@Autowired
	ClusterService clusterService;

	private JmxConfigDao jmxConfigDao;

	private static volatile List<MbConnVo> connections;
	
	public JmxConfigService() {}
	
	@Autowired
	public JmxConfigService(JmxConfigDao jmxConfigDao) {
		super(jmxConfigDao);
		this.jmxConfigDao = jmxConfigDao;
	}

	/**
	 * 添加
	 * @param jmxConf
	 */
	public boolean insert(String jmxConf,String cid){
		String[] jc = jmxConf.split(",");
		if(jc.length == 0){
			return false;
		}
		List<JmxConfig> jmxConfigs = new ArrayList<JmxConfig>(jc.length);
		for(int i=0;i<jc.length;i++){
			String[] jcOne = jc[i].split(":");
			if(jcOne.length < 2){
				return false;
			}
			String host = jcOne[0];
			if(!isIp(host)){
				return false;
			}
			String port = jcOne[1];
			//校验是否连通
			JMXConnector jmxConn = getJmxConn(host, port);
			if(jmxConn == null){
				return false;
			}
			JmxConfig jmxConfig = new JmxConfig();
			jmxConfig.setHost(host);
			jmxConfig.setCid(Integer.parseInt(cid));
			jmxConfig.setPort(port);
			jmxConfigs.add(jmxConfig);
		}
		jmxConfigDao.insertBatch(jmxConfigs);
		//重新创建jmx连接
		init();
		runAllTime();
		return true;
	}

	/**
	 * 初始化连接后执行一次数据获取
	 */
	public void runAllTime(){
		ThreadPoolRunner.run(DateTypeEnum.MINUTES.getValue());
	}

	/**
	 * 校验IP
	 * @param ipAddr
	 * @return
	 */
	public boolean isIp(String ipAddr) {
		if (ipAddr != null && !ipAddr.isEmpty()) {
			String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
					+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
					+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
					+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
			if (ipAddr.matches(regex)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 返回机器列表，把所属的机房cname也设置进去了
	 * @param map
	 * @return
	 */
	@Override
	public List<Map<String, String>> getList(Map<String, String> map) {
		List<Map<String, String>> list = super.getList(map);
		List<Map<String, String>> clusterList = clusterService.getList(null);
		Map<String,String> clusterMap = new HashMap<String, String>(clusterList.size());
		if(clusterList != null && clusterList.size() > 0){
			for(Map<String, String> map1 : clusterList){
				clusterMap.put(map1.get("id"),map1.get("cname"));
			}
		}
		if(list != null && list.size() > 0){
			for(Map<String, String> map1 : list){
				Integer cid;
				if(map1.get("cid") != null){
					cid = Integer.parseInt(String.valueOf(map1.get("cid")));
					map1.put("cname",clusterMap.get(cid));
				}
			}
		}
		return list;
	}

	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public boolean delete(int id){
		try {
			jmxConfigDao.delete(id);
			//重新创建jmx连接
			init();
			return true;
		} catch (Exception e) {
			logger.error("delete JmxConfig error",e);
			return false;
		}
	}

	public void setConnections(){
		if(connections == null){
			synchronized (this){
				if(connections == null){
					init();
				}
			}
		}
	}

	public List<MbConnVo> getMbConn(){
		return connections;
	}

	public void init(){
		List<Map<String,String>> list = getList(null);
		if(list == null){
			return;
		}
		if(connections != null){
			connections.clear();
		}
		connections = new ArrayList<MbConnVo>(list.size());
		for(Map<String,String> map : list){
			String port = "",id = "",host = "";
			if(map.get("port") != null){
				port = map.get("port").toString();
			}
			if(map.get("id") != null){
				id = String.valueOf(map.get("id"));
			}
			if(map.get("host") != null){
				host = map.get("host").toString();
			}
			JMXConnector conn = null;
			MBeanServerConnection mbs = null;
			try {
				conn = getJmxConn(host, port);
				mbs=conn.getMBeanServerConnection();
			} catch (IOException e) {
				logger.error("init jmx connection error", e);
			}
			connections.add(new MbConnVo(id,mbs));
		}
	}

	public JMXConnector getJmxConn(String host,String port){
		try {
			JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+host+":"+port+"/jmxrmi");
			JMXConnector conn = JMXConnectorFactory.connect(jmxServiceURL);
			return conn;
		} catch (Exception e) {
			logger.error("init jmx connection error", e);
			return null;
		}
	}

}

