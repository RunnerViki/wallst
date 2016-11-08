package com.viki.stock.pojo;

public class StockChargeDetailBean {

	private String STOCK_CODE;
	
	private String CHARGE_TIME;
	
	private String PRICE;
	
	private String PRICE_CHANGE;
	
	private String AMOUNT;
	
	private String VOLUME;
	
	private String TYPE;
	
	private String table_num;

	@Override
	public String toString() {
		return STOCK_CODE + ", " + CHARGE_TIME + ", " + PRICE + ", " + PRICE_CHANGE + ", " + AMOUNT + ", " + VOLUME + ", " + TYPE;
	}

	public String getSTOCK_CODE() {
		return STOCK_CODE;
	}

	public void setSTOCK_CODE(String sTOCKCODE) {
		STOCK_CODE = sTOCKCODE;
	}

	public String getCHARGE_TIME() {
		return CHARGE_TIME;
	}

	public void setCHARGE_TIME(String cHARGETIME) {
		CHARGE_TIME = cHARGETIME;
	}

	public String getPRICE() {
		return PRICE;
	}

	public void setPRICE(String pRICE) {
		PRICE = pRICE;
	}

	public String getPRICE_CHANGE() {
		return PRICE_CHANGE;
	}

	public void setPRICE_CHANGE(String pRICECHANGE) {
		PRICE_CHANGE = pRICECHANGE;
	}

	public String getAMOUNT() {
		return AMOUNT;
	}

	public void setAMOUNT(String aMOUNT) {
		AMOUNT = aMOUNT;
	}

	public String getVOLUME() {
		return VOLUME;
	}

	public void setVOLUME(String vOLUME) {
		VOLUME = vOLUME;
	}

	public String getTYPE() {
		return TYPE;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public String getTable_num() {
		return table_num;
	}

	public void setTable_num(String tableNum) {
		table_num = tableNum;
	}
	
	
}
