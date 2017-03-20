/**
 * Copyright &copy; 2014 uletian All rights reserved
 */
package com.uletian.ultcrm.business.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uletian.ultcrm.business.entity.Customer;
import com.uletian.ultcrm.business.entity.Children;
import com.uletian.ultcrm.business.repo.CustomerRepository;
import com.uletian.ultcrm.common.util.DateUtils;
import com.uletian.ultcrm.business.repo.ChildrenRepository;

/**
 * 
 * @author robertxie
 * 2015年9月8日
 */
@RestController
public class NewChildController {
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private ChildrenRepository childrenRepository;
	
	private static Logger logger = Logger.getLogger(NewChildController.class);
	
	@RequestMapping(value="/createChildForCustomer/{customerId}/{childId}/{childName}/{mobilephone}/{type}",method=RequestMethod.GET)
	public String createChildForCustomer(@PathVariable("customerId")Long customerId,@PathVariable("childId")Long childId,
			@PathVariable("childName")String childName,@PathVariable("mobilephone")String mobilephone,@PathVariable("type")String type){
		String result = "0";
		try {
			
			boolean bl = true;
			if("new".equals(type)){
				logger.info("先验证该教练是否已存在，参数为【customerId：" + customerId + ",childId:" + childId + ",childName:" + childName);
			    //Robert 2017-03-08
				/*
				List<Children> list = childrenRepository.findChildrenList(customerId,childName);
				if(list != null && list.size() > 0){
					bl = false;
					logger.info("查询到同名教练数量为：" + list.size());
					result = "";
				}
				*/
			}
			
			if(bl){
				logger.info("开始" + type + "教练信息，参数为【customerId：" + customerId + ",childId:" + childId 
						+ ",childName:" + childName + ",mobilephone:" + mobilephone  + ",type:" + type + "】 ");
				Children child = new Children();
				if("edit".equals(type) && childId != null){
					child.setId(childId);
				}
				if(childName != null && !"".equals(childName)){
					child.setName(childName);
				}
				if(mobilephone != null && !"".equals(mobilephone) ){					
					child.setMobilephone(mobilephone);
				}
				child.setCreateTime(new Date());
				child.setLastUpdateTime(new Date());
				
				if(customerId != null){
					Customer c = customerRepository.findOne(customerId);
					if(c != null){
						child.setCustomer(c);
					}
				}
				childrenRepository.save(child);
				result = "1";
				logger.info(type + "教练信息成功 ");
			}
		} 
	    catch (Exception e) {
			logger.error(type + "教练信息失败 ");
			result = "0";
			logger.error(e.getMessage());
		}
		return result;
	}
}
