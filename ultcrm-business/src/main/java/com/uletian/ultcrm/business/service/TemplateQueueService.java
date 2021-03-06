package com.uletian.ultcrm.business.service;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.uletian.ultcrm.business.entity.MessageTemplate;
import com.uletian.ultcrm.business.repo.MessageTemplateRepository;
import com.uletian.ultcrm.business.value.TemplateMessage;

@Service
public class TemplateQueueService {
	
	// wangyunjian 2017-04-08 for delete JMS
//	@Qualifier("templateMessageQueue")
//	@Autowired
//	private Destination templateMessageQueue;
//	
//	@Autowired
//	private JmsTemplate jmsTemplate; 
	
	@Autowired
	private MessageTemplateRepository messageTemplateRepository;
	
	public void sendTemplateMessage(TemplateMessage message){
		// wangyunjian 2017-04-08 for delete JMS
//		jmsTemplate.send(templateMessageQueue, new TemplateMessageCreator(message));
	}
	
	@Cacheable("tech")
	public ArrayList<MessageTemplate> getMessageTemplates(){
		ArrayList<MessageTemplate> arrays = new ArrayList<MessageTemplate>(0);
		Iterable<MessageTemplate> messageTemplates = messageTemplateRepository.findAll();
		if (messageTemplates != null) {
			for (MessageTemplate messageTemplate : messageTemplates) {
				arrays.add(messageTemplate);
			}
		}
		return arrays;
	}
	
	public MessageTemplate getMessageTemplate(String code){
		MessageTemplate messageTemplate = null;
		ArrayList<MessageTemplate> messageTemplates = getMessageTemplates();
		for (MessageTemplate tempMessageTemplate : messageTemplates) {
			if (code.equals(tempMessageTemplate.getCode())) {
				messageTemplate = tempMessageTemplate;
			}
		}
		return messageTemplate;
	}
	
	//wangyunjian 2017-04-08 to delete JMS
//	private class TemplateMessageCreator implements MessageCreator{
//		private TemplateMessage message;
//
//		public TemplateMessageCreator(TemplateMessage message){
//			this.message = message;
//		}
//
//		@Override
//		public Message createMessage(Session session) throws JMSException {
//			Document doc = DocumentHelper.createDocument();
//			Namespace namespace = new Namespace("ns0", "http://crm/ultjjy.cn");
//			Element root = doc.addElement(new QName("message", namespace));
//			Element template = root.addElement(new QName("template"));
//			template.addElement(new QName("openid")).addText(message.getOpenid());
//			template.addElement(new QName("templateId")).addText(message.getTemplateId());
//			template.addElement(new QName("url")).addCDATA(message.getUrl() == null ? "" : message.getUrl());
//			Element params = template.addElement(new QName("params"));
//			HashMap<String, String> paramMap = message.getParam();
//			Iterator<String> iterator = paramMap.keySet().iterator();
//			while (iterator.hasNext()) {
//				String keyStr = iterator.next();
//				Element param = params.addElement(new QName("param"));
//				param.addElement(new QName("key")).addText(keyStr);
//				param.addElement(new QName("value")).addCDATA(paramMap.get(keyStr));
//			}
//			return session.createTextMessage(doc.asXML());
//		}
//	}
}
