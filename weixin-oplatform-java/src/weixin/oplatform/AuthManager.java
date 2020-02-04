/**
 * 
 * 包到位小程序SaaS
 * cn.a86.weixin4open.biz
 * AuthManager.java
 * Ver0.0.1
 * 2017年4月19日-上午12:23:14
 * 2017全智道(北京)科技有限公司-版权所有
 * 
 */

package weixin.oplatform;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import weixin.aes.AesException;
import weixin.aes.WXBizMsgCrypt;

import weixin.msg.builder.RequestMsgBuilder;
import weixin.msg.builder.ResponseMsgBuilder;
import weixin.msg.model.normal.Text;
import weixin.util.DateTimeUtil;
import weixin.util.HTTPSDataManager;


public class AuthManager {
	
	private static Logger logger = Logger.getLogger(AuthManager.class);   	
	private static String component_appid;
	private static String component_appsecret;
	
	static{
			Properties prop = new Properties();  
			InputStream in = AuthManager.class.getResourceAsStream("/weixin3rd.properties"); 
			
			try {  
	            prop.load(in);  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  	
			component_appid = prop.getProperty("component_appid");  
			component_appsecret = prop.getProperty("component_appsecret");  
	}
	
	/**
	 * 
	 * 第三方平台获取component_access_token
	 * @param component_verify_ticket
	 * @return String
	 * @exception 
	 * @since  0.0.1
	 */
	public String  getComponentAccessToken(String component_verify_ticket){
		
			String  component_access_token =null;		
			String 	url ="https://api.weixin.qq.com/cgi-bin/component/api_component_token";	
					
			try {				
					JSONObject   reqJSON   = new JSONObject();
					reqJSON.put("component_appid", component_appid);
					reqJSON.put("component_appsecret", component_appsecret);
					reqJSON.put("component_verify_ticket",component_verify_ticket);
	
					String data =reqJSON.toString();
					String res  = HTTPSDataManager.sendData(url, data);		
	
					JSONObject   resJSON   = new JSONObject(res);
					
					logger.info("AuthManager-getComponentAccessToken-Result:"+resJSON.toString());
					
					component_access_token   = resJSON.getString("component_access_token");			
			} catch (JSONException e) {
				e.printStackTrace();
			}				
			return component_access_token;
	}
		
	/**
	 * 
	 * 第三方平台发起授权之前获取预授权码pre_auth_code
	 * @param component_access_token
	 * @return String
	 * @exception 
	 * @since  0.0.1
	 */
	public  String getPreAuthCode(String component_access_token){
		
				String url = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token="+component_access_token;
								
				try {
					
					JSONObject   reqJSON   = new JSONObject();
					reqJSON.put("component_appid", component_appid);
					String data =reqJSON.toString();
					String res  = HTTPSDataManager.sendData(url, data);
					
					logger.info("AuthManager-getPreAuthCode:"+res);
					JSONObject   resJSON   = new JSONObject(res);
					String pre_auth_code   = resJSON.getString("pre_auth_code");
					
					return pre_auth_code;	
					
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
				
	}
	
	/**
	 * 
	 * 第三方平台使用授权码换取   公众号(Authorizer)的接口调用凭据和授权信息
	 * (这里描述这个方法适用条件 – 可选)
	 * @param authorization_code
	 * @return 
	 *JSONObject
	 * @exception 
	 * @since  0.0.1
	 */
	public  JSONObject getAuthInfo(String  authorization_code){
		
				JSONObject   reqJSON   = new JSONObject();
				reqJSON.put("component_appid", component_appid);
				reqJSON.put("authorization_code", authorization_code);				
				String data =reqJSON.toString();
				
				//MemcachedManager  mc    = MemcachedManager.getMemcacheManager();
				//String component_access_token = (String)mc.get("component_access_token");
				String component_access_token = null;
				
				/////////  @todo 需要自己来处理完成
				
				String url = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token="+component_access_token;
				String res  = HTTPSDataManager.sendData(url, data);
			
				logger.info("AuthManager-getAuthorizerAccessToken:"+res);
			
				JSONObject authInfo   = new JSONObject(res);				
				String authorizer_appid= null ;
				String authorizer_access_token= null ;
				String authorizer_refresh_token = null ;
				String mp_type =null;
				
				JSONObject  authorizer_info =authInfo.getJSONObject("authorizer_info");
				JSONObject  authorization_info =authInfo.getJSONObject("authorization_info");
				
						   		 		authorizer_appid   = authorization_info.getString("authorizer_appid");
						   		 		authorizer_access_token   =  authorization_info.getString("authorizer_access_token");
						   		 		//int   expires_in   =  authorization_info.getInt("expires_in");
						   		 		authorizer_refresh_token  =  authorization_info.getString("authorizer_refresh_token");
									

				//mc.setKeyValue(authorizer_appid+"4authorizer_access_token", authorizer_access_token);
				//mc.setKeyValue(authorizer_appid+"4authorizer_refresh_token", authorizer_refresh_token);	
				
		        //mc.setKeyValue(authorizer_appid+"4authorizer_token_time", DateTimeUtil.getCurrentTime());//令牌获取时间
									
				JSONArray  func_info  =  authorization_info.getJSONArray("func_info");	
				//本次授权进入数据库		
				authorInfo2DB(authorizer_appid, authorizer_access_token, authorizer_refresh_token,func_info.toString());
				
				//后面授权落地页面需要使用
				JSONObject   resJSON  =  getAuthorizerInfo(authorizer_appid);
				
				try {
							JSONObject   minappJSON  = resJSON.getJSONObject("authorizer_info").getJSONObject("MiniProgramInfo");					
							if(minappJSON!=null){
								mp_type = "mp_minapp";
							}else{
								mp_type = "mp_gongzhonghao";
							}
							authInfo.put("mp_type", mp_type);
				}catch(JSONException  ex) {
					       mp_type = "mp_gongzhonghao";
					       authInfo.put("mp_type", mp_type);
				}

				return authInfo;
	}
	
	/**
	 * 第三方平台获取（刷新）授权公众号的接口调用凭据（令牌）
	 * refreshAuthorizerAccessToken(这里用一句话描述这个方法的作用)
	 * (这里描述这个方法适用条件 – 可选)
	 * @param authorizer_appid 
	 *void
	 * @exception 
	 * @since  0.0.1
	 */
	public  void refreshAuthorizerAccessToken(String authorizer_appid){
		
				//MemcachedManager  mc    = MemcachedManager.getMemcacheManager();
				//String component_access_token  = (String) mc.get("component_access_token");
				
				String component_access_token  = null;
				String url  = "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token="+component_access_token;
				//String authorizer_refresh_token  = (String) mc.get(authorizer_appid+"4authorizer_refresh_token");	
				String authorizer_refresh_token  = null;	
				
				JSONObject   reqJSON   = new JSONObject();
				reqJSON.put("component_appid", component_appid);
				reqJSON.put("authorizer_appid", authorizer_appid);		
				reqJSON.put("authorizer_refresh_token", authorizer_refresh_token);
				
				String data =reqJSON.toString();
				String res  = HTTPSDataManager.sendData(url, data);
				JSONObject   resJSON   = new JSONObject(res);
		
					logger.info("AuthManager-refreshAuthorizerAccessToken:"+res);
				
				if(resJSON.get("authorizer_access_token")!=null){			
						//mc.setKeyValue(authorizer_appid+"4authorizer_access_token", resJSON.getString("authorizer_access_token"));
						//mc.setKeyValue(authorizer_appid+"4authorizer_refresh_token", resJSON.getString("authorizer_refresh_token"));
				        //mc.setKeyValue(authorizer_appid+"4authorizer_token_time", DateTimeUtil.getCurrentDate());//令牌获取时间
				}else{
					logger.info("AuthManager-refreshAuthorizerAccessToken:fail");
				}
		
	}
	
	/**
	 * 第三方平台获取授权方的公众号帐号基本信息
	 * getAuthorizerInfo(这里用一句话描述这个方法的作用)
	 * (这里描述这个方法适用条件 – 可选)
	 * @param authorizer_appid
	 * @return 
	 *JSONObject
	 * @exception 
	 * @since  0.0.1
	 */
	public JSONObject getAuthorizerInfo(String authorizer_appid){
		
		//MemcachedManager  mc    = MemcachedManager.getMemcacheManager();		
		//String component_access_token  = (String) mc.get("component_access_token");
		String component_access_token  = null;
		String url  = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token="+component_access_token;
				
		JSONObject   reqJSON   = new JSONObject();
		reqJSON.put("component_appid", component_appid);
		reqJSON.put("authorizer_appid", authorizer_appid);		

		String data =reqJSON.toString();
		String res  = HTTPSDataManager.sendData(url, data);
		JSONObject   resJSON   = new JSONObject(res);
		
		logger.info("AuthManager-getAuthorizerInfo:"+res);

		String nick_name  =resJSON.getJSONObject("authorizer_info").getString("nick_name");
		String head_img = resJSON.getJSONObject("authorizer_info").getString("head_img");
		String user_name  = resJSON.getJSONObject("authorizer_info").getString("user_name");
		String principal_name =  resJSON.getJSONObject("authorizer_info").getString("principal_name");
		String qrcode_url  = resJSON.getJSONObject("authorizer_info").getString("qrcode_url");
		String authorizer_info = resJSON.getJSONObject("authorizer_info").toString();
		String authorization_info  =  resJSON.getJSONObject("authorization_info").toString();
		
		upateAuthorizerInfor(authorizer_appid, nick_name, head_img, user_name, principal_name, qrcode_url, authorizer_info, authorization_info);
		
		return resJSON;
	}
	
	
	//第三方平台获取授权方的选项设置信息
	
	
	//第三方平台设置授权方的选项信息
	
	
	//微信平台推送授权相关通知
	
	
	/**
	 * 处理微信给第三方平台推送的消息【用于接收取消授权通知、授权成功通知、授权更新通知，也用于接收ticket】
	 */
	public void processData(String postdata,String timestamp,String nonce,String msg_signature,String encrypt_type) {
		
		logger.info("component_verify-timestamp:"+timestamp);
		logger.info("component_verify-nonce:"+nonce);
		logger.info("component_verify-msg_signature:"+msg_signature);
		logger.info("component_verify-encrypt_type:"+encrypt_type);		
		logger.info("component_verify-postdata:"+postdata);
						
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document document = null;
		StringReader sr;
		InputSource is;
		
		try {
				db = dbf.newDocumentBuilder();
				sr = new StringReader(postdata);
				is = new InputSource(sr);
				document = db.parse(is);
		} catch (ParserConfigurationException | SAXException e1) {
				e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Element root = document.getDocumentElement();
		NodeList nodelistAppId = root.getElementsByTagName("AppId");	
		NodeList nodelistEncrypt = root.getElementsByTagName("Encrypt");	
		
		String appid = nodelistAppId.item(0).getTextContent();
		String encrypt = nodelistEncrypt.item(0).getTextContent();
		
		String  token, encodingAesKey,mingwen=null;
		token="wukonglai_jingubang_72bian";
		encodingAesKey="1wukonglai2jingubang372bian4huoyanjingjing5";
		//appId="wxef1378bc4ec6857a";
		
		try {
			WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appid);
			
			String newPostdata = "<xml><AppId><![CDATA["+appid+"]]></AppId><Encrypt><![CDATA["+encrypt+"]]></Encrypt></xml>";
			logger.info("component_verify-ciphertext:"+newPostdata);
			
			mingwen  = pc.decryptEvent(msg_signature, timestamp, nonce, newPostdata);
			logger.info("component_verify-text:"+mingwen);
			
		} catch (AesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		sr = new StringReader(mingwen);
		is = new InputSource(sr);
		
		try {
			document = db.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		root = document.getDocumentElement();
		nodelistAppId = root.getElementsByTagName("AppId");
		NodeList nodelistInfoType = root.getElementsByTagName("InfoType");
					appid = nodelistAppId.item(0).getTextContent();
        String infotype = nodelistInfoType.item(0).getTextContent();

		logger.info("component_verify-appid:"+appid);
		logger.info("component_verify-infotype:"+infotype);
		

		//对infotype 进行判定
		if("component_verify_ticket".equals(infotype)){
			
			NodeList nodelistComponentVerifyTicket = root.getElementsByTagName("ComponentVerifyTicket");
			String ticket = nodelistComponentVerifyTicket.item(0).getTextContent();			
			componentVerifyTicket(ticket);
			
			logger.info("AuthManager-[ component_verify_ticket ] component_verify_ticket:"+ticket);
			
		}else if("unauthorized".equals(infotype)){
			
			NodeList nodelistComponentVerifyAuthorizerAppid = root.getElementsByTagName("AuthorizerAppid");
			String AuthorizerAppid = nodelistComponentVerifyAuthorizerAppid.item(0).getTextContent();
			
			logger.info("AuthManager-[ unauthorized ] AuthorizerAppid:"+AuthorizerAppid);
			
		}else if("authorized".equals(infotype)){
			
			NodeList nodelistComponentVerifyAuthorizerAppid = root.getElementsByTagName("AuthorizerAppid");
			String AuthorizerAppid = nodelistComponentVerifyAuthorizerAppid.item(0).getTextContent();
			
			NodeList nodelistComponentVerifyAuthorizationCode= root.getElementsByTagName("AuthorizationCode");
			String AuthorizationCode = nodelistComponentVerifyAuthorizationCode.item(0).getTextContent();
			
			NodeList nodelistComponentVerifyAuthorizationCodeExpiredTime= root.getElementsByTagName("AuthorizationCodeExpiredTime");
			String AuthorizationCodeExpiredTime = nodelistComponentVerifyAuthorizationCodeExpiredTime.item(0).getTextContent();
			
			logger.info("AuthManager-[ authorized ] AuthorizerAppid:"+AuthorizerAppid);
			logger.info("AuthManager-[ authorized ] AuthorizationCode:"+AuthorizationCode);
			logger.info("AuthManager-[ authorized ] AuthorizationCodeExpiredTime:"+AuthorizationCodeExpiredTime);
			
		}
	}

	
	/**
	 * 
	 * 微信公众号的授权信息进入数据库
	 * (这里描述这个方法适用条件 – 可选)
	 * @param authorizer_appid
	 * @param authorizer_access_token
	 * @param authorizer_refresh_token 
	 *void
	 * @exception 
	 * @since  0.0.1
	 */
	 public void authorInfo2DB(String  authorizer_appid,String authorizer_access_token,String authorizer_refresh_token,String func_info){
		 
				String sql = " insert  into `wx_authorizer`(`authorizer_appid`, `authorizer_access_token`, `authorizer_refresh_token`,`func_info`) "
						+ "VALUES('" + authorizer_appid + "','" + authorizer_access_token + "','" + authorizer_refresh_token
						+ "','" + func_info + "')";
		
//				try {
//					//QueryRunner runner = new QueryRunner(C3p0Utils.getDataSource());
//					runner.update(sql);
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
		
	 }
	 
	 /**
	  * 更新授权方信息
	  * upateAuthorizerInfor(这里用一句话描述这个方法的作用)
	  * (这里描述这个方法适用条件 – 可选)
	  * @param authorizer_appid
	  * @param nick_name
	  * @param head_img
	  * @param user_name
	  * @param principal_name
	  * @param qrcode_url
	  * @param authorizer_info
	  * @param authorization_info 
	  *void
	  * @exception 
	  * @since  0.0.1
	  */
	 public void upateAuthorizerInfor(String  authorizer_appid,String nick_name,String head_img,String user_name,String principal_name,String qrcode_url,String authorizer_info,String authorization_info){
		 
//			QueryRunner runner = new QueryRunner(C3p0Utils.getDataSource());
//	
//			String sql = " update `wx_authorizer` set `nick_name` ='" + nick_name + "', `head_img` ='" + head_img
//					+ "',`user_name`='" + user_name + "' ,`principal_name`='" + principal_name + "',`qrcode_url`='"
//					+ qrcode_url + "',`authorizer_info`='" + authorizer_info + "',`authorization_info`='"
//					+ authorization_info + "' where authorizer_appid='" + authorizer_appid + "'";
//	
//			try {
//				runner.update(sql);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
		
		 
	 }

	 /**
	  * 心跳包入数据库  component_verify_ticket
	  * componentVerifyTicket(这里用一句话描述这个方法的作用)
	  * @param component_verify_ticket 
	  *void
	  * @exception 
	  * @since  0.0.1
	  */
	 public void componentVerifyTicket(String component_verify_ticket){
		 
//		 QueryRunner runner = new QueryRunner(C3p0Utils.getDataSource());
//		 String sql = " INSERT INTO `wx_thir3d`(`component_verify_ticket`) "
//		 		+ "VALUES('"+component_verify_ticket+"')";	
//			
//		    try {
//				runner.update(sql);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
		 
	 }
	 
	
	 public void authorizerExpired2DB(String appid){
		 
		 //String sql4ExpireTime = "insert into rules_appid(appid,openid,ts4expire,idtpt,price) values('"+appid+"','"+openid+"',DATE_ADD(now(),INTERVAL 30 DAY),'"+tptName+"','"+price+"')";
		 
	 }
	 
/**
	 * 开放平台验证需要的
	 * weixinOpenVertify(这里用一句话描述这个方法的作用)
	 * (这里描述这个方法适用条件 – 可选)
	 * @param query_auth_code
	 * @param csText 
	 *void
	 * @exception 
	 * @since  0.0.1
		 */
	 public void weixinOpenVertify(String query_auth_code,Text csText){
			
			//MemcachedManager  mc    = MemcachedManager.getMemcacheManager();
			//String component_access_token = (String)mc.get("component_access_token");
			String component_access_token = null;
			
			JSONObject  authurizerAccessTokenInfo  = getAuthInfo(query_auth_code);
			
			JSONObject  authorization_info =authurizerAccessTokenInfo.getJSONObject("authorization_info");

			String 	authorizer_appid   = authorization_info.getString("authorizer_appid");
	        String   	authorizer_access_token   =  authorization_info.getString("authorizer_access_token");
			       
//	        ResponseMsgBuilder  rcsMsg  = new ResponseMsgBuilder();
//	        String rcsJSON  = rcsMsg.text(csText, "");
	        
	        //logger.info("weixin open vertify cs msg:"+rcsJSON);
	        
	        //Response  res  = new Response(authorizer_access_token);
	        //res.sendCSMsg(rcsJSON);				
		}
		
		

}
