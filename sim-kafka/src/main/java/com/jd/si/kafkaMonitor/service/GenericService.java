package com.jd.si.kafkaMonitor.service;

import com.jd.si.kafkaMonitor.dao.GenericDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GenericService{
	
	private GenericDao genericDao;
	
	public GenericService() {}
	
	public GenericService(GenericDao genericDao) {
        this.genericDao = genericDao;
    }
	
	public List<Map<String,String>> getList(Map<String,String> map){
		return genericDao.getList(map);
	}
	
}
