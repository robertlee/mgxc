/**
 * Copyright &copy; 2014 uletian All rights reserved
 */
package com.uletian.ultcrm.business.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.uletian.ultcrm.business.entity.Coach;

/**
 * 
 * @author robertxie
 * 
 */
@RepositoryRestResource(collectionResourceRel = "coach", path = "coach")
public interface CoachRepository extends  PagingAndSortingRepository<Coach, Long>{	

	@Query("from Coach")
	public List<Coach> getCoachAllList();	
	@Query("from Coach where servicestore = ?1")
	public List<Coach> getCoachAllListByServiceStore(String serviceStore);	

	@Query("from Coach where id = ?1")
	public Coach getCoachById(Long id); 
	
	@Query(value="SELECT * FROM coach order by score desc LIMIT 0,3;",nativeQuery=true)
    public List<Coach> getCoachFirst3();
}
