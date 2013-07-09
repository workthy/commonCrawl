package common.crawl.framework;




import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.log4j.Logger;


//import org.apache.log4j.Logger;

import org.htmlparser.util.ParserException;



import common.crawl.extractor.LinkExtractor;
import common.crawl.fetcher.ClientManager;
import common.crawl.fetcher.PageFetch;
import common.crawl.fetcher.PageFetchByClient;
import common.crawl.filter.LinksFilter;
import common.io.Constant;

import common.io.IoUtils;


/**
 * @desc 爬取网页的线程
 * @author netease-huangze
 * @date 2012-10-09
 * 
 */
/**
 * @author netease-huangze
 *
 */
public class GetThread extends Thread {
	
	Logger logger = Logger.getLogger(GetThread.class);

	private HttpClient httpClient;
	private final CrawlContext crawlContext;//传递爬行信息的对象
//	private final HttpContext context;
//	private HttpGet httpget;
	private final int id;
	private ConcurrentHashMap<Integer, BlockingQueue<String>> queue;
	private final int queueNum;
	private boolean shouldrun;	
	//同步加锁
//	private Lock lock = new ReentrantLock();	
	/**
	 * 网页内容抓取方法
	 */
	private PageFetch pageFetch;
	private String webPageEncoding = Constant.Web_Encoding;
	/**
	 * 控制运行状态和监管代理
	 */
	private Random random = new Random();	
	private int delaySeconds = 4;		
	private boolean isProxied = false;//是否使用代理
	boolean isProxyChanged = false;//代理是否切换过
	int baseDelay = 10;
	int proxyChangeForFailCount = 0;//代理异常（数据异常）导致的代理更换次数
	int succBound = Constant.SUCC_UPBOUND_PER_PROXY;//成功抓取完这个此时之后、切换代理
	int failCrawlNum = 0;
	int succCrawlNum = 0;
	

	
	
	/**
	 * 对抓取失败的网页进行处理
	 */
	private ConcurrentHashMap<String , Integer> failedUrlMap;//存储榨取失败的<url地址，重抓次数>
	private final int RECRAWL_LIMIT_MAX = Constant.RECRAWL_LIMIT_MAX;//设置重新抓取的次数上限
	private int CONTENT_LENTH_LOW_BOUND;//网页内容长度下限，如果内容长度小于这个值，网页需要重新抓取	
	/**
	 * 链接过滤
	 */
	private LinksFilter linksFilter;//根据日期、或者昨天之前的历史数据进行过滤
	private final ConcurrentHashMap<String, Boolean> linkUrlsMap;//对一次爬行中重复的url进行过滤，只存储不重复的，（梦幻里边有很多重复数据）		
	/**
	 * 链接抽取
	 */
	private LinkExtractor linkExtractor;//null表示不抽取、否则进行链接抽取

	private Map<String,String> dataMap;//论坛信息
	//--------------保存网页 -----------------
	private String outDir = Constant.STORAGE_DIR;
	
	@SuppressWarnings("unchecked")
	public GetThread(HttpClient httpClient,
			ConcurrentHashMap<Integer, BlockingQueue<String>> queue, 
			int id, 
			CrawlContext crawlContext) {
		this.httpClient = httpClient;
		this.queue = queue;
		this.id = id;
		this.crawlContext = crawlContext;//存储游戏相关信息
						
		// -------------以下是一些初始化操作------------------------------------------------
		this.shouldrun = true;		
		pageFetch = new PageFetchByClient(this.httpClient);	
		this.linkExtractor = crawlContext.getLinkExtractor();
		this.linksFilter = crawlContext.getLinksFilter();
		this.webPageEncoding = crawlContext.getWebPageEncoding();
		this.delaySeconds = crawlContext.getDelaySeconds();
		this.outDir = crawlContext.getStorageDir();
		this.isProxied = crawlContext.isProxied();
		
		
		
		
		this.CONTENT_LENTH_LOW_BOUND = 1800;
//		this.context = new BasicHttpContext();			
		this.queueNum = queue.size();
		this.succBound = Constant.SUCC_UPBOUND_PER_PROXY/this.queueNum;

		//------------存储抓取失败的URL--------------------------------------
		this.failedUrlMap = new ConcurrentHashMap<String, Integer>();		
		this.linkUrlsMap = crawlContext.getLinkUrlsMap();
		
		dataMap = (Map<String,String>)crawlContext.getDataObj();
		
	
	}
	
