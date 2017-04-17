/**
 * Copyright &copy; 2014 uletian All rights reserved
 */
package com.uletian.ultcrm.business.controller;

import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;

//import net.sf.json.JSONObject;


import org.apache.log4j.Logger;
//import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import net.sf.json.JSONObject;

import com.pingplusplus.Pingpp;
import com.pingplusplus.model.Charge;
import com.pingplusplus.util.WxpubOAuth;
import com.uletian.ultcrm.business.entity.Order;
import com.uletian.ultcrm.business.repo.OrderRepository;
//import com.uletian.ultcrm.business.repo.StoreRepository;




import com.uletian.ultcrm.business.entity.Card;
import com.uletian.ultcrm.business.entity.CardBatch;
import com.uletian.ultcrm.business.entity.Customer;
import com.uletian.ultcrm.business.entity.Event;
import com.uletian.ultcrm.business.repo.CardBatchRepository;
import com.uletian.ultcrm.business.repo.CardRepository;
import com.uletian.ultcrm.business.entity.Store;
import com.uletian.ultcrm.business.entity.Customer;
import com.uletian.ultcrm.business.repo.CustomerRepository;
import com.uletian.ultcrm.business.repo.EventRepository;
import com.uletian.ultcrm.common.util.DateUtils;
import com.uletian.ultcrm.business.entity.BusinessType;
import com.uletian.ultcrm.business.repo.AppointmentRepository;
import com.uletian.ultcrm.business.repo.BusinessTypeRepository;
import com.uletian.ultcrm.business.service.TemplateQueueService;
import com.uletian.ultcrm.business.service.WeixinConfig;
import com.uletian.ultcrm.business.service.WeixinServletUtil;

import com.uletian.ultcrm.business.value.TemplateMessage;
import com.uletian.ultcrm.business.entity.MessageTemplate;
import com.uletian.ultcrm.business.entity.OrderComment;
import com.uletian.ultcrm.business.repo.OrderCommentRepository;
import com.uletian.ultcrm.business.service.SmsQueueService;



import org.springframework.beans.BeanUtils;

 /**
 * 
 * @author robertxie
 * 2015年9月10日
 */
@RestController
public class OrderController{
	private static Logger logger = Logger.getLogger(OrderController.class);
	@Value("${pingApiKeyTest}")
	private String pingApiKeyTest;
	@Value("${pingApiKey}")
	private String pingApiKey;
	@Value("${pingAppId}")
	private String pingAppId;
	@Value("${appId}")
	private String appId;		
	@Value("${pingSecKey}")
	private String pingSecKey;
    @Value("${appSecret}")
    private String appSecret;
    @Value("${assetTokenUrl}")
    private String assetTokenUrl;
    @Value("${weixinMsgUrl}")
    private String weixinMsgUrl;
	
	@Autowired
	private OrderRepository orderRepository;
	
	//@Autowired
	//private StoreRepository storeRepository;
    @Autowired
    private WeixinConfig weixinConfig;		
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private BusinessTypeRepository businessTypeRepository;
	@Autowired
	private TemplateQueueService templateQueueService;

	@Autowired
	private OrderCommentRepository orderCommentRepository;
    @Autowired
    private SmsQueueService smsQueueService;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private CardBatchRepository cardBatchRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

	private static Map<Long, String> imgUrlMap = new HashMap<Long,String>();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Integer PAGE_SIZE=10000;
	
	static {
		imgUrlMap.put(1L, "icon_a.png");//业务1
		imgUrlMap.put(2L, "icon_b.png");//业务2
		imgUrlMap.put(3L, "icon_c.png");//业务3
		imgUrlMap.put(4L, "icon_d.png");//业务4
		imgUrlMap.put(5L, "icon_e.png");//业务5
		imgUrlMap.put(6L, "icon_f.png");//业务6
	}
 
