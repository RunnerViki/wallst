package com.viki.stock.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class StockDailyCharge {

	
	private String stockId;
	
	
	private String STOCK_NAME;
	
	
	private String STOCK_CODE;
	
	
	private String ZUI_XIN_JIA;
	
	
	private String ZHANG_DIE_E;
	
	
	private String ZHANG_DIE_FU;
	
	
	private String ZOU_SHOU;
	
	
	private String JIN_KAI;
	
	
	private String ZUI_GAO_JIA;
	
	
	private String ZUI_DI_JIA;
	
	
	private String CHENG_JIAO_LIANG;
	
	
	private String CHENG_JIAO_E;
	
	
	private String ZIN_JI_JIN_LIU_RU;
	
	
	private String HUAN_SHOU_LV;
	
	
	private String REPORT_TIME;
	
	private Date ADD_DATE;

	
	public String getStockId() {
		return stockId;
	}

	@JSONField(name="stockid")
	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	
	public String getSTOCK_NAME() {
		return STOCK_NAME;
	}

	@JSONField(name="stockname")
	public void setSTOCK_NAME(String STOCKNAME) {
		this.STOCK_NAME = STOCKNAME;
	}

	
	public String getSTOCK_CODE() {
		return STOCK_CODE;
	}

	@JSONField(name="stockcode")
	public void setSTOCK_CODE(String sTOCKCODE) {
		STOCK_CODE = sTOCKCODE;
	}

	
	public String getZUI_XIN_JIA() {
		return ZUI_XIN_JIA;
	}

	@JSONField(name="zxj")
	public void setZUI_XIN_JIA(String zUIXINJIA) {
		ZUI_XIN_JIA = zUIXINJIA;
	}

	
	public String getZHANG_DIE_E() {
		return ZHANG_DIE_E;
	}

	@JSONField(name="zde")
	public void setZHANG_DIE_E(String zHANGDIEE) {
		ZHANG_DIE_E = zHANGDIEE;
	}

	
	public String getZHANG_DIE_FU() {
		return ZHANG_DIE_FU;
	}

	@JSONField(name="zdf")
	public void setZHANG_DIE_FU(String zHANGDIEFU) {
		ZHANG_DIE_FU = zHANGDIEFU;
	}

	
	public String getZOU_SHOU() {
		return ZOU_SHOU;
	}

	@JSONField(name="zs")
	public void setZOU_SHOU(String zOUSHOU) {
		ZOU_SHOU = zOUSHOU;
	}

	
	public String getJIN_KAI() {
		return JIN_KAI;
	}

	@JSONField(name="jk")
	public void setJIN_KAI(String jINKAI) {
		JIN_KAI = jINKAI;
	}

	
	public String getZUI_GAO_JIA() {
		return ZUI_GAO_JIA;
	}
	
	@JSONField(name="zgj")
	public void setZUI_GAO_JIA(String zUIGAOJIA) {
		ZUI_GAO_JIA = zUIGAOJIA;
	}

	
	public String getZUI_DI_JIA() {
		return ZUI_DI_JIA;
	}

	@JSONField(name="zdj")
	public void setZUI_DI_JIA(String zUIDIJIA) {
		ZUI_DI_JIA = zUIDIJIA;
	}

	
	public String getCHENG_JIAO_LIANG() {
		return CHENG_JIAO_LIANG;
	}

	@JSONField(name="cjl")
	public void setCHENG_JIAO_LIANG(String cHENGJIAOLIANG) {
		CHENG_JIAO_LIANG = cHENGJIAOLIANG;
	}

	
	public String getCHENG_JIAO_E() {
		return CHENG_JIAO_E;
	}

	@JSONField(name="cje")
	public void setCHENG_JIAO_E(String cHENGJIAOE) {
		CHENG_JIAO_E = cHENGJIAOE;
	}

	
	public String getZIN_JI_JIN_LIU_RU() {
		return ZIN_JI_JIN_LIU_RU;
	}

	@JSONField(name="jlr")
	public void setZIN_JI_JIN_LIU_RU(String zINJIJINLIURU) {
		ZIN_JI_JIN_LIU_RU = zINJIJINLIURU;
	}

	
	public String getHUAN_SHOU_LV() {
		return HUAN_SHOU_LV;
	}

	@JSONField(name="hsl")
	public void setHUAN_SHOU_LV(String hUANSHOULV) {
		HUAN_SHOU_LV = hUANSHOULV;
	}

	
	public String getREPORT_TIME() {
		return REPORT_TIME;
	}

	@JSONField(name="rtime")
	public void setREPORT_TIME(String rEPORTTIME) {
		REPORT_TIME = rEPORTTIME;
	}

	public Date getADD_DATE() {
		return ADD_DATE;
	}

	public void setADD_DATE(Date aDDDATE) {
		ADD_DATE = aDDDATE;
	}
}
