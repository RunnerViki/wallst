package com.viki.stock.dao;

import com.viki.stock.bean.StockBasicInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "StockBasicInfoDao") 
public interface StockBasicInfoDao extends BaseDao<StockBasicInfo> {
	
	public List<StockBasicInfo> queryMissingStockData(String date) throws Exception;

}
