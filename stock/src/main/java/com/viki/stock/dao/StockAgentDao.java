package com.viki.stock.dao;

import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository(value = "StockAgentDao")
public interface StockAgentDao extends BaseDao<HashMap> {

}
