package com.viki.stock.dao;

import com.viki.stock.bean.CMFB;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository(value = "CMFBDao")
public interface CMFBDao extends BaseDao<CMFB> {

	public void batchInsert(List<CMFB> batch);
}
