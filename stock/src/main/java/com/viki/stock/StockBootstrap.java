package com.viki.stock;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.ArrayList;

public class StockBootstrap {
	
	public static void main(String[] args){
		ArrayList<String> files = new ArrayList<String>();
		files.add("classpath:applicationContext.xml");
		if(new File(System.getProperty("user.dir") + File.separator + "applicationContext-crawler.xml").exists()){
			files.add("file:${user.dir}/applicationContext-crawler.xml");
			System.out.println("加载外部配置文件");
		}else{
			files.add("classpath:applicationContext-crawler.xml");
			System.out.println("加载自带配置文件");
		}
		
		String[] str = files.toArray(new String[]{});
		new ClassPathXmlApplicationContext(str);
        System.out.println("数据抽取模块初始化完成");
	}
}