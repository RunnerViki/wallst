/**  
 * Project Name:stk_extraction  
 * File Name:CMFBBean.java  
 * Package Name:com.stk.cyq  
 * Date:2015年8月26日下午2:29:09  
 * Copyright (c) 2015, Dell All Rights Reserved.  
 *  
*/  
  
package com.viki.stock.bean;  

import java.util.Date;
import java.util.TreeMap;

/**  
 * ClassName:CMFBBean <br/>  
 * Function: 个股的价位分布情况 <br/>  
 * Date:     2015年8月26日 下午2:29:09 <br/>  
 * @author   阳翅翔  
 * @version    
 * @since    JDK 1.7  
 * @see        
 */
public class CMFBBean {

	public String stockCode;
	
	//键为所在价位，值为该价位占比百分数
	public TreeMap<Double, Double> distributionNodes = new TreeMap<Double,Double>();
	
	//记录日期
	private Date dateTag = new Date();
	
	//当前日期对应的价格
	public Double price;

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public TreeMap<Double, Double> getDistributionNodes() {
		return distributionNodes;
	}

	public Date getDateTag() {
		return dateTag;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
	
}
  
