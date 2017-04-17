package com.uletian.ultcrm.business.repo;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.uletian.ultcrm.business.entity.Store;
import com.uletian.ultcrm.business.entity.TimeSegment;

@RepositoryRestResource(collectionResourceRel = "timeSegment", path = "timeSegment")
public interface TimeSegmentRepository extends PagingAndSortingRepository<TimeSegment, Long>{

	/**
	 * 查询某段时间内的门店预约时间
	 * @param storeId 门店ID
	 * @param startdate 开始日期
	 * @param enddate 结束日期
	 * @return 已经预约过的数据
	 */
	 @Query("from TimeSegment where storeid = ?1 and dateSegment between ?2 and ?3") 
	 public List<TimeSegment> queryTimeSegmentInPeriodByStoreId(Long storeId, Date startdate, Date enddate);
	
	 @Query("from TimeSegment where coachid = ?1") 
	 public List<TimeSegment> findByCoachId(Long coachid);
	 
	 //public List<TimeSegment> findByCoachId(Long coachId);	 
	 
	 public TimeSegment findByStoreAndBusiTypeIdAndDateSegmentAndTimeSegment(Store store,Long busiTypeId, Date dateSegment,Long timeSegment);
	 
	 @Query("select ts from TimeSegment ts where ts.id in ?1")
	 public List<TimeSegment> findByIds(Collection<Long> ids);

//	 @Query("select id, coachid, dateSegment, timeSegment, busiTypeId from TimeSegment ts where ts.coachid = ?1 and ts.dateSegment=?2")
	 @Query("from TimeSegment ts where ts.coachid = ?1 and ts.dateSegment=?2")
	 public List<TimeSegment> queryTimeSegments(long coachid, Date appointDate);
}

