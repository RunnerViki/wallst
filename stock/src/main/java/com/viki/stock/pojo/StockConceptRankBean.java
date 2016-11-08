/**  
 * Project Name:stk_extraction  
 * File Name:StockBean.java  
 * Package Name:com.stk.pojo  
 * Date:2015年8月26日下午3:35:14  
 * Copyright (c) 2015, Dell All Rights Reserved.  
 *  
*/  
  
package com.viki.stock.pojo;  

import java.util.Date;

public class StockConceptRankBean {

	
	
	public Integer CONCEPT_ID;
	
	public String STOCK_CODE;
	
	public Integer RANK_NUM;
	
	public Date ADD_DATE;
	
	public Date UPDATE_TIME;
	
	public Double TURNOVER_RATE_INFIVE;
	
	public Double SPREAD_RATE_IN_FIVE;

	public Integer getCONCEPT_ID() {
		return CONCEPT_ID;
	}

	public void setCONCEPT_ID(Integer cONCEPTID) {
		CONCEPT_ID = cONCEPTID;
	}

	public String getSTOCK_CODE() {
		return STOCK_CODE;
	}

	public void setSTOCK_CODE(String sTOCKCODE) {
		STOCK_CODE = sTOCKCODE;
	}

	public Integer getRANK_NUM() {
		return RANK_NUM;
	}

	public void setRANK_NUM(Integer rANKNUM) {
		RANK_NUM = rANKNUM;
	}

	public Date getADD_DATE() {
		return ADD_DATE;
	}

	public void setADD_DATE(Date aDDDATE) {
		ADD_DATE = aDDDATE;
	}

	public Date getUPDATE_TIME() {
		return UPDATE_TIME;
	}

	public void setUPDATE_TIME(Date uPDATETIME) {
		UPDATE_TIME = uPDATETIME;
	}

	public Double getTURNOVER_RATE_INFIVE() {
		return TURNOVER_RATE_INFIVE;
	}

	public void setTURNOVER_RATE_INFIVE(Double tURNOVERRATEINFIVE) {
		TURNOVER_RATE_INFIVE = tURNOVERRATEINFIVE;
	}

	public Double getSPREAD_RATE_IN_FIVE() {
		return SPREAD_RATE_IN_FIVE;
	}

	public void setSPREAD_RATE_IN_FIVE(Double sPREADRATEINFIVE) {
		SPREAD_RATE_IN_FIVE = sPREADRATEINFIVE;
	}

}
  
