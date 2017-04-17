package com.uletian.ultcrm.weixin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uletian.ultcrm.business.repo.CustomerRepository;

@Component
//wangyunjian 2017-04-08 for delete JMS
public class TemplateMessageQueueListener //implements MessageListener 
{
	
	@Autowired
	private WeixinTplMsgService weixinTplMsgService;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(TemplateMessageQueueListener.class);
	
	// wangyunjian 2017-04-08 for delete JMS
//	@Override
//	public void onMessage(Message message) {
//		TextMessage textMsg = (TextMessage) message;
//		try {
//			StringWriter writer = new StringWriter();
//			OutputFormat format = OutputFormat.createPrettyPrint();
//			format.setEncoding("UTF-8");
//			XMLWriter xmlwriter = new XMLWriter(writer, format);
//			Document doc = DocumentHelper.parseText(textMsg.getText());
//			xmlwriter.write(doc);
//			logger.info("|======================================uletian======================================\n" );
//			logger.info("|收到模板消息报文\n" + writer.toString());
//			logger.info("|======================================uletian=====================================\n" );
//			Element rootElement = doc.getRootElement();
//			Element templateElement = rootElement.element(new QName("template"));
//			Customer customer = customerRepository.findByOpenid(templateElement.elementText("openid"));
//			String url = templateElement.elementText("url");
//			String templateId = templateElement.elementText("templateId");
//			
//			HashMap<String, String> param = new HashMap<String, String>(0);
//			Element paramsElement = templateElement.element(new QName("params"));
//			List<Element> paramsList = paramsElement.elements(new QName("param"));
//			for (int i = 0; i < paramsList.size(); i++) {
//				Element paramElement = paramsList.get(i);
//				String keyStr = paramElement.elementText(new QName("key"));
//				String valueStr = paramElement.elementText(new QName("value"));
//				param.put(keyStr, valueStr);
//			}
//			
//			weixinTplMsgService.sendMessage(customer, url, templateId, param);
//		} catch (JMSException e) {
//			logger.error("模板消息格式转换出错", e);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//	}
	
}
