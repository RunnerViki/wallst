package com.viki.stock.crawler;

import com.viki.stock.config.*;
import com.viki.stock.bean.StockBasicInfo;
import com.viki.stock.bean.StockSnapInfo;
import com.viki.stock.dao.DailyChargeDao;
import com.viki.stock.dao.StockBasicInfoDao;
import com.viki.stock.dao.StockSnapInfoDao;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 爬取股票基本信息 保存到数据库
 * @author Viki
 *
 */
public class THSStockBasicInfoCrawler {
	
	private static final Logger LOG = LoggerFactory.getLogger(THSStockBasicInfoCrawler.class);

	SimpleDateFormat stockDateFormat = new SimpleDateFormat("yyyyMMdd");
	
	@Autowired 
	@Qualifier("StockBasicInfoDao")
	private StockBasicInfoDao stockBasicInfoDao;
	
	@Autowired
	@Qualifier("StockSnapInfoDao")
	private StockSnapInfoDao stockSnapInfoDao;

	@Autowired
	@Qualifier("commonThreadPoolExecutor")
	private ThreadPoolExecutor commonThreadPoolExecutor;
	
	@Autowired
	@Qualifier("DailyChargeDao")
	DailyChargeDao dailyChargeDao;
	
	/**
	 * 1、获取最近一次爬取到的股票每日数据
	 * 2、提取股票号码
	 * 3、进入股票基本信息页，爬取相关信息
	 * 4、保存到股票基本信息表和股票拓展属性表
	 */
	public void run(){
		try{
			//如果是本地的机器，则复制云服务器上的数据后直接返回
			if(SysConfig.getProperty("machine.flag.is.in.local", "false").equals("true")){
				//copyFromCloudMachine();
				return;
			}
			
			Date nextTime = TaskManager.getNextStartUp(this.getClass());
			if(null == nextTime){
				return;
			}
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("recentDay", "1");
			//List<StockDailyCharge> stockBasicInfoList = dailyChargeDao.query(params);
			List<StockBasicInfo> stockBasicInfoList = stockBasicInfoDao.query(null);
			for(StockBasicInfo sbi : stockBasicInfoList){
				crawlStockInfoByCode(sbi);
			}
			TaskManager.refreshTaskWatermark(getClass(), DateUtil.getStockRealDate());
		}catch(Throwable t){
			LOG.error("爬取股票基本数据时发生异常:",t);
		}
	}
	

	
	/**
	 * 数据内容格式转换
	 * @param item
	 * @return
	 */
	private BigDecimal transDecimalFormat(String item){
		if(item.equals("亏损")){
			return new BigDecimal("-9.99999");
		}
		if(item.equals("未公布")){
			return new BigDecimal("-0.00001");
		}
		if(StringUtils.isEmpty(item.replace("-", ""))){
			return new BigDecimal(0);
		}else{
			if(item.contains("亿股")){
				return new BigDecimal(item.replace("亿股", "")).multiply(new BigDecimal(100000000));
			}else if (item.contains("万股")){
				return new BigDecimal(item.replace("万股", "")).multiply(new BigDecimal(10000));
			}
			item = item.replace("元", "");
			try{
				return new BigDecimal(item);
			}catch(Exception e){
				LOG.error("转换异常:{}", item);
				return new BigDecimal("-0.00001");
			}
		}
	}
	
	/**
	 * 爬取单个股票数据，并保存到本地
	 * @param stockDailyCharge
	 */
	private void crawlStockInfoByCode(StockBasicInfo stockDailyCharge){
		try {
			StockBasicInfo stockBasicInfo = new StockBasicInfo();
			Document doc = JsoupUtil.crawlPage(String.format(SysConfig.getProperty("stock.basic.info.url.formatter"), stockDailyCharge.getStockCode()));
			String title = doc.select(".code > h1").text();
			stockBasicInfo.setStockName(title.split("\\s")[0]);
			stockBasicInfo.setStockCode(title.split("\\s")[1]);
			stockBasicInfo.setMarketCode(StockUtil.getMarketTagByStockCode(stockBasicInfo.getStockCode()));
			stockBasicInfo.setMainBusiness(doc.getElementsContainingOwnText("主营业务").parents().get(0).select("span").get(1).text());
			stockBasicInfo.setSectorBelong(doc.getElementsContainingOwnText("所属行业").parents().get(0).select("span").get(1).text());
			stockBasicInfo.setVolumnType(doc.getElementsContainingOwnText("分类").parents().get(0).select("span").get(1).text());
			stockBasicInfoDao.merge(stockBasicInfo);
			StockSnapInfo stockSnapInfo = new StockSnapInfo();
			stockSnapInfo.setStockCode(stockBasicInfo.getStockCode());
			stockSnapInfo.setEarnPerStock(transDecimalFormat(doc.getElementsContainingOwnText("每股收益：").parents().get(0).select("span").get(1).text()));
			stockSnapInfo.setLiuTongGu(transDecimalFormat(doc.getElementsContainingOwnText("流通A股").parents().get(0).select("span").get(1).text()));
			stockSnapInfo.setShiJingLv(transDecimalFormat(doc.getElementsContainingOwnText("市净率：").parents().get(0).select("span").get(1).text()));
			stockSnapInfo.setShiYinLv(transDecimalFormat(doc.getElementsContainingOwnText("市盈率(动态)").parents().get(0).select("span").get(1).text()));
			stockSnapInfo.setShiYinLvStatic(transDecimalFormat(doc.getElementsContainingOwnText("市盈率(静态)").parents().get(0).select("span").get(1).text()));
			stockSnapInfo.setVersion(Constants.FILE_DATE_FORMAT.get().format(DateUtil.getStockRealDate()));
			stockSnapInfo.setZongGuBen(transDecimalFormat(doc.getElementsContainingOwnText("总股本").parents().get(0).select("span").get(1).text()));
			stockSnapInfoDao.merge(stockSnapInfo);
			LOG.debug("{}:{}股票基本信息下载完成.",stockDailyCharge.getStockCode(), stockDailyCharge.getStockCode());
		} catch (Exception e) {
			LOG.error("爬取股票{}:{}时发生异常:","http://basic.10jqka.com.cn/"+stockDailyCharge.getStockCode()+"/", stockDailyCharge.getStockCode(), e);
		}
	}
}
