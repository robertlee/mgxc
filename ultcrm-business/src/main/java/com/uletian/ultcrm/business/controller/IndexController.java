/**
 * Copyright &copy; 2014 uletian All rights reserved
 */
package com.uletian.ultcrm.business.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.uletian.ultcrm.business.entity.Advertise;
import com.uletian.ultcrm.business.repo.AdvertiseRepository;
import com.uletian.ultcrm.business.entity.BusinessType;
import com.uletian.ultcrm.business.repo.BusinessTypeRepository;
import com.uletian.ultcrm.business.entity.Coach;
import com.uletian.ultcrm.business.repo.CoachRepository;
import com.uletian.ultcrm.business.entity.Store;
import com.uletian.ultcrm.business.repo.StoreRepository;



@RestController
public class IndexController {
	private static Logger logger = Logger.getLogger(IndexController.class);
	
	@Autowired
	private AdvertiseRepository advertiseRepository;
	
	@Autowired
	private BusinessTypeRepository businessTypeRepository;

	@Autowired
	private CoachRepository coachRepository;	

	@Autowired
	private StoreRepository storeRepository;	
	
	

    @RequestMapping(value = "/getAdvertiseList", method = RequestMethod.GET)
    public List<Advertise> getAdvertiseList(){ 
    	List<Advertise> list = new ArrayList<Advertise>();
    	try {
    		logger.debug("开始获取门店数据");
    		list = advertiseRepository.getAdvertiseList();
    		logger.debug("获取广告数据条数" + list.size());
		} catch (Exception e) {
			logger.error("获取广告数据信息失败");
			logger.error(e.getMessage());
		}
    	return list;
    }	

    @RequestMapping(value = "/getBusinessAllList", method = RequestMethod.GET)
    public List<BusinessType> getBusinessAllList(){ 
    	List<BusinessType> list = new ArrayList<BusinessType>();
    	try {
    		logger.debug("开始获取业务数据");
    		list = businessTypeRepository.getBusinessAllList();
    		logger.debug("获取业务数据条数" + list.size());
		} catch (Exception e) {
			logger.error("获取业务数据信息失败");
			logger.error(e.getMessage());
		}
    	return list;
    }	
    
    // old version
//	@RequestMapping(value = "/getCoachAllList", method = RequestMethod.GET)
//	public List<Coach> getCoachAllList(){
//		List<Coach> list = new ArrayList<Coach>();
//		try {
//			logger.debug("开始获取教练业务数据");
//			list = coachRepository.getCoachAllList();
//			logger.debug("获取教练数据条数" + list.size());
//			} catch (Exception e) {
//				logger.error("获取教练数据信息失败");
//				logger.error(e.getMessage());
//			}
//			
//		return list;
//	}	
	@RequestMapping(value = "/getCoachAllList/{serviceStore}", method = RequestMethod.GET)
	public List<Coach> getCoachAllList(@PathVariable("serviceStore")String serviceStore){
		List<Coach> list = new ArrayList<Coach>();
		try {
			logger.debug("开始获取教练业务数据");
			if((null==serviceStore || "".equals(serviceStore))||( "null".equals(serviceStore)) ){
			if(null==serviceStore || "".equals(serviceStore)){
				list = coachRepository.getCoachAllList();
			} else {
				list = coachRepository.getCoachAllListByServiceStore(serviceStore);
			}
			logger.debug("获取教练数据条数" + list.size());
			} catch (Exception e) {
				logger.error("获取教练数据信息失败");
				logger.error(e.getMessage());
			}
			
		return list;
	}

	@RequestMapping(value = "/getCoachFirst3", method = RequestMethod.GET)
	public List<Coach> getCoachFirst3(){
		List<Coach> list = new ArrayList<Coach>();
		try {
			logger.debug("开始获取教练业务数据");
			list = coachRepository.getCoachFirst3();
			logger.debug("获取前3名教练数据条数" + list.size());
			} catch (Exception e) {
				logger.error("获取前3名教练数据失败");
				logger.error(e.getMessage());
			}			
		return list;
	}			
	 
	@RequestMapping(value = "/getCoachById/{Id}", method = RequestMethod.GET)
	public Coach getCoachById(@PathVariable("Id")Long Id){
		 Coach result = null;
		try {
			logger.debug("开始获取被选教练数据");
			result = coachRepository.getCoachById(Id);			
			} catch (Exception e) {
				logger.error("获取教练数据信息失败");
				logger.error(e.getMessage());
			}
			
		return result;
	}	
	@RequestMapping(value = "/getStoreById/{Id}", method = RequestMethod.GET)
	public Store getStoreById(@PathVariable("Id")Long Id){
		 Store result = null;
		try {
			logger.debug("开始获取训练场数据");
			result = storeRepository.getStoreById(Id);			
			} catch (Exception e) {
				logger.error("获取训练场数据信息失败");
				logger.error(e.getMessage());
			}
			
		return result;
	}	
	@RequestMapping(value = "/getStoreAllList/{serviceStore}", method = RequestMethod.GET)
	public List<Store> getStoreAllList(@PathVariable("serviceStore")String serviceStore){
		List<Store> list = new ArrayList<Store>();
		try {
			logger.debug("开始获取训练场业务数据");
			if((null==serviceStore || "".equals(serviceStore))||("null".equals(serviceStore))) {
			//if(null==serviceStore || "".equals(serviceStore)) {	
				list = storeRepository.getStoreList();
			} else {
				list = storeRepository.getStoreListByServiceStore(serviceStore);
			}
			logger.debug("获取训练场数据条数" + list.size());
			} catch (Exception e) {
				logger.error("获取训练场数据信息失败");
				logger.error(e.getMessage());
			}
			
		return list;
	}	
}
