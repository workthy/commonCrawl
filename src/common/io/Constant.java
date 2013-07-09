package common.io;

import java.text.SimpleDateFormat;

public interface Constant {
	String FILE_SEG_TAG = "\n";
	String Line_SEG_TAG = "\t";
	String File_Encoding = "utf-8";
	String Web_Encoding = "utf-8";
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	String STORAGE_DIR = "D:/market";
	//对失败的同一网页进行重新抓取的上限次数
	int RECRAWL_LIMIT_MAX = 10;	
	int SUCC_UPBOUND_PER_PROXY = 4;//每个代理成功抓取的上限，超过这个上限则切换代理
	int FAIL_UPBOUND_PER_PROXY = 1;//每个代理失败次数的上限，超过这个上限则切换代理
	
	//页面类型
	int PAGETYPE_LIST = 1;//列表页
	int PAGETYPE_ITEM = 2;//内容页
	
	


}
