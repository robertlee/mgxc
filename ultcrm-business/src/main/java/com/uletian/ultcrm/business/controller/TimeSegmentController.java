package com.uletian.ultcrm.business.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.crsh.console.jline.internal.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uletian.ultcrm.business.entity.Appointment;
import com.uletian.ultcrm.business.entity.Order;
import com.uletian.ultcrm.business.entity.Store;
import com.uletian.ultcrm.business.entity.TimeSegment;
import com.uletian.ultcrm.business.repo.AppointmentRepository;
import com.uletian.ultcrm.business.repo.OrderRepository;
import com.uletian.ultcrm.business.repo.StoreRepository;
import com.uletian.ultcrm.business.repo.TimeSegmentRepository;
import com.uletian.ultcrm.business.service.OrderMessageServcie;
import com.uletian.ultcrm.business.value.Result;
/**
 * 时间段选择模块
 * 
 * @author Administrator
 *
 */
@RestController
public class TimeSegmentController {	
	private static final Logger logger = LoggerFactory.getLogger(TimeSegmentController.class);	
	@Autowired
	private TimeSegmentRepository timeSegmentRepository;
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private StoreRepository storeRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderMessageServcie orderMessageServcie;
	
	private String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
@RequestMapping(value = "/timesegment/store/{storeId}/{busiTypeId}", method = RequestMethod.GET)
	public List<SegmentData> getTimeSegmentList(
			@PathVariable("storeId") Long storeId,
			@PathVariable("busiTypeId") Long busiTypeId) {
		
		Store store = storeRepository.findOne(storeId);
		List<SegmentData> currentTimes = new ArrayList<SegmentData>(0);
				
		Calendar c = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		end.add(Calendar.DAY_OF_MONTH, 5); //开发T+4业务
		List<TimeSegment> segments = timeSegmentRepository.findByStoreAndBusiTypeId(store,busiTypeId);		
		for (int i = 0; i < 5; i++) {
			SegmentData date = new SegmentData();
			date.setDate(sdf.format(c.getTime()));
			date.setWeek(weekOfDays[c.get(Calendar.DAY_OF_WEEK)-1]);
			List<TimeSegment> currentSegment = getDateSegment(segments, date);			
			for (int j = 0; j < 16; j++) {
				SegmentTime time = new SegmentTime();
				time.setTime(j + 6);
				for (TimeSegment timeSegment : currentSegment) {					
					if (time.getTime().intValue()==timeSegment.getTimeSegment()) {
						time.setCount(timeSegment.getCount().intValue());					
					}
				}
				// 检查这个时间是否可用，提前8小时预约
				if (i<=1) {
					time.setEnable(false);
					time.setPastTime(true);
				}				
				//Robert Lee 2016-5-3
				int currSegment = time.getTime();
				if( currSegment>0)
				{
					if (time.getCount() != null && time.getCount()>=1) { //大于1次时
						time.setEnable(false);
						time.setUsedAll(true);
					}
				}
				date.getSegments().add(time);
				if (time.getCount() == null) {
					time.setCount(0);
				}
			}
			currentTimes.add(date);
			
			//计算出前一天与后一天，今天的前一天和最后一天的后一天为null
			Calendar p = Calendar.getInstance();
			p.setTime(c.getTime());
			p.add(Calendar.DAY_OF_MONTH, -1);
			date.setPdate(sdf.format(p.getTime()));
			date.setPweek(weekOfDays[p.get(Calendar.DAY_OF_WEEK)-1]);
			
			Calendar n = Calendar.getInstance();
			n.setTime(c.getTime());
			n.add(Calendar.DAY_OF_MONTH, 1);
			date.setNdate(sdf.format(n.getTime()));
			date.setNweek(weekOfDays[n.get(Calendar.DAY_OF_WEEK)-1]);
			
			if (i ==0 ) {
				date.setPdate(null);
				date.setPweek(null);
			}
			
			if (i == 4) {
				date.setNdate(null);
				date.setNweek(null);
			}
			
			//当前时间算完后把当前时间设置为第二天
			c.add(Calendar.DAY_OF_MONTH, 1);
		}
		return currentTimes;
	}
	
	@RequestMapping(value = "/addTimeCount", method = RequestMethod.POST)
	public TimeSegment addTimeCount(@RequestBody TimeSegment timeSegment) {
		//timeSegment
		Store store = storeRepository.findOne(timeSegment.getStoreId());
		
		TimeSegment timeSegmentOld = timeSegmentRepository.findByStoreAndBusiTypeIdAndDateSegmentAndTimeSegment(store, 
				                                                         timeSegment.getBusiTypeId(),
				                                                         timeSegment.getDateSegment(), 
				                                                         timeSegment.getTimeSegment());
		
		if (timeSegmentOld !=null) {
			timeSegmentOld.setCount(timeSegmentOld.getCount()+1);
			timeSegmentRepository.save(timeSegmentOld);
			return timeSegmentOld;
		}
		else {
			timeSegment.setCount(1L);
			timeSegment.setStore(store);
			timeSegmentRepository.save(timeSegment);
			return timeSegment;
		}
		
	}
	
