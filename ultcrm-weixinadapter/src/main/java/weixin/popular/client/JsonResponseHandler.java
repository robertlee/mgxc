package weixin.popular.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import weixin.popular.util.JsonUtil;

public class JsonResponseHandler{
	private static Logger logger = Logger.getLogger(JsonResponseHandler.class);
	private static Map<String, ResponseHandler<?>> map = new HashMap<String, ResponseHandler<?>>();

	@SuppressWarnings("unchecked")
	public static <T> ResponseHandler<T> createResponseHandler(final Class<T> clazz){

		if(map.containsKey(clazz.getName())){
			return (ResponseHandler<T>)map.get(clazz.getName());
		}else{
				ResponseHandler<T> responseHandler = new ResponseHandler<T>() {
				@Override
				public T handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
	                if (status >= 200 && status < 300) {
	                    HttpEntity entity = response.getEntity();
						if (entity!=null)
						{
							String str = EntityUtils.toString(entity);
							//String str = JsonUtil.toJSONString(entity);
							logger.info("response str is "+str);
							//logger.info("response data is "+str);
							return JsonUtil.parseObject(new String(str.getBytes("iso-8859-1"),"utf-8"), clazz);		
						}
						else
						{
							logger.error("response data is null");	
							return null;
						}				
	                    
	                } else {
	                    throw new ClientProtocolException("Unexpected response status: " + status);
	                }
				}
			};
			map.put(clazz.getName(), responseHandler);
			return responseHandler;
		}
	}

}
