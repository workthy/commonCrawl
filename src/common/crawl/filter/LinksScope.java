/**
 * @title:Filter.java
 * @Package netease.trading.crawl.filter
 * @Description: 链接过滤的接口
 * @author netease-huangze
 * @date 2012-10-17 上午10:41:47
 * @version V1.0
 */
package common.crawl.filter;

/**
 * 
 *
 */
public interface LinksScope {
	
	/**
	 * 
	  * 对link url进行过滤，判定该url是否需要保留
	  * @date: 2012-10-17上午10:44:24
	  * @author: netease-huangze
	  * @Description: 
	  * @param url 待过滤的url,判断url是否需要保留
	  * @return boolean    
	  * @throws
	 */
	boolean isInScope(String url);

}
