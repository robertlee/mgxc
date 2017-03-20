package com.uletian.ultcrm.business.repo;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.uletian.ultcrm.business.entity.Seat;

@RepositoryRestResource(collectionResourceRel = "seat", path = "seat")
public interface SeatRepository extends PagingAndSortingRepository<Seat, Long>{
	
	
	/**
	 * 查询某教室的座位信息
	 * @return
	 */
	@Query("from Seat where schedule.id = ?1 and status = 1")
	public List<Seat> getSeatListByRoom(Long roomId);
	
	@Query("from Seat where id = ?1")
	public Seat getSeatById(Long id);
	

	
	@Query("from Seat where status = 1 and name = ?1 and schedule.id = ?2")
	public Seat getSeatByName(String name,Long scheId);
	
}
