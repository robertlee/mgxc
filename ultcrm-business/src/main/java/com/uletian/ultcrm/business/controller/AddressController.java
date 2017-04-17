/**
 * @author huliangqing 2017-04-02
 */
package com.uletian.ultcrm.business.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uletian.ultcrm.business.entity.Address;
import com.uletian.ultcrm.business.entity.Tech;
import com.uletian.ultcrm.business.entity.Customer;
import com.uletian.ultcrm.business.entity.Order;
import com.uletian.ultcrm.business.entity.Score;
import com.uletian.ultcrm.business.entity.Store;
import com.uletian.ultcrm.business.repo.TechRepository;
import com.uletian.ultcrm.business.repo.CustomerRepository;
import com.uletian.ultcrm.business.repo.ScoreRepository;
import com.uletian.ultcrm.business.service.AddressService;
import com.uletian.ultcrm.common.util.DateUtils;


@RestController
public class AddressController {
	private static Logger logger = Logger.getLogger(AddressController.class);

	@Autowired
	private AddressService addressService;

	/**
	 * 查询报名点列表
	 */
	@RequestMapping(value="/getAddressList", method=RequestMethod.GET)
	public List<Address> getAddressList(){		
		List<Address> list = new ArrayList<Address>();		
		
		try {
			logger.info("开始查询报名点列表");
			list = addressService.getAddressList();
		} catch (Exception e) {
			logger.error("查询报名点列表失败", e);
		}
		return list;
	};
	
}
