package common.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class IoUtils {


	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	static String  ENCODE =  System.getProperty("file.encoding");
	
	public static void main(String[] args) throws IOException {
		System.out.println(Calendar.getInstance().getTime());
	}
	

	/**
	 * 
	  * @Description: 保存内容到指定文件路径
	  * @param content
	  * @param outPath  
	  * @return void    
	  * @throws
	 */
	public static void sink(String content, String outPath){
		
		checkOutPath(outPath);		
		
		OutputStreamWriter osw;
		try {
			osw = new OutputStreamWriter(new FileOutputStream(
			        outPath), ENCODE);
		
			osw.write(content);
			osw.close();
		
		} catch (IOException e) {		
			e.printStackTrace();
		} 
		
	}
	/**
	 * 
	  * @Description: 以追加方式写入文件
	  * @param content
	  * @param outPath
	  * @param toAppend  
	  * @return void    
	  * @throws
	 */
	
	public static void sink(String content, String outPath, Boolean toAppend){
		
		checkOutPath(outPath);		
		try{
			FileWriter osw = new FileWriter(outPath, toAppend);	
			osw.write(content);
			osw.close();
		
		} catch (IOException e) {		
			e.printStackTrace();
		} 
		
	}
	
	/**
	  * 
	  * @Description: 检查文件夹路径是否存在，如果父目录不存在，则创建
	  * @param outPath  
	  * @return void    
	  * @throws
	 */
	
	public static void checkOutPath(String outPath){	
		//---如果文件夹路径不存在,则创建父文件夹路径-------
		File file = new File(outPath);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}	
	}
	
	
	public static String openFile(String FileName) {
		
		return openFile(new File(FileName), ENCODE);
	}
	
	public static String openFile(File file, String encode){
		
		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), encode));
			StringBuilder szContent = new StringBuilder();
			String szTemp;

			while ((szTemp = bis.readLine()) != null) {
				szContent.append(szTemp + "\n");
			}
			bis.close();
			return szContent.toString();
		} catch (Exception e) {
			return null;
		}
		
	}
	public static void message(String szMsg) {
		try {
			System.out.println(new String(szMsg.getBytes(ENCODE), System
					.getProperty("file.encoding")));
		} catch (Exception e) {
		}
	}
	public static String genFileName(String url){
//		try {
//			url = URLDecoder.decode(url, Constant.Web_Encoding);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		url = url.substring(url.lastIndexOf('?'));	
			
		String urlFileName = url.substring(url.lastIndexOf('/')+1).replaceAll("(\\?|/)", ".") + 
		"-" + sdf.format(new Date()) + ".html";
		return urlFileName;
	}

}
