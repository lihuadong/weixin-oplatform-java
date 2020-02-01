/**
 * 包打听全知道-微信H5版本
 * jingubang.base.weixin.open.service
 * MPCoreService.java
 * Ver0.0.1
 * Sep 4, 2019-4:19:04 PM
 * 2019全智道(北京)科技有限公司-版权所有
 * 
 */
package weixin.oplatform;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;

import jingubang.th3rd.service.MemcachedManager;
import jingubang.util.db.C3p0Utils;
import weixin.msg.Request;
import weixin.msg.builder.ResponseMsgBuilder;
import weixin.msg.model.base.WeixinEventMsg;
import weixin.msg.model.base.WeixinMsgBase;
import weixin.msg.model.common.Image;
import weixin.msg.model.common.Link;
import weixin.msg.model.common.Location;
import weixin.msg.model.common.Text;
import weixin.msg.model.common.Video;
import weixin.msg.model.common.Voice;
import weixin.msg.model.cs.Content;
import weixin.msg.model.event.ScanEvent;
import weixin.msg.model.event.SubscribeEvent;
import weixin.user.UserManager;

/**
 * 
 * MPCoreService
 * 
 * 李华栋
 * Sep 4, 2019 4:19:04 PM
 * 
 * @version 0.0.1
 * 
 */
public class MPCoreService {
	
	private static Logger logger = Logger.getLogger(MPCoreService.class);      
	private static String component_appid;
	private static String component_token;
	private static String component_encoding_aes_key;
	
	WXBizMsgCrypt crypt;
		
	static{
					Properties prop = new Properties();  
					InputStream in = AuthManager.class.getResourceAsStream("/weixin3rd.properties"); 
					
					try {  
			            prop.load(in);  
			        } catch (IOException e) {  
			            e.printStackTrace();  
			        }  	
			
					component_appid = prop.getProperty("component_appid");  
					component_token = prop.getProperty("component_token");  
					component_encoding_aes_key= prop.getProperty("component_encoding_aes_key");  				
	}
	
	 public  MPCoreService() {
		 try {
			crypt = new WXBizMsgCrypt(component_token, component_encoding_aes_key, component_appid);
		} catch (AesException e) {
			e.printStackTrace();
		}
	 }
	 
     public  String  processData(String timestamp,String nonce,String msg_signature,String encrypt_type,String postdata) {
			
		    logger.info("MPCoreService-msg_receiver-timestamp:"+timestamp);
			logger.info("MPCoreService-msg_receiver-nonce:"+nonce);
			logger.info("MPCoreService-msg_receiver-msg_signature:"+msg_signature);
			logger.info("MPCoreService-msg_receiver-encrypt_type:"+encrypt_type);		
			logger.info("MPCoreService-msg_receiver-postdata-ciphertext:"+postdata);
			
			String resStr=null;
			try{

				 String resXml=preProcessData(timestamp,nonce,msg_signature,encrypt_type, postdata);
		         InputStream weixinMsgStream = new   ByteArrayInputStream(resXml.getBytes("UTF-8"));  ;
		         String resTempStr = pushWeixinMsg(weixinMsgStream);
		         weixinMsgStream.close();
		         
		         logger.info("MPCoreService-msg_receiver-senddata-text:"+resXml);
		         resStr = crypt.encryptMsg(resTempStr, timestamp, nonce); //返回的消息再加密封装成XML		         
		         logger.info("MPCoreService-msg_receiver-senddata-ciphertext:"+resXml);		         
		         
		      }catch (Exception e) {
		   	   		e.printStackTrace();
			  }
			
			  return resStr;
	 }
	 
