package com.viki.stock.dao;

import com.viki.stock.bean.StockSnapInfo;
import org.springframework.stereotype.Repository;

@Repository(value = "StockSnapInfoDao") 
public interface StockSnapInfoDao extends BaseDao<StockSnapInfo> {

}
