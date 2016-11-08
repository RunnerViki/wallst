package com.viki.stock.dao;

import com.viki.stock.bean.StockDailyPriceThrehold;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository(value = "StockDailyPriceThreholdDao")
public interface StockDailyPriceThreholdDao extends BaseDao<StockDailyPriceThrehold> {
	public void batchInsert(List<StockDailyPriceThrehold> batch);
}
