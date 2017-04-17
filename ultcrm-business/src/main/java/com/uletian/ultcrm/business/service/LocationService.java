/**
 * @author huliangqing 2017-04-02
 */
package com.uletian.ultcrm.business.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uletian.ultcrm.business.entity.Address;
import com.uletian.ultcrm.business.entity.Store;
import com.uletian.ultcrm.business.repo.AddressRepository;
import com.uletian.ultcrm.business.repo.StoreRepository;

@Component
public class LocationService {
	
	private static Logger logger = Logger.getLogger(LocationService.class);
	
	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private AddressRepository addressRepository;

	public Map<String, Object> getStoreLocation(Long id,Integer itemType) {
		Map<String,Object> map = new HashMap<String,Object>();
		if(itemType == 1){
			//itemType等于1表示查询报名点的位置信息 
			Address address = addressRepository.findByAddressId(id);
			map.put("name", address.getName());
			map.put("fullAddress", address.getContent());
			//纬度
			map.put("latitude", address.getLocation().getLatitude());
			//经度
			map.put("longitude", address.getLocation().getLongitude());
		}else if(itemType ==2){
			//itemType等于2表示查询训练场的位置信息 
			Store store = storeRepository.getStoreById(id);
			map.put("name", store.getName());
			map.put("fullAddress", store.getFullAddress());
			//纬度
			map.put("latitude", store.getLocation().getLatitude());
			//经度
			map.put("longitude", store.getLocation().getLongitude());
		}
		return map;
	}
	
}	
