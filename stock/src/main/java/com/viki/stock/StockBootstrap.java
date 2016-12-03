package com.viki.stock;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.ArrayList;

public class StockBootstrap {
	
	public static void main(String[] args){
		ArrayList<String> files = new ArrayList<String>();
		files.add("classpath:applicationContext.xml");
		String externalFilePath = System.getProperty("user.dir") + File.separator + "config" +File.separator+"applicationContext-crawler.xml";
		if(new File(externalFilePath).exists()){
			files.add("file:${user.dir}/config/applicationContext-crawler.xml");
			System.out.println("加载外部配置文件");
		}else{
			files.add("classpath:applicationContext-crawler.xml");
			System.out.println("加载自带配置文件,用户路径:"+System.getProperty("user.dir"));
		}
		
		String[] str = files.toArray(new String[]{});
		new ClassPathXmlApplicationContext(str);
	}
}