	@RequestMapping(value = "/timesegment", method = RequestMethod.PUT)
	public Result updateTimesegment(@RequestBody Map<String,String> timesegmentMap){
		String paramOrderIdStr = timesegmentMap.get("orderId");
		String paramDateStr = timesegmentMap.get("date");
		String paramTimeStr = timesegmentMap.get("time");
		logger.info("\nparamOrderIdStr:["+paramOrderIdStr
				+ "]\nparamDateStr:["+paramDateStr
				+ "]\nparamTimeStr:["+paramTimeStr
				+ "]");
		Result result = new Result();
		Order order = orderRepository.findOne(Long.decode(paramOrderIdStr));
		try {
			Date newDate = sdf.parse(paramDateStr);
			Long newTime = Long.decode(paramTimeStr);
			TimeSegment oldSegment = order.getAppointment().getTimeSegment();
			long count = oldSegment.getCount().intValue();
			count--;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Timestamp nowTime = new Timestamp(System.currentTimeMillis());
			if ( count == 0) {
				TimeSegment newSegment = timeSegmentRepository.findByStoreAndBusiTypeIdAndDateSegmentAndTimeSegment(
						oldSegment.getStore(), oldSegment.getBusiTypeId(), newDate, newTime);
				if (newSegment == null) {
					newSegment = new TimeSegment();
					newSegment.setCount(1L);
				}else{
					newSegment.setCount(newSegment.getCount() + 1);
				}
				newSegment.setStore(oldSegment.getStore());
				newSegment.setBusiTypeId(oldSegment.getBusiTypeId());
				newSegment.setDateSegment(newDate);
				newSegment.setTimeSegment(newTime);
				newSegment.setCreateTime(nowTime);
				newSegment.setLastUpdateTime(nowTime);
				timeSegmentRepository.save(newSegment);
				Appointment appointment = order.getAppointment();
				appointment.setTimeSegment(newSegment);
				appointmentRepository.save(appointment);
				oldSegment.setCount(count);
				timeSegmentRepository.save(oldSegment);
			}else{
				oldSegment.setCount(count);
				timeSegmentRepository.save(oldSegment);
			}
			//createOrderToCrm(order.getCustomer().getId().toString(), order.getCustomer().getName(), order.getCustomer().getPhone(), paramDateStr, paramTimeStr, 
			//		order.getId(), order.getAppointment().getStore().getId().toString(), order.getDescription(), order.getTech() == null ? "":order.getTech().getTechlevelno());
			result.setCode(0);
			result.setResult(true);
			result.setMsg("修改成功");
		} catch (Exception e) {
			Log.error("修改预约时间失败", e.getMessage());
			result.setResult(false);
		}
		return result;
	}

	private List<TimeSegment> getDateSegment(List<TimeSegment> segments, SegmentData date){
		List<TimeSegment> timeSegments = new ArrayList<TimeSegment>(0);
		for (int i = 0; i < segments.size(); i++) {
			TimeSegment ts = segments.get(i);
			String str = sdf.format(ts.getDateSegment());
			if (date.getDate().equals(str)) {
				System.out.println(str);
				timeSegments.add(ts);
			}
		}
		return timeSegments;
	}
	
	public void createOrderToCrm(String customerId,String custName,String custPhone,String sdate,String stime,
			Long orderId,String storeId,String descrip,String platNo)
	{
		StringBuffer xmlstrbuf = orderMessageServcie.orderStatusInfoJAVAToXML(customerId,custName, 
				custPhone, sdate, stime, 
				orderId, storeId, descrip,platNo,OrderMessageServcie.ACTION_UPDATE_ORDER_DATETIME,null);
		
		//发送消息
		orderMessageServcie.sendMessage(xmlstrbuf.toString());
	}
	private class SegmentData {
		private String date;
		private String pdate;
		private String ndate;
		private String week;
		
		private String pweek;
		public String getPweek() {
			return pweek;
		}

		public void setPweek(String pweek) {
			this.pweek = pweek;
		}

		public String getNweek() {
			return nweek;
		}

		public void setNweek(String nweek) {
			this.nweek = nweek;
		}

		private String nweek;
		
		private ArrayList<SegmentTime> segments = new ArrayList<TimeSegmentController.SegmentTime>(0);

		public String getPdate() {
			return pdate;
		}

		public void setPdate(String pdate) {
			this.pdate = pdate;
		}

		public String getNdate() {
			return ndate;
		}

		public void setNdate(String ndate) {
			this.ndate = ndate;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getWeek() {
			return week;
		}

		public void setWeek(String week) {
			this.week = week;
		}
		public ArrayList<SegmentTime> getSegments() {
			return segments;
		}
		public void setSegments(ArrayList<SegmentTime> segments) {
			this.segments = segments;
		}
	}

	private class SegmentTime {
		private Integer time;
		private Integer count;
		private Boolean enable;
		private Boolean usedAll = false;
		private Boolean pastTime = false;
		
		
		public SegmentTime() {
			this.enable = true;
		}

		public Integer getTime() {
			return time;
		}

		public void setTime(Integer time) {
			this.time = time;
		}

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		/**
		 * @return the enable
		 */
		public Boolean getEnable() {
			return enable;
		}

		/**
		 * @param enable the enable to set
		 */
		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public Boolean getUsedAll() {
			return usedAll;
		}

		public void setUsedAll(Boolean usedAll) {
			this.usedAll = usedAll;
		}

		public Boolean getPastTime() {
			return pastTime;
		}

		public void setPastTime(Boolean pastTime) {
			this.pastTime = pastTime;
		}
	}
}
