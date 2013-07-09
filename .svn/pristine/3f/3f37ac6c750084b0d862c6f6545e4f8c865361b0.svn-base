/**
 * @title:Parser.java
 * @Package netease.trading.crawl.parser
 * @Description: 用于页面解析的主要接口
 * @author netease-huangze
 * @date 2012-10-17 上午10:10:59
 * @version V1.0
 */
package common.crawl.extractor;

import java.util.List;

import org.htmlparser.util.ParserException;


public interface LinkExtractor {
	
	
	/**
	 * 
	  * 从网页中抽取需要的链接
	  * 列表页返回一个list对象（可能是size=0的对象）
	  * 非列表页返回null
	  * @date: 2012-10-17上午11:15:40
	  * @author: netease-huangze
	  * @param source 网页内容等
	  * @return List<String>   以列表的形式返回获取的链接 
	  * @throws
	 */
	List<String> extractLinks( String content) throws ParserException;

	
	
	
	

}
