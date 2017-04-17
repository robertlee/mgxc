package com.uletian.ultcrm.business.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.uletian.ultcrm.business.controller.OrderController;




import net.sf.json.JSONObject;

/**
 * 修改人：吴云
 * 修改时间：2017-04-04
 * 修改内容：微信接口调用
 */
public class WeixinServletUtil {
    private static Logger logger = Logger.getLogger(WeixinServletUtil.class);
    /**
     * 微信消息发送
     * @param msgUrl		消息发送接口
     * @param openId		用户openID
     * @param templateId	消息模板ID
     * @param map			消息接口参数
     * @return
     */
    public static boolean sendMsg(String msgUrl,String openId,String templateId,Map<String,Object> map){
        logger.info("开始发送微信消息");
        boolean bl = false;
        try {
            if(map == null){
                map = new HashMap<String,Object>();
            }
            //消息通知
            String jsonData = "{" +
            "\"touser\":\"" + openId + "\"," +
            "\"template_id\":\"" + templateId + "\"," +
            "\"topcolor\":\"#FF0000\"," +
            "\"data\":{" +
            "\"first\":{\"value\":\"" + map.get("first") + "\",\"color\":\"#173177\"}," +
            "\"keyword1\":{\"value\":\"" + map.get("keyword1") + "\",\"color\":\"#173177\"}," +
            "\"keyword2\":{\"value\":\"" + map.get("keyword2") + "\",\"color\":\"#173177\"}," +
            "\"keyword3\":{\"value\":\"" + map.get("keyword3") + "\",\"color\":\"#173177\"}," +
            "\"keyword4\":{\"value\":\"" + map.get("keyword4") + "\",\"color\":\"#173177\"}," +
            "\"remark\":{\"value\":\"" + map.get("remark") + "\",\"color\":\"#173177\"}}}";
            
            logger.info("微信消息接口参数为：" + jsonData);
            String reMsg = UrlReqUtil.postRequestMethod(msgUrl, jsonData);
            logger.info("调用微信消息接口结果为：" + reMsg);
            if(reMsg != null){
                JSONObject jsonObject = JSONObject.fromObject(reMsg);
                String result = jsonObject.getString("errmsg");
                if(null != result && !"".equals(result) && "ok".equals(result)){
                    bl = true;
                }
            }
        } catch (Exception e) {
            logger.info("调用微信消息接口报错：" + e.getMessage());
        }
        logger.info("发送微信消息结束");
        return bl;
    }
    
    /**
     * 获取asset_token
     * @param getTokenUrl	获取asset_token接口
     * @param appid
     * @param secret
     * @return
     */
    public static String getAssetToken(String getTokenUrl,String appid,String secret){
        logger.info("获取token，参数【getTokenUrl：" + getTokenUrl + ",appid：" + appid + ",secret：" + secret + "】");
        String token = "";

        try {
            String url = getTokenUrl + "?grant_type=client_credential&appid=" + appid + "&secret=" + secret;
            JSONObject jsonObj = UrlReqUtil.getRequestMethod(url);
            logger.info("调用获取token接口，返回json对象为：" + jsonObj);
            if(jsonObj != null){
                token = jsonObj.getString("access_token");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }
	
	public static JSONObject getUserInfo(String getTokenUrl,String getUserUrl,String appid,String secret,String openid){
		try {
			String token = getAssetToken(getTokenUrl,appid,secret);
			logger.info("token:"+token);
			
			String personUrl = getUserUrl + "?access_token=" + token + "&lang=zh_CN&openid=" + openid;
			JSONObject person = UrlReqUtil.getRequestMethod(personUrl);
			return person;	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
}
