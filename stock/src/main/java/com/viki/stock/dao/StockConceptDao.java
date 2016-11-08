package com.viki.stock.dao;

import com.viki.stock.pojo.StockConceptBean;
import org.springframework.stereotype.Repository;

@Repository(value = "StockConceptDao")
public interface StockConceptDao extends BaseDao<StockConceptBean> {

}
