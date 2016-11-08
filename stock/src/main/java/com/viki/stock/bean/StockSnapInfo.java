package com.viki.stock.bean;

import java.math.BigDecimal;


public class StockSnapInfo {
	
	public String snapId;

	public String stockCode;
	
	public BigDecimal shiYinLv;
	
	public BigDecimal shiYinLvStatic;
	
	public BigDecimal shiJingLv;
	
	public BigDecimal earnPerStock;
	
	public BigDecimal zongGuBen;
	
	public BigDecimal LiuTongGu;
	
	public String version;

	public String getSnapId() {
		return snapId;
	}

	public void setSnapId(String snapId) {
		this.snapId = snapId;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public BigDecimal getShiYinLv() {
		return shiYinLv;
	}

	public void setShiYinLv(BigDecimal shiYinLv) {
		this.shiYinLv = shiYinLv;
	}

	public BigDecimal getShiYinLvStatic() {
		return shiYinLvStatic;
	}

	public void setShiYinLvStatic(BigDecimal shiYinLvStatic) {
		this.shiYinLvStatic = shiYinLvStatic;
	}

	public BigDecimal getShiJingLv() {
		return shiJingLv;
	}

	public void setShiJingLv(BigDecimal shiJingLv) {
		this.shiJingLv = shiJingLv;
	}

	public BigDecimal getEarnPerStock() {
		return earnPerStock;
	}

	public void setEarnPerStock(BigDecimal earnPerStock) {
		this.earnPerStock = earnPerStock;
	}

	public BigDecimal getZongGuBen() {
		return zongGuBen;
	}

	public void setZongGuBen(BigDecimal zongGuBen) {
		this.zongGuBen = zongGuBen;
	}

	public BigDecimal getLiuTongGu() {
		return LiuTongGu;
	}

	public void setLiuTongGu(BigDecimal liuTongGu) {
		LiuTongGu = liuTongGu;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}