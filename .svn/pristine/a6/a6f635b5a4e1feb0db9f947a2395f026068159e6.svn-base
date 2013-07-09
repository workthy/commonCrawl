/**
 * @title:XmlConfigReader.java
 * @Package netease.trading.crawl.config
 * @Description: 读取xml配置文件
 * @author netease-huangze
 * @date 2012-10-12 下午3:05:11
 * @version V1.0
 */
package common.crawl.config;

import java.io.File;
import java.io.InputStream;



import org.apache.log4j.Logger;
//import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.htmlparser.Node;


public class XmlConfigReader {
//	static Logger logger = Logger.getLogger(XmlConfigReader.class.getName());
	
	//单例模式-懒汉式(真正使用的时候实例化<延迟加载>) 
	private static XmlConfigReader instance = null;
	private Document doc = null;
	
	private static final String configFile = "";
 
	//XmlConfigReader.getClass().getResource("./c/d.jpg")
	
	
	 //私有构造器,
	
	 /**
	  * 构造函数，需要载入配置文件的路径
	  * 通过SAXReader的方式读取配置文件config.xml文件中的信息.
	  * @param configFilePath 配置文件的路径
	  */
    private XmlConfigReader(){
        // 使用SAXReader的方式读取XML
        SAXReader reader = new SAXReader();
        //通过当前线程装载文件配置文件
//        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/crawl-config.xml");
        try {
            //将配置文件装载到Document对象中  
//        	Document doc = reader.read(in);
            doc = reader.read(new File(configFile));    
//            doc = reader.read(XmlConfigReader.class.getResource(configFile));
            //采用Xpath方式获取配置文件中的相关信息
            //取得jdbc相关配置信息.
//            Element driverName = (Element)doc.selectObject("/config/craw");
//   
//            logger.info(driverName.getStringValue());
            
           
  
        } catch (DocumentException  e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    
//    public void select(){
//    	
//    	for(Object node: doc.selectNodes("/config/gamelist/game")){
//         	Element e = (Element) node;       
//         	System.out.println(e.getName() + "\t" + e.attributeValue("name"));
//         }	
//    }
    
    
    public String selectNode(String node){
    	Element e = (Element) doc.selectSingleNode(node);
    	return e.getTextTrim();
    	
    }


	/**
	 * 	
      * 公共的静态的入口方法  
      * 为了防止多线程多个对象保证单例,添加同步关键字synchronized.  
	  *
	  * @date: 2012-10-12下午3:09:48
	  * @author: netease-huangze
	  * @Title: getInstance
	  * @Description: 
	  * @param @return  
	  * @return XmlConfigReader    
	  * @throws
	 */
    public static synchronized XmlConfigReader getInstance(){
        //判断是否实例化
        if(instance==null){
            instance = new XmlConfigReader();
        }
        return instance;
    }
	
    
	/**
	 * 
	 * @date: 2012-10-12下午3:05:11
	 * @author: netease-huangze
	 * @Title: main
	 * @Description: 
	 * @param @param args  
	 * @return void    
	 * @throws
	 */
	public static void main(String[] args) {
		
		XmlConfigReader xcfr = XmlConfigReader.getInstance();
			
		System.out.println(xcfr.selectNode("/config/crawler/parallelQueue"));
		
	}
	
	

}


 
