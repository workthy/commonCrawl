/**
 * @title:GetPageContent.java
 * @Package netease.trading.crawl.fetcher
 * @Description: TODO
 * @author netease-huangze
 * @date 2012-10-15 下午4:58:25
 * @version V1.0
 */
package common.crawl.fetcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;


public class PageFetchByClient implements PageFetch{
	

			
	private HttpClient httpclient;
	private final HttpContext context;
	public  PageFetchByClient(HttpClient httpClient){
		httpclient = httpClient;
		this.context = new BasicHttpContext();
		
	}
	
	/**
	 * 
	  * @date: 2012-10-16上午10:18:55
	  * @author: netease-huangze
	  * @Title: getPage
	  * @Description: 
	  * @param @param url
	  * @return String  url的页面源代码  
	  * @throws
	 */
	public String getPageByClient(String url) throws Exception{
//		if(url.endsWith("\r")){
//			System.out.println(url + " ends with /r");
//		}
		HttpGet httpget = new HttpGet(url);
		
//		System.out.println(url + "--- get executed!");
		
		String content = null;
	

			HttpResponse response = httpclient.execute(httpget,context);
			
//			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			
			if(entity==null)
				return null;
			
			
			content = EntityUtils.toString(entity);
			/**
			 *设置编码格式
			 */
//			content = EntityUtils.toString(entity,"gb2312");

//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					entity.getContent(), "gb2312"));
//	
//			StringBuilder sb = new StringBuilder();
//			String line = null;
//	
//			while ((line = reader.readLine()) != null) {
//				sb.append(line);
//				sb.append("\n");
//			}
//			content = sb.toString();
	
		//释放请求
		httpget.abort();
		return content;
	}

	
	public String getPageByClient(String url, String charset) throws Exception{

		HttpGet httpget = new HttpGet(url.trim());
		
//		System.out.println(url + "--- get executed!");
		
		String content = null;
	
		HttpResponse response = httpclient.execute(httpget,context);
			
//		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
			
		if(entity==null)
			return null;
			
			/**
			 *设置编码格式
			 */
		content = EntityUtils.toString(entity,charset);

		//释放请求
		httpget.abort();
		return content;
	}


	@Override
	public Object fetchStatic(String url) throws Exception {
		
		return getPageByClient(url);
	}

	@Override
	public Object fetchDynamic(String url) throws Exception {
	
		return null;
	}

	@Override
	public Object fetchStatic(String url, String encoding) throws Exception {
	
		return getPageByClient(url, encoding);
	}




	
//	/**
//	 * 
//	  * 
//	  * @date: 2012-10-16上午10:18:59
//	  * @author: netease-huangze
//	  * @Title: closeClient
//	  * @Description: 
//	  * @param   
//	  * @return void    
//	  * @throws
//	 */
//	public static void closeClient(){
//		
//		httpclient.getConnectionManager().shutdown();
//	}



	
	
	

}