	public void forceStop() {
		this.shouldrun = false;
	}

	@Override
	public void run(){ 	
		
		// /整个队列为空，当前线程全部结束，终止
		while (shouldrun) {
			System.out.println("队列" + this.id + "的size: ---"+ this.queue.get(this.id).size());
			//从队列中取出一项
			String url = this.queue.get(this.id).poll();				
			boolean isSucc = true;//是否抓取成功
			try {
				
				//队列为空	
				if (url == null) {
					TimeUnit.SECONDS.sleep(15);
				}								
				else {				
					String content = null;
				
					//抓取网页，获得网页内容，如果抓取失败，则重新抓取；但重新抓取的次数不能超过抓取上限
					try{						
						content = String.valueOf(pageFetch.fetchStatic(url));//,this.webPageEncoding));												
						if(content==null || content.length() < this.CONTENT_LENTH_LOW_BOUND){
							throw new RuntimeException("数据为空或过短");				
						}								
																					
					}catch (Exception e){//爬行错误					
						e.printStackTrace();
						//-循环重定向引起的错误CircularRedirectException，这种情况下通常已经成功抓取过一次
						if( e instanceof ClientProtocolException){
							System.out.println("url: " + url + " 循环重定向错误");								
							continue;
						}
						//如果代理服务器出问题、通常会遇到这个错(为了迅速切换代理、把错误次数调大)
						else if( e instanceof HttpHostConnectException || e instanceof SocketException){
							this.failCrawlNum += 10;
						}		
						
						isSucc = false;
						this.handleFail(url);																
					}	
					
					if(isSucc == true){
						this.proccessWhenSucc(url, content);
						
						//将新抽取的url添加到工作队列
						if(this.linkExtractor!=null){//不为空、即需要进行链接抽取	
							this.extractLinksToQueue(url, content);	
						}
					}
					if(this.isProxied==true){//如果使用了代理
						this.handleUrlWhenProxied(url, isSucc);
					}
																														
					//---------------------------- 延时抓取 -----------------------------
					int sleepSec = this.baseDelay + random.nextInt(this.delaySeconds);
//					System.out.println(" 队列"+ id + (isSucc?"成功":"失败")  + " 抓取" + url +"休眠"+ sleepSec+"秒");
					logger.info(" 队列"+ id + (isSucc?"成功":"失败")  + " 抓取" + url +"休眠"+ sleepSec+"秒");
					TimeUnit.SECONDS.sleep(sleepSec);
				}		
			} catch (Exception e) {//解析异常或者写入文件异常
				e.printStackTrace();
//				System.out.println(id +"   url -"+ url + " - error: " + e);
			}
		}// while (shouldRun)	
		//结束线程的时候，关闭数据源，并把缓冲区的数据写入数据库
	
		if(this.isProxyChanged==true){//对于更换过的代理需要各个线程自己关闭
			this.httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * 然后添加到相应的工作队列（使用hashcode，进行散列）
	 * 注意： 对url进行散列，有可能会出现各工作队列数据分布不均匀的情况 （留待后期改进）
	 * @return
	 * @throws ParserException 
	 */
	public void extractLinksToQueue(String url, String content) throws ParserException {
	
		//从列表页提取链接
		List<String> urlList = this.linkExtractor.extractLinks(content);	
		if(urlList == null || urlList.isEmpty()){//抽取页面数为0
			return;
		}
			
		//url去重
		List<String> tmpList = new ArrayList<String>();
		for(String aUrl: urlList){
//			System.out.println("抽取获得" + aUrl);
			//在当天的list中已经重复
			synchronized(this.linkUrlsMap){
				if(this.linkUrlsMap.containsKey(aUrl)==true){
					System.out.println("重复url：" + aUrl);
					continue;
				}
				else{
					this.linkUrlsMap.put(aUrl, true);
				}
			}		
			//加入不重复的数据
			tmpList.add(aUrl);										
		}
		urlList = tmpList;
		System.out.println("抽取当天list中不含重复链接的link数：" + urlList.size());
			
		//url过滤
		if(this.linksFilter!=null){//不为null，表示要进行抽取					
			urlList = this.linksFilter.filtLinks(urlList);		
			if(urlList == null)
				return;
			System.out.println("经过filter之后的链接数：" + urlList.size());
		}
		//url分配到各个队列
		int validUrlSize = urlList.size();	
		int base = new Random().nextInt(this.queueNum);
		for(int i = 0; i < validUrlSize; ++i){				
			String detailUrl = urlList.get(i);
			//计算url应该添加到哪个工作队列					
			int queueId = (i + base) % this.queueNum;				
//			System.out.println("添加一次到队列:" + queueId + "   url:"+detailUrl);
			queue.get(queueId).add(detailUrl);	
//			queue.get(queueId).offer(detailUrl);
		}
			
	}//extractLinksToQueue	

	
//	public boolean handleFail(String url, Exception e ){
//		//-循环重定向引起的错误CircularRedirectException，这种情况下通常已经成功抓取过一次
//		if( e instanceof ClientProtocolException){//不属于抓取错误
//			System.out.println("url: " + url + " 循环重定向错误");								
//			return false;
//		}
//
//		//如果代理服务器出问题、通常会遇到这个错(为了迅速切换代理、把错误次数调大)
//		else if( e instanceof HttpHostConnectException || e instanceof SocketException){
//			this.failCrawlNum += 10;
//		}	
////		}			
//		return handleFail(url);
//	}
	
	public boolean handleFail(String url){
		boolean isContinueCrawl = false;
		//--------------------统计一个url的失败次数-----------------------					
//		synchronized(this.failedUrlMap){		
			int preTimes = this.failedUrlMap.containsKey(url)?this.failedUrlMap.get(url):0;		
			//小于抓取上限，才进行重新抓取，否则继续执行，这样可以保存一份缺失的数据（也有可能是长度下限设置不准确的问题）
			if(preTimes <= this.RECRAWL_LIMIT_MAX){
				//已抓取次数加1		
				this.failedUrlMap.put(url, preTimes + 1);
				//重新抓取						
				queue.get(this.id).add(url);
//				System.out.println("失败次数：" + (preTimes+1) + " 重新添加到队列" + this.id + " url:" + url);
				logger.warn("失败次数：" + (preTimes+1) + " 重新添加到队列" + this.id + " url:" + url);
				isContinueCrawl = true;
			}
//		}			
		return isContinueCrawl;
	}
	
	public void proccessWhenSucc(String url, String content){
		
		//保存网页					
		IoUtils.sink(content, this.outDir + "/"+ IoUtils.genFileName(url));

	}
	
	public void handleUrlWhenProxied(String url, boolean isSucc){
//		if(this.isProxied==false){
//			return ;
//		}
			
		if(isSucc == true){//页面抓取成功
			this.succCrawlNum ++;
			if(this.succCrawlNum >= this.succBound){//初始的SUCC_UPBOUND=80；因为所有线程共享一个client，相当于这个client成功抓取完80*线程数之后再进行切换     
//				System.out.println("**************队列"+ id+ "成功抓取完" + this.succCrawlNum + "次:切换代理*****");
				logger.info("**************队列"+ id+ "成功抓取完" + this.succCrawlNum + "次:切换代理*****");
				changeProxy(true);
				this.succCrawlNum = 0;			
			}		
		}
		else{//页面抓取失败
			this.failCrawlNum++;
			if(this.failCrawlNum >= Constant.FAIL_UPBOUND_PER_PROXY){//失败3次则更换代理
				System.out.println( id + "--------------------多次失败,更换代理--------------------");
				this.failCrawlNum = 0;//重新清0
				this.proxyChangeForFailCount ++;
				boolean isToProxied = true;
				if(this.proxyChangeForFailCount > 20){//因失败引起的代理更换超过20次，停止使用代理
					isToProxied = false;//
//					System.out.println( id + "--------------终止代理，直接使用本地ip进行抓取------");
					logger.info(id + "--------------终止代理，直接使用本地ip进行抓取------");
					this.proxyChangeForFailCount = 0;//重新清0
				}    
			    this.changeProxy(isToProxied);
			}		 	
		}		
	}
	
	public void changeProxy(boolean isToProxied){
		
	   /* 不要关闭初次分配的代理ip，初始的代理被所有线程共享、且会在crawlController中关闭；
       * 如果是新分配的代理，才由各个线程自己负责关闭，原先的代理
       */	
	    if(this.isProxyChanged == true){
        	this.httpClient.getConnectionManager().shutdown();
        }

        this.httpClient = ClientManager.getInstance().getClient(isToProxied);       
        this.pageFetch = new PageFetchByClient(this.httpClient);
       
        this.isProxyChanged = true;     
        this.succBound = Constant.SUCC_UPBOUND_PER_PROXY;//切换过代理之后，每个线程是单独的client,成功抓取页面数量调整为600
		
	}
}
