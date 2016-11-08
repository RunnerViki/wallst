package com.viki.stock.bean;

import java.util.Date;

public class NeteaseCrawRecord {
	private int stock_code;
	
	private String stock_date;
	
	private Date craw_date;
	
	private String result;

	public int getStock_code() {
		return stock_code;
	}

	public void setStock_code(int stockCode) {
		stock_code = stockCode;
	}

	public String getStock_date() {
		return stock_date;
	}

	public void setStock_date(String stockDate) {
		stock_date = stockDate;
	}

	public Date getCraw_date() {
		return craw_date;
	}

	public void setCraw_date(Date crawDate) {
		craw_date = crawDate;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
