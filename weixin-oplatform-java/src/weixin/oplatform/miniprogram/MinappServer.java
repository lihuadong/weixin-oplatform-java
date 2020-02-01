/**
 * 包到位小程序SaaS
 * cn.a86.weixin4open.biz
 * MinappManager.java
 * Ver0.0.1
 * 2017年5月8日-下午6:15:10
 *  2017全智道(北京)科技有限公司-版权所有
 * 
 */
package weixin.oplatform.miniprogram;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import jingubang.base.weixin.open.service.AuthManager;
import jingubang.th3rd.service.MemcachedManager;
import jingubang.util.http.HttpsDataManager;


/**
 * 
 * MinappManager
 * 
 * 李华栋
 * 李华栋
 * 2017年5月8日 下午6:15:10
 * 
 * @version 0.0.1
 * 
 */
public class MinappServer {

	private Logger  logger  =  Logger.getLogger(MinappServer.class);
	private String authorizer_access_token;
	
	
	public MinappServer(String authorizer_appid){
		
		//获取历史时间
		MemcachedManager  mc    = MemcachedManager.getMemcacheManager();
		String historyTimeStr  = (String) mc.get(authorizer_appid+"4authorizer_token_time");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			Date historyDate   = sdf.parse(historyTimeStr);
			long  historyTime  = historyDate.getTime();
			//获取当前时间
			long currentTime  = System.currentTimeMillis();
			long timeTag  = (currentTime-historyTime)/1000; 
			if(timeTag >7200){//如果已经超时则刷新
				AuthManager  am  = new AuthManager();
				am.refreshAuthorizerAccessToken(authorizer_appid);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		this.authorizer_access_token= (String) mc.get(authorizer_appid+"4authorizer_access_token");
	}
	
	//add服务器地址
	public JSONObject  addServerAddress(){
		
		 JSONObject   resJSON;
		 String url  = "https://api.weixin.qq.com/wxa/modify_domain?access_token="+authorizer_access_token;
		
		 
		 JSONObject   reqJSON   = new JSONObject();
		 reqJSON.put("action", "add");
		 
		 JSONArray   urlDomainArray  = new JSONArray();

		 
//		 urlDomainArray.put("https://fullstackeer.com");
//		 urlDomainArray.put("https://fullstackeer.cn");
//		 urlDomainArray.put("https://fullstacker.cn");		 
//		 urlDomainArray.put("https://mainfn.com");	
//		 urlDomainArray.put("https://atbole.com");
//		 urlDomainArray.put("https://1fenzi.com");
//		 urlDomainArray.put("https://trsum.com");
//		 urlDomainArray.put("https://yihujilai.com");
		 
		 urlDomainArray.put("https://a86.cn");
		 urlDomainArray.put("https://wukonglai.com");
		 urlDomainArray.put("https://baodaowei.com");
//		 urlDomainArray.put("https://bdtqzd.com");
		 
		 urlDomainArray.put("https://baodating.net.cn");
//		 urlDomainArray.put("https://afuquanzhidao.com");
//		 urlDomainArray.put("https://yihujiulai.com");
//		 urlDomainArray.put("https://yihujilai.cn");
		 
		 reqJSON.put("requestdomain", urlDomainArray);
		 reqJSON.put("uploaddomain", urlDomainArray);
		 reqJSON.put("downloaddomain", urlDomainArray);
		 
		 JSONArray   wssDomainArray  = new JSONArray();
		 
		 wssDomainArray.put("wss://a86.cn");
		 wssDomainArray.put("wss://wukonglai.com");
		 wssDomainArray.put("wss://baodaowei.com");
		 wssDomainArray.put("wss://bdtqzd.com");

		 wssDomainArray.put("wss://baodating.net.cn");		 
//		 wssDomainArray.put("wss://afuquanzhidao.com");
//		 wssDomainArray.put("wss://yihujiulai.com");
//		 wssDomainArray.put("wss://yihujilai.cn");

//		 wssDomainArray.put("wss://yihujilai.com");
//		 wssDomainArray.put("wss://trsum.com");
//		 wssDomainArray.put("wss://1fenzi.com");
//		 wssDomainArray.put("wss://baodaowei.com");
		 
//		 wssDomainArray.put("wss://fullstacker.cn");
//		 wssDomainArray.put("wss://fullstackeer.cn");
//		 wssDomainArray.put("wss://fullstackeer.com");
//		 wssDomainArray.put("wss://mainfn.com");
		 
		 
		 reqJSON.put("wsrequestdomain", wssDomainArray);
		 
		 //logger.info("添加服务器地址JSON信息:"+reqJSON.toString());
		 //System.out.println("添加服务器地址JSON信息:"+reqJSON.toString());
		 String resStr   =  HttpsDataManager.sendData(url, reqJSON.toString());
		 
		resJSON   = new JSONObject(resStr);	
		
		return resJSON;
	}  
	
	//delete服务器地址
	public JSONObject  deleteServerAddress(){
		
		 JSONObject   resJSON;
		 String url  = "https://api.weixin.qq.com/wxa/modify_domain?access_token="+authorizer_access_token;
		
		 
		 JSONObject   reqJSON   = new JSONObject();
		 reqJSON.put("action", "delete");
		 
		 JSONArray   urlDomainArray  = new JSONArray();
		 
		 urlDomainArray.put("https://baodaowei.com");
		 urlDomainArray.put("https://wukonglai.com");
		 urlDomainArray.put("https://baodaowei.com");
		 urlDomainArray.put("https://bdtqzd.com");
		 
		 urlDomainArray.put("https://baodating.net.cn");
		 
		 reqJSON.put("requestdomain", urlDomainArray);
		 reqJSON.put("uploaddomain", urlDomainArray);
		 reqJSON.put("downloaddomain", urlDomainArray);
		 
		 JSONArray   wssDomainArray  = new JSONArray();
		 
		 wssDomainArray.put("wss://a86.cn");
		 wssDomainArray.put("wss://wukonglai.com");
		 wssDomainArray.put("wss://baodaowei.com");
		 wssDomainArray.put("wss://bdtqzd.com");

		 wssDomainArray.put("wss://baodating.net.cn");	
		 
		 reqJSON.put("wsrequestdomain", wssDomainArray);
		 
		 String resStr   =  HttpsDataManager.sendData(url, reqJSON.toString());
		 
		resJSON   = new JSONObject(resStr);	
		
		return resJSON;
	} 
	
	//set服务器地址
	public JSONObject  setServerAddress(){
		
		 JSONObject   resJSON;
		 String url  = "https://api.weixin.qq.com/wxa/modify_domain?access_token="+authorizer_access_token;
		
		 
		 JSONObject   reqJSON   = new JSONObject();
		 reqJSON.put("action", "set");
		 
		 JSONArray   urlDomainArray  = new JSONArray();

		 urlDomainArray.put("https://baodaowei.com");
		 urlDomainArray.put("https://wukonglai.com");
		 urlDomainArray.put("https://baodaowei.com");
		 urlDomainArray.put("https://bdtqzd.com");
		 
		 urlDomainArray.put("https://baodating.net.cn");
		 
		 reqJSON.put("requestdomain", urlDomainArray);
		 reqJSON.put("uploaddomain", urlDomainArray);
		 reqJSON.put("downloaddomain", urlDomainArray);
		 
		 JSONArray   wssDomainArray  = new JSONArray();
		 wssDomainArray.put("wss://a86.cn");
		 wssDomainArray.put("wss://wukonglai.com");
		 wssDomainArray.put("wss://baodaowei.com");
		 wssDomainArray.put("wss://bdtqzd.com");

		 wssDomainArray.put("wss://baodating.net.cn");	
		 
		 reqJSON.put("wsrequestdomain", wssDomainArray);
		 
		 String resStr   =  HttpsDataManager.sendData(url, reqJSON.toString());
		 
		resJSON   = new JSONObject(resStr);	
		
		return resJSON;
	} 
	
	//get服务器地址
	public JSONObject  getServerAddress(){
		
		 JSONObject   resJSON;
		 String url  = "https://api.weixin.qq.com/wxa/modify_domain?access_token="+authorizer_access_token;
		
		 
		 JSONObject   reqJSON   = new JSONObject();
		 reqJSON.put("action", "get");

		 String resStr   =  HttpsDataManager.sendData(url, reqJSON.toString());		 
		 resJSON   = new JSONObject(resStr);	
		
		 return resJSON;
	} 
	
	
	/**
	 * main(这里用一句话描述这个方法的作用)
	 * (这里描述这个方法适用条件 – 可选)
	 * @param args 
	 *void
	 * @exception 
	 * @since  0.0.1
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        //
		String token  = "unBhnb_r7NkhYy-_Ai8d3C_YXOBtJxlBMdchgnGr7ibChuqaQgEd0CXDf2EdDX09_JXfhHU2A8DKhm4phWY3Sxi4guWNAjdJj1VDLPHK68Y6dewKeLjcGlKcAnud9wIOKJFgAGDUTP";
		MinappServer   ms  = new MinappServer(token);
		
		JSONObject  resJSON4Del   = ms.deleteServerAddress();		
		System.out.println("删除sever address:"+resJSON4Del.toString());
		
		JSONObject  resJSON4Get   = ms.getServerAddress();		
		System.out.println("获取sever address:"+resJSON4Get.toString());
		
		JSONObject  resJSON4Add   = ms.addServerAddress();		
		System.out.println("添加sever address:"+resJSON4Add.toString());
//		
//		JSONObject  resJSON4Set   = ms.setServerAddress();		
//		System.out.println("覆盖sever address:"+resJSON4Set.toString());
//		
		JSONObject  resJSON4Get2   = ms.getServerAddress();		
		System.out.println("再次获取sever address:"+resJSON4Get2.toString());
		
	}

}
