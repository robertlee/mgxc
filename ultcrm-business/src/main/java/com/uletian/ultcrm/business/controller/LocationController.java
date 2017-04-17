/**
 * @author huliangqing 2017-04-02
 */
package com.uletian.ultcrm.business.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uletian.ultcrm.business.entity.Store;
import com.uletian.ultcrm.business.service.LocationService;

@RestController
@RequestMapping(value = "/location")
public class LocationController {
	private static Logger logger = Logger.getLogger(LocationController.class);
	
	@Autowired
	private LocationService locationService;

	/**
	 * 查询门店位置信息
	 */
	@RequestMapping(value = "/getStoreLocation/{itemType}/{id}", method = RequestMethod.GET)  
	public Map<String,Object> getBusinessBy(@PathVariable("itemType")Integer itemType,@PathVariable("id")Long id){
		Map<String,Object> map = null;
		try {
			logger.info("开始查询门店位置信息,传入的门店id为:" + id);
			map = locationService.getStoreLocation(id,itemType);
		} catch (Exception e) {
			logger.error("根据门店id:" + id + "获取门店信息失败");
			logger.error("获取门店位置信息失败", e);
		}
		return map;
	}
	
	
}
