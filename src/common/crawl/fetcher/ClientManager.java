/**
 * @title:ClientManager.java
 * @Package netease.trading.crawl.fetcher
 * @Description: 使用单例模式、实现从数据库中读取有效的代理ip、并为各链接自动分配代理ip
 * @author netease-huangze
 * @date 2012-11-27 下午3:48:23
 * @version V1.0
 */
package common.crawl.fetcher;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.log4j.Logger;

import common.crawl.framework.GetThread;
import common.database.Dbop;



/**
 * @author netease-huangze
 *
 */
public class ClientManager {
	Logger logger = Logger.getLogger(ClientManager.class);
	private static ClientManager instance = null;
	/**
	 * 设置启动日期，每天进行重置
	 */
	private static String startDate;
	private static String curDate;
	/**
	 * 已经使用的代理ip数目
	 */
	private static int activeAccount = 0;
	/**
	 * 从数据库中取得有效ip存入ipList，元素为 ip:port:type(http/https)
	 */
	private static List<String> ipList;	
	private HttpClient httpClient;
	
	private ClientManager(){			
		// 从数据库中获取代理ip:port
		ipList = this.getProxy();		 	 		
		startDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());	
	}
	
	
   public static synchronized ClientManager getInstance(){
       //判断是否实例化
        if(instance==null){    	   
    		instance = new ClientManager();       
        }      
 	   // 对于连续运行多天的情况，每天重置代理（从数据库中获取）
 	   curDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
 	   if(curDate.compareTo(startDate)>0){
 		   System.out.println("........每天重置代理..........");
 		   startDate = curDate;
 		   activeAccount = 0;
 		   // -----------重新测试代理	-------------------
 		   ipList = instance.getProxy(); 
 	   }   	   
        return instance;
    }
 
  

   
   /**
    * 
     * @Description: 获取一个httpclient对象
     * @param isProxied 是否使用代理
     * @return HttpClient    
     *
    */
   
   	public synchronized HttpClient getClient( boolean isProxied){
   		
   		PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
		cm.setDefaultMaxPerRoute(20);//每个站点的并发数上限
		cm.setMaxTotal(100);
	
		this.httpClient = new DefaultHttpClient(cm);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 6*1000);
   			
//		((AbstractHttpClient) httpClient).setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(2, true));

		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/20100101 Firefox/17.0");//设置信息 
//		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Googlebot");//"Baiduspider");//设置信息 
			
		//--------------伪造的报头-------------------
//		List<Header> headers = new ArrayList<Header>();			
//		headers.add(new BasicHeader("HTTP_CLIENT_IP","211.141.33.56"));
//		headers.add(new BasicHeader("X-Forwarded-For","180.70.92.43"));
//		headers.add(new BasicHeader("X-Real-IP","123.126.50.185"));
//		httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headers);
		
		
//		HttpHost virtualHost = new HttpHost("211.154.83.37");
//		httpClient.getParams().setParameter(ClientPNames.VIRTUAL_HOST,virtualHost);
	
		//没有可用代理
		if(ipList.size() < 1){		
			isProxied = false;
		}
   		if(isProxied == true){	
   			//设置表头
   			httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/20100101 Firefox/17.0");
   			int id = (activeAccount + ipList.size()) % ipList.size();
//   			int id = Calendar.getInstance().get(Calendar.SECOND)%ipList.size();
   			++ activeAccount;					
   			String[] info = ClientManager.ipList.get(id).split(":");// ip:port:type
//   		HttpHost proxy = new HttpHost(info[0], Integer.valueOf(info[1]), info[2]);
   			//默认是HTTP协议,即便收集代理的时候标明的协议是HTTPS,使用的时候仍然用HTTP，否则会报错
   			HttpHost proxy = new HttpHost(info[0], Integer.valueOf(info[1]));
   			//设置重试次数
			((AbstractHttpClient) httpClient).setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(1, false));
   			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);  					  			
//   			System.out.println("第" + round + "轮---activeAccount: " + activeAccount);		
   			System.out.println(activeAccount + "---当前使用ip为" + info[0] + ":" + info[1]);
   		}//使用代理	
   		return this.httpClient;
   	}
   	
   	/**
   	 * 
   	  * @Description: 从数据库中读取响应时间范围的ip列表，没行存储为 ip:port:protocol
   	  * @author: netease-huangze
   	  * @return List<String>    
   	  *
   	 */
   	
	public List<String> getProxy() {
		List<String> resls = new ArrayList<String>();
		int validResponseTime = 25;// 不超过这个上限反应时间的认为是有效代理
		String proxyTableName = "proxy_ip";
		boolean hasEnoughIps = false;
		try {
			while (hasEnoughIps == false) {

				System.out.println("--------------选取的代理IP的响应时间上限："	+ validResponseTime + "-------------");

				Dbop db = new Dbop();	
				Connection con = db.getConnetion();
				String sql = " SELECT ip, port, protocol FROM " + proxyTableName  
						+ " WHERE test_response > -1 AND test_response < " + validResponseTime
						+ " ORDER BY test_response";
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet res = ps.executeQuery();
				while (res.next()) {
					int port = res.getInt("port");
					resls.add(res.getString("ip") + ":" + port + ":"
						+ res.getString("protocol"));
				}
				res.close();
				ps.close();
				db.closeConnection(con);

				// 如果找到的代理数量少于10个，增加响应时间的上限以获得更多的ip
				if (resls.size() < 10) {
					System.out.println("-------------代理ip数量太少，增加响应时间上限--------");
					validResponseTime += 2;
					if (validResponseTime > 5) {
						break;
					}
					resls.clear();// 清空结果列表，重新获取
				} else {
					hasEnoughIps = true;
				}
			}// while( hasEnoughIps==false )
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("...........读取代理数据库时出现错误...........");
			logger.error("...........读取代理数据库时出现错误...........");
			
		}
		// for(String str: resls)
		// System.out.println(str);
		System.out.println("有效代理ip数量:" + resls.size());
		return resls;
	}

	public static void main(String[] args) {
		
		test();
		

	}
	
	public static void test(){
		
		ClientManager clientManager = ClientManager.getInstance();
		HttpClient client = clientManager.getClient(true);	
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10*1000);
		for (int i = 1; i < 11; i++) {
			String url = "http://trading.5173.com/search/1900-" + i + ".shtml";
//			url = "http://www.yahoo.com/";
			HttpGet httpget = new HttpGet(url);
			HttpResponse response;
			try {
				response = client.execute(httpget);
				if(i<6)
					throw new Exception("hello world");
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					System.out.println(i + "--响应正常..");			
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println( i + "--------异常状态：切换ip--------------------");
				client.getConnectionManager().shutdown();	
				client = ClientManager.getInstance().getClient(true);					
				continue;			
			} 

			httpget.abort();		
			if(i>=6){
				System.out.println("--------正常状态：切换ip--------------------");
				client.getConnectionManager().shutdown();
				client = ClientManager.getInstance().getClient(true);
			}
		}

		client.getConnectionManager().shutdown();
		
	}

}
