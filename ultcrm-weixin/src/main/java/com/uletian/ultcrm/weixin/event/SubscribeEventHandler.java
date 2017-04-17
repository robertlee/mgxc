/**
 * Copyright &copy; 2014 uletian All rights reserved
 */
package com.uletian.ultcrm.weixin.event;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import weixin.popular.bean.EventMessage;
import weixin.popular.bean.xmlmessage.XMLMessage;
import weixin.popular.bean.xmlmessage.XMLTextMessage;
import weixin.popular.event.EventHandler;

import com.uletian.ultcrm.business.entity.Config;
import com.uletian.ultcrm.business.entity.Customer;
import com.uletian.ultcrm.business.entity.Event;
import com.uletian.ultcrm.business.repo.ConfigRepository;
import com.uletian.ultcrm.business.repo.EventRepository;
import com.uletian.ultcrm.business.service.CustomerService;
import com.uletian.ultcrm.business.service.EventMessageService;

import com.uletian.ultcrm.business.service.WeixinConfig;
import com.uletian.ultcrm.weixin.service.WeixinAuthService;


/**
 * 
 * @author robertxie
 * 2015年9月16日
 */
@Component
public class SubscribeEventHandler implements EventHandler{

	private static Logger logger = Logger.getLogger(SubscribeEventHandler.class);
	
	private static final String WEIXIN_WELCOME="WEIXIN_WELCOME";
	private static final String WEIXIN_BACK="WEIXIN_BACK";
		
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private ConfigRepository configRepository;
	
	@Autowired
	private WeixinConfig weixinConfig;
	
	@Autowired
	private EventRepository eventRepository;
	
	@Autowired
	private EventMessageService eventMessageService;
	
	@Autowired
	private WeixinAuthService weixinAuthService;	
	/**
	 * 关注的时候，创建客户信息
	 * @throws UnsupportedEncodingException 
	 */
	@Override
	public XMLMessage handleEvent(EventMessage eventMessage)  {
		// 获取用户的授权，获取用户的详细信息，然后通过openId创建用户
		// 第一步获取code		
		logger.debug("eventMessage is "+eventMessage.toString());		
		Config config = null;		
		String csOpenid=eventMessage.getFromUserName();
		Customer customer = customerService.getCustomerByOpenId(csOpenid);
		// 首次关注
		if (customer == null) {
			//Author:robert Lee
			//Date: 2017-04-06				
			logger.info("首次关注生成用户并赠送10元 openid =>" +csOpenid );	
			config = configRepository.findByCode(WEIXIN_WELCOME);	

				
			customer = new Customer();
			//customer = weixinAuthService.getWeixinUserInfo(csOpenid);			
			customer = weixinAuthService.getUserInfo(csOpenid);
			
			
			customer.setOpenid(csOpenid);		
			customerService.createCustomer(customer);
			
			Event event = eventRepository.findEventByCode("firstjoin");
			eventMessageService.sendEvent(event, customer, null);
		}else{
			config = configRepository.findByCode(WEIXIN_BACK);
		}
		
		//创建用户
		return new XMLTextMessage(eventMessage,config.getValue());
	}
	

}

