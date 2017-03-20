/**
 * Copyright &copy; 2014 uletian All rights reserved
 */
package com.uletian.ultcrm.business.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uletian.ultcrm.business.entity.Tech;
import com.uletian.ultcrm.business.entity.Card;
import com.uletian.ultcrm.business.entity.CardBatch;
import com.uletian.ultcrm.business.entity.Coupon;
import com.uletian.ultcrm.business.entity.CouponBatch;
import com.uletian.ultcrm.business.entity.Customer;
import com.uletian.ultcrm.business.entity.Event;
import com.uletian.ultcrm.business.entity.Order;
import com.uletian.ultcrm.business.repo.CardRepository;
import com.uletian.ultcrm.business.repo.CouponRepository;
import com.uletian.ultcrm.business.repo.CustomerRepository;
import com.uletian.ultcrm.business.repo.EventRepository;
import com.uletian.ultcrm.business.value.CardCoupon;

/**
 * 卡券公共类
 * @author robertxie
 * 2015年10月28日
 */
@Service
public class CardCouponService {
	
	@Autowired
	private CardService cardService;
	
	@Autowired
	private CouponService couponService;
	
	@Autowired
	private CardRepository cardRepository;
	
	@Autowired
	private CouponRepository couponRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private EventRepository eventRepository;
	
	public void publishToCrm(CardCoupon cardCoupon) {
		String type = cardCoupon.getType();
		if (type.equalsIgnoreCase("card")) {
			cardService.ultcrmPublishCard((Card)cardCoupon.getData());
		}
		else if (type.equalsIgnoreCase("coupon")) {
			couponService.ultcrmPublish((Coupon)cardCoupon.getData());
		}
	}
	
	public Card create(CardBatch cardBatch, Customer customer,Tech tech){
		return cardService.createCard(cardBatch, customer,tech);
	}
	
	public Coupon create(CouponBatch couponBatch, Customer customer,Tech tech){
		return couponService.createCoupon(couponBatch, customer,tech);
	}
	
	public void cancel(Event event, Customer customer){
		List<Card> cards = cardRepository.findByEventAndCustomer(event, customer);
		List<Coupon> coupons = couponRepository.findByEventAndCustomer(event, customer);
		for (Card card : cards) {
			if (Card.STATUS_PUBLISH.equals(card.getStatus())) {
				cardService.ultcrmCancelCard(card);
			}
		}
		for (Coupon coupon : coupons) {
			if (Coupon.STATUS_PUBLISH.equals(coupon.getStatus())) {
				couponService.ultcrmCancel(coupon);
			}
		}
	}
	
	public void cancelOrder(Order order) {
		Long busiTypeId = order.getAppointment().getTimeSegment().getBusiTypeId();
		switch (busiTypeId.intValue()) {
		case 10:
			// business1
			cancelCard(order.getCustomer().getId(), "appointment_xcfwC1");			
			break;
		case 11:
		    //business2			
			cancelCard(order.getCustomer().getId(), "appointment_xcfwC2");
			break;
		case 20:
			//business3
			cancelCard(order.getCustomer().getId(), "appointment_ksfwC1");
			break;
		case 21:
		    // business4
			cancelCard(order.getCustomer().getId(), "appointment_ksfwC2");			
			break;
		case 30:
			// business5
			cancelCard(order.getCustomer().getId(), "appointment_wyfwC1");
			break;
		case 31:
			// business6
			cancelCard(order.getCustomer().getId(), "appointment_wyfwC2");			
			break;		
		case 40:
			// business5
			cancelCard(order.getCustomer().getId(), "appointment_jsxcC1");
			break;
		case 41:
			// business6
			cancelCard(order.getCustomer().getId(), "appointment_jsxcC2");			
			break;					
		case 50:
			// 其他
			cancelCard(order.getCustomer().getId(), "appointment_other");
			break;
		default:
			break;
		}
	}

	private void cancelCard(Long customerid, String code) {
		Customer customer = customerRepository.findOne(customerid);
		Event event = eventRepository.findEventByCode(code);
		if (customer == null || event == null) {
			
		} else {
			cancel(event, customer);
		}
	}
}
