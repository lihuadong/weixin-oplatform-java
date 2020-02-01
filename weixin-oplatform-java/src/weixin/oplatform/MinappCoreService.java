/**
 * 包到位小程序SaaS
 * cn.a86.weixin4open.biz
 * MinappCode.java
 * Ver0.0.1
 * 2017年5月8日-下午8:54:47
 * 2017全智道(北京)科技有限公司-版权所有
 */
package weixin.oplatform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import jingubang.th3rd.service.MemcachedManager;
import jingubang.util.http.HttpsDataManager;
import jingubang.util.http.HttpsDataManagerAdv;

/**
 * 
 * MinappCode
 * 
 * 李华栋
 * 李华栋
 * 2017年5月8日 下午8:54:47
 * @version 0.0.1
 * 
 */
public class MinappCoreService {

	private static Logger  logger   = Logger.getLogger(MinappCoreService.class);
	private String authorizer_access_token;
	
	public MinappCoreService(String authorizer_appid){
		
		//获取历史时间
		MemcachedManager  mc    = MemcachedManager.getMemcacheManager();
		String historyTimeStr  = (String) mc.get(authorizer_appid+"4authorizer_token_time");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		AuthManager  am  = new AuthManager();
		
		if(historyTimeStr == null) {			
			am.refreshAuthorizerAccessToken(authorizer_appid);
		}else {
				Date historyDate = null;
				
				try {
					historyDate = sdf.parse(historyTimeStr);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				long historyTime  = historyDate.getTime();
				long currentTime  = System.currentTimeMillis();
				long timeTag  = (currentTime-historyTime)/1000; 
				
				if(timeTag > 7200){//如果已经超时则刷新
					am.refreshAuthorizerAccessToken(authorizer_appid);
				}			
		}		
		this.authorizer_access_token= (String) mc.get(authorizer_appid+"4authorizer_access_token");
		
	}
	
	//1、为授权的小程序帐号上传小程序代码
	public JSONObject  uploadCode(String template_id,JSONObject ext_json_obj,String user_version,String user_desc){
				 
		 String url  = "https://api.weixin.qq.com/wxa/commit?access_token="+authorizer_access_token;
				 
		 JSONObject   reqDataJSON   = new JSONObject();
		 reqDataJSON.put("template_id", template_id);		 
		 reqDataJSON.put("user_version", user_version);
		 reqDataJSON.put("user_desc", user_desc); 
		 reqDataJSON.put("ext_json", ext_json_obj.toString());
		 
		 String resStr   =  HttpsDataManager.sendData(url, reqDataJSON.toString());
		 JSONObject   resJSON  = new JSONObject(resStr);	
		
		return resJSON;
	}
		
	//2、获取体验小程序的体验二维码
	public JSONObject  getTasteQRCode(String authorizer_appid){
		
		String url  = "https://api.weixin.qq.com/wxa/get_qrcode?access_token="+authorizer_access_token;
		try {
			download(url, authorizer_appid+".jpg","/data/data4img/baodaowei/minapp_test_qr");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		JSONObject resJSON  = new JSONObject();
		resJSON.put("errcode", "0");
		resJSON.put("errmsg", "success");
		
		return resJSON;
	}
	
	//3、获取授权小程序帐号的可选类目
	public JSONObject  getCategory(){	

		 String url  = "https://api.weixin.qq.com/wxa/get_category?access_token="+authorizer_access_token;	 
		 String resStr   =  HttpsDataManager.sendData(url);		 
		 
		 JSONObject  resJSON  = new JSONObject(resStr);	
		 logger.info("MinappCode-getCategory()-resJSON:"+resJSON.toString());		 
		 return resJSON;
	}
	
	//4、获取小程序的第三方提交代码的页面配置（仅供第三方开发者代小程序调用）
	public JSONObject  getPage(){
		
		 String url  = "https://api.weixin.qq.com/wxa/get_page?access_token="+authorizer_access_token;
		 String resStr   =  HttpsDataManager.sendData(url);
		 
		 JSONObject   resJSON   = new JSONObject(resStr);	
		 logger.info("MinappCode-getPage()-resJSON:"+resJSON.toString());		 
		 return resJSON;
	}
	
	//5、将第三方提交的代码包提交审核（仅供第三方开发者代小程序调用）
	public JSONObject submitAudit(JSONObject item_list) {

		String url = "https://api.weixin.qq.com/wxa/submit_audit?access_token=" + authorizer_access_token;

		String resStr = HttpsDataManager.sendData(url, item_list.toString());
		
		JSONObject resJSON = new JSONObject(resStr);
		logger.info("MinappCode-submitAudit()-resJSON:"+resJSON.toString());
		return resJSON;
	}
	
	//5、（修订版本）将第三方提交的代码包提交审核（仅供第三方开发者代小程序调用）
	public String submitAuditByAdv(JSONObject item_list) {

		String url = "https://api.weixin.qq.com/wxa/submit_audit?access_token=" + authorizer_access_token;

		byte[] resStr = null;
		try {
			resStr = HttpsDataManagerAdv.post(url, item_list.toString(), "utf-8");
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		
		logger.info("MinappCode-submitAudit()-resJSON:"+resStr.toString());
		return new String(resStr);
	}
	
	//6、获取审核结果 微信会自动推送消息
	
	
	//7、获取第三方提交的审核版本的审核状态（仅供第三方代小程序调用）
	public JSONObject  getAuditstatus(int auditid){
		
		 String url  = "https://api.weixin.qq.com/wxa/get_auditstatus?access_token="+authorizer_access_token;
		 
		 JSONObject   reqJSON   = new JSONObject(); 
		 reqJSON.put("auditid", auditid);
		 String resStr   =  HttpsDataManager.sendData(url, reqJSON.toString());
		 
		 JSONObject   resJSON   = new JSONObject(resStr);	
		 logger.info("MinappCode-getAuditstatus()-resJSON:"+resJSON.toString());

		 return resJSON;
	}
	
	
	//8、查询最新一次提交的审核状态（仅供第三方代小程序调用）
	public JSONObject  getLatestAuditstatus(){

		 String url  = "https://api.weixin.qq.com/wxa/get_latest_auditstatus?access_token="+authorizer_access_token;
				 
		 String resStr   =  HttpsDataManager.sendData(url);		 
		 
		 JSONObject resJSON   = new JSONObject(resStr);	
		 logger.info("MinappCode-getLatestAuditstatus()-resJSON:"+resJSON.toString());
		 return resJSON;
	}
	
	
	//9、发布已通过审核的小程序（仅供第三方代小程序调用）
	public JSONObject  release(){
		
		 String url  = "https://api.weixin.qq.com/wxa/release?access_token="+authorizer_access_token;
				 
		 JSONObject   reqJSON   = new JSONObject(); //传空数据即可		 	 
		 String resStr   =  HttpsDataManager.sendData(url, reqJSON.toString());	
		 
		 JSONObject	resJSON   = new JSONObject(resStr);	
		 logger.info("MinappCode-release()-resJSON:"+resJSON.toString());
		 return resJSON;
	}
	
	//10、修改小程序线上代码的可见状态（仅供第三方代小程序调用）	
	public JSONObject  changeVisitstatus(String action){

		 String url  = "https://api.weixin.qq.com/wxa/change_visitstatus?access_token="+authorizer_access_token;
				 
		 JSONObject   reqJSON   = new JSONObject();
		 reqJSON.put("action", action);		 
		 String resStr   =  HttpsDataManager.sendData(url, reqJSON.toString());
		 
		 JSONObject resJSON   = new JSONObject(resStr);	
		 logger.info("MinappCode-changeVisitstatus()-resJSON:"+resJSON.toString());
		 return resJSON;
	}
	
	
    private  void download(String urlString, String filename,String savePath) throws Exception {  
        // 构造URL  
        URL url = new URL(urlString);  
        // 打开连接  
        URLConnection con = url.openConnection();  
        //设置请求超时为5s  
        con.setConnectTimeout(5*1000);  
        // 输入流  
        InputStream is = con.getInputStream();  
      
        // 1K的数据缓冲  
        byte[] bs = new byte[1024];  
        // 读取到的数据长度  
        int len;  
        // 输出的文件流  
       File sf=new File(savePath);  
       if(!sf.exists()){  
           sf.mkdirs();  
       }  
       OutputStream os = new FileOutputStream(sf.getPath()+"//"+filename);  
        // 开始读取  
        while ((len = is.read(bs)) != -1) {  
          os.write(bs, 0, len);  
        }  
        // 完毕，关闭所有链接  
        os.close();  
        is.close();  
    }
	
       
	/**
	 * main(这里用一句话描述这个方法的作用)
	 * (这里描述这个方法适用条件 – 可选)
	 * @param args 
	 *void
	 * @exception 
	 * @since  0.0.1
	 */
	public static void main(String[] args) throws Exception {
		
		String appid = "wx61018ce8a8ff6abf";//舒苑养生馆
//		MinappCode mc  = new  MinappCode(appid);
//		JSONObject    resJSONObject   = mc.uploadCode("44", genExtJSON(appid), "1.0.0", "身边的养生馆");
//		System.out.println("发布已通过审核的小程序（仅供第三方代小程序调用）:\n"+resJSONObject.toString());
		MinappCoreService  minapp  = new MinappCoreService(appid);
		minapp.authorizer_access_token  = "20_jQKqJ4WSA4HAJ09LBX4X8qnnVuQn_AarouB9WNNqsqLReVuQVCwX4YK2OLWClAgWlIBtAzzo9qw24H8YtF23KccpJx-MVdtQw4-CltI07khIS8CsG-c1dvfHB-mo-MIDh40MooU_KxqnzllrYPKjAGDGXS";
		JSONObject resJSON  = minapp.getCategory();
		System.out.print(resJSON.toString());
		
	}

}
