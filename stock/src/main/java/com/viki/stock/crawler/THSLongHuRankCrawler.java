package com.viki.stock.crawler;

import com.viki.stock.config.*;
import com.viki.stock.bean.LongHuRank;
import com.viki.stock.bean.Ranklistbean;
import com.viki.stock.dao.LongHuRankDao;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 原地址：http://data.10jqka.com.cn/market/longhu/
 * 保存股票排名信息，存在数据库
 * @author Viki
 *
 */
public class THSLongHuRankCrawler {
private static final Logger LOG = LoggerFactory.getLogger(THSLongHuRankCrawler.class.getSimpleName());
	
	private static final Boolean persistenceInFile = false;

	@Autowired
	@Qualifier("LongHuRankDao")
	private LongHuRankDao longHuRankDao;

	
	@Autowired
	@Qualifier("commonThreadPoolExecutor")
	private ThreadPoolExecutor commonThreadPoolExecutor;

	SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	

	public void run(){
		try{
			//如果是本地的机器，则复制云服务器上的数据后直接返回
			if(SysConfig.getProperty("machine.flag.is.in.local", "false").equals("true")){
				//copyFromCloudMachine();
				return;
			}
			
			Date realDate = TaskManager.getNextStartUp(this.getClass());
			if(realDate == null){
				return;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdf.format(realDate);
			startDate = startDate == null ? "2015-09-01" : startDate;
			Date startDateDT = null;
			while(true){
				try {
					startDateDT = startDateDT == null ? fileDateFormat.parse(startDate) : startDateDT;
					if(!DateUtil.getStockRealDate().before(startDateDT)){
						crawlRankListByDate(fileDateFormat.format(startDateDT));
						startDateDT = DateUtil.dateCalculate(startDateDT, 1, Calendar.DATE);
					}else{
						break;
					}
				} catch (Exception e) {
					LOG.error("",e);
				}
			}
			if(startDateDT != null){
				TaskManager.refreshTaskWatermark(this.getClass(), startDateDT);
			}
		}catch(Throwable t){LOG.error("爬取股票龙虎榜数据时发生异常:",t);}
	}
	
	private void crawlRankListByDate(String startDate){
		try{
			String fileName = "";
			int pageTotals = 100;
			Date chargeDate = fileDateFormat.parse(startDate);
			List<Ranklistbean> ranklistbeanList = new ArrayList<Ranklistbean>();
			for(Integer page = 1; page<= pageTotals; page++){
				try {
					//页面的地址及开始日起和结束日起
					String url = String.format(SysConfig.getProperty("ths.longhu.rank.url.formatter"), page, startDate);
					Document doc = JsoupUtil.crawlPage(url);
					Elements trs = doc.select("#maintable > tbody > tr");
					if(StringUtils.isEmpty(trs.text()) || trs.text().contains("今日此板块龙虎榜数据暂未公布")){	
						break; 
					}
					for(Element tr : trs){
						try{
							Elements tds = tr.select("td");
							if(persistenceInFile){
								Ranklistbean ranklistbean = new Ranklistbean();
								ranklistbean.setStockCode(tds.get(1).text());
								ranklistbean.setStockShort(tds.get(2).text());
								ranklistbean.setExclusiveInterpretation(tds.get(3).text());
								ranklistbean.setClosingPrice(new BigDecimal(tds.get(4).text()));
								ranklistbean.setPriceLimit(new BigDecimal(tds.get(5).text().replace("%", "")));
								ranklistbean.setTurnover(new BigDecimal(tds.get(6).text().replace("", "")).multiply(new BigDecimal(10000)));
								ranklistbean.setPurchase(new BigDecimal(tds.get(7).text().replace("%", "")));
								ranklistbean.setSell(new BigDecimal(tds.get(8).text().replace("%", "")));
								ranklistbean.setNetByingAount(new BigDecimal(tds.get(9).text()).multiply(new BigDecimal(10000)));
								ranklistbean.setLstTpe(tds.get(10).text());
								ranklistbeanList.add(ranklistbean);
							}
							LongHuRank longHuRank = new LongHuRank();
							longHuRank.setADD_DATE(chargeDate);
							longHuRank.setCLOSING_PRICE(new BigDecimal(tds.get(4).text()).doubleValue());
							longHuRank.setEXCLUSIVE_INTERPRETATION(tds.get(3).text());
							longHuRank.setNET_BUYING_AMOUNT(new BigDecimal(tds.get(9).text()).multiply(new BigDecimal(10000)).doubleValue());
							longHuRank.setPRICE_LIMIT(new BigDecimal(tds.get(5).text().replace("%", "")).doubleValue());
							longHuRank.setPURCHASE(new BigDecimal(tds.get(7).text().replace("%", "")).doubleValue());
							longHuRank.setSELL(new BigDecimal(tds.get(8).text().replace("%", "")).doubleValue());
							longHuRank.setSTOCK_CODE(tds.get(1).text());
							longHuRank.setSTOCK_SHORT(tds.get(2).text());
							try{
								longHuRank.setSUCCESS_RATE(tds.get(3).text().split("成功率为")[1].replace("%", ""));
							}catch(Exception e){
							}
							longHuRank.setTURNOVER(new BigDecimal(tds.get(6).text().replace("", "")).multiply(new BigDecimal(10000)).doubleValue());
							longHuRank.setRANK_TYPE(tds.get(10).text());
							longHuRankDao.merge(longHuRank);
						}catch(Exception e){
							LOG.error("爬取{}时出错,当前页面元素内容:{}",url,tr.html(),e);
						}
					}
				} catch (Exception e) {
					LOG.error("爬取时发生异常", e);
				}
			}
			if(persistenceInFile){
				if(ranklistbeanList.isEmpty()){
					return;
				}
				fileName = Constants.FILE_DATE_FORMAT.get().format(chargeDate);
				FileUtil.serializeObjByJson(SysConfig.getProperty("data.persistence.rootpath") + SysConfig.getProperty("data.persistence.subpath.stock.ranklist").replace("{date}", fileName), ranklistbeanList);
			}
		}catch(Exception e){
			LOG.error("",e);
		}
	}
}
