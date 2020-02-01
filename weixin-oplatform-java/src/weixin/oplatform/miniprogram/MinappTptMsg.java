/**
 * 包到位小程序SaaS
 * cn.a86.weixin4open.biz
 * MinappTtpMsg.java
 * Ver0.0.1
 * 2017年5月15日-下午9:05:16
 *  2017全智道(北京)科技有限公司-版权所有
 * 
 */
package weixin.oplatform.miniprogram;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import jingubang.base.weixin.open.service.AuthManager;
import jingubang.th3rd.service.MemcachedManager;
import jingubang.util.db.C3p0Utils;
import jingubang.util.http.HttpsDataManager;


/**
 * 
 * MinappTtpMsg
 * 
 * 李华栋
 * 李华栋
 * 2017年5月15日 下午9:05:16
 * 
 * @version 0.0.1
 * 
 */
public class MinappTptMsg {

	private static Logger logger = Logger.getLogger(MinappTptMsg.class);

//订单号
//{{keyword1.DATA}}
//顾客姓名
//{{keyword2.DATA}}
//所需商品
//{{keyword3.DATA}}
//订单金额
//{{keyword4.DATA}}
//数量
//{{keyword5.DATA}}
//购买时间
//{{keyword6.DATA}}
//备注
//{{keyword7.DATA}}
	
    public static void sendMinappTptMsg(String orderid,String openid,String formid,String tptid,String access_token){
    	          
    				JSONObject  json  = new JSONObject();
    				json.put("touser", openid);
    				json.put("template_id",tptid);	
    				json.put("page", "/pages/orderList/orderDetail/orderDetail?orderid="+orderid);	
    				json.put("form_id", formid);
    				
    				
    				 SimpleDateFormat  sdf  = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss"); 
    				 String ts = sdf.format(new Date()); 
    				
    				JSONObject  keyword1  = new JSONObject();
    				keyword1.put("value", "2806463724");
    				keyword1.put("color","#173177");
    				
    				JSONObject  keyword2  = new JSONObject();
    				keyword2.put("value","黄智杰");
    				keyword2.put("color","#173177");
    				
    				JSONObject  keyword3  = new JSONObject();
    				keyword3.put("value", "馒头");
    				keyword3.put("color","#173177");
    				
    				JSONObject  keyword4  = new JSONObject();
    				keyword4.put("value", "5.00元");
    				keyword4.put("color","#173177");
    				
    				JSONObject  keyword5  = new JSONObject();
    				keyword5.put("value", "1袋");
    				keyword5.put("color","#173177");
    				
    				JSONObject  keyword6  = new JSONObject();
    				keyword6.put("value", ts);
    				keyword6.put("color","#173177");
    				
    				JSONObject  keyword7  = new JSONObject();
    				keyword7.put("value", "请尽量安排吸烟房");
    				keyword7.put("color","#173177");

    			JSONObject  data  = new JSONObject();
    			data.put("keyword1", keyword1);
    			data.put("keyword2", keyword2);
    			data.put("keyword3", keyword3);
   			
    			data.put("keyword4", keyword4);
    			data.put("keyword5", keyword5);
    			data.put("keyword6", keyword6);
    			data.put("keyword7", keyword7);
    			
    			
    			json.put("data", data);
    			json.put("emphasis_keyword", keyword3);
    			
    			System.out.println(json.toString());
    			//sendMinappTptMsg(json,access_token);
    			
    }
    

    
    
    
    public  static void sendMinappTptMsg(String appid,JSONObject json){
    	   	      
	      	//根据appid查找tptid
	    		String tptid  = getTptID(appid);
	    		
	    	    //根据appid查找accesstoken    	
			MemcachedManager  mc    = MemcachedManager.getMemcacheManager();
	    	    String access_token =  (String) mc.get(appid+"4authorizer_access_token");
	    	    json.put("template_id",tptid);
	    	    
	    	   //根据appid查找openid
	    	    List<String> openidList   = getNotifyOpenidList(appid);
	    	    
	    	    if(openidList!=null){
	    	      	logger.info("发送模版消息openid size："+openidList.size());
	    	    	    for(int i=0;i<openidList.size();i++){
	    	    	    	    String openid  = openidList.get(i);
	    	    	    	     json.put("touser", openid);
	    	    	    	     logger.info("发送模版消息内容:"+json.toString());
	    	    	    		String tmpRES  = sendMinappTptMsgBase(access_token,json);
	    	    	    		if(tmpRES.contains("42001")){ //
	    	    	    			AuthManager  am  = new AuthManager();
	    	    	    			am.refreshAuthorizerAccessToken(appid);
	    	    	    			access_token  = (String) mc.get(appid+"4authorizer_access_token");
	    	    	    			tmpRES  = sendMinappTptMsgBase(access_token,json);
	    	    	    		}
	    	    	    		logger.info("发送模版消息反馈:"+tmpRES);
	        	    }
	    	    }else{
	    	    	   logger.info("没有绑定模版消息的收信人 openid");
	    	    }
	    	    
	    	    logger.info("发送模版消息结束");

    }
    
    public  static String sendMinappTptMsgBase(String access_token,JSONObject json){
    	
	    String url  = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token="+access_token;
	    String resStr  = HttpsDataManager.sendData(url,json.toString());  
	    return resStr;
    	
    }
    
    //获取需要发送通知的openid列表
    public static List<String>  getNotifyOpenidList(String appid){
    	
       	List<String>  openidList = null;
		QueryRunner runner = new QueryRunner(C3p0Utils.getDataSource());
		String  strSQL  =  "SELECT openid FROM crm_user where is_block = '1' and appid = ?";
		
		Object [] params = new Object[]{appid};
		
	    try {
			List<Map<String,Object>> list = runner.query(strSQL, new MapListHandler(), params);
			if(list.size()>0){
				openidList   = new ArrayList<String>();
				for(int i=0;i<list.size();i++){
					openidList.add((String) ((Map<String,Object>)list.get(i)).get("openid"));
				}
			}else{
				logger.info(appid +"还没有绑定消息推送人员");
			}		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return openidList;

    }
    
    //获取需要发送的模板ID
    private static String  getTptID(String appid){
    	
    	    String tptID="tptid";
    		QueryRunner runner = new QueryRunner(C3p0Utils.getDataSource());
    		String  strSQL  =  "SELECT tptmsg_id  from wx_tptmsg where appid = ?";
    		
    		Object [] params = new Object[]{appid};
        try {
			List<Map<String,Object>> list = runner.query(strSQL, new MapListHandler(), params);
			if(list  !=null ){
				
				tptID  =  (String) ((Map<String,Object>)list.get(0)).get("tptmsg_id");
			}else{
				logger.info("没有可以使用的模板ID");
			}			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return tptID;
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
        String  formid  = "AfBTrEoBGplW7BClpUOfhj_vm_hDXhjbbQ97_1jm94A"; //悟空来
        String  openid = "o8M3t0GitNLo81iZsBy0u304q7Fs";//悟空来
        String  tptid = "AfBTrEoBGplW7BClpUOfhj_vm_hDXhjbbQ97_1jm94A";//悟空来
        String  orderid = "orderid";
        String  access_token = "";
        
        sendMinappTptMsg(orderid,openid,formid,tptid, access_token);
        
	}

}
