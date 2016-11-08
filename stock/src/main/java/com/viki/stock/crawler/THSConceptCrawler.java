package com.viki.stock.crawler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.viki.stock.config.DateUtil;
import com.viki.stock.config.JsoupUtil;
import com.viki.stock.config.SysConfig;
import com.viki.stock.config.TaskManager;
import com.viki.stock.dao.StockConceptChangeDao;
import com.viki.stock.dao.StockConceptDao;
import com.viki.stock.dao.StockConceptRefDao;
import com.viki.stock.pojo.StockConceptBean;
import com.viki.stock.pojo.StockConceptChangeBean;
import com.viki.stock.pojo.StockConceptRefBean;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class THSConceptCrawler {
	
	protected static Logger LOG = LoggerFactory.getLogger(THSConceptCrawler.class.getSimpleName());
	
	@Autowired
	@Qualifier("StockConceptDao")
	private StockConceptDao stockConceptDao;
	
	@Autowired
	@Qualifier("StockConceptRefDao")
	private StockConceptRefDao stockConceptRefDao;
	
	@Autowired
	@Qualifier("StockConceptChangeDao")
	private StockConceptChangeDao stockConceptChangeDao;

	
	@Autowired
	@Qualifier("commonThreadPoolExecutor")
	private ThreadPoolExecutor commonThreadPoolExecutor;
	
	
	public HashMap<String,String> conceptSet = new HashMap<String,String>(3000);
	
	private void copyFromCloudMachine(){}

	public void run() throws IOException{
		try{
			//如果是本地的机器，则复制云服务器上的数据后直接返回
			if(SysConfig.getProperty("machine.flag.is.in.local", "false").equals("true")){
				copyFromCloudMachine();
				return;
			}
			
			//获取下次启动该任务的时间
			Date nextTime = TaskManager.getNextStartUp(this.getClass());
			if(null == nextTime){
				return;
			}
			
			Elements conceptEles = JsoupUtil.crawlPage(SysConfig.getProperty("concept.entrance.url")).select(".cate_items > a");
			for(Element ele : conceptEles){
				Document conceptDoc = JsoupUtil.crawlPage(ele.attr("href").trim());
				StockConceptBean stockConceptBean = new StockConceptBean();
				stockConceptBean.setCONCEPT_NAME(ele.text().trim());
				stockConceptBean.setVISIT_URL(ele.attr("href").trim());
				stockConceptBean.setCONCEPT_ID(Integer.parseInt(conceptDoc.select("body > div.container.w1200 > div > div.body > div > div.board-main.w900 > div.heading > div.board-hq > h3 > span").text()));
				stockConceptBean.setCONCEPT_CODE(ele.attr("href").trim().split("\\/|_")[ele.attr("href").trim().split("\\/|_").length - 1]);
				stockConceptDao.merge(stockConceptBean);
				
				Element summary = conceptDoc.select(".board-infos").first();
				StockConceptChangeBean stockConceptChangeBean = new StockConceptChangeBean();
				stockConceptChangeBean.setCONCEPT_ID(stockConceptBean.getCONCEPT_ID());
				//stockConceptChangeBean.setINDEX_CHANGE(Double.parseDouble(conceptDoc.select(".board-zdf").text().split("\\s")[0]));
				//stockConceptChangeBean.setINDEX_(Double.parseDouble(conceptDoc.select(".stock-trend > .price").text()));
				stockConceptChangeBean.setCHANGE_PERCENT(Double.parseDouble(summary.getElementsContainingOwnText("板块涨幅").parents().get(0).select("dl > dd").text().trim().replace("%", "")));
				String chengJiaoE = summary.getElementsContainingOwnText("成交额").parents().get(0).select("dl > dd").text().trim();
				Double chengJiaoEDouble = 0D;
				if(chengJiaoE.contains("亿元")){
					chengJiaoEDouble = new BigDecimal(chengJiaoE.replace("亿元", "")).multiply(new BigDecimal(100000000)).doubleValue();
				}else if(chengJiaoE.contains("万元")){
					chengJiaoEDouble = new BigDecimal(chengJiaoE.replace("万元", "")).multiply(new BigDecimal(10000)).doubleValue();
				}
				stockConceptChangeBean.setCHENG_JIAO_E(chengJiaoEDouble);
				
				
				String ziJinJingLiuRu = summary.getElementsContainingOwnText("资金净流入").parents().get(0).select("dl > dd").text().trim();
				Double ziJinJingLiuRuDouble = 0D;
				if(ziJinJingLiuRu.contains("亿元")){
					ziJinJingLiuRuDouble = new BigDecimal(ziJinJingLiuRu.replace("亿元", "")).multiply(new BigDecimal(100000000)).doubleValue();
				}else if(ziJinJingLiuRu.contains("万元")){
					ziJinJingLiuRuDouble = new BigDecimal(ziJinJingLiuRu.replace("万元", "")).multiply(new BigDecimal(10000)).doubleValue();
				}
				stockConceptChangeBean.setZI_JING_JIN_LIU_RU(ziJinJingLiuRuDouble);
				
				stockConceptChangeBean.setSHANG_ZHANG_SHU(Integer.parseInt(summary.getElementsContainingOwnText("涨跌家数").first().select("i").first().text().trim()));
				stockConceptChangeBean.setXIA_DIE_SHU(Integer.parseInt(summary.getElementsContainingOwnText("涨跌家数").first().select("i").last().text().trim()));
				stockConceptChangeBean.setADD_DATE(DateUtil.getStockRealDate());
				stockConceptChangeDao.merge(stockConceptChangeBean);
				int totalStocks = stockConceptChangeBean.getSHANG_ZHANG_SHU() + stockConceptChangeBean.getXIA_DIE_SHU();
				int pages = totalStocks % 50 == 0 ? totalStocks / 50 : totalStocks / 50 + 1;
				int page = 1;
				while(page <= pages){
					try{
						String stockListDoc = JsoupUtil.crawlPage(String.format(SysConfig.getProperty("concept.stock.list.url"), page, stockConceptBean.getCONCEPT_CODE())).text();
						JSONArray stockList = JSONObject.parseObject(stockListDoc).getJSONArray("data");
						for(Object obj : stockList){
							try{
								JSONObject jsonObj = ((JSONObject)JSONObject.toJSON(obj));
								StockConceptRefBean stockConceptRefBean = new StockConceptRefBean();
								stockConceptRefBean.setCONCEPT_ID(stockConceptBean.getCONCEPT_ID());
								stockConceptRefBean.setSTOCK_CODE(jsonObj.getString("stockcode"));
								stockConceptRefDao.merge(stockConceptRefBean);
							}catch(Exception e){LOG.error("",e);}
						}
					}catch(Exception e){
						LOG.error("",e);
					}finally{
						page++;
					}
				}
			}
			
			TaskManager.refreshTaskWatermark(this.getClass(), nextTime);
		}catch(Throwable t){LOG.error("爬取股票概念数据时发生异常:",t);}
	}

	public static void main(String[] args) {
	}
}
