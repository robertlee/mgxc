package com.uletian.ultcrm.business.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.uletian.ultcrm.business.value.OrderMessage;


/**
 * 测试订单的监听器
 * @author robertliu1
 *
 */
@Component
//wangyunjian 2017-04-08 to delete JMS
public class TestCustomerListener // implements MessageListener
{


	private static Logger logger = Logger.getLogger(TestCustomerListener.class);

		
//	@Override
//	public void onMessage(Message arg0) {
//	
// 		logger.info("into TestCustomerListener.....");
//        try {
//        	
//        	String sender = arg0.getStringProperty("SENDER");
//        	logger.info("消息头sender: "+sender);
//        	String orderMessage = ((TextMessage)arg0).getText();
//        	logger.info("消息体: "+orderMessage);
//    		logger.info("over TestCustomerListener!");
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
