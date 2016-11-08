package com.viki.stock.crawler;

import com.viki.stock.config.DateUtil;
import com.viki.stock.config.JsoupUtil;
import com.viki.stock.config.SysConfig;
import com.viki.stock.config.TaskManager;
import com.viki.stock.bean.HugeChargeCrarge;
import com.viki.stock.dao.HugeChargeDao;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 大宗商品交易爬取
 * 保存到数据库
 * @author Fanhui
 *
 */
public class THSHugeChargeCrawler {
	private static final Logger LOG = LoggerFactory.getLogger(THSHugeChargeCrawler.class.getSimpleName());
	 
	//文件以日期命名
	SimpleDateFormat stockDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	@Qualifier("HugeChargeDao")
	private HugeChargeDao hugeChargeDao;
	
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
			
			Date nextTime = TaskManager.getNextStartUp(this.getClass());
			if(nextTime == null){
				return;
			}
			String startDate = stockDateFormat.format(nextTime);
			//如果没有开始日起就设定个开始日起
			Date endDateDt = DateUtil.getStockRealDate();
			String endDate = stockDateFormat.format(endDateDt);
			//设定最大页数
			int pageTotals = 100;
			List<HugeChargeCrarge> hugeChargeCrargeList = new ArrayList<HugeChargeCrarge>();
			for(Integer page = 1; page<= pageTotals; page++){
				try {
					//页面的地址及开始日起和结束日起
					String url = String.format(SysConfig.getProperty("ths.huge.charge.url.formatter"), startDate, endDate, page);
					Document doc = JsoupUtil.crawlPage(url);
					LOG.error("爬取页面:{},现在已获取:{}条数据",url,hugeChargeCrargeList.size());
					Elements trs = doc.select(".m-table > tbody > tr");
					if(StringUtils.isEmpty(trs.text())){
						break; 
					}
					
					for(Element tr : trs){
						try{
							Elements tds = tr.select("td");
							HugeChargeCrarge hugeChargeCrarge = new HugeChargeCrarge();
							hugeChargeCrarge.setDate(tds.get(1).text());
							hugeChargeCrarge.setStockCode(tds.get(2).text());
							hugeChargeCrarge.setStockShort(tds.get(3).text());
							hugeChargeCrarge.setClosingPrice(new BigDecimal(tds.get(4).text()));
							hugeChargeCrarge.setBidPrice(new BigDecimal(tds.get(5).text()));
							hugeChargeCrarge.setVolume(new BigDecimal(tds.get(6).text().replace("%", "")).multiply(new BigDecimal(10000)));
							hugeChargeCrarge.setPremiumRate(new BigDecimal(tds.get(7).text().replace("%", "")));
							hugeChargeCrarge.setBuyer(tds.get(8).text());
							hugeChargeCrarge.setSaler(tds.get(9).text());
							hugeChargeDao.insert(hugeChargeCrarge);
						}catch(Exception e){
							LOG.error("",e);
						}
					}
				} catch (Exception e) {
					LOG.error("爬取时发生异常", e);
				}
			}
			TaskManager.refreshTaskWatermark(getClass(), endDateDt);
		}catch(Throwable t){LOG.error("爬取股票大单交易数据时发生异常:",t);}
	}
}
