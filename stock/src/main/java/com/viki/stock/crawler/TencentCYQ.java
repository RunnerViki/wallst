/**  
 * Project Name:stk_extraction  
 * File Name:TencentCYQ.java  
 * Package Name:com.stk.cyq  
 * Date:2015年8月18日下午4:20:00  
 * Copyright (c) 2015, Dell All Rights Reserved.  
 *  
*/  
  
package com.viki.stock.crawler;  

import com.viki.stock.config.*;
import com.viki.stock.bean.CMFB;
import com.viki.stock.bean.CMFBBean;
import com.viki.stock.bean.StockBasicInfo;
import com.viki.stock.dao.CMFBDao;
import com.viki.stock.dao.StockBasicInfoDao;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import sun.org.mozilla.javascript.internal.NativeObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**  
 * ClassName:TencentCYQ <br/>  
 * Function: 腾讯的筹码分布数据 <br/>  保存到数据库
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2015年8月18日 下午4:20:00 <br/>  
 * @author   阳翅翔  
 * @version    
 * @since    JDK 1.7  
 * @see        
 */
public class TencentCYQ {
	
	protected static Logger LOG = LoggerFactory.getLogger(TencentCYQ.class.getSimpleName());
	
	@Autowired
	@Qualifier("StockBasicInfoDao")
	private StockBasicInfoDao stockBasicInfoDao;

	@Autowired
	@Qualifier("commonThreadPoolExecutor")
	private ThreadPoolExecutor commonThreadPoolExecutor;

	@Autowired
	@Qualifier("CMFBDao")
	private CMFBDao CMFBDao;
	

	
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
			
			Document doc = null;
			Elements eles = null;
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("js");
			List<StockBasicInfo> stockBasicInfoList = stockBasicInfoDao.query(null);
			ArrayList<CMFBBean> CMFBBeanList = new ArrayList<CMFBBean>(stockBasicInfoList.size());
			for(StockBasicInfo stock :stockBasicInfoList){
				String url = String.format(SysConfig.getProperty("cmfb.url.formatter"), StockUtil.getMarketTagByStockCode(stock.getStockCode()), stock.getStockCode());
				try {
					Connection conn = JsoupUtil.getConn();
					conn.header("referer", String.format(SysConfig.getProperty("cmfb.referer.url.formatter"), stock.getStockCode()));
					doc = JsoupUtil.crawlPage(url);
					eles = doc.select(".picbox.big");
					for(Element ele : eles){
						if(!ele.html().contains("znzCMFB")){
							continue;
						}
						String[] itemArr = parseCMFB(ele,engine);
						if(itemArr == null || itemArr.length == 0){
							continue;
						}
						CMFBDao.merge(transCMFBArrToCMFB(itemArr, stock.getStockCode()));
					}
				} catch (Throwable e) {
					LOG.error("爬取{}出错",url,e);
				}
			}
			FileUtil.serializeObjByJson(SysConfig.getProperty("data.persistence.rootpath") + 
					SysConfig.getProperty("data.persistence.subpath.cmfb")
					.replace("{date}", Constants.FILE_DATE_FORMAT.get().format(DateUtil.getStockRealDate())), CMFBBeanList);
			TaskManager.refreshTaskWatermark(getClass(), DateUtil.getStockRealDate());
		}catch(Throwable t){
			LOG.error("爬取股票筹码分布图时发生异常:",t);
		}
	}
	
	
	/**  
	 * methodName: parseCMFB
	 *
	 * 解析个股页面源码中的筹码分布对象，得到一个数组，前三位分别为最低价，最高价，当前价，后一百位为一百分位的占比<br/>    
	 *   
	 * @param ele
	 * @param engine
	 * @return
	 * @throws ScriptException   
	 */
	private String[] parseCMFB(Element ele,ScriptEngine engine) throws ScriptException{
		String content;
		content = ele.select("script").html();
		engine.eval(content.split(";")[0]);
		NativeObject p = (NativeObject) engine.get("p");
		String items = (String)p.get("params");
		items = items.split("=")[1].replaceAll("\\[|\\]", "");
		String[] itemArr = items.split(",");
		if(itemArr.length == 103){
			return itemArr;
		}else{
			return null;
		}
	}
	
	
	/**  
	 * methodName: transCMFBArrToCMFBBean
	 *
	 * 转换<br/>    
	 *   
	 * @param itemArr
	 * @param stockCode
	 * @return   
	 */
	public CMFBBean transCMFBArrToCMFBBean(String[] itemArr, String stockCode){
		Double lowPrice = new BigDecimal(itemArr[0]).doubleValue();
		Arrays.toString(ArrayUtils.subarray(itemArr, 3, itemArr.length - 1));
		Double highPrice = new BigDecimal(itemArr[1]).doubleValue();
		Double nowPrice = new BigDecimal(itemArr[2]).doubleValue();
		Double priceRange = highPrice - lowPrice;
		
		CMFBBean cMFBBean = new CMFBBean();
		cMFBBean.setPrice(nowPrice);
		cMFBBean.setStockCode(stockCode);
		TreeMap<Double, Double> distributionNodes = cMFBBean.getDistributionNodes();
		for(int i = 0; i< 100; i++){
			distributionNodes.put(new BigDecimal(lowPrice + i * priceRange / 100).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(), new BigDecimal(itemArr[3+i]).doubleValue());
		}
		return cMFBBean;
	}
	
	private CMFB transCMFBArrToCMFB(String[] itemArr, String stockCode){
		BigDecimal lowPrice = new BigDecimal(itemArr[0]);
		BigDecimal highPrice = new BigDecimal(itemArr[1]);
		Double nowPrice = new BigDecimal(itemArr[2]).doubleValue();
		CMFB cmfb = new CMFB();
		cmfb.setADD_DATE(new Date());
		cmfb.setHIGH_PRICE(highPrice.doubleValue());
		cmfb.setLOW_PRICE(lowPrice.doubleValue());
		cmfb.setPRICE(nowPrice);
		cmfb.setPRICE_DEGREE(Arrays.toString(ArrayUtils.subarray(itemArr, 3, itemArr.length - 1)));
		cmfb.setSTOCK_CODE(stockCode);
		BigDecimal priceRangePercent = highPrice.subtract(lowPrice).divide(new BigDecimal(100));
		Double TRENDSCOEFFICIENT = 0D;
		for(int i = 0; i< 100; i++){
			if(lowPrice.add(priceRangePercent.multiply(new BigDecimal(i))).doubleValue() > nowPrice){
				TRENDSCOEFFICIENT += (new Double(itemArr[3+i]) * (i+1));
			}else{
				TRENDSCOEFFICIENT -= (new Double(itemArr[3+i]) * (i+1));
			}
		}
		cmfb.setTRENDS_COEFFICIENT(TRENDSCOEFFICIENT);
		return cmfb;
	}
	
	
	public static void main(String[] args){
		new TencentCYQ().run();
	}
}
  
