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

/**  
 * ClassName:CMFBBean <br/>  
 * Function: 个股的价位分布情况 <br/>  
 * Date:     2015年8月26日 下午2:29:09 <br/>  
 * @author   阳翅翔  
 * @version    
 * @since    JDK 1.7  
 * @see        
 */
public class CMFB {

	public String STOCK_CODE;
	
	//键为所在价位，值为该价位占比百分数
	public String PRICE_DEGREE;
	
	//记录日期
	private Date ADD_DATE = new Date();
	
	//当前日期对应的价格
	public Double PRICE;
	
	public Double HIGH_PRICE;
	
	public Double LOW_PRICE;
	
	public Double TRENDS_COEFFICIENT;

	public String getSTOCK_CODE() {
		return STOCK_CODE;
	}

	public void setSTOCK_CODE(String sTOCKCODE) {
		STOCK_CODE = sTOCKCODE;
	}

	public String getPRICE_DEGREE() {
		return PRICE_DEGREE;
	}

	public void setPRICE_DEGREE(String pRICEDEGREE) {
		PRICE_DEGREE = pRICEDEGREE;
	}

	public Date getADD_DATE() {
		return ADD_DATE;
	}

	public void setADD_DATE(Date aDDDATE) {
		ADD_DATE = aDDDATE;
	}

	public Double getPRICE() {
		return PRICE;
	}

	public void setPRICE(Double pRICE) {
		PRICE = pRICE;
	}

	public Double getHIGH_PRICE() {
		return HIGH_PRICE;
	}

	public void setHIGH_PRICE(Double hIGHPRICE) {
		HIGH_PRICE = hIGHPRICE;
	}

	public Double getLOW_PRICE() {
		return LOW_PRICE;
	}

	public void setLOW_PRICE(Double lOWPRICE) {
		LOW_PRICE = lOWPRICE;
	}

	public Double getTRENDS_COEFFICIENT() {
		return TRENDS_COEFFICIENT;
	}

	public void setTRENDS_COEFFICIENT(Double tRENDSCOEFFICIENT) {
		TRENDS_COEFFICIENT = tRENDSCOEFFICIENT;
	}
}
  
