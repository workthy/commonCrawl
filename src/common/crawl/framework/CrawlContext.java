/**
 * @title:CrawlContext.java
 * @Package netease.trading.crawl.fetcher
 * @Description: TODO
 * @author netease-huangze
 * @date 2012-10-24 下午2:36:43
 * @version V1.0
 */
package common.crawl.framework;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import common.crawl.extractor.LinkExtractor;
import common.crawl.filter.LinksFilter;
import common.io.Constant;



/**
 * @author netease-huangze
 *
 */
public class CrawlContext {
	
	private int queueNum = 5;
	
	/**
	 * 抓取完一个网页之后的休眠时间：抓取频度的控制
	 */
	private int delaySeconds = 8;
	
	/**
	 * 是否采用代理进行抓取
	 */
	private boolean isProxied = true;
	
	/**
	 * 是否对链接进行过滤, 如果linksFilter不为null，则进行过滤
	 */
	private LinksFilter linksFilter = null;
	
	//默认不对列表页进行抽取
	private LinkExtractor linkExtractor = null;
	
	
	/**
	 * 种子网页
	 */
	private List<String> seeds;
	
	/**
	 * 传递信息的对象
	 */
	private Object dataObj;
	
	/**
	 * 抓取的数据的输出路径
	 */
	private String storageDir = Constant.STORAGE_DIR;
	
	/**
	 * 网页编码
	 */
	private String webPageEncoding = Constant.Web_Encoding;
	
	/* 
	 *  getter() and setter()方法
	 */
	
	
	/**
	 * 存储从列表页面抽取出来的url,有些会重复，用这个map进行去重
	 */
	ConcurrentHashMap<String, Boolean> linkUrlsMap = new ConcurrentHashMap<String, Boolean>();
	
	
	
	public List<String> getSeeds() {
		return seeds;
	}

	public void setSeeds(List<String> seeds) {
		this.seeds = seeds;
	}

	public Object getDataObj() {
		return dataObj;
	}

	public void setDataObj(Object dataObj) {
		this.dataObj = dataObj;
	}

	public String getStorageDir() {
		return storageDir;
	}

	public void setStorageDir(String storageDir) {
		this.storageDir = storageDir;
	}
	
	public ConcurrentHashMap<String, Boolean> getLinkUrlsMap(){
		
		return this.linkUrlsMap;
	}

	public void setQueueNum(int queueNum) {
		this.queueNum = queueNum;
	}

	public void setDelaySeconds(int delaySeconds) {
		this.delaySeconds = delaySeconds;
	}

	public void setProxied(boolean isProxied) {
		this.isProxied = isProxied;
	}

	public void setLinksFilter(LinksFilter linksFilter) {
		this.linksFilter = linksFilter;
	}

	public void setLinkExtractor(LinkExtractor linkExtractor) {
		this.linkExtractor = linkExtractor;
	}

	public void setLinkUrlsMap(ConcurrentHashMap<String, Boolean> linkUrlsMap) {
		this.linkUrlsMap = linkUrlsMap;
	}

	public int getQueueNum() {
		return queueNum;
	}

	public int getDelaySeconds() {
		return delaySeconds;
	}

	public boolean isProxied() {
		return isProxied;
	}

	public LinksFilter getLinksFilter() {
		return linksFilter;
	}

	public LinkExtractor getLinkExtractor() {
		return linkExtractor;
	}

	public String getWebPageEncoding() {
		return webPageEncoding;
	}

	public void setWebPageEncoding(String webPageEncoding) {
		this.webPageEncoding = webPageEncoding;
	}
	
	
	

}
