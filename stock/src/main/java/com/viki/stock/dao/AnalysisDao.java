package com.viki.stock.dao;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository(value = "AnalysisDao")
public interface AnalysisDao extends BaseDao<Object> {

	public void insertABC(Map params);
	
	public List<HashMap<String,Object>> queryD_summary(Map params);
	
	public void insertD(Map params);
	
	public TreeMap<Date,Double> queryTrends(Map params);
}
