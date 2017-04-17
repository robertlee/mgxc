package com.uletian.ultcrm.business.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.json.JSONObject;

/**
 * 修改人：吴云
 * 修改时间：2017-04-04
 * 修改内容：新增https接口请求工具类
 */
public class UrlReqUtil {
	public static void main(String[] args) {
		try {
			String appid = "wxc3ec764a012b40af";
			String secret = "42ec32b0c2cb4bfbec0466563f5d9963";
			String grant_type = "client_credential";
			String openid = "ok4ttv_7I6fEfn5uQjSFbKEEGzvw";
			String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=" + grant_type + "&appid=" + appid + "&secret=" + secret;
			JSONObject jsonObj = getRequestMethod(url);
			System.out.println(jsonObj);
			
			String token = jsonObj.getString("access_token");
			System.out.println(token);
			
			String jsonData = "{" +
	                "\"touser\":\"" + openid + "\"," +
	                "\"template_id\":\"0nOZVGBrLr5xo1b7G8lCkPOD4JLrA1TvCEpO_NhEHFY\"," +
	                "\"topcolor\":\"#FF0000\"," +
	                "\"data\":{" +
	                "\"first\":{\"value\":\"恭喜您中奖啦\",\"color\":\"#173177\"}," +
	                "\"keyword1\":{\"value\":\"1111\",\"color\":\"#173177\"}," +
	                "\"keyword2\":{\"value\":\"222222\",\"color\":\"#173177\"}," +
	                "\"keyword3\":{\"value\":\"44444\",\"color\":\"#173177\"}," +
	                "\"keyword4\":{\"value\":\"55555\",\"color\":\"#173177\"}," +
	                "\"remark\":{\"value\":\"10积分\",\"color\":\"#173177\"}}}";
			
			String msgUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token;
			
			String reMsg1 = UrlReqUtil.postRequestMethod(msgUrl,jsonData);
//	        JSONObject jsonObject = JSONObject.fromObject(reMsg1);
			System.out.println(reMsg1);
			
			String personUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + token + "&lang=zh_CN&openid=" + openid;
			JSONObject person = getRequestMethod(personUrl);
			System.out.println(person);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * get请求方式
	 * @param url	接口地址
	 * @return
	 */
	public static JSONObject getRequestMethod(String url){
        HttpURLConnection http = null;
        InputStream is = null;
        try {
            URL urlGet = new URL(url);
            http = (HttpURLConnection) urlGet.openConnection();

            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
            System.setProperty("sun.net.client.defaultReadTimeout", "30000");

            http.connect();

            is =http.getInputStream();
            int size =is.available();
            byte[] jsonBytes =new byte[size];
            is.read(jsonBytes);
            String message=new String(jsonBytes,"UTF-8");
            return JSONObject.fromObject(message);
        } catch (Exception e) {
            return null;
        }finally {
            if(null != http) http.disconnect();
            try {
                if (null != is) is.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

	/**
	 * post请求方式
	 * @param url		接口地址
	 * @param data		接口参数：json字符串
	 * @return
	 */
    public static String postRequestMethod(String url,String data){
        HttpURLConnection http = null;
        PrintWriter out = null;
        BufferedReader reader = null;
        try {
            //创建连接
            URL urlPost = new URL(url);
            http = (HttpURLConnection) urlPost.openConnection();
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestMethod("POST");
            http.setUseCaches(false);
            http.setInstanceFollowRedirects(true);
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            http.connect();
            //POST请求
            OutputStreamWriter outWriter = new OutputStreamWriter(http.getOutputStream(), "utf-8");
            out = new PrintWriter(outWriter);
            out.print(data);
            out.flush();
            out.close();
            out = null;

            //读取响应
            reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            reader.close();
            reader = null;
//            System.out.println(sb.toString());
            return sb.toString();
        } 
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if(null != http) http.disconnect();
            if(null != out) out.close();
            try{
                if(null != reader) reader.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
