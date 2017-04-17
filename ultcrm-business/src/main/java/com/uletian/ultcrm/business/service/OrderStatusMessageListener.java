package com.uletian.ultcrm.business.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 订单相关信息同步的监听器
 * @author robertliu1
 *
 */
@Component
public class OrderStatusMessageListener //implements MessageListener
{

	private static String SOURCESYS_CRM= "CRM";

	private static Logger logger = Logger.getLogger(OrderStatusMessageListener.class);

	@Autowired
	private OrderMessageServcie orderMessageService;
		
	// wangyunjian 2017-04-08 for delete JMS
//	@Override
//	public void onMessage(Message arg0) {
//		
//		System.out.println("into receive msg order status -----------------------");
// 		logger.info("进入 OrderStatusMessageListener.....");
//        try {
//        	String orderMessage = ((TextMessage)arg0).getText();
//        	
//        	logger.info("消息体: "+orderMessage);
//    		OrderMessage orderMsg = orderMessageService.orderXMLToJAVA(orderMessage);
//    		
//    		//System.out.println("OSListener:  Action " + orderMsg.getAction() + 
//    		//		"  source " +orderMsg.getSourceSys() + 
//    		//		" status " + orderMsg.getStatus()) ;
//    		logger.info("OSListener:  Action " + orderMsg.getAction() + 
//    				"  source " +orderMsg.getSourceSys() + 
//    				" status " + orderMsg.getStatus());
//    		
//    		String sourceSys = orderMsg.getSourceSys();
//    		
//    		//只接收来自CRM的消息
//    		if(sourceSys != null && sourceSys.equals(SOURCESYS_CRM));
//    		{    			
//    			orderMessageService.updateOrderFromCRM(orderMsg);
//    		}
//    		
//    		
//    		logger.info("OrderStatusMessageListener sucess!");
//            
//        }catch(Exception e)
//        {
//        	e.printStackTrace();
//        	logger.info(e.getMessage());
//        }
//        
//        
//        
//	}
	
}
