package com.uletian.ultcrm.business.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.uletian.ultcrm.business.entity.BusinessType;


@RepositoryRestResource(collectionResourceRel = "businessType", path = "businessType")
public interface BusinessTypeRepository extends PagingAndSortingRepository<BusinessType, Long>{
	/**
	 * 查询课程数据
	 * @return
	 */

	
	@Query("from BusinessType where id = ?1")
	public BusinessType getBusinessById(Long id); 
	
	@Query("from BusinessType")
	public List<BusinessType> getBusinessAllList();
}
