package com.viki.stock.dao;

import com.viki.stock.bean.NeteaseCrawRecord;
import org.springframework.stereotype.Repository;

@Repository(value = "NeteaseCrawlRecordDao")
public interface NeteaseCrawlRecordDao extends BaseDao<NeteaseCrawRecord> {

}
