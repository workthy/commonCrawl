/**
 * @title:Filter.java
 * @Package netease.trading.crawl.filter
 * @Description: TODO
 * @author netease-huangze
 * @date 2012-10-17 上午10:48:12
 * @version V1.0
 */
package common.crawl.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author netease-huangze
 *
 */
public class LinksFilter {
	
	private LinksScope linksScope;

	public List<String> filtLinks(List<String> urls) {		
		if (urls != null) {
			List<String> res = new ArrayList<String>();
			for (String url : urls) {			
				if (this.linksScope.isInScope(url))			
					res.add(url);
			}
			if (!res.isEmpty())
				return res;
		}
		return null;
	}
	/**
	 * 构造函数
	 * @param linksScope
	 */
	public LinksFilter(LinksScope linksScope){
		this.linksScope = linksScope;
		
	}

	public LinksScope getLinksScope() {
		return linksScope;
	}

	public void setLinksScope(LinksScope linksScope) {
		this.linksScope = linksScope;
	}
	
	
	

	
}
