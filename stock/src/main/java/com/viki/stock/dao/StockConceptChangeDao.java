package com.viki.stock.dao;

import com.viki.stock.pojo.StockConceptChangeBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "StockConceptChangeDao")
public interface StockConceptChangeDao extends BaseDao<StockConceptChangeBean> {

	public void batchInsert(List<StockConceptChangeBean> batch);
}
