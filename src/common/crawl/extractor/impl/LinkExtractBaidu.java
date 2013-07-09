package common.crawl.extractor.impl;

import java.util.List;
import java.util.ArrayList;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import common.crawl.extractor.LinkExtractor;



/**
 * @Description: 从具体业务页面抽取其他层次的URL
 * @author netease-huangze
 *
 */
public class LinkExtractBaidu implements LinkExtractor {



	public static void main(String[] args) throws ParserException {
		String url = "http://news.baidu.com/ns?cl=2&rn=20&tn=news&word=%E8%8B%B1%E9%9B%84%E8%81%94%E7%9B%9F&ie=utf-8";
		LinkExtractBaidu leb = new LinkExtractBaidu();
		leb.extractLinks(url);
	}

	@Override
	public List<String> extractLinks(String source) throws ParserException {
		List<String> res = new ArrayList<String>();
		
//		System.out.println(source);
		Parser parser = new Parser(source);
		NodeFilter nf = new HasAttributeFilter("class","page");	
		NodeList nl = parser.extractAllNodesThatMatch(nf).elementAt(0).getChildren();
		for(int i=0; i<nl.size(); ++i){
			Node node = nl.elementAt(i);
			if(node instanceof LinkTag){
//				System.out.println(((LinkTag)node).extractLink());
//				res.add(((LinkTag)node).extractLink());	
//				System.out.println("http://news.baidu.com"+((LinkTag)node).extractLink());
				res.add("http://news.baidu.com"+((LinkTag)node).extractLink());
			}
		}
		
		return res;
	
	}

	
	
}
