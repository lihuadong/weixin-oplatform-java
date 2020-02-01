/**
 * 包到位小程序SaaS
 * cn.a86.weixin4open.biz
 * ComponentAccessTokenManager.java
 * Ver0.0.1
 * 2017年4月19日-下午4:30:15
 * 2017全智道(北京)科技有限公司-版权所有
 * 
 */
package weixin.oplatform;

import java.sql.SQLException;
import java.util.TimerTask;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import jingubang.th3rd.service.MemcachedManager;
import jingubang.util.db.C3p0Utils;
import jingubang.util.tools.DateTimeUtil;


/**
 * 
 * ComponentAccessTokenManager
 * 
 * 李华栋
 * 2017年4月19日 下午4:30:15
 * 
 * @version 0.0.1
 * 
 */
public class ComponentAccessTokenTimer extends TimerTask{

	 String  component_verify_ticket;
	 
	 private static Logger logger = Logger.getLogger(ComponentAccessTokenTimer.class);   

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {

		QueryRunner runner = new QueryRunner(C3p0Utils.getDataSource());
		String sql = "select component_verify_ticket from wx_thir3d ORDER by idthir3d DESC";

		try {
			component_verify_ticket = runner.query(sql, new ScalarHandler<String>());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		AuthManager am = new AuthManager();
		String component_access_token = am.getComponentAccessToken(component_verify_ticket);

		if (component_access_token.length() > 10) {
			MemcachedManager mc = MemcachedManager.getMemcacheManager();
			mc.setKeyValue("component_access_token", component_access_token);
			
			logger.info("ComponentAccessTokenTimer-run\n");
			logger.info("ComponentAccessToken insert to memcached:" + DateTimeUtil.getCurrentTime()+"\n");
			logger.info("ComponentAccessToken:"+component_access_token);
		} else {
			logger.info("component_access_token get not it " + DateTimeUtil.getCurrentTime());
		}

	}

	
}
