/**
 * 包到位小程序SaaS
 * cn.a86.weixin4open.biz
 * MinappQR.java
 * Ver0.0.1
 * 2017年7月18日-下午5:15:42
 *  2017全智道(北京)科技有限公司-版权所有
 * 
 */
package weixin.oplatform.miniprogram;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import jingubang.base.weixin.open.service.AuthManager;
import jingubang.ebiz.serive.OrderManager;
import jingubang.th3rd.service.MemcachedManager;
import jingubang.util.http.HttpsDataManager;

/**
 * 
 * MinappQR
 * 
 * 李华栋
 * 李华栋
 * 2017年7月18日 下午5:15:42
 * 
 * @version 0.0.1
 * 
 */
public class MinappQR {
	
	private String authorizer_access_token;
	private static Logger logger = Logger.getLogger(OrderManager.class);
	
	public MinappQR(String authorizer_appid){
		
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
	
	//生成二维码
	public String  CreateCode(String path,int width){
		
		 JSONObject   resJSON;
		 String url  = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode?access_token="+authorizer_access_token;
		 JSONObject   reqJSON   = new JSONObject(); 
		 reqJSON.put("path", path);
		 reqJSON.put("width", width);

		 String resStr   =  HttpsDataManager.sendData(url, reqJSON.toString());

		 byte[] imgByte2 = hex22byte(resStr);

		 try {
			download(resStr.getBytes(), "1234.jpg","/data/data4img/a86/minapp_test_qr");

			download(imgByte2, "123456.jpg","/data/data4img/a86/minapp_test_qr");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		 resJSON   = new JSONObject(resStr);	
		 resJSON   = new JSONObject();
		resJSON.put("data", resStr);
		return resJSON.toString();
	}
	

	
	  public  void download(byte[] imgByte, String filename,String savePath) throws Exception {  
	        // 构造URL  
//	        URL url = new URL(urlString);  
//	        // 打开连接  
//	        URLConnection con = url.openConnection();  
//	        //设置请求超时为5s  
//	        con.setConnectTimeout(5*1000);  
	        // 输入流  
//		  InputStream is = con.getInputStream();  
		   InputStream is = new ByteArrayInputStream(imgByte); 

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
	  * 字符串转二进制
	  * @param str 要转换的字符串
	  * @return 转换后的二进制数组
	  */
	  public static byte[] hex2byte(String str) { // 字符串转二进制
	  if (str == null)
	  return null;
	  str = str.trim();
	  int len = str.length();
	  if (len == 0 || len % 2 == 1)
	  return null;
	  byte[] b = new byte[len / 2];
	  try {
	  for (int i = 0; i < str.length(); i += 2) {
	  b[i / 2] = (byte) Integer
	  .decode("0X" + str.substring(i, i + 2)).intValue();
	  }
	  return b;
	  } catch (Exception e) {
	  return null;
	  }
	  }
	  
	  /**   
	   * 反格式化byte   
	   *    
	   * @param s   
	   * @return   
	   */    
	  public static byte[] hex22byte(String s) {     
	      byte[] src = s.toLowerCase().getBytes();     
	      byte[] ret = new byte[src.length / 2];     
	      for (int i = 0; i < src.length; i += 2) {     
	          byte hi = src[i];     
	          byte low = src[i + 1];     
	          hi = (byte) ((hi >= 'a' && hi <= 'f') ? 0x0a + (hi - 'a')     
	                  : hi - '0');     
	          low = (byte) ((low >= 'a' && low <= 'f') ? 0x0a + (low - 'a')     
	                  : low - '0');     
	          ret[i / 2] = (byte) (hi << 4 | low);     
	      }     
	      return ret;     
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
		MinappQR mc  = new  MinappQR("wxb8e766245551ed99");
		//JSONObject ResJSON = mc.CreateCode("pages/pay/pay", 100);
		//System.out.println(ResJSON.toString());
	}

}
