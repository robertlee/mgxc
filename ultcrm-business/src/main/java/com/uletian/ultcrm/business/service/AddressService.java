/**
 * @author huliangqing 2017-04-02
 */
package com.uletian.ultcrm.business.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uletian.ultcrm.business.entity.Address;
import com.uletian.ultcrm.business.repo.AddressRepository;

@Component
public class AddressService {
	
	private static Logger logger = Logger.getLogger(AddressService.class);
	
	@Autowired
	private AddressRepository addressRepository;

	public List<Address> getAddressList() {
		List<Address> addressList = new ArrayList<Address>();
		
		Iterable<Address> it =  addressRepository.findAll();
		for (Address address : it) {
			addressList.add(address);
		}
		return addressList;	
	}
	
}	
