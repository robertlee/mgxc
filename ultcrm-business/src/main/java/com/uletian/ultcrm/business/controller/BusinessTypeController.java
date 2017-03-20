package com.uletian.ultcrm.business.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uletian.ultcrm.business.entity.BusinessType;
import com.uletian.ultcrm.business.repo.BusinessTypeRepository;
import com.uletian.ultcrm.business.entity.Store;
import com.uletian.ultcrm.business.repo.StoreRepository;


@RestController
public class BusinessTypeController {
	private static Logger logger = Logger.getLogger(BusinessTypeController.class);
	
	@Autowired
	private BusinessTypeRepository businessTypeRepository;
	@Autowired
	private StoreRepository storeRepository;


    @RequestMapping(value = "/getStoreListToHome", method = RequestMethod.GET)
    public List<Store> getStoreListToHome(){ 
    	List<Store> list = new ArrayList<Store>();
    	try {
    		logger.debug("开始获取门店数据");
    		list = storeRepository.getStoreList();
    		logger.debug("获取门店数据条数" + list.size());
		} catch (Exception e) {
			logger.error("获取门店信息失败");
			logger.error(e.getMessage());
		}
    	return list;
    }
}