	@RequestMapping(value = "/createPayOrder/{jsonStr}", method = RequestMethod.GET)
	public Map<String,Object> getOrderList(@PathVariable("jsonStr")String jsonStr){
        Map<String, Object> map = new HashMap<String, Object>();
        Long oid = 0l;
       
        try {
            logger.info("开始创建支付订单，订单参数为：【" + jsonStr + "】 ");
            JSONObject jsonObj = JSONObject.fromObject(jsonStr);
            Object classId = jsonObj.get("classId");
            Object className = jsonObj.get("className");
            
            Object price = jsonObj.get("price");
            Object contactphone = jsonObj.get("contactphone");
            Object totalPrice = jsonObj.get("totalPrice");
            Object openId= jsonObj.get("openId");//用户openID
             Object type = jsonObj.get("payType");//支付类型
            String payType = "";
            if(type != null){
            	payType = type.toString();
            }           
            boolean bl = true;
            
            if(openId == null){
                bl = false;
                map.put("msg", "false");
                logger.error("未获取到用户openId");
                return map;
            }
            if(classId == null){
                bl = false;
                logger.error("课程编号为空");
                map.put("msg", "false");
                return map;
            }
            
            //根据openId获取用户信息
            Customer customer = customerRepository.findByOpenid(openId.toString());
            if(customer == null){
                bl = false;
                logger.error("根据openId获取用户信息失败");
                map.put("msg", "false");
                return map;
            }
            
            if(!"0".equals(totalPrice.toString()) && "appointment".equals(payType)){//预约教练，需要支付
                int existTodayAppontment = appointmentRepository.findTodayAppointByCustomerId(customer.getId());
                if(existTodayAppontment > 0){
                    map.put("msg", "预约失败,一天只能预约一次");
                    map.put("code", "error");
                    return map;
                }
                // 报名时的订单信息
                List<Order> orders = orderRepository.findOrderByCustomerId(customer.getId());
                if(null==orders || orders.isEmpty()) {
                    map.put("msg", "你还没有购买报名订单");
                    map.put("code", "error");
                    return map;
                }
            }
            
			//处理并发性用户下单事件(教练的时间端冲突)
			
			
		
            
            logger.info("生成订单编号");
            Long tmp = System.currentTimeMillis()/1000;
            Random random = new Random();
            int num = (int)(random.nextDouble()*(10000 - 1000) + 1000);
            String orderId = tmp.toString() + num;
            
            if(bl){
                logger.info("开始保存订单");
                Order o = new Order();
                o.setOrderId(orderId);
                o.setCustomer(customer);
                o.setCustomername(customer.getNickname());
                if(classId != null && !"".equals(classId)){
                    o.setClassid(Long.parseLong(classId.toString()));
                    o.setBusitypeid(Long.parseLong(classId.toString()));
                    o.setClassname(className.toString());
                }
                o.setStatus(1);
                o.setCreateTime(new Date());
                o.setLastUpdateTime(new Date());
                
                if(contactphone != null && !"".equals(contactphone)){
                    o.setContactphone(contactphone.toString());
                }
                if(price != null && !"".equals(price)){
                    o.setPrice(new BigDecimal(price.toString()));
                }
                if(totalPrice != null && !"".equals(totalPrice)){
                    o.setTotalPrice(new BigDecimal(totalPrice.toString()));
                }
                
                orderRepository.save(o);
                logger.info("创建支付订单成功 ");
                oid = Long.parseLong(o.getOrderId());
                
                logger.info("支付总价为：" + totalPrice.toString());
                if("0".equals(totalPrice.toString()) && "appointment".equals(payType)){//预约教练，不需要支付
                    map.put("msg", "true");
                }
                else{
                    map = startPay(oid,totalPrice.toString(),className.toString(),payType,customer);
                }
                map.put("orderId", oid);
            }
        } catch (Exception e) {
            map.put("msg", "false");
            logger.error("创建支付订单失败");
            logger.error(e.getMessage());
            delOrder(oid);
        }
        return map;
	}

