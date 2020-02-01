/**
 * 包到位小程序SaaS
 * cn.a86.weixin4open.biz
 * MinappMemberManager.java
 * Ver0.0.1
 * 2017年5月8日-下午8:09:45
 *  2017全智道(北京)科技有限公司-版权所有
 * 
 */
package weixin.oplatform.miniprogram;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import jingubang.base.weixin.open.service.AuthManager;
import jingubang.th3rd.service.MemcachedManager;
import jingubang.util.http.HttpsDataManager;


/**
 * 
 * MinappMemberManager
 * 
 * 李华栋
 * 李华栋
 * 2017年5月8日 下午8:09:45
 * 
 * @version 0.0.1
 * 
 */
public class MinappTester {

	
	private String authorizer_access_token;
	
	public MinappTester(String authorizer_appid){
		
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
	
	//1、绑定微信用户为小程序体验者
    public JSONObject  bindMember(String wechatid){
		
		 JSONObject   resJSON;

		 String url  = "https://api.weixin.qq.com/wxa/bind_tester?access_token="+authorizer_access_token;
		
		 
		 JSONObject   reqJSON   = new JSONObject();
		 reqJSON.put("wechatid", wechatid);
		 String resStr   =  HttpsDataManager.sendData(url, reqJSON.toString());
		 
		 resJSON   = new JSONObject(resStr);	
			
		return resJSON;
    }	 
	
	//2、解除绑定小程序的体验者
    public JSONObject  unbindMember(String wechatid){
		
		 JSONObject   resJSON;
		 

		 String url  = "https://api.weixin.qq.com/wxa/unbind_tester?access_token="+authorizer_access_token;
		
		 
		 JSONObject   reqJSON   = new JSONObject();
		 reqJSON.put("wechatid", wechatid);
		 
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

	}

}
