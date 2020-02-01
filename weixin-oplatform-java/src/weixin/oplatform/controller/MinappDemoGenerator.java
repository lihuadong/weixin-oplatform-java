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
import jingubang.base.weixin.open.service.minapp.MinappTester;
import jingubang.util.tools.DateTimeUtil;

/**
 * Servlet implementation class MinappDemoGenerator
 */
@WebServlet("/jingubang/minapp/MinappDemoGenerator")
public class MinappDemoGenerator extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MinappDemoGenerator() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String  wechatid  = request.getParameter("wechatid");
		String  openid  = request.getParameter("openid");
		
		//MPTptMsg  tptMsg  = new MPTptMsg();
		//openid ="o7DxpuMwD_wmh0Ef-VhV6OCP7mrA";
		//tptMsg.sendOrderTptMsg(openid, "体验用户", "goodsnum", "goodstotal", "goodsname", wechatid, "orderid");
		
//		ArrayList<String>  tasterList  = new ArrayList<String>();
//		if(tasterList.size()==20){
//			tasterList.remove(19);			
//		}
//		tasterList.add(wechatid);
		
		Properties prop = new Properties();  
		InputStream in = MinappDemoGenerator.class.getResourceAsStream("/minapp4tpt.properties"); 
		try {  
	            prop.load(in);  
	    }catch (IOException e) {  
	            e.printStackTrace();  
	    }
		
		String tpt_id = prop.getProperty("QRMenu");		//扫码点餐吃饭
		String appid  = prop.getProperty("appid"); 	

		//out.println("<p>1、代码管理(上传模板代码)</p>");
		MinappCoreService  code  = new  MinappCoreService(appid);
		code.uploadCode(tpt_id, genExtJSON(appid), "ver_1.0.0", "prd_version"); 
		
		//out.println("<p>2、绑定微信用户为小程序体验者</p>");
		MinappTester   tester  = new MinappTester(appid);
		JSONObject resultJSON = tester.bindMember(wechatid);
			
		//out.println("<p>3、获取体验小程序的体验二维码</p>");
		JSONObject tasteQRCodeJSON = code.getTasteQRCode(appid);
		
		try {  
			in = MinappDemoGenerator.class.getResourceAsStream("/config.properties"); 
			prop = new Properties(); 
            prop.load(in);  
		}catch (IOException e) {  
            e.printStackTrace();  
		}
		String qrFileFix  = prop.getProperty("QRFileFix");  
		
		resultJSON   = new JSONObject();
		resultJSON.put("resdata", tasteQRCodeJSON.toString());
		resultJSON.put("msg", "success");
		resultJSON.put("imgurl", qrFileFix+appid+".jpg");
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
		ext_obj.put("desc", "扫码点单");
		ext_obj.put("time", DateTimeUtil.getCurrentDate());
				
		ext_json_obj.put("ext", ext_obj);		
		return ext_json_obj;
		
	}

}