    @RequestMapping(value = "/getMessageTemplate/{orderId}/{jsonStr}", method = RequestMethod.GET)
    public void  getMessageTemplate(@PathVariable("orderId")Long orderId,@PathVariable("jsonStr")String jsonStr){
        try {
            logger.info("开始消息模板参数为：【" + jsonStr + "】 ");
            String className = "";
            String totalPrice = "";
            String payType = "";
            String openId= "";
            
            JSONObject jsonObj = JSONObject.fromObject(jsonStr);
            if(jsonObj != null){
                if(jsonObj.containsKey("className")){
                    className = jsonObj.get("className").toString();
                }
                if(jsonObj.containsKey("totalPrice")){
                    totalPrice = jsonObj.get("totalPrice").toString();
                }
                if(jsonObj.containsKey("payType")){
                    payType = jsonObj.get("payType").toString();
                }
                if(jsonObj.containsKey("openId")){
                    openId = jsonObj.get("openId").toString();
                }
            }
            
            if(null != className && !"".equals(className) && null != totalPrice && !"".equals(totalPrice)
               && null != payType && !"".equals(payType) && null != openId && !"".equals(openId)){
                //根据openId获取用户信息
                Customer customer = customerRepository.findByOpenid(openId);
                if(customer == null){
                    logger.error("支付成功发送消息时根据openId获取用户信息失败");
                }
                else{
                    //短信、微信通知用户
                    msgSend(orderId,className,totalPrice,payType,customer);
                }
                
                //赠送200元优惠券
                giveCard(customer);
            }
            //notifycationSuccess(orderId,openId.toString(),className.toString(),startTime.toString());
        }
        
        catch (Exception e) {
            logger.error("发送模板消息失败！");
            logger.error(e.getMessage());
        }
        
    }

	
	@RequestMapping(value = "/delOrder/{orderId}", method = RequestMethod.GET)
	public String delOrder(@PathVariable("orderId")Long orderId){
		try {
			logger.info("开始取消订单 ，参数【orderId：" + orderId + "】");
			Order o = orderRepository.findByOrderId(orderId);			
			if(o != null){
				o.setStatus(0);
				orderRepository.save(o);
				logger.info("取消订单成功,开始删除订单中的座位信息，参数【orderId：" + orderId + "】 ");
			}
		} catch (Exception e) {
			logger.error("订单失效失败 ");
			logger.error(e.getMessage());
			return "false";
		}
		return "true";
	}
	
