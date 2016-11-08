package com.viki.stock.dao;

import com.viki.stock.pojo.StockChargeDetailBean;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository(value = "StockChargeDetailDao") 
public interface StockChargeDetailDao extends BaseDao<StockChargeDetailBean> {

	public void batchInsert(HashMap<String, Object> params);
}
