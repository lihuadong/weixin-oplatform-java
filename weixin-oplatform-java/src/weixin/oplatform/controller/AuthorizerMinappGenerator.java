/**
 * 处理小程序业务的URL
 */

package weixin.oplatform.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import jingubang.base.weixin.open.service.MinappCoreService;
import jingubang.base.weixin.open.service.minapp.MinappServer;
import jingubang.base.weixin.open.service.minapp.MinappTester;



@WebServlet("/jingubang/minapp/MinappGenerator")
public class AuthorizerMinappGenerator extends HttpServlet {
	private static final long serialVersionUID = 1L;	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthorizerMinappGenerator() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String  wechatid  =request.getParameter("wechatid");
        String  minapp =request.getParameter("minapp");
        String  authorizer_appid   =  request.getParameter("authorizer_appid");
 
		JSONObject resultJSON;
		
		//out.println("<p>1、修改服务器地址</p>");
		MinappServer  server   = new MinappServer(authorizer_appid);
		resultJSON   = server.addServerAddress();
				
		//out.println("<p>2、代码管理(上传模板代码)</p>");
	    MinappCoreService  code  = new  MinappCoreService(authorizer_appid);


		Properties prop = new Properties();  
		InputStream in = AuthorizerMinappGenerator.class.getResourceAsStream("/minapp4tpt.properties"); 
		try {  
	            prop.load(in);  
	    }catch (IOException e) {  
	            e.printStackTrace();  
	    }
		
		String tpt_id = prop.getProperty(minapp);  		
		resultJSON = code.uploadCode(tpt_id, genExtJSON(authorizer_appid), "ver_1.0.0", "prd_version");       
		//logger.info("上传模板代码结果:"+resultJSON.toString());
		
		//out.println("<p>4、绑定微信用户为小程序体验者</p>");
		MinappTester   tester  = new MinappTester(authorizer_appid);
		resultJSON = tester.bindMember(wechatid);
		//logger.info("绑定微信用户为小程序体验者结果:"+resultJSON.toString());
		
		
		//out.println("<p>4、获取体验小程序的体验二维码</p>"); 
		JSONObject tasteQRCodeJSON = code.getTasteQRCode(authorizer_appid);
		//logger.info("获取体验小程序的体验二维码"+tasteQRCodeJSON.toString());
		
		try {  
			in = AuthorizerMinappGenerator.class.getResourceAsStream("/config.properties"); 
			prop = new Properties(); 
            prop.load(in);  
		}catch (IOException e) {  
            e.printStackTrace();  
		}
		String qrFileFix  = prop.getProperty("QRFileFix");  
		
		resultJSON   = new JSONObject();
		resultJSON.put("msg", "success");
		resultJSON.put("imgurl", qrFileFix+authorizer_appid+".jpg");
		
        response.setContentType("text/json");  
	    response.setCharacterEncoding("utf-8");  
		PrintWriter out =  response.getWriter();
		out.write(resultJSON.toString());		
	}
	
	
	private JSONObject  genExtJSON(String authorizer_appid){
		
		JSONObject  ext_json_obj  = new JSONObject();
		ext_json_obj.put("extEnable",true);
		ext_json_obj.put("extAppid",authorizer_appid);
				
		JSONObject  ext_obj  = new JSONObject();
		ext_obj.put("appid", authorizer_appid);
		ext_obj.put("desc", "扫码吃饭");
		ext_obj.put("time", "2017-07-08");
				
		ext_json_obj.put("ext", ext_obj);		
		return ext_json_obj;
		
	}
	
}



//{
//	  "extEnable": true,
//	  "extAppid": "wxf9c4501a76931b33",
//	  "ext": {
//	    "name": "wechat",
//	    "attr": {
//	      "host": "open.weixin.qq.com",
//	      "users": [
//	        "user_1",
//	        "user_2"
//	      ]
//	    }
//	  },
//	  "extPages": {
//	    "pages/logs/logs": {
//	      "navigationBarTitleText": "logs"
//	    }
//	  },
//	  "window":{
//	    "backgroundTextStyle":"light",
//	    "navigationBarBackgroundColor": "#fff",
//	    "navigationBarTitleText": "Demo",
//	    "navigationBarTextStyle":"black"
//	  },
//	  "tabBar": {
//	    "list": [{
//	      "pagePath": "pages/index/index",
//	      "text": "首页"
//	    }, {
//	      "pagePath": "pages/logs/logs",
//	      "text": "日志"
//	    }]
//	  },
//	  "networkTimeout": {
//	    "request": 10000,
//	    "downloadFile": 10000
//	  }
//	}
