package com.viki.stock.bean;

import java.util.Date;

public class LongHuRank {
	public String STOCK_CODE;
	
	public String STOCK_SHORT;//股票简称
	
	public String EXCLUSIVE_INTERPRETATION; //股票解读
	
	public Double CLOSING_PRICE;
	
	public Double PRICE_LIMIT;//涨跌幅
	
	public Double TURNOVER;//成交金额单位:元
	
	public Double PURCHASE;//买入占比
	
	public Double SELL; //卖出占比
	
	public Double NET_BUYING_AMOUNT; //净买入额， 单位:元
	
	public String RANK_TYPE;
	
	public String SUCCESS_RATE;
	
	public Date ADD_DATE;

	public String getSTOCK_CODE() {
		return STOCK_CODE;
	}

	public void setSTOCK_CODE(String sTOCKCODE) {
		STOCK_CODE = sTOCKCODE;
	}

	public String getSTOCK_SHORT() {
		return STOCK_SHORT;
	}

	public void setSTOCK_SHORT(String sTOCKSHORT) {
		STOCK_SHORT = sTOCKSHORT;
	}

	public String getEXCLUSIVE_INTERPRETATION() {
		return EXCLUSIVE_INTERPRETATION;
	}

	public void setEXCLUSIVE_INTERPRETATION(String eXCLUSIVEINTERPRETATION) {
		EXCLUSIVE_INTERPRETATION = eXCLUSIVEINTERPRETATION;
	}

	public Double getCLOSING_PRICE() {
		return CLOSING_PRICE;
	}

	public void setCLOSING_PRICE(Double cLOSINGPRICE) {
		CLOSING_PRICE = cLOSINGPRICE;
	}

	public Double getPRICE_LIMIT() {
		return PRICE_LIMIT;
	}

	public void setPRICE_LIMIT(Double pRICELIMIT) {
		PRICE_LIMIT = pRICELIMIT;
	}

	public Double getTURNOVER() {
		return TURNOVER;
	}

	public void setTURNOVER(Double tURNOVER) {
		TURNOVER = tURNOVER;
	}

	public Double getPURCHASE() {
		return PURCHASE;
	}

	public void setPURCHASE(Double pURCHASE) {
		PURCHASE = pURCHASE;
	}

	public Double getSELL() {
		return SELL;
	}

	public void setSELL(Double sELL) {
		SELL = sELL;
	}

	public Double getNET_BUYING_AMOUNT() {
		return NET_BUYING_AMOUNT;
	}

	public void setNET_BUYING_AMOUNT(Double nETBUYINGAMOUNT) {
		NET_BUYING_AMOUNT = nETBUYINGAMOUNT;
	}

	public String getSUCCESS_RATE() {
		return SUCCESS_RATE;
	}

	public void setSUCCESS_RATE(String sUCCESSRATE) {
		SUCCESS_RATE = sUCCESSRATE;
	}

	public Date getADD_DATE() {
		return ADD_DATE;
	}

	public void setADD_DATE(Date aDDDATE) {
		ADD_DATE = aDDDATE;
	}

	public String getRANK_TYPE() {
		return RANK_TYPE;
	}

	public void setRANK_TYPE(String rANKTYPE) {
		RANK_TYPE = rANKTYPE;
	}
}
