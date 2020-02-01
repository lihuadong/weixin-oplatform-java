/**
 * 包到位小程序SaaS
 * cn.a86.weixin4open.biz
 * Code2SessionKey.java
 * Ver0.0.1
 * 2017年5月8日-上午8:40:46
 *  2017全智道(北京)科技有限公司-版权所有
 * 
 */
package weixin.oplatform.miniprogram;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import jingubang.th3rd.service.MemcachedManager;
import jingubang.util.http.HttpsDataManager;

/**
 * 
 * Code2SessionKey
 * 
 * 李华栋
 * 2017年5月8日 上午8:40:46
 * 
 * @version 0.0.1
 * 
 */
public class MinappWXLogin {

	 private static Logger logger = Logger.getLogger(MinappWXLogin.class);
	 private static String component_appid; 	 

	 public MinappWXLogin() {
		    
		    Properties prop = new Properties();  
			InputStream in = MinappWXLogin.class.getResourceAsStream("/weixin3rd.properties"); 
			
			try {  
	            prop.load(in);  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }
			
			component_appid = prop.getProperty("component_appid");   
	 }
	 
	 private JSONObject getOpenidSessionKey(String js_code,String appid,String component_appid,String component_access_token){
		 
			String url = "https://api.weixin.qq.com/sns/component/jscode2session?appid=" + appid + "&" + "js_code="
					+ js_code + "&grant_type=authorization_code&component_appid=" + component_appid
					+ "&component_access_token=" + component_access_token + "";
	
			 String resStr = HttpsDataManager.sendData(url, "");
			JSONObject resJSON = new JSONObject(resStr);	
			 logger.info("MinappWXLogin-getOpenidSessionKey:"+resJSON.toString());
			 
	         return resJSON;
	 }
	 
	 //第三方平台开发者的服务器使用
	 //第三方平台开发者的服务器使用登录凭证 code 
     //第三方平台的component_access_token 获取 session_key 和 openid。
	 public JSONObject getOpenidSessionKey(String js_code,String appid){
		 		 		 
		 MemcachedManager  mc    = MemcachedManager.getMemcacheManager();		 
		 String component_access_token =(String) mc.get("component_access_token");
		 JSONObject  resJSON   = getOpenidSessionKey(js_code,appid,component_appid,component_access_token);	
		 
        return resJSON;
	 }
	 


}
