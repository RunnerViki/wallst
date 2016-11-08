package com.viki.stock.bean;

import java.math.BigDecimal;

public class HugeChargeCrarge {
	
	public String date;
	
	public String stockCode;
	
	public String stockShort;
	
	public BigDecimal closingPrice;
	
	public BigDecimal bidPrice;
	
	public BigDecimal volume;
	
	//溢价率
	public BigDecimal premiumRate;
	
	public String buyer;
	
	public String saler;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

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

	public BigDecimal getClosingPrice() {
		return closingPrice;
	}

	public void setClosingPrice(BigDecimal closingPrice) {
		this.closingPrice = closingPrice;
	}

	public BigDecimal getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(BigDecimal bidPrice) {
		this.bidPrice = bidPrice;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getPremiumRate() {
		return premiumRate;
	}

	public void setPremiumRate(BigDecimal premiumRate) {
		this.premiumRate = premiumRate;
	}

	public String getBuyer() {
		return buyer;
	}

	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	public String getSaler() {
		return saler;
	}

	public void setSaler(String saler) {
		this.saler = saler;
	}
}
