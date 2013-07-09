package common.crawl.framework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

//import org.apache.log4j.Logger;


import common.crawl.extractor.impl.LinkExtractBaidu;

import common.io.Constant;


public class CrawlSchedule {
	
	public void scheduleMore() throws Exception{
		for(int i=0; i < 10 ; ++i){
			scheduleOne();
		}
	}
	

	public void scheduleOne()throws Exception{	//一个二级论坛下面各个板块串行执行
			
	
		
		List<Map<String,String>> zoneUrlList = new ArrayList<Map<String,String>>();
		List<String> rows = new ArrayList<String>();//DbQuery.getQueryResult(sql);
		for(String row: rows){
			String[] data = row.split(Constant.Line_SEG_TAG);
			Map<String,String> map = new HashMap<String,String>();
			String typeUrl = data[3];
			map.put("gameName", data[0]);
			map.put("forumId", data[1]);
			map.put("typeId", data[2]);
			map.put("typeUrl", typeUrl);
			zoneUrlList.add(map);
		}
		
		//--------------------------- 执行爬虫线程 -------------------			
		ExecutorService exec = Executors.newSingleThreadExecutor();
//		ExecutorService exec = Executors.newFixedThreadPool(2);
		
	
		for(Map<String,String> zoneMap: zoneUrlList){

			System.out.println(zoneMap);
			CrawlContext crawlContext = new CrawlContext();
			

			List<String> seeds = new ArrayList<String>();
			seeds.add(zoneMap.get("typeUrl"));

			crawlContext.setSeeds(seeds);		
//			for(String url: seeds){
//				System.out.println(url);
//			}
			crawlContext.setDataObj(zoneMap);
			crawlContext.setQueueNum(2);
//			crawlContext.setStorageDir(Constant.STORAGE_DIR);
			crawlContext.setWebPageEncoding("utf-8");
			crawlContext.setLinkExtractor(new LinkExtractBaidu());
			
			exec.execute(new GameThread(crawlContext));	
			
		}		
		exec.shutdown();
	
	}
	

	public static void main(String[] args) throws Exception {
		CrawlSchedule cs = new CrawlSchedule();
//		cs.schedule("qqgame",10553);	

			
	}

	private class GameThread extends Thread {
		CrawlContext crawlContext;
		public GameThread(CrawlContext crawlContext) {
			this.crawlContext = crawlContext;
		}

		@Override
		public void run(){				
			//开始爬行
			CrawlController cc = new CrawlController(crawlContext);
			cc.runCrawl();					
		}

	}

}
