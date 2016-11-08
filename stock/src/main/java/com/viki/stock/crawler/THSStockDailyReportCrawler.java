/**  
 * Project Name:stk_extraction  
 * File Name:StockCrawler.java  
 * Package Name:com.stk.basic  
 * Date:2015年8月26日下午3:34:07  
 * Copyright (c) 2015, Dell All Rights Reserved.  
 *  
*/  
  
package com.viki.stock.crawler;  

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.viki.stock.config.*;
import com.viki.stock.bean.StockDailyCharge;
import com.viki.stock.dao.DailyChargeDao;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**  
 * 真实地址：http://q.10jqka.com.cn/stock/fl/ 行情中心
 * ClassName:StockCrawler <br/>  
 * Function: 抓取股票涨跌幅数据到数据库. <br/>  
 * Date:     2015年8月26日 下午3:34:07 <br/>  
 * @author   阳翅翔  
 * @version    
 * @since    JDK 1.7  
 * @see        
 */
public class THSStockDailyReportCrawler {

	public static void main(String[] args){ 
		new THSStockDailyReportCrawler().run();
	}
	
	private static final Boolean persistenceInFile = false;
	
	@Autowired
	@Qualifier("DailyChargeDao")
	DailyChargeDao dailyChargeDao;

	
	@Autowired
	@Qualifier("commonThreadPoolExecutor")
	private ThreadPoolExecutor commonThreadPoolExecutor;
	
	private static final Logger LOG = LoggerFactory.getLogger(THSStockDailyReportCrawler.class.getSimpleName());
	
	public void run(){
		try{
			//如果是本地的机器，则复制云服务器上的数据后直接返回
			if(SysConfig.getProperty("machine.flag.is.in.local", "false").equals("true")){
				//copyFromCloudMachine();
				return;
			}
			
			//获取下次启动该任务的时间
			Date nextTime = TaskManager.getNextStartUp(this.getClass());
			Date startDate = DateUtil.getStockRealDate();
			if(null == nextTime){
				return;
			}
			if(DateUtil.isWeekEnd(nextTime)){
				LOG.info("周末，跳过");
				TaskManager.refreshTaskWatermark(getClass(), startDate);
				return;
			}
			
			//如果得到时间，则开始执行任务
			LOG.info("开始股票日交易统计数据爬取任务");
			Document doc;
			Connection conn = JsoupUtil.getConn();
			
			ArrayList<StockDailyCharge> stockBasicInfoList = new ArrayList<StockDailyCharge>(3000);
			try {
				// 开始爬取指定网页，并且拼接固定的分页号码，得到共56个分页的页面信息
				for(int pageNum = 1; pageNum <=56; pageNum++){
					try{
						conn.header("host", "q.10jqka.com.cn");
						conn.header("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0");
						conn.header("x-requested-with", "XMLHttpRequest");
						conn.header("referer", "http://q.10jqka.com.cn/stock/fl/");
						conn.header("cookie", "__utma=156575163.1691202685.1437924817.1441036897.1441121174.25; __utmz=156575163.1440172894.15.2.utmcsr=10jqka.com.cn|utmccn=(referral)|utmcmd=referral|utmcct=/; Hm_lvt_78c58f01938e4d85eaf619eae71b4ed1=1440314175,1440341293,1440518056,1440914387; historystock=600050%7C*%7C601390; spversion=20130314; Hm_lvt_f79b64788a4e377c608617fba4c736e2=1439915214; __utmb=156575163.4.10.1441121174; __utmc=156575163; concern=a%3A1%3A%7Bs%3A9%3A%22stock%2Ffl%2F%22%3Bs%3A12%3A%22%25B7%25D6%25C0%25E0%22%3B%7D; __utmt=1");
						conn.header("Connection", "keep-alive");
						doc = conn.url("http://q.10jqka.com.cn/interface/stock/fl/zdf/desc/"+pageNum+"/hsa/quote").get();
						String content = doc.text();
						
						//把页面内容转换成JSON对象，并取出data节点
						JSONObject jsonobj = JSONObject.parseObject(content);
						JSONArray data = (JSONArray)jsonobj.get("data");
						
						if(persistenceInFile){
							//把data节点的字符串内容转换为json对象，该对象是一个StockDailyCharge类型的ARRAYLIST
							stockBasicInfoList.addAll(JSONArray.parseObject(data.toJSONString(), new TypeReference<ArrayList<StockDailyCharge>>(){}));
						}
						List<StockDailyCharge> stockDailyChargeList = JSONArray.parseArray(data.toJSONString(),StockDailyCharge.class);
						Date add_date = new Date();
						for(StockDailyCharge stockDailyCharge : stockDailyChargeList){
							try{
								stockDailyCharge.setADD_DATE(add_date);
								if(StringUtils.isNotEmpty(stockDailyCharge.getZIN_JI_JIN_LIU_RU())){
									stockDailyCharge.setZIN_JI_JIN_LIU_RU(new BigDecimal(stockDailyCharge.getZIN_JI_JIN_LIU_RU()).multiply(new BigDecimal(10000)).toString());
								}
								if(StringUtils.isNotEmpty(stockDailyCharge.getCHENG_JIAO_E())){
									stockDailyCharge.setCHENG_JIAO_E(new BigDecimal(stockDailyCharge.getCHENG_JIAO_E()).multiply(new BigDecimal(10000)).toString());
								}
								dailyChargeDao.merge(stockDailyCharge);
							}catch(Exception e){
								LOG.error("爬取股票日交易统计数据时发生异常{}",JSON.toJSONString(stockDailyCharge),e);
							}
						}
					}catch(Throwable e){
						LOG.error("爬取股票日交易统计数据时发生异常:",e);
					}
				}
				
				if(persistenceInFile){
					//把得到的所有data全部加入到stockBasicInfoList之后，把stockBasicInfoList保存到一个路径，该路径就是path
					String path = SysConfig.getProperty("data.persistence.rootpath") + SysConfig.getProperty("data.persistence.subpath.stock.dailyreport").replace("{date}", Constants.FILE_DATE_FORMAT.get().format(startDate));
					FileUtil.serializeObjByJson(path, stockBasicInfoList);
				}
			} catch (Throwable e) {
				LOG.error("爬取股票日交易统计数据时发生异常:",e);
			}
			
			//任务执行完之后，把这一个的执行时间保存到tasklist文件中
			TaskManager.refreshTaskWatermark(getClass(), startDate);
		}catch(Throwable t){LOG.error("爬取股票日交易统计数据时发生异常:",t);}
	}
	
}
  
