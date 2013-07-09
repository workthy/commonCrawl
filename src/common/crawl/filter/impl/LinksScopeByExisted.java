/**
 * @title:LinksGameScope.java
 * @Package netease.trading.crawl.filter
 * @Description: 
 * @author netease-huangze
 * @date 2012-11-8 上午10:18:38
 * @version V1.0
 */
package common.crawl.filter.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import common.crawl.filter.LinksScope;
import common.database.Dbop;



/**
 * @author netease-huangze
 *
 */
/**
 * @author netease-huangze
 *
 */
public class LinksScopeByExisted implements LinksScope {


		
	private ConcurrentHashMap<String, Boolean> urlsCrawledMap; 
	
	/**
	 * 选择数据库中近interval天内的历史urls
	 */
	private int interval = 15;
	
	
	public LinksScopeByExisted(){
		


		this.buildUrlsCrawledMap();
		
		
	}

	@Override
	public boolean isInScope(String url) {
				
		return isExisted(url)?false:true;//如果已经在数据库，就不在抓取范围内，否则在抓取范围内				
	}
	
	public boolean isExisted(String url){
		if(this.urlsCrawledMap == null)
			return false;
			
		return this.urlsCrawledMap.containsKey(url);
	}
	
	
	
	public ConcurrentHashMap<String, Boolean> getUrlsFromDB( int intervalDays) throws SQLException {
		

		return null;

	}



	public void setInterval(int interval) {
		this.interval = interval;
	}

	public void buildUrlsCrawledMap() {
		
			try {
				this.urlsCrawledMap = this.getUrlsFromDB(interval);
			} catch (SQLException e) {
				
				System.out.println("从数据库读取历史网页出现错误...");
				
				this.urlsCrawledMap = null;
				
				e.printStackTrace();
			}
	
	
	}
	
	public static void main(String[] args) {
		

	}
	

}
