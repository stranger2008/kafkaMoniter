package com.jd.si.kafkaMonitor.dao;

import java.util.List;
import java.util.Map;

public interface GenericDao<T> {
	//查询
	public List<Map<String,String>> getList(Map<String, String> map);

	//根据主键删除
	public void delete(int id);

	//根据主键查询
	public T getByPk(int id);
}
