package com.viki.stock.bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用于保存股票的价位压力位与支撑位，以及当前股票的控盘程度
 * @author Viki
 *
 */
public class StockDailyPriceThrehold {

	public String STOCK_CODE;
	
	public Date ADD_DATE;
	
	public BigDecimal PRESS_PRICE;
	
	public BigDecimal HOLD_PRICE;
	
	public BigDecimal KONGPAN_RATE;
	
	public BigDecimal KONGPAN_RATE_LAST;

	public String getSTOCK_CODE() {
		return STOCK_CODE;
	}

	public void setSTOCK_CODE(String sTOCKCODE) {
		STOCK_CODE = sTOCKCODE;
	}

	public Date getADD_DATE() {
		return ADD_DATE;
	}

	public void setADD_DATE(Date aDDDATE) {
		ADD_DATE = aDDDATE;
	}

	public BigDecimal getPRESS_PRICE() {
		return PRESS_PRICE;
	}

	public void setPRESS_PRICE(BigDecimal pRESSPRICE) {
		PRESS_PRICE = pRESSPRICE;
	}

	public BigDecimal getHOLD_PRICE() {
		return HOLD_PRICE;
	}

	public void setHOLD_PRICE(BigDecimal hOLDPRICE) {
		HOLD_PRICE = hOLDPRICE;
	}

	public BigDecimal getKONGPAN_RATE() {
		return KONGPAN_RATE;
	}

	public void setKONGPAN_RATE(BigDecimal kONGPANRATE) {
		KONGPAN_RATE = kONGPANRATE;
	}

	public BigDecimal getKONGPAN_RATE_LAST() {
		return KONGPAN_RATE_LAST;
	}

	public void setKONGPAN_RATE_LAST(BigDecimal kONGPANRATELAST) {
		KONGPAN_RATE_LAST = kONGPANRATELAST;
	}
}
