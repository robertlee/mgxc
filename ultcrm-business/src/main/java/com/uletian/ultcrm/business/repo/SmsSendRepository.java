package com.uletian.ultcrm.business.repo;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.uletian.ultcrm.business.entity.SmsSend;

@RepositoryRestResource(collectionResourceRel = "smsSend", path = "smsSend")
public interface SmsSendRepository extends PagingAndSortingRepository<SmsSend, Long>{
	
	@Query("from SmsSend where ipaddress = ?1 and createTime > ?2")
	List<SmsSend> findIpCount(String ipaddress, Timestamp date);

	@Query("from SmsSend where phone = ?1 and createTime > ?2")
	List<SmsSend> findPhoneCount(String phone, Timestamp currentDate);
}
