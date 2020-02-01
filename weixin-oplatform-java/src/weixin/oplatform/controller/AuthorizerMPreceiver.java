/**
 * 处理公众号的消息
* 本URL用于接收已授权公众号的消息和事件，消息内容、消息格式、签名方式
* 加密方式与普通公众号接收的一致
* 唯一区别在于签名token和加密symmetric_key使用的是服务方申请时所填写的信息。
* 由于消息具体内容不会变更，故根据消息内容里的ToUserName，服务方是可以区分出具体消息所属的公众号。
 */

package weixin.oplatform.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jingubang.base.weixin.open.service.MPCoreService;



@WebServlet("/jingubang/Receiver")
public class AuthorizerMPreceiver extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthorizerMPreceiver() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			// TODO Auto-generated method stub
			String  timestamp    = request.getParameter("timestamp");
			String  nonce   =  request.getParameter("nonce");
			String  msg_signature   = request.getParameter("msg_signature");
			String  encrypt_type   = request.getParameter("encrypt_type");// 默认是AES
			
			InputStream  is4Postdata   = request.getInputStream();
			StringBuffer   sb   =   new   StringBuffer();
			byte[]   b   =   new   byte[4096];
			
			for   (int n;   (n   =   is4Postdata.read(b))   !=   -1;) {
			        sb.append(new  String(b,   0,   n));
			} 
			String postdata = sb.toString(); 
			
	
			MPCoreService processor  = new MPCoreService();
			String resXml= processor.processData(timestamp,nonce,msg_signature,encrypt_type,postdata);  
			
			response.setContentType("text/json");  
		    response.setCharacterEncoding("utf-8");  
			PrintWriter out =  response.getWriter();
			out.write(resXml);
			out.flush();
			out.close();	     
	}

}
