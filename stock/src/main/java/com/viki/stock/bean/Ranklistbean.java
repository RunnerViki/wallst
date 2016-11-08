package com.viki.stock.bean;

import java.math.BigDecimal;

public class Ranklistbean {
	public String stockCode;
	
	public String stockShort;//股票简称
	
	public String exclusiveInterpretation; //股票解读
	
	public BigDecimal closingPrice;
	
	public BigDecimal priceLimit;//涨跌幅
	
	public BigDecimal turnover;//成交金额单位:元
	
	public BigDecimal purchase;//买入占比
	
	public BigDecimal sell; //卖出占比
	
	public BigDecimal netByingAount; //净买入额， 单位:元
	
	public String lstTpe;

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getStockShort() {
		return stockShort;
	}

	public void setStockShort(String stockShort) {
		this.stockShort = stockShort;
	}

	public String getExclusiveInterpretation() {
		return exclusiveInterpretation;
	}

	public void setExclusiveInterpretation(String exclusiveInterpretation) {
		this.exclusiveInterpretation = exclusiveInterpretation;
	}

	public BigDecimal getClosingPrice() {
		return closingPrice;
	}

	public void setClosingPrice(BigDecimal closingPrice) {
		this.closingPrice = closingPrice;
	}

	public BigDecimal getPriceLimit() {
		return priceLimit;
	}

	public void setPriceLimit(BigDecimal priceLimit) {
		this.priceLimit = priceLimit;
	}

	public BigDecimal getTurnover() {
		return turnover;
	}

	public void setTurnover(BigDecimal turnover) {
		this.turnover = turnover;
	}

	public BigDecimal getPurchase() {
		return purchase;
	}

	public void setPurchase(BigDecimal purchase) {
		this.purchase = purchase;
	}

	public BigDecimal getSell() {
		return sell;
	}

	public void setSell(BigDecimal sell) {
		this.sell = sell;
	}

	public BigDecimal getNetByingAount() {
		return netByingAount;
	}

	public void setNetByingAount(BigDecimal netByingAount) {
		this.netByingAount = netByingAount;
	}

	public String getLstTpe() {
		return lstTpe;
	}

	public void setLstTpe(String lstTpe) {
		this.lstTpe = lstTpe;
	}

	
	

}
