/**
 * 
 * 包到位小程序SaaS
 * cn.a86.weixin4open.biz
 * Ver0.0.1
 * 2017年4月19日-上午12:23:14
 * 2017全智道(北京)科技有限公司-版权所有
 * 
 */

package weixin.oplatform.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import jingubang.base.weixin.open.service.AuthManager;


@WebServlet("/jingubang/AuthorizerAccessToken")
public class AuthorizerAccessToken extends HttpServlet {
	
	private static final long serialVersionUID = 1L;      
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String auth_code   = request.getParameter("auth_code");
		String auth_code_expires_in  =  request.getParameter("expires_in");
		

		AuthManager  am   = new AuthManager();
		JSONObject  authurizerAccessTokenInfo  = am.getAuthInfo(auth_code);
		String mp_type  =  authurizerAccessTokenInfo.getString("mp_type");
		
		String authorizer_appid= null;
		String authorizer_access_token= null;

		
		JSONObject  authorization_info = null;
		try {
			authorization_info =authurizerAccessTokenInfo.getJSONObject("authorization_info");
   		 	authorizer_appid   = authorization_info.getString("authorizer_appid");
   		 	authorizer_access_token   =  authorization_info.getString("authorizer_access_token");					   		 	
		}catch (JSONException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("mp_type",mp_type);
		request.setAttribute("authorizer_appid",authorizer_appid);
		request.setAttribute("authorizer_access_token",authorizer_access_token);
		request.setAttribute("authorization_info",authorization_info);
		request.setAttribute("auth_code",auth_code);
		request.setAttribute("auth_code_expires_in",auth_code_expires_in);
		

		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/weixin3rd/main.jsp");
		dispatcher.forward(request,response);
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request,response);
	}

}
