package com.viki.stock.crawler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.viki.stock.config.*;
import com.viki.stock.bean.StockBasicInfo;
import com.viki.stock.bean.StockDailyPriceThrehold;
import com.viki.stock.dao.StockBasicInfoDao;
import com.viki.stock.dao.StockDailyPriceThreholdDao;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 股票诊断页面爬取， 保存到数据库
 * http://doctor.10jqka.com.cn/000539/
 * @author Viki
 * 
 */
public class THSStockDoctorCrawler {

	private static final Logger LOG = LoggerFactory.getLogger(THSStockDoctorCrawler.class.getSimpleName());

	SimpleDateFormat stockDateFormat = new SimpleDateFormat("yyyyMMdd");
	
	private static final Boolean persistenceInFile = false;
	
	@Autowired
	@Qualifier("StockDailyPriceThreholdDao")
	StockDailyPriceThreholdDao stockDailyPriceThreholdDao;
	
	@Autowired
	@Qualifier("StockBasicInfoDao")
	private StockBasicInfoDao stockBasicInfoDao;

	
	@Autowired
	@Qualifier("commonThreadPoolExecutor")
	private ThreadPoolExecutor commonThreadPoolExecutor;
	

	public void run(){
		try{
			//如果是本地的机器，则复制云服务器上的数据后直接返回
			if(SysConfig.getProperty("machine.flag.is.in.local", "false").equals("true")){
				//copyFromCloudMachine();
				return;
			}
			
			//获取下次启动该任务的时间
			Date nextTime = TaskManager.getNextStartUp(this.getClass());
			if(null == nextTime){
				return;
			}
			
			Date addDate = new Date();
			List<StockBasicInfo> stockBasicInfoList = stockBasicInfoDao.query(null);
			String path = "";
			for(StockBasicInfo stockBasicInfo : stockBasicInfoList){
				try {
					if(persistenceInFile){
						path = SysConfig.getProperty("data.persistence.rootpath")+SysConfig.getProperty("data.persistence.subpath.stock.extension");
						path = path.replace("{date}", stockDateFormat.format(DateUtil.getStockRealDate())).replace("{stock}", stockBasicInfo.getStockCode());
						if(new File(path).exists()){
							LOG.info("文件已存在，不再爬取");
							continue;
						}
					}
					StockDailyPriceThrehold stockDailyPriceThrehold = new StockDailyPriceThrehold();
					stockDailyPriceThrehold.setSTOCK_CODE(stockBasicInfo.getStockCode());
					Document doc = JsoupUtil.crawlPage("http://doctor.10jqka.com.cn/"+stockBasicInfo.getStockCode()+"/");
					String presureAndHoldPrice = doc.select("#PressureChartData").text();
					JSONArray pahpJson = (JSONArray)JSONArray.parse(presureAndHoldPrice);
					if(pahpJson == null){
						continue;
					}
					for(Object item : pahpJson){
						JSONObject jsonItem = (JSONObject)item;
						String flag = jsonItem.get("text").toString();
						String value = (jsonItem.containsKey("value") && jsonItem.get("value") != null) ?jsonItem.get("value").toString():null;
						if("压力位".equals(flag)){
							stockDailyPriceThrehold.setPRESS_PRICE(value == null ? null :new BigDecimal(value));
						}else if("支撑位".equals(flag)){
							stockDailyPriceThrehold.setHOLD_PRICE(value == null ? null :new BigDecimal(value));
						}
					}
					Element ele = doc.select("#position_value_rate").first();
					stockDailyPriceThrehold.setKONGPAN_RATE(new BigDecimal(ele.attr("value")));
					Element eleLast = doc.select("#position_value_last").first();
					stockDailyPriceThrehold.setKONGPAN_RATE_LAST(new BigDecimal(eleLast.attr("value")));
					if(persistenceInFile){
						FileUtil.serializeObjByJson(path, JSONObject.toJSONString(stockDailyPriceThrehold), false);
					}
					stockDailyPriceThrehold.setADD_DATE(addDate);
					stockDailyPriceThreholdDao.merge(stockDailyPriceThrehold);
					LOG.debug("股票{}诊断页面爬取完成",stockBasicInfo.getStockCode());
				} catch (Exception e) {
					LOG.error("爬取{}:时发生异常", stockBasicInfo.getStockCode(), e);
				}
			}
			TaskManager.refreshTaskWatermark(getClass(), DateUtil.getStockRealDate());
		}catch(Throwable t){LOG.error("爬取股票诊断数据时发生异常:",t);}
	}
	
}
