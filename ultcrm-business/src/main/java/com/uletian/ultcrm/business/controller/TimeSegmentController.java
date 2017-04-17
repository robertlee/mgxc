package com.uletian.ultcrm.business.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uletian.ultcrm.business.entity.Appointment;
import com.uletian.ultcrm.business.entity.BusinessType;
import com.uletian.ultcrm.business.entity.Customer;
import com.uletian.ultcrm.business.entity.Order;
import com.uletian.ultcrm.business.entity.Store;
import com.uletian.ultcrm.business.entity.TimeSegment;
import com.uletian.ultcrm.business.repo.AppointmentRepository;
import com.uletian.ultcrm.business.repo.BusinessTypeRepository;
import com.uletian.ultcrm.business.repo.CoachRepository;
import com.uletian.ultcrm.business.repo.CustomerRepository;
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
	private CustomerRepository customerRepository;
	
	@Autowired
	private OrderMessageServcie orderMessageServcie;
	
	@Autowired
	private BusinessTypeRepository businessTypeRepository;
    @Autowired
    private CoachRepository coachRepository;
	
	private String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@RequestMapping(value = "/timesegment/store/{coachId}", method = RequestMethod.GET)
	public List<SegmentData> getTimeSegmentList(@PathVariable("coachId") Long coachId) {
		
		//Store store = storeRepository.findOne(storeId);
		List<SegmentData> currentTimes = new ArrayList<SegmentData>(0);
				
		Calendar c = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		end.add(Calendar.DAY_OF_MONTH, 5); //开发T+4业务
		List<TimeSegment> segments = timeSegmentRepository.findByCoachId(coachId);		
		for (int i = 0; i < 5; i++) {
			SegmentData date = new SegmentData();
			date.setDate(sdf.format(c.getTime()));
			date.setWeek(weekOfDays[c.get(Calendar.DAY_OF_WEEK)-1]);
			List<TimeSegment> currentSegment = getDateSegment(segments, date);			
			int countTemp = 0;
			for (int j = 0; j < 16; j++) {
				SegmentTime time = new SegmentTime();
				time.setTime(j + 6);
				for (TimeSegment timeSegment : currentSegment) {					
					if (time.getTime().intValue()==timeSegment.getTimeSegment()) {
						time.setCount(timeSegment.getCount().intValue());					
					}
					if (time.getCount() == null) {
						time.setCount(0);
					}
				}
				
				// 检查这个时间是否可用，提前8小时预约
				if (i<=1) {
					time.setEnable(false);
					time.setPastTime(true);
				}				
				//Robert Lee 2016-5-3
				int currSegment = time.getTime();
				if( currSegment>0){
					if (time.getCount() != null && time.getCount()>=1) { //大于1次时
						countTemp = time.getCount();
					}
					if(countTemp>0){
						time.setEnable(false);
						time.setUsedAll(true);
						countTemp--;
					}
						
					
				}
				date.getSegments().add(time);
				
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
			//robert Lee
			//if (i ==0 ) {
			//	date.setPdate(null);
			//	date.setPweek(null);
			//}
			
			if (i ==4) {
				date.setNdate(null);
				date.setNweek(null);
			}
			
			//当前时间算完后把当前时间设置为第二天
			c.add(Calendar.DAY_OF_MONTH, 1);
		}
		return currentTimes;
	}

	@RequestMapping(value = "/timesegment/{coachid}/{dateSegment}", method = RequestMethod.GET)
	public List<TimeSegment> getTimeSegmentList(
		@PathVariable("coachid") Long coachid, 
		@PathVariable("dateSegment") String dateSegment) {
		try {
			Date appointDate = sdf.parse(dateSegment);
			return timeSegmentRepository.queryTimeSegments(coachid, appointDate);
		} catch (ParseException e) {
			logger.error("Failed to appoint coach", e);
			return null;
		}
	}

	@Transactional
	@RequestMapping(value="/createTimeSegment", method=RequestMethod.POST)
	public Map<String,Object> createTimeSegment(@RequestBody Map<String,String> timeSegmentData) {
		HashMap<String,Object> result = new HashMap<String,Object>();
		try {
			long coachid = Long.parseLong(timeSegmentData.get("coachid"));
			long customerid = Long.parseLong(timeSegmentData.get("customerid"));
			long timeSegmentStart = Long.parseLong(timeSegmentData.get("timeSegment"));
			int count = Integer.parseInt(timeSegmentData.get("count"));
			if(timeSegmentStart+count > 22) {
				throw new Exception("appoint time more than 22.");
			}
			int existTodayAppontment = appointmentRepository.findTodayAppointByCustomerId(customerid);
			if(existTodayAppontment > 0){
				result.put("msg", "预约失败,一天只能预约一次");
				result.put("code", "error");
				return result;
			}
			Date dateSegment = null;
			dateSegment = sdf.parse(timeSegmentData.get("date_segment"));
			// 获取当前时间
			long currentTime = System.currentTimeMillis();
			// 客户信息
			Customer customer = customerRepository.findById(customerid);
			// 报名时的订单信息
			//Order order = orderRepository.findOrderByCustomerId(customerid);
			List<Order> orders = orderRepository.findOrderByCustomerId(customerid);
			if(null==orders || orders.isEmpty()) {
				throw new Exception("你还没有购买报名订单");
			}
			Order order = orders.get(0);
			// 报名时的业务
//			BusinessType businessType = businessTypeRepository.getBusinessById(order.getBusiTypeId());
			String orderId =null;
			boolean hasCard = false;//是否有卡券
			// 创建订单，学时学车
			if(10==order.getBusitypeid() || 11==order.getBusitypeid()) {
				hasCard = true;
				Long tmp = System.currentTimeMillis()/1000;
		    	Random random = new Random();
		    	int num = (int)(random.nextDouble()*(10000 - 1000) + 1000);
		    	orderId = tmp.toString() + num;
				Order newOrder = new Order();
				newOrder.setOrderId(orderId);
				newOrder.setCustomer(customer);
				newOrder.setCustomername(customer.getNickname());
				newOrder.setStatus(1);
				newOrder.setCreateTime(new Date());
				newOrder.setLastUpdateTime(new Date());
				String contactphone = customer.getPhone();
				if(contactphone != null && !"".equals(contactphone)){
					newOrder.setContactphone(contactphone.toString());
				}
				Long coachType = order.getBusitypeid();
				if(10 == coachType) {
					coachType = 40L;
				} else {
					coachType = 41L;
				}
				newOrder.setBusitypeid(coachType);
				BusinessType businessTypeTmp = businessTypeRepository.getBusinessById(coachType);
				Float price = businessTypeTmp.getCost();
				if(price != null && !"".equals(price)) {
					newOrder.setPrice(new BigDecimal(price));
				}
				newOrder.setTotalPrice(new BigDecimal(price*count));
				newOrder.setClassid(1L);
				orderRepository.save(newOrder);
				
				// 创建教练预约
				TimeSegment timeSegment = new TimeSegment();
				timeSegment.setBusiTypeId(coachType);
				timeSegment.setCoachid(coachid);
				timeSegment.setDateSegment(dateSegment);
				timeSegment.setTimeSegment(timeSegmentStart);
				timeSegment.setCount((long) count);
				timeSegment.setCreateTime(new Timestamp(currentTime));
				timeSegment.setLastUpdateTime(new Timestamp(currentTime));
				timeSegment.setCreateUserId(0L);
				timeSegment.setLastUpdateUserid(0L);
				timeSegmentRepository.save(timeSegment);
				Appointment appointment = new Appointment();
				appointment.setCoachId(coachid);
				appointment.setCustomerId(customerid);
				appointment.setOrder(newOrder);
	//			appointment.setTimeSegmentId(timeSegmentId);
				appointment.setTimeSegment(timeSegment);
				appointment.setCreateTime(new Timestamp(currentTime));
				appointment.setLastUpdateTime(new Timestamp(currentTime));
				appointment.setCreateUserId(0);
				appointment.setLastUpdateUserid(0);
				appointmentRepository.save(appointment);
				
			} else {
				Long tmp = System.currentTimeMillis()/1000;
		    	Random random = new Random();
		    	int num = (int)(random.nextDouble()*(10000 - 1000) + 1000);
		    	orderId = tmp.toString() + num;
				Order newOrder = new Order();
				newOrder.setOrderId(orderId);
				newOrder.setCustomer(customer);
				newOrder.setCustomername(customer.getNickname());
				newOrder.setStatus(1);
				newOrder.setCreateTime(new Date());
				newOrder.setLastUpdateTime(new Date());
				String contactphone = customer.getPhone();
				if(contactphone != null && !"".equals(contactphone)){
					newOrder.setContactphone(contactphone.toString());
				}
				
				newOrder.setPrice(new BigDecimal(0));
				newOrder.setTotalPrice(new BigDecimal(0));
				newOrder.setClassid(1L);
				orderRepository.save(newOrder);
				// 创建教练预约
				TimeSegment timeSegment = new TimeSegment();
				timeSegment.setBusiTypeId(order.getBusitypeid());
				timeSegment.setCoachid(coachid);
				timeSegment.setDateSegment(dateSegment);
				timeSegment.setTimeSegment(timeSegmentStart);
				timeSegment.setCount((long) count);
				timeSegment.setCreateTime(new Timestamp(currentTime));
				timeSegment.setLastUpdateTime(new Timestamp(currentTime));
				timeSegment.setCreateUserId(0L);
				timeSegment.setLastUpdateUserid(0L);
				timeSegmentRepository.save(timeSegment);
			//		long timeSegmentId = timeSegmentResult.getId();
				Appointment appointment = new Appointment();
				appointment.setCoachId(coachid);
				appointment.setCustomerId(customerid);
			//		appointment.setOrder(newOrder);
			//		appointment.setTimeSegmentId(timeSegmentId);
				appointment.setTimeSegment(timeSegment);
				appointment.setCreateTime(new Timestamp(currentTime));
				appointment.setLastUpdateTime(new Timestamp(currentTime));
				appointment.setCreateUserId(0);
				appointment.setLastUpdateUserid(0);
				appointmentRepository.save(appointment);
			}

            /**
             * 修改人：吴云
             * 修改时间：2017-04-12
             * 修改内容：预约成功后，给预约教练累积积分，1元1积分
             */
            BigDecimal price = order.getTotalPrice();
            int socre = 0;
            if(price != null){
                socre = price.intValue();
            }
            logger.info("教练" + coachid + "累积积分为" + socre);
            coachRepository.updateCoachScore(socre,coachid);
            
			String info = "Success to create appoint coach to train, please arrive at [" + dateSegment + ", " + dateSegment+count + "]";
			logger.info(info);			
			result.put("orderId", orderId);
			result.put("hasCard", hasCard);
			result.put("msg", info);
			result.put("busitypeid", order.getBusitypeid());
			result.put("code", "200");
			return result;
		} catch(Exception e) {
			logger.error("预约失败!!!!!!!!!!!!!!", e);			
			result.put("msg", "预约失败Failed to create coach, reason: " + e.getMessage());
			result.put("code", "error");		
			return result;
		}
	}
	
	@Transactional
	@RequestMapping(value="/createTimeSegmentByOrder", method=RequestMethod.POST)
	public Map<String,Object> createTimeSegmentByOrder(@RequestBody Map<String,String> timeSegmentData) {
		HashMap<String,Object> result = new HashMap<String,Object>();
		try {
			long coachid = Long.parseLong(timeSegmentData.get("coachid"));
			long customerid = Long.parseLong(timeSegmentData.get("customerid"));
			long timeSegmentStart = Long.parseLong(timeSegmentData.get("timeSegment"));
			int count = Integer.parseInt(timeSegmentData.get("count"));
			long orderid = Long.parseLong(timeSegmentData.get("orderId"));
			
			if(timeSegmentStart+count > 22) {
				throw new Exception("appoint time more than 22.");
			}
			int existTodayAppontment = appointmentRepository.findTodayAppointByCustomerId(customerid);
			if(existTodayAppontment > 0){
				result.put("msg", "预约失败,一天只能预约一次");
				result.put("code", "error");
				return result;
			}
			Date dateSegment = null;
			dateSegment = sdf.parse(timeSegmentData.get("date_segment"));
			// 获取当前时间
			long currentTime = System.currentTimeMillis();
			// 客户信息
			Customer customer = customerRepository.findById(customerid);
			// 报名时的订单信息
			//Order order = orderRepository.findOrderByCustomerId(customerid);
			List<Order> orders = orderRepository.findOrderByCustomerId(customerid);
			if(null==orders || orders.isEmpty()) {
				throw new Exception("你还没有购买报名订单");
			}
			Order order = orders.get(0);
			// 报名时的业务
//			BusinessType businessType = businessTypeRepository.getBusinessById(order.getBusiTypeId());
			String orderId =null;
			boolean hasCard = false;//是否有卡券
			// 创建订单，学时学车
			if(10==order.getBusitypeid() || 11==order.getBusitypeid()) {
				hasCard = true;
				Order newOrder = orderRepository.findByOrderId(orderid);
//				Long tmp = System.currentTimeMillis()/1000;
//		    	Random random = new Random();
//		    	int num = (int)(random.nextDouble()*(10000 - 1000) + 1000);
//		    	orderId = tmp.toString() + num;
//				Order newOrder = new Order();
//				newOrder.setOrderId(orderId);
//				newOrder.setCustomer(customer);
//				newOrder.setCustomername(customer.getNickname());
//				newOrder.setStatus(1);
//				newOrder.setCreateTime(new Date());
//				newOrder.setLastUpdateTime(new Date());
//				String contactphone = customer.getPhone();
//				if(contactphone != null && !"".equals(contactphone)){
//					newOrder.setContactphone(contactphone.toString());
//				}
//				Long coachType = order.getBusitypeid();
//				if(10 == coachType) {
//					coachType = 40L;
//				} else {
//					coachType = 41L;
//				}
//				newOrder.setBusitypeid(coachType);
//				BusinessType businessTypeTmp = businessTypeRepository.getBusinessById(coachType);
//				Float price = businessTypeTmp.getCost();
//				if(price != null && !"".equals(price)) {
////					newOrder.setPrice(new BigDecimal(price));
//				}
//				newOrder.setTotalPrice(new BigDecimal(price*count));
//				newOrder.setClassid(1L);
//				orderRepository.save(newOrder);
				
				//使用支付过来的
				Long coachType = newOrder.getBusitypeid();
				
				// 创建教练预约
				TimeSegment timeSegment = new TimeSegment();
				timeSegment.setBusiTypeId(coachType);
				timeSegment.setCoachid(coachid);
				timeSegment.setDateSegment(dateSegment);
				timeSegment.setTimeSegment(timeSegmentStart);
				timeSegment.setCount((long) count);
				timeSegment.setCreateTime(new Timestamp(currentTime));
				timeSegment.setLastUpdateTime(new Timestamp(currentTime));
				timeSegment.setCreateUserId(0L);
				timeSegment.setLastUpdateUserid(0L);
				timeSegmentRepository.save(timeSegment);
				Appointment appointment = new Appointment();
				appointment.setCoachId(coachid);
				appointment.setCustomerId(customerid);
				appointment.setOrder(newOrder);
	//			appointment.setTimeSegmentId(timeSegmentId);
				appointment.setTimeSegment(timeSegment);
				appointment.setCreateTime(new Timestamp(currentTime));
				appointment.setLastUpdateTime(new Timestamp(currentTime));
				appointment.setCreateUserId(0);
				appointment.setLastUpdateUserid(0);
				appointmentRepository.save(appointment);
				
			} else {
				Order newOrder = orderRepository.findByOrderId(orderid);
//				Long tmp = System.currentTimeMillis()/1000;
//		    	Random random = new Random();
//		    	int num = (int)(random.nextDouble()*(10000 - 1000) + 1000);
//		    	orderId = tmp.toString() + num;
//				Order newOrder = new Order();
//				newOrder.setOrderId(orderId);
//				newOrder.setCustomer(customer);
//				newOrder.setCustomername(customer.getNickname());
//				newOrder.setStatus(1);
//				newOrder.setCreateTime(new Date());
//				newOrder.setLastUpdateTime(new Date());
//				String contactphone = customer.getPhone();
//				if(contactphone != null && !"".equals(contactphone)){
//					newOrder.setContactphone(contactphone.toString());
//				}
//				
//				newOrder.setPrice(new BigDecimal(0));
//				newOrder.setTotalPrice(new BigDecimal(0));
//				newOrder.setClassid(1L);
//				orderRepository.save(newOrder);
				
				// 创建教练预约
				TimeSegment timeSegment = new TimeSegment();
				timeSegment.setBusiTypeId(order.getBusitypeid());
				timeSegment.setCoachid(coachid);
				timeSegment.setDateSegment(dateSegment);
				timeSegment.setTimeSegment(timeSegmentStart);
				timeSegment.setCount((long) count);
				timeSegment.setCreateTime(new Timestamp(currentTime));
				timeSegment.setLastUpdateTime(new Timestamp(currentTime));
				timeSegment.setCreateUserId(0L);
				timeSegment.setLastUpdateUserid(0L);
				timeSegmentRepository.save(timeSegment);
				Appointment appointment = new Appointment();
				appointment.setCoachId(coachid);
				appointment.setCustomerId(customerid);
				appointment.setOrder(newOrder);
				appointment.setTimeSegment(timeSegment);
				appointment.setCreateTime(new Timestamp(currentTime));
				appointment.setLastUpdateTime(new Timestamp(currentTime));
				appointment.setCreateUserId(0);
				appointment.setLastUpdateUserid(0);
				appointmentRepository.save(appointment);
			}
            
            /**
             * 修改人：吴云
             * 修改时间：2017-04-12
             * 修改内容：预约成功后，给预约教练累积积分，1元1积分
             */
            BigDecimal price = order.getTotalPrice();
            int socre = 0;
            if(price != null){
                socre = price.intValue();
            }
            logger.info("教练" + coachid + "累积积分为" + socre);
            coachRepository.updateCoachScore(socre,coachid);

			String info = "Success to create appoint coach to train, please arrive at [" + dateSegment + ", " + dateSegment+count + "]";
			logger.info(info);			
			result.put("orderId", orderId);
			result.put("hasCard", hasCard);
			result.put("msg", info);
			result.put("busitypeid", order.getBusitypeid());
			result.put("code", "200");
			return result;
		} catch(Exception e) {
			logger.error("预约失败!!!!!!!!!!!!!!", e);			
			result.put("msg", "预约失败Failed to create coach, reason: " + e.getMessage());
			result.put("code", "error");		
			return result;
		}
	}
	//修改预约时间段
	@Transactional
	@RequestMapping(value="/updateTimeSegment", method = RequestMethod.POST)
	public Map<String,Object> updateTimeSegment(@RequestBody Map<String,String> timeSegmentData) {
		HashMap<String,Object> result = new HashMap<String,Object>();
		try {
			long orderid = Long.parseLong(timeSegmentData.get("orderid"));
			long timeSegmentStart = Long.parseLong(timeSegmentData.get("timeSegment"));
			int count = Integer.parseInt(timeSegmentData.get("count"));
			if(timeSegmentStart+count > 22) {
				throw new Exception("appoint time more than 22.");
			}
			Date dateSegment = null;
			dateSegment = sdf.parse(timeSegmentData.get("date_segment"));
			// 获取当前时间
			long currentTime = System.currentTimeMillis();
			// 报名时的订单信息
			Order order = orderRepository.findById(orderid);
			TimeSegment oldSegment = order.getAppointment().getTimeSegment();
			if(count != oldSegment.getCount()) {
				throw new Exception("appoint time is not equal with old count for timeSegment table.");
			}
			oldSegment.setDateSegment(dateSegment);
			oldSegment.setTimeSegment(timeSegmentStart);
			oldSegment.setLastUpdateTime(new Timestamp(currentTime));
			oldSegment.setLastUpdateUserid(0L);
			timeSegmentRepository.save(oldSegment);
			String info = "Success to update appoint coach to train, please arrive at [" + dateSegment + ", " + dateSegment+count + "]";
			logger.info(info);
			result.put("msg", info);
			result.put("code", "200");
			return result;
		} catch (Exception e) {
			logger.error("Failed to create coach", e);
			result.put("msg", "Failed to update coach, reason: " + e.getMessage());
			result.put("code", "error");		
			return result;
		}
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
//			Log.error("修改预约时间失败", e.getMessage());
			logger.error("修改预约时间失败", e.getMessage());
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
		private Boolean choose = false;
		
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

		public Boolean getChoose() {
			return choose;
		}

		public void setChoose(Boolean choose) {
			this.choose = choose;
		}
		
	}
	
	@RequestMapping(value = "/searchTimesegmentsByOrderId/{orderId}", method = RequestMethod.GET)
	public List<SegmentData> getTimeSegmentsByOrderId(@PathVariable("orderId") Long orderId) {
		
		Order order = orderRepository.findById(orderId);
		Long coachId = order.getAppointment().getCoachId();
//		Date dateSegment = order.getAppointment().getTimeSegment().getDateSegment();
		Long timeSegmentId = order.getAppointment().getTimeSegment().getId();
		
		//Store store = storeRepository.findOne(storeId);
		List<SegmentData> currentTimes = new ArrayList<SegmentData>(0);
		
		Calendar c = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		end.add(Calendar.DAY_OF_MONTH, 5); //开发T+4业务
		List<TimeSegment> segments = timeSegmentRepository.findByCoachId(coachId);
		for (int i = 0; i < 5; i++) {
			SegmentData date = new SegmentData();
			date.setDate(sdf.format(c.getTime()));
			date.setWeek(weekOfDays[c.get(Calendar.DAY_OF_WEEK)-1]);
			List<TimeSegment> currentSegment = getDateSegment(segments, date);
			int countTemp = 0;
			boolean isCurrentTimeSegment = false;
			for (int j = 0; j < 16; j++) {
				SegmentTime time = new SegmentTime();
				time.setTime(j + 6);
				time.setEnable(true);
				for (TimeSegment timeSegment : currentSegment) {					
					if (time.getTime().intValue()==timeSegment.getTimeSegment()) {
						time.setCount(timeSegment.getCount().intValue());
						// 判断是否是当前订单所对应的预约时段
						if(timeSegmentId == timeSegment.getId()) {
							isCurrentTimeSegment = true;
						}
					}
					if (time.getCount() == null) {
						time.setCount(0);
					}
				}
				
				//Robert Lee 2016-5-3
				int currSegment = time.getTime();
				if( currSegment>0){
					if (time.getCount() != null && time.getCount()>=1) { //大于1次时
						countTemp = time.getCount();
					}
					if(countTemp>0){
						if(isCurrentTimeSegment) {
							time.setEnable(true);
						} else {
							time.setEnable(false);
						}
						time.setUsedAll(true);
						countTemp--;
					} 
					if(0 == countTemp) {
						isCurrentTimeSegment = false;
					}
				}
				// 检查这个时间是否可用，提前8小时预约
				// 提前2天预约
				if (i<=1) {
					time.setEnable(false);
					time.setPastTime(true);
				}
				
				date.getSegments().add(time);
				
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
			//robert Lee
			//if (i ==0 ) {
			//	date.setPdate(null);
			//	date.setPweek(null);
			//}
			
			if (i ==4) {
				date.setNdate(null);
				date.setNweek(null);
			}
			
			//当前时间算完后把当前时间设置为第二天
			c.add(Calendar.DAY_OF_MONTH, 1);
		}
		return currentTimes;
	}
}