	 //预先处理数据进行解包操作
	 private  String  preProcessData(String timestamp,String nonce,String msg_signature,String encrypt_type,String postdata) {
		 
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = null;
					Document document = null;
					StringReader sr= null;
					InputSource is= null;
					
					String tousername = null;
					String encrypt = null;
			
					try {
								db = dbf.newDocumentBuilder();
								sr = new StringReader(postdata);
								is = new InputSource(sr);
								document = db.parse(is);
								
								Element root = document.getDocumentElement();
								NodeList nodelistToUserName = root.getElementsByTagName("ToUserName");	
								NodeList nodelistEncrypt = root.getElementsByTagName("Encrypt");	
								
								tousername = nodelistToUserName.item(0).getTextContent();
								encrypt = nodelistEncrypt.item(0).getTextContent();
								
					} catch (ParserConfigurationException | SAXException e1) {
						e1.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
			
			
					String  plainStr=null;
					try {
						
						String newPostdata = "<xml><ToUserName><![CDATA["+tousername+"]]></ToUserName><Encrypt><![CDATA["+encrypt+"]]></Encrypt></xml>";
						plainStr  = crypt.decryptMsg(msg_signature, timestamp, nonce, newPostdata);
						
					} catch (AesException e) {
						e.printStackTrace();
					}
			
			       return plainStr;
	 }

	 
     private  String  pushWeixinMsg(InputStream is){
		

		WeixinMsgBase  wxMsg  = Request.getMessage(is);
		String  returnMsg ="success"; //默认返回字符串
		
		String openid   = wxMsg.getFromUserNameOpenID();
		String gid  = wxMsg.getToUserNameGID();

	    String msgType  = wxMsg.getMsgType();
	    if(msgType.equals("text")){
	    	
	 	       if(gid.equals("gh_3c884a361561") &&((Text)wxMsg).getContent().equals("TESTCOMPONENT_MSG_TYPE_TEXT")){   //测试平台专用
	 	    	        returnMsg ="TESTCOMPONENT_MSG_TYPE_TEXT_callback"; 
	 	    	       logger.info("open platform test msg :TESTCOMPONENT_MSG_TYPE_TEXT_callback");
		       }
	 	       
	 	       if(((Text)wxMsg).getContent().contains("QUERY_AUTH_CODE")){ //测试平台专用
	 	    	   		
	 	    	   		AuthManager  am  = new AuthManager();
	 	    	   		String str  =  (String)((Text)wxMsg).getContent();
	 	    	   		String auth_code  = str.substring(16);
	 	    	   		
	 	    	   	     weixin.msg.model.cs.Text csText  = new weixin.msg.model.cs.Text();
	 	    	   		 csText.setMsgtype("text");
	 	    	   		 Content c   = new Content();
	 	    	   		 c.setContent(auth_code+"_from_api");
	 	    	   		 csText.setText(c);csText.setTouser(openid);
	 	    	   		
	 	    	   	    am.weixinOpenVertify(auth_code,csText);	
	 	    	   	   
	 	    	   	    logger.info("open platform test msg :QUERY_AUTH_CODE");
	 	       }
	 	       //如果没有设置则会自动使用默认的文字
	    	        	    	        
	    }else if(msgType.equals("image")){
	    			
	    			returnMsg ="收到图片\n"+((Image)wxMsg).getMediaId()+"\n"+((Image)wxMsg).getPicUrl()+"\n"; 

	    }else if(msgType.equals("voice")){
	    			returnMsg ="收到语音\n"+((Voice)wxMsg).getMediaId()+"\n"+((Voice)wxMsg).getRecognition()+"\n"+((Voice)wxMsg).getFormat(); 
	
	    }else if(msgType.equals("video")){
	    			returnMsg ="收到视频\n"+((Video)wxMsg).getMediaId()+"\n"+((Video)wxMsg).getDescription()+"\n"+((Video)wxMsg).getThumbMediaId(); 
	    			
	    }else if(msgType.equals("shortvideo")){
	    			returnMsg ="收到小视频\n"+((Video)wxMsg).getMediaId()+"\n"+((Video)wxMsg).getDescription()+"\n"+((Video)wxMsg).getThumbMediaId(); ; 
	    			
	    }else if(msgType.equals("link")){
	    			returnMsg ="收到链接\n"+((Link)wxMsg).getTitle()+"\n"+((Link)wxMsg).getDescription()+"\n"+((Link)wxMsg).getUrl(); 
	    			
	    }else if(msgType.equals("location")){
				returnMsg ="收到位置\n"+((Location)wxMsg).getLabel()+"\n"+((Location)wxMsg).getLocation_X()+"\n"+((Location)wxMsg).getLocation_Y()+"\n"+((Location)wxMsg).getScale(); 
			
	    }else if(msgType.equals("event")){
	    	
	    	       if(((WeixinEventMsg)wxMsg).getEvent().equals("subscribe")){
	    	    	           //两种情形一是带有参数的，二是不带参数的
	    	    	   		   return subscribeEvent((WeixinEventMsg)wxMsg);
	    	       }else if(((WeixinEventMsg)wxMsg).getEvent().equals("SCAN")){
	    	    	           //根据不同的场景完成不同的扫码任务
	    	    	   		   return scanEvent((WeixinEventMsg)wxMsg);
	    	       }else if(((WeixinEventMsg)wxMsg).getEvent().equals("LOCATION")){
	    	    	            returnMsg ="收到自动上报位置"; 
	 	           }else if(((WeixinEventMsg)wxMsg).getEvent().equals("CLICK")){
	 	        	   				returnMsg ="收到CLICK事件"; 
		 	       }else if(((WeixinEventMsg)wxMsg).getEvent().equals("VIEW")){
		 	    	   				returnMsg ="收到VIEW事件"; 
		 	       }else if(((WeixinEventMsg)wxMsg).getEvent().equals("weapp_audit_success")){
	    	   			       //当小程序有审核结果后，第三方平台将可以通过开放平台上填写的回调地址，获得审核结果通知; 
		 	    	            releaseMinapp(wxMsg);
	               }
	    	       
	    	      ///////////////////////////////测试平台专用
	    	       if(gid.equals("gh_3c884a361561")){   
		    	    	      returnMsg =((WeixinEventMsg)wxMsg).getEvent()+"from_callback"; 
		    	    	      logger.info("open platform test msg:event_from_callback");
	    	       }
	    }
		
		Text  text   = new Text();
		text.setContent(returnMsg);
		text.setCreateTime(String.valueOf(System.currentTimeMillis()));
		text.setFromUserNameOpenID(gid);			
		text.setToUserNameGID(openid);    //这里要正好反来过
		text.setMsgType("text");       
        returnMsg   = ResponseMsgBuilder.text(text);
    
		return returnMsg;  //返回的XML化的数据或空数据
		
	}
	
	 //扫码事件处理分为两大类来处理
	 private  String  scanEvent(WeixinEventMsg  msg){
		 
			 
			 MemcachedManager  mc    =MemcachedManager.getMemcacheManager();			 
			 String eventKey    = msg.getEventKey();
			 String eventTicket  =  ((ScanEvent)msg).getTicket();
		 
			 String openid   = msg.getFromUserNameOpenID();
			 UserManager   um  = new UserManager((String)mc.get("at4wukonglai"));
			 HashMap<String, Object> userInfo;
			 JSONObject  json  =  new JSONObject();
			 String  senceDesc = "扫码";
				
			 try {
					 userInfo = um.getUserInfoByOpenid(openid);

					 json.put("nickname", (String)userInfo.get("nickname"));
					 json.put("headimgurl", (String)userInfo.get("headimgurl"));
					 json.put("sex", String.valueOf(userInfo.get("sex")));
					 //json.put("unionid", (String)userInfo.get("unionid"));
					 json.put("openid",openid);
				}catch (Exception e) {
					e.printStackTrace();
				}
			 
			 
			 if(eventKey.equals("999")){  		/////////////////////////////////项目类的扫码
				 senceDesc = "悟空来-扫码登陆成功\n可能是最时髦的技能、时间交易服务平台";		//悟空来扫码登陆
			 }else if(eventKey.equals("666")){
				 senceDesc = "包到位小程序-扫码登陆成功\n现在可以免费体验二维码生成器、短链接生成器";	//包到位小程序SaaS扫码登陆
				 
			 }else if(eventKey.equals("1001")){ /////////////////////////////运营市场类的扫码
           	 	senceDesc = "success";//恭喜您参与春节财神送红包活动
           	 	//NewyYearxunbaoWorker  nyxb  = new NewyYearxunbaoWorker(openid,eventTicket);
           	 	// t   = new Thread(nyxb);t.start();
           	 	return senceDesc;
			 }
			 			 
			try {
				json.put("desc", senceDesc);				 
				 mc.put(eventTicket, json.toString());//扫码信息放到Mem缓存中
			} catch (JSONException e) {
				e.printStackTrace();
			}
			 
			 Text  text4Scan   = new Text();
			 text4Scan.setContent(senceDesc);
			 text4Scan.setCreateTime(String.valueOf(System.currentTimeMillis()));
			 text4Scan.setFromUserNameOpenID(msg.getToUserNameGID());
			 text4Scan.setToUserNameGID(msg.getFromUserNameOpenID());
			 text4Scan.setMsgType("text");
		     return   ResponseMsgBuilder.text(text4Scan);

	 }
	 
	 //关注事件
	 private  String  subscribeEvent(WeixinEventMsg msg){
		 
		 String  senceDesc = "关注";
		 //带参数需要转换成扫描对象
		 if(msg.getEventKey()!=null){
			 
			 //logger.debug("接收微信关注事件消息KEY:"+ (msg).getEventKey());
			 //logger.debug("接收微信关注事件消息TICKET:"+ ((SubscribeEvent)msg).getTicket());
			 String eventKey =  msg.getEventKey();			
			 String eventTicket = ((SubscribeEvent)msg).getTicket();
			 
			 ScanEvent   scanMsg  =  new ScanEvent();
			 scanMsg.setTicket(eventTicket);			 
			 scanMsg.setFromUserNameOpenID(msg.getFromUserNameOpenID());
			 scanMsg.setToUserNameGID(msg.getToUserNameGID());
			 if(eventKey.equals("qrscene_1001")){				 
				    scanMsg.setEventKey("1001");
        	 		return scanEvent(scanMsg); 
			 }else if(eventKey.equals("qrscene_999")){				 
				    scanMsg.setEventKey("999");
        	 		return scanEvent(scanMsg); 
			 }else if(eventKey.equals("qrscene_888")){				 
				    scanMsg.setEventKey("888");
        	 		return scanEvent(scanMsg); 
			 }else if(eventKey.equals("qrscene_777")){				 
				    scanMsg.setEventKey("777");
        	 		return scanEvent(scanMsg); 
			 }else if(eventKey.equals("qrscene_998")){				 
				    scanMsg.setEventKey("998");
        	 		return scanEvent(scanMsg); 
			 }
		 }
		 
		 senceDesc = senceDesc+"成功";
		 Text  text4Subscribe   = new Text();
		 text4Subscribe.setContent(senceDesc);
		 text4Subscribe.setCreateTime(String.valueOf(System.currentTimeMillis()));
		 text4Subscribe.setFromUserNameOpenID(msg.getToUserNameGID());
		 text4Subscribe.setToUserNameGID(msg.getFromUserNameOpenID());
		 text4Subscribe.setMsgType("text");
	     return   ResponseMsgBuilder.text(text4Subscribe);

	 }
	
	 //小程序审核通过之后，自动发布内容
	 private  void  releaseMinapp(WeixinMsgBase wxMsg){
		 
		 String  gid  =  wxMsg.getFromUserNameOpenID();

		try {			 
				 QueryRunner runner = new QueryRunner(C3p0Utils.getDataSource());		
				 String sql = "SELECT authorizer_appid FROM wx_authorizer  where user_name='"+gid+"'";
				 String authorizer_appid = runner.query(sql,new ScalarHandler<String>());			
				//小程序审核通过之后，自动发布	
				MinappCoreService  mc  = new MinappCoreService(authorizer_appid);
			    mc.release();
			    
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 }

}
