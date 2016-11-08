package com.viki.stock.bean;


public class StockChargeDetailBean {

	private String stockNum;
	
	private int year;
	
	private int month;
	
	private int dayOfMonth;
	
	private int hour;
	
	private int minute;
	
	private int second;
	
	private double price;
	
	private double changePrice;
	
	private double turnover;
	
	private double turnoverVector;
	
	private double sum;
	
	private String type;
	
	private String dt_;
	
	public String getDt_() {
		return dt_;
	}

	public void setDt_(String dt) {
		dt_ = dt;
	}

	public String getStockNum() {
		return stockNum;
	}

	public void setStockNum(String stockNum) {
		this.stockNum = stockNum;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getChangePrice() {
		return changePrice;
	}

	public void setChangePrice(double changePrice) {
		this.changePrice = changePrice;
	}

	public double getTurnover() {
		return turnover;
	}

	public void setTurnover(double turnover) {
		this.turnover = turnover;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getTurnoverVector() {
		return turnoverVector;
	}

	public void setTurnoverVector(double turnoverVector) {
		this.turnoverVector = turnoverVector;
	}
}
