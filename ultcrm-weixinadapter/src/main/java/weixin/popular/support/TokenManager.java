package weixin.popular.support;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import net.sf.json.JSONObject;

import weixin.popular.api.TokenAPI;
import weixin.popular.bean.Token;
import weixin.popular.api.WeixinAPI;
import weixin.popular.bean.UserInfo;

/**
 * TokenManager token 自动刷新
 * @author LiYi
 *
 */
public class TokenManager {

	private static Map<String,String> tokenMap = new LinkedHashMap<String,String>();

	private static Map<String,Timer> timerMap = new HashMap<String, Timer>();

	/**
	 * 初始化token 刷新，每118分钟刷新一次。
	 * @param appid
	 * @param secret
	 */
	public static void init(final String appid,final String secret){
		if(timerMap.containsKey(appid)){
			timerMap.get(appid).cancel();
		}
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				setToken(appid,secret);
			}
		},0,1000*60*118);
		timerMap.put(appid,timer);
	}

	/**
	 * 获取 access_token
	 * @param appid
	 * @return
	 */
	public static String getToken(String appid,String openId){
        //判断token是否失效，若失效，刷新token
        String token = tokenMap.get(appid);
        UserInfo userInfo = WeixinAPI.userInfo(token, openId);
        if(userInfo == null){
            setToken(appid,"42ec32b0c2cb4bfbec0466563f5d9963");
        }
        System.out.println("access_token:" + tokenMap.get(appid));
//		return tokenMap.get(appid);
        return "mHJXCzeVoL_H87u0uI85RiYDgbqeHCekTrQJWTXNRJK4RXlN8pdAqqNuzl6T4-QGSNmZb6wtvXH48IskqwTdSSGnwkYaN_BHwphqxEiJKdOEEyHnVfFk5ZrUO40c-nuERTRfADACEC";
	}
	
	/**
	 * 设置access_token
	 * @param appid
	 * @return
	 */
	public static String setToken(String appid,String secret){
//		Token token = TokenAPI.token(appid,secret);
//		tokenMap.put(appid,token.getAccess_token());
//		return tokenMap.get(appid);
        
        String accessToken = "";
        String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid
                + "&secret=" + secret;
        try{
            URL url = new URL(tokenUrl);
            URLConnection con = url.openConnection();
            BufferedReader read = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
            String line = read.readLine();
            
            JSONObject jsonobject = JSONObject.fromObject(line);
            accessToken = (String)jsonobject.get("access_token");
            System.out.println("ACCESSTOKEN为" + accessToken);
            
            read.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return accessToken;
    }

}
