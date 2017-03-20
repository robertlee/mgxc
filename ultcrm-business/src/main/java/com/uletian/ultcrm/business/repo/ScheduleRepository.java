package com.uletian.ultcrm.business.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.uletian.ultcrm.business.entity.Schedule;

@RepositoryRestResource(collectionResourceRel = "schedule", path = "schedule")
public interface ScheduleRepository extends PagingAndSortingRepository<Schedule, Long>{
	/**
	 * 查询课程安排
	 * @return
	 */
	@Query("from Schedule where id = ?1")
	public Schedule getScheduleById(Long classId);
	
	@Query("from Schedule s where s.businessType.id = ?1 and s.status = ?2 and s.startBatch = ?3 and s.storeId = ?4")
	public List<Schedule> getScheduleByClassId(Long classId,String status,String startBatch,Long storeId); 
}
