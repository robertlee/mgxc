/**
 * Copyright &copy; 2016 uletian All rights reserved
 */
package com.uletian.ultcrm.weixin.service;

import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.uletian.ultcrm.business.entity.Customer;
import com.uletian.ultcrm.business.repo.CustomerRepository;
import com.uletian.ultcrm.business.service.CustomerService;
import com.uletian.ultcrm.business.service.EventMessageService;

import com.uletian.ultcrm.business.service.WeixinConfig;
import com.uletian.ultcrm.business.service.WeixinServletUtil;

import com.uletian.ultcrm.common.util.StringUtils;

import com.uletian.ultcrm.weixin.controller.WeixinAuthController;
import com.google.gson.Gson;

import weixin.popular.client.LocalHttpClient;
import weixin.popular.api.Global;
import weixin.popular.api.WeixinAPI;
import weixin.popular.bean.UserInfo;
import weixin.popular.support.TokenManager;
import net.sf.json.JSONObject;
import weixin.popular.util.JsonUtil;
import java.util.Date;

/** 
 * @author robertxie
 * 2015年9月19日
 */
@Component
public class WeixinAuthService {
	
	private static Logger logger = Logger.getLogger(WeixinAuthService.class);
    @Value("${assetTokenUrl}")
    private String assetTokenUrl;
	@Value("${getUserUrl}")
    private String getUserUrl;
    @Value("${appId}")
    private String appId;
    @Value("${appSecret}")
    private String appSecret;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private WeixinConfig weixinConfig;
	
	// wangyunjian 2017-04-08 for delete JMS
//	@Autowired
//	private EventMessageService eventMessageService;
	
	public Customer getWeixinUserInfo(String weixinCode) {
		Customer result = null;
		logger.info("getWeixinUserInfo openid is "+weixinCode);
		//通过code换取网页授权access_token
		
	
		Map tokenMap = WeixinAPI.getOpenId(weixinCode, weixinConfig.getAppId(), weixinConfig.getAppSecret(), Global.grantType);
		//获取到access_token, openId, refresh_token, 暂时不刷新access_token
		//第三步：刷新access_token（如果需要）
		//暂时不需要处理
		//第四步：拉取用户信息(需scope为 snsapi_userinfo)
		if (tokenMap.get("errcode") != null) {
			logger.error("can not get openid, return info is "+tokenMap);
		}
		else {
			// 先用openId去检查一下，比较一下最近检查的时间
			String openId = tokenMap.get("openid").toString();
			//南昌培训项目不进行30天在线分析 Robert Lee
			result = customerService.checkWeixinUpdateTime(tokenMap.get("openid").toString());
			if (result != null) {
				return result;
			}			
			
			for (int j=0;j<=2;j++)
			{
				UserInfo userInfo = WeixinAPI.userInfo(tokenMap.get("access_token").toString(), tokenMap.get("openid").toString());
				logger.info("access_token: "+tokenMap.get("access_token").toString());
				logger.info("openid:"+ tokenMap.get("openid").toString());	
				logger.info("getWeixinUserInfo userInfo："+userInfo.toString());					
				if (userInfo.getNickname()==null) {					
					logger.error("getWeixinUserInfo is failed");
					continue;
				}
				// 保存userInfo 到customer表中
				if (userInfo.isSuccess()) {
					Customer customer = customerService.getCustomerByOpenId(openId);
					boolean isFirst = false;
					if (customer == null) {
						customer = new Customer();
						customer.setOpenid(openId);
						isFirst = true;
					}
					customer.setStatus(1);
					BeanUtils.copyProperties(userInfo, customer);					
					// 用户昵称的判断
					String nickName = customer.getNickname();
					if (StringUtils.isNotBlank(nickName) && nickName.contains("\\")) {
						int i = nickName.indexOf("\\");
						String newNickName = nickName.substring(0, i);
						customer.setNickname(newNickName);
					}
					
					result = customerService.createCustomer(customer);
					logger.info("A new customer created."+customer.getId());
					if (isFirst) {
						// wangyunjian 2017-04-08 for delete JMS
//						eventMessageService.sendEvent("firstjoin", customer.getId(), null);
					}
					break;
				}
				
				
			}
		}
		return result;
	}
	public Customer getUserInfo(String openid) {
		Customer result = null;		
		logger.info("getUserInfo openid is "+openid);
		JSONObject userobj = WeixinServletUtil.getUserInfo(assetTokenUrl,getUserUrl,appId,appSecret,openid);
        
        /*
         * 修改人：吴云
         * 修改时间：2017-04-06
         * 修改内容：获取用户基本信息，将信息放入UserInfo对象中
         */
        String subscribe = "";
        String sex = "";
        String subscribeTime = "";
        UserInfo userInfo = new UserInfo();
        if(userobj != null){           
            logger.info("response data is " + userobj);
            
            if(userobj.has("subscribe")){
                subscribe = userobj.getString("subscribe");
            }
            if(userobj.has("sex")){
                sex = userobj.getString("sex");
            }
            if(userobj.has("subscribe_time")){
                subscribeTime = userobj.getString("subscribe_time");
            }
            
            if(userobj.has("country")){
                userInfo.setCountry(userobj.getString("country"));
            }
            if(userobj.has("unionid")){
                userInfo.setUnionid(userobj.getString("unionid"));
            }
            if(userobj.has("city")){
                userInfo.setCity(userobj.getString("city"));
            }
            if(userobj.has("openid")){
                userInfo.setOpenid(userobj.getString("openid"));
            }
            if(userobj.has("province")){
                userInfo.setProvince(userobj.getString("province"));
            }
            if(userobj.has("nickname")){
                userInfo.setNickname(userobj.getString("nickname"));
            }
            if(userobj.has("headimgurl")){
                userInfo.setHeadimgurl(userobj.getString("headimgurl"));
            }
            if(userobj.has("language")){
                userInfo.setLanguage(userobj.getString("language"));
            }
        }
        
        long lt = new Long(subscribeTime);
        Date date = new Date(lt);
        userInfo.setSubscribe(Integer.getInteger(subscribe));
        userInfo.setSex(Integer.getInteger(sex));
        userInfo.setSubscribe_time(date);        
		
		//LocalHttpClient.executeJsonResult(userobj, UserInfo.class);;
		//UserInfo userInfo = WeixinServletUtil.getUserInfo(openid);
		//李旭斌 2017-4-6
		//将用户信息保存到 userInfo
		
		//logger.info("getUserInfo userInfo："+userInfo.toString());					
		if (userInfo.getNickname()==null) {					
			logger.error("getUserInfo is failed");			
		}
		
		// 保存userInfo 到customer表中
		if (userInfo.isSuccess()) {
			Customer customer = customerService.getCustomerByOpenId(openid);
			boolean isFirst = false;
			if (customer == null) {
				customer = new Customer();
				customer.setOpenid(openid);
				isFirst = true;
			}
			customer.setStatus(1);
			BeanUtils.copyProperties(userInfo, customer);					
			// 用户昵称的判断
			String nickName = customer.getNickname();
			if (StringUtils.isNotBlank(nickName) && nickName.contains("\\")) {
				int i = nickName.indexOf("\\");
				String newNickName = nickName.substring(0, i);
				customer.setNickname(newNickName);
			}
			
			result = customerService.createCustomer(customer);
			logger.info("A new customer created."+customer.getId());
			if (isFirst) {
				// wangyunjian 2017-04-08 for delete JMS
//				eventMessageService.sendEvent("firstjoin", customer.getId(), null);
			}
		
		}
			
		return result;	

	}
		

	
}
