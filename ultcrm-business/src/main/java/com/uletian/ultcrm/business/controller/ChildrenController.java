/**
 * Copyright &copy; 2014 uletian All rights reserved
 */
package com.uletian.ultcrm.business.controller;

import java.util.ArrayList;
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

import com.uletian.ultcrm.business.entity.Children;
import com.uletian.ultcrm.business.entity.Customer;
import com.uletian.ultcrm.business.entity.Order;
import com.uletian.ultcrm.business.repo.ChildrenRepository;
import com.uletian.ultcrm.business.repo.CustomerRepository;
import com.uletian.ultcrm.business.repo.OrderRepository;


/**
 * 
 * @author robertlee
 * 2017年3月8日
 */
@RestController
public class ChildrenController {
	private static Logger logger = Logger.getLogger(ChildrenController.class);
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ChildrenRepository childrenRepository;

	@Autowired
	private OrderRepository orderRepository;
		
	
	
	@RequestMapping(value="/findChildrenByCustomer/{customerId}", method=RequestMethod.GET)
	public List<Children>  getChildrenByCustomer(@PathVariable("customerId")Long customerId){
		//Customer customer = customerRepository.findOne(customerId);

		List<Children> ChildrenList = childrenRepository.findChildrenListByCustomer(customerId);
		if(ChildrenList==null){
			ChildrenList = new ArrayList<Children>();
		}
		// 查看有没有课程的的行
		// 如果有至少1门培训的课程，那么只选择有课程编码的课程，然后放到list中去
		for (Children theChildren : ChildrenList) {
			theChildren.setLastUpdateUserid(theChildren.getId());
			theChildren.fillOtherFields();
		}
		
		return ChildrenList;
	}

	@RequestMapping(value="/createChildrenForCustomer",method=RequestMethod.POST)
	public Children createChildrenForCustomer(@RequestBody Children inputChildren){		
		Children child   = new Children();
		child.setCustomerId(inputChildren.getCustomerId());
		child.setName(inputChildren.getName());	
		if (StringUtils.isNotBlank(inputChildren.getMobilephone()) ) {
			
			try {
				String mobile = inputChildren.getMobilephone();
				child.setMobilephone(mobile);
			}
			catch(Exception e) {
				logger.error("解析教练数据出错，输入的数据是："+inputChildren.getMobilephone());
			}
		}
		childrenRepository.save(child);
		child.fillOtherFields();
		return child;

	}
	
}
