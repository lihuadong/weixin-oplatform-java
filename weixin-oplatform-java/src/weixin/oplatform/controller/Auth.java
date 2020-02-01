/**
	* 授权事件接收URL
	* 用于接收取消授权通知、授权成功通知、授权更新通知，也用于接收ticket
	* ticket是验证平台方的重要凭据
	* 服务方在获取component_access_token时需要提供最新推送的ticket以供验证身份合法性。
	* 此ticket作为验证服务方的重要凭据，请妥善保存。
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

import jingubang.base.weixin.open.service.AuthManager;


@WebServlet("/jingubang/Auth")
public class Auth extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public Auth() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	doPost(request,response);
    }
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//////////////////////////////////////////////////////////////////////////////////从URL中获取4个参数
		String  timestamp    = request.getParameter("timestamp");
		String  nonce   =  request.getParameter("nonce");
		String  msg_signature   = request.getParameter("msg_signature");
		String  encrypt_type   = request.getParameter("encrypt_type");// 默认是AES
		
       //////////////////////////////////////////////////////////////////////////////////这部分是通post的方式的消息体
		InputStream  is4Postdata   = request.getInputStream();
		StringBuffer   sb   =   new   StringBuffer();
		byte[]   b   =   new   byte[4096];
		for   (int n;   (n   =   is4Postdata.read(b))   !=   -1;) {
		        sb.append(new  String(b,   0,   n));
		}
		
		String postdata = sb.toString(); 		
		AuthManager  auth  = new AuthManager();
		auth.processData(postdata,timestamp,nonce,msg_signature,encrypt_type);

		response.setContentType("text/json");  
	    response.setCharacterEncoding("utf-8");  
		PrintWriter out =  response.getWriter();
		out.write("success");

	}
	
}