    /**
     * @param id	订单号
     * @param className	课程名称
     * @param classAddr 开课地址
     * @param datatime	开课时间
     * {{first.DATA}}
     * 课程：{{keyword1.DATA}}
     * 时间：{{keyword2.DATA}}
     * 地点：{{keyword3.DATA}}
     * {{remark.DATA}}
     
     
     {{first.DATA}}
     服务人员：{{keyword1.DATA}}
     联系方式：{{keyword2.DATA}}
     服务类型：{{keyword3.DATA}}
     服务时间：{{keyword4.DATA}}
     {{remark.DATA}}
     */
        public void notifycationSuccess(Long id,String openId, String className,String datatime)
    {
        try {
            TemplateMessage messageValue    = new TemplateMessage();
            MessageTemplate messageTemplate = templateQueueService.getMessageTemplate("order_success");
            
            if (messageTemplate == null) {
                logger.warn("找不到消息对应的模板");
            }else{
                HashMap<String, String> param = new HashMap<String, String>(0);
                String first = "您好,您的报名已成功登记";
                first += "\n报名编号："+id;
                param.put("first", first);
                param.put("keyword1", className);
                param.put("keyword2", "18675515034");
                param.put("keyword3", "深圳芒果学车");
                param.put("keyword4", datatime);
                String csRemak= "\n欢迎您使用，客服电话：4008935866 ！";
                param.put("remark",csRemak);
                
                messageValue.setOpenid(openId);
                messageValue.setTemplateId(messageTemplate.getTmpid());
                messageValue.setParam(param);
                //messageValue.setUrl(messageTemplate.makeUrl(weixinConfig,id.toString()));
                String csURL=messageTemplate.makeUrl(weixinConfig,"create");
                logger.info("访问csURL："+csURL);
                messageValue.setUrl(csURL);
                templateQueueService.sendTemplateMessage(messageValue);
            }
        } catch (Exception e) {
            logger.error("发送消息失败 ");
            logger.error(e.getMessage());
        }
    }
   public Map<String,Object> startPay(Long orderId,String price,String className,String payType,Customer customer){
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String ticket = WxpubOAuth.getJsapiTicket(appId, pingSecKey);
            //	    	String ticket = WxpubOAuth.getJsapiTicket(pingAppId, pingSecKey);
            logger.info("ticket " + ticket);
            // 创建 Charge
            Charge charge = payApp(orderId,price,className,payType,customer);
            // 获得签名
            String signature = WxpubOAuth.getSignature(charge.toString(), ticket, "");
            logger.info("------- JSAPI 签名 -------");
            logger.info(signature);
            logger.info("charge：【" + charge + "】");
            map.put("charge", charge);
            map.put("signature", signature);
        }
        catch (Exception e) {
            map.put("charge", null);
            logger.error("调用支付接口失败 ");
            logger.error(e.getMessage());
        }
        return map;
    }
    public Charge payApp(Long orderId,String price,String className,String payType,Customer customer){
        Charge charge = null;
        try {
            //李旭斌
            //2017-04-07
            Pingpp.apiKey = pingApiKeyTest;
            
            Map<String, Object> chargeMap = new HashMap<String, Object>();
            // 某些渠道需要添加extra参数，具体参数详见接口文档
            //Robert Lee 2016-08-01
            chargeMap.put("amount", Long.parseLong(price)*100);//金额，单位为分，例 100 表示 1.00 元，233 表示 2.33 元
            chargeMap.put("currency", "cny");//货币类型   cny：人民币
            chargeMap.put("subject", "学车报名费用");
            chargeMap.put("body", className);
            chargeMap.put("order_no", orderId);// 订单号
            chargeMap.put("channel", "wx_pub");//支付方式   wx_pub： 微信公众账号支付
            chargeMap.put("client_ip", "127.0.0.1");// 客户端的 IP 地址
            
            Map<String, String> app = new HashMap<String, String>();
            //Ping++ 管理平台【应用名称】->【应用信息】中得得到/////支付使用的 app 对象的 id
            app.put("id", pingAppId);
            chargeMap.put("app", app);
            
            Map<String, String> extramap = new HashMap<String, String>();
            //extra的参数根据文档: https://pingxx.com/document/api#api-c-new
            extramap.put("open_id", customer.getOpenid());
            chargeMap.put("extra", extramap);
            
            //发起交易请求
            charge = Charge.create(chargeMap);
            System.out.println(charge);
            //notifycationSuccess(orderId,openId,className,"2017-3-21");
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("调用支付payApp接口失败");
            logger.error(e.getMessage());
        }
        return charge;
    }
    
	 /**
     * 修改人：吴云
     * 修改时间：2017-04-08
     * 修改内容：修改短信通知和微信消息通知代码位置，之前是创建订单即通知，现在是支付成功通知
     * @param orderId			订单ID
     * @param className			服务名称
     * @param price				订单价格
     * @param openId			用户openID
     * @param contactphone		联系电话
     */
    public void msgSend(Long orderId,String className,String price,String payType,Customer customer){
        try {
            //发送短信通知
            String content = "\"name\":\"" + customer.getPhone() + "\",\"servicename\":\"" + className + "\"";
            smsQueueService.sendMessage(customer.getPhone(), content, "", false,"pay");
            //支付成功后，短信通知领导
            smsQueueService.sendMessage("18682455891", content, "", false,"pay");
            
            logger.info("返回订单号和选中座位号【orderId：" + orderId +"】");
            //Robert Lee 2016-07-11
            
            /**
             * 修改人：吴云
             * 修改时间：2017-04-04
             * 修改内容：下单成功微信通知用户
             */
            //消息通知用户下单成功
            String token = WeixinServletUtil.getAssetToken(assetTokenUrl,appId,appSecret);
            String msgUrl = weixinMsgUrl + "?access_token=" + token;
            String templateId= "k3SCB_EZYm6T5Hvw7eiGE4XI8-ANcDmUuVp3XVl9-CE";
            Map<String,Object> msgMap = new HashMap<String,Object>();
            msgMap.put("first", "恭喜您支付成功，我们会及时处理");
            if("appointment".equals(payType)){
                msgMap.put("keyword1", "芒果学车预约成成功");
            }
            else{
                msgMap.put("keyword1", "芒果学车报名成成功");
            }
            msgMap.put("keyword2", customer.getNickname());
            msgMap.put("keyword3", price);
            msgMap.put("keyword4", sdf.format(new Date()));
            msgMap.put("remark", "感谢您的使用");
            boolean msgBl = WeixinServletUtil.sendMsg(msgUrl,customer.getOpenid(),templateId,msgMap);
            if(msgBl){
                logger.info("给用户" + customer.getOpenid() + "发送微信通知成功");
            }
            //支付成功微信通知领导
            msgBl = WeixinServletUtil.sendMsg(msgUrl,"ok4ttv2cbxgsS8_C_6gkVUVSL8Tc",templateId,msgMap);
            if(msgBl){
                logger.info("给领导ok4ttv2cbxgsS8_C_6gkVUVSL8Tc发送微信通知成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    /**
     * 修改人：吴云
     * 修改时间：2017-04-08
     * 修改内容：报名成功赠送用户200元优惠券
     * @param orderId			订单ID
     * @param className			服务名称
     * @param price				订单价格
     * @param openId			用户openID
     * @param contactphone		联系电话
     */
    public void giveCard(Customer customer){
        try {
            CardBatch batch = cardBatchRepository.findByBatchNo("C11141");
            Event event = eventRepository.findEventByCode("appointment_xcfwC1");
            if(batch != null && event != null){
                //生成卡券信息
                String cardNo = getRandomString(11);
                Card card = new Card();
                card.setCardNo(cardNo);
                card.setBatchId(batch.getId().toString());
                card.setEvent(event);
                card.setPeriodType(batch.getPeriodType());
                card.setCustomer(customer);
                card.setCardBatch(batch);
                //                card.setTech(null);
                card.setName(batch.getName());
                card.setDescription(batch.getDescription());
                card.setType(batch.getType());
                card.setStartDate(new Date());
                card.setEndDate(new Date());
                card.setTotalCount(1);
                card.setStatus("002");
                card.setPublishTime(new Date());
                
                cardRepository.save(card);
                
                //微信通知用户
                //    			String token = WeixinServletUtil.getAssetToken(assetTokenUrl,appId,appSecret);
                //    			String msgUrl = weixinMsgUrl + "?access_token=" + token;
                //    			String templateId= "k3SCB_EZYm6T5Hvw7eiGE4XI8-ANcDmUuVp3XVl9-CE";
                //    			Map<String,Object> msgMap = new HashMap<String,Object>();
                //    			msgMap.put("first", "恭喜您获得芒果学车200元优惠券一张");
                //    			msgMap.put("keyword1", "芒果学车预约成成功");
                //    			msgMap.put("keyword2", "");
                //    			msgMap.put("keyword3", "");
                //    			msgMap.put("keyword4", sdf.format(new Date()));
                //    			msgMap.put("remark", "感谢您的使用");
                //    			boolean msgBl = WeixinServletUtil.sendMsg(msgUrl,"",templateId,msgMap);
                //    			if(msgBl){
                //    			    logger.info("给用户" + "" + "发送微信通知成功");
                //    			}
            }
            else{
                if(batch == null){
                    logger.info("用户报名成功后，没有找到C11141优惠券");
                }
                if(event == null){
                    logger.info("用户报名成功后，没有找到appointment_xcfwC1事件");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@RequestMapping("/getOrderListByCustomerId/{customerId}")
	public Map<String,List<Order>> getOrderList(@PathVariable("customerId")Long customerId){
		logger.info("The customer id "+customerId);
		Map<String,List<Order>> result = new HashMap<String,List<Order>>();
		try {
			Customer customer = customerRepository.findOne(customerId);
			// 根据状态分组查询订单列表, 分页查询，查询5个
			Sort sort = new Sort(Direction.DESC, "lastUpdateTime");
			Pageable pageable = new PageRequest(0, PAGE_SIZE, sort);
			logger.info("开始查询状态数据");
			List<Order> newOrderList  = orderRepository.findByCustomerAndStatus(customer, 1,pageable).getContent();
			logger.info("查询状态数据【newOrderList：" + newOrderList.size() + "】");
			List<Order> workingOrderList  = orderRepository.findByCustomerAndStatus(customer, 2,pageable).getContent();
			
			List<Integer> statusList = new ArrayList<Integer>();
			statusList.add(3);
			statusList.add(4);
			statusList.add(5);
			List<Order> completeOrderList  = orderRepository.findByCustomerAndStatusIn(customer,statusList ,pageable).getContent();

			// 添加待评价的订单列表
			List<Order> commentOrderList = new ArrayList<Order>();
			if (CollectionUtils.isNotEmpty(completeOrderList)) {
				for (Order order : completeOrderList) {
					if (order.getStatus().equals(Order.STATUS_COMPLETE) && CollectionUtils.isEmpty(order.getOrderComments())) {
						commentOrderList.add(order);
					}
				}
			}
			
			if(newOrderList != null)
			{
				for(int i=0;i < newOrderList.size(); i++ )
				{
					Order od = newOrderList.get(i);
					if(od.getAppointment() != null && od.getAppointment().getId() != null)
					{
						od.setAppointmentId(od.getAppointment().getId());
					}
				}
			}
			
			if(workingOrderList != null)
			{
				for(int i=0;i < workingOrderList.size(); i++ )
				{
					Order od = workingOrderList.get(i);
					if(od.getAppointment() != null && od.getAppointment().getId() != null)
					{
						od.setAppointmentId(od.getAppointment().getId());
					}
				}			
			}
			
			if(completeOrderList != null)
			{
				for(int i=0;i < completeOrderList.size(); i++ )
				{
					Order od = completeOrderList.get(i);
					if(od.getAppointment() != null && od.getAppointment().getId() != null)
					{
						od.setAppointmentId(od.getAppointment().getId());
					}
				}			
			}
			
			if(commentOrderList != null)
			{
				for(int i=0;i < commentOrderList.size(); i++ )
				{
					Order od = commentOrderList.get(i);
					if(od.getAppointment() != null && od.getAppointment().getId() != null)
					{
						od.setAppointmentId(od.getAppointment().getId());
					}
				}			
			}
			
			newOrderList = arrange(newOrderList);
			logger.info("查询状态数据【newOrderList：" + newOrderList.size() + "】");
			workingOrderList = arrange(workingOrderList);
			completeOrderList = arrange(completeOrderList);
			commentOrderList = arrange(commentOrderList);
			
			result.put("new", newOrderList);
			result.put("working", workingOrderList);
			result.put("complete", completeOrderList);
			result.put("comment", commentOrderList);
		} catch (Exception e) {
			logger.error("查询状态失败 ");
			logger.error(e.getMessage());
		}
		
		return result;
	}
	
	private List<Order> arrange(List<Order> inputOrderList) {
		List<Order> result = new ArrayList<Order>();
		for (int i = 0 ; i < inputOrderList.size(); i ++) {
			Order order = inputOrderList.get(i);
			try {
				// 根据appointment对象来判断是否是预约订单还是线下订单
//				if (order.getAppointment() != null) {
//					Date theDate = order.getAppointment().getTimeSegment().getDateSegment();
//					Long segIndex = order.getAppointment().getTimeSegment().getTimeSegment();
//					String dateString = DateUtils.formatDate(theDate,"yyyy/MM/dd");
//					String segString = segIndex+":00-"+(segIndex+1)+":00";
//					order.setAppointTimeStr(dateString+" "+segString);
//					order.setBusiTypeId(order.getAppointment().getTimeSegment().getBusiTypeId());
//					String imgUrl = imgUrlMap.get(order.getAppointment().getTimeSegment().getBusiTypeId());
//					order.setImgUrl(imgUrl);
//				}
//				else {
//					// 这种是线下订单
					order.setImgUrl("icon_u.png");
					// 线下订单设置业务类型为0
					// 线上订单设置业务类型为1
					//order.setBusitypeid(1L);
//				}
				if (order.getTech() != null) {
					order.setTechlevelno(order.getTech().getTechlevelno());
				}
				else {
					order.setTechlevelno("");
				}
				
				// 检查是否有评论信息
				if (order.getStatus().equals(Order.STATUS_COMPLETE)) {
					if (CollectionUtils.isNotEmpty(order.getOrderComments())) {
						order.setHasComment(true);
					}
				}
				
				String tmpTime = "";
				if(order.getStartTime() != null){
					tmpTime = DateUtils.formatDate(order.getStartTime(),"yyyy-MM-dd HH:mm");
				}
				order.setStartClassTime(tmpTime);

				String tmpStr = "";
				if(order.getSeatname() != null){
					tmpStr = order.getSeatname().replace("-","排");
				}
				order.setSeatname(tmpStr);
                if(order.getCreateTime() != null){
                    tmpTime = "";
                    tmpTime = DateUtils.formatDate(order.getCreateTime(),"yyyy-MM-dd");
                    order.setClassTimeDetail(tmpTime);
                }
				result.add(order);
			}
			catch(Exception e){
				logger.error("Can't handle the dirty order data.",e);
				continue;
			}
		}
		return result;
	}
	
	@RequestMapping(value = "/getOrderInfo/{orderid}", method = RequestMethod.GET)
	public Map<String, String> getOrderInfo(@PathVariable("orderid") Long orderid){
		HashMap<String, String> infoMap = new HashMap<String, String>(0);
		try {
			logger.info("开始查询订单详情，参数为【orderid：" + orderid + "】 ");
			Order order = orderRepository.findOne(orderid);
			
			infoMap.put("orderId", order.getOrderId());
			
			infoMap.put("classId", order.getClassid()+"");
			
			infoMap.put("classname", order.getClassname());
			
			//infoMap.put("roomName", order.getRoomName());
			infoMap.put("teachName", order.getTeacherName());
			
			infoMap.put("teachId", order.getTeacherId()+"");
			
			//infoMap.put("classHour", order.getClassHour().toString());
			
			infoMap.put("price", order.getPrice().intValue() + "");
			
			//infoMap.put("childName", order.getChildName());
			infoMap.put("classTimeDetail", order.getClassTimeDetail());
			
			// writen by wangyunjian
			infoMap.put("create_Date", sdf.format(order.getCreateTime()));
			Long businessTypeId = order.getBusitypeid();
			if(null == businessTypeId) {
				//李旭斌 2017-4-10
				infoMap.put("businessName", "芒果学车预约");
			} else {
				BusinessType businessType = businessTypeRepository.findOne(businessTypeId);
				infoMap.put("businessName", businessType.getName());
			}
			// end
			
			String tmpTime = "";
			if(order.getStartTime() != null){
				tmpTime = DateUtils.formatDate(order.getStartTime(),"yyyy/MM/dd");
				
			}
			if(order.getEndTime() != null){
				tmpTime += "--" + DateUtils.formatDate(order.getEndTime(),"yyyy/MM/dd");
				
			}
			infoMap.put("classTime",tmpTime);
			
			String tmp = "";
			
			if(order.getCreateTime() != null){
				tmp = DateUtils.formatDate(order.getCreateTime(),"yyyy/MM/dd HH:mm:ss");
				
			}
			infoMap.put("createTime", tmp);
			
			
			//Schedule sche = scheduleRepository.getScheduleById(order.getSchedule().getId());
			//if(sche != null){
			//	infoMap.put("roomAddress", sche.getbusinessAddress());
			//}
		} catch (Exception e) {
			logger.error("查询订单详情失败 ");
			logger.error(e.getMessage());
			
		}
		return infoMap;
	}

	// 获取订单的评价数据
	@RequestMapping("/getCommentByOrderId/{orderId}")
	public OrderComment getCommentByOrderId(@PathVariable("orderId")Long orderId){
		
		Order order = orderRepository.findOne(orderId);
		
		OrderComment orderComment = orderCommentRepository.findByOrder(order);
		
		
		return orderComment;
	}

	// 创建订单的评价数据
	@RequestMapping(value="/createCommentForOrder/{orderId}/{customerId}",method=RequestMethod.POST)
	public OrderComment createCommentForOrder(@PathVariable("orderId")Long orderId,@PathVariable("customerId")Long customerId,@RequestBody OrderComment orderComment){
		Order order = orderRepository.findOne(orderId);
		Customer customer = customerRepository.findOne(customerId);
		OrderComment orderCommentOld = orderCommentRepository.findByOrder(order);
		
		if (orderCommentOld != null) {
			BeanUtils.copyProperties(orderComment, orderCommentOld,"id");
			orderCommentOld.setCustomer(customer);
			orderCommentOld.setOrder(order);
			orderCommentRepository.save(orderCommentOld);
		}
		else {
			orderComment.setCustomer(customer);
			orderComment.setOrder(order);
			orderCommentRepository.save(orderComment);
		}
		return orderComment;
	}

	//查询登录人信息
	@RequestMapping(value="/searchCutomerId/{openId}",method=RequestMethod.GET)
	public long searchCutomerId(@PathVariable("openId")String openId){
		
		//根据openId获取用户信息
		logger.info("开始根据用户openId查询用户信息，查询参数为【openId：" + openId + "】 ");
		Customer customer = customerRepository.findByOpenid(openId.toString());
		if(customer != null){
			return customer.getId();
		}
		else{
			return 0;
		}
		
	}
	
	//检验用户是否有订单
	@RequestMapping(value="/findExistOrder/{customerId}",method=RequestMethod.GET)
	public Order findExistOrder(@PathVariable("customerId")Long customerId){
		logger.info("开始查询用户是否有订单");
		Order order = null;
		List<Order> orders = orderRepository.findOrderByCustomerId(customerId);
		if(orders.size()>0){
			order = orders.get(0);
		}
		return order;
	}
	/**
     * 修改人：吴云
     * 修改时间：2017-04-08
     * 修改内容：随机获取11为字符
     * @param length
     * @return
     */
    public String getRandomString(int length) { //length表示生成字符串的长度
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }	
}

