/**
 * @author huliangqing 2017-04-02
 */
package com.uletian.ultcrm.business.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uletian.ultcrm.business.entity.BusinessType;
import com.uletian.ultcrm.business.repo.BusinessTypeRepository;

@Component
public class BusinessTypeService {
	
	private static Logger logger = Logger.getLogger(BusinessTypeService.class);
	
	@Autowired
	private BusinessTypeRepository businessTypeRepository;

	public BusinessType getBusinessById(Long id) {
		return businessTypeRepository.getBusinessById(id);
	}
	
}	
