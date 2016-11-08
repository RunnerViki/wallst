package com.viki.stock.dao;

import com.viki.stock.bean.StockDailyCharge;
import org.springframework.stereotype.Repository;


@Repository(value = "DailyChargeDao")
public interface DailyChargeDao extends BaseDao<StockDailyCharge> {

}
