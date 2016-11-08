/**  
 * Project Name:stk_extraction  
 * File Name:StockBean.java  
 * Package Name:com.stk.pojo  
 * Date:2015年8月26日下午3:35:14  
 * Copyright (c) 2015, Dell All Rights Reserved.  
 *  
*/  
  
package com.viki.stock.pojo;  
public class StockConceptBean {

	public String CONCEPT_NAME;
	
	public String CONCEPT_CODE;
	
	public Integer CONCEPT_ID;
	
	public String VISIT_URL;

	public String getCONCEPT_NAME() {
		return CONCEPT_NAME;
	}

	public void setCONCEPT_NAME(String cONCEPTNAME) {
		CONCEPT_NAME = cONCEPTNAME;
	}

	public Integer getCONCEPT_ID() {
		return CONCEPT_ID;
	}

	public void setCONCEPT_ID(Integer cONCEPTID) {
		CONCEPT_ID = cONCEPTID;
	}

	public String getVISIT_URL() {
		return VISIT_URL;
	}

	public void setVISIT_URL(String vISITURL) {
		VISIT_URL = vISITURL;
	}

	public String getCONCEPT_CODE() {
		return CONCEPT_CODE;
	}

	public void setCONCEPT_CODE(String cONCEPTCODE) {
		CONCEPT_CODE = cONCEPTCODE;
	}
}
  
