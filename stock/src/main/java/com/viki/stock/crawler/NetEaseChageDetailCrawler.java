package com.viki.stock.crawler;

import com.viki.stock.config.*;
import com.viki.stock.bean.NeteaseCrawRecord;
import com.viki.stock.bean.StockBasicInfo;
import com.viki.stock.dao.NeteaseCrawlRecordDao;
import com.viki.stock.dao.StockBasicInfoDao;
import com.viki.stock.dao.StockChargeDetailDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 网易股票交易详细记录，保存到文件
 * @author Viki
 *
 */
public class NetEaseChageDetailCrawler {

	private static final Logger LOG = LoggerFactory.getLogger(NetEaseChageDetailCrawler.class.getSimpleName());

	private static final String YEAR = Calendar.getInstance().get(Calendar.YEAR) + "";
	
	@Autowired
	@Qualifier("StockBasicInfoDao")
	private StockBasicInfoDao stockBasicInfoDao;
	
	@Autowired
	@Qualifier("NeteaseCrawlRecordDao")
	private NeteaseCrawlRecordDao neteaseCrawlRecordDao;
	
	@Autowired
	@Qualifier("StockChargeDetailDao")
	private StockChargeDetailDao stockChargeDetailDao;


	
	@Autowired
	@Qualifier("commonThreadPoolExecutor")
	private ThreadPoolExecutor commonThreadPoolExecutor;
	
	@SuppressWarnings("static-access")
	public void run() {
		try {
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
			
			Calendar startDate = Calendar.getInstance();
			startDate.setTime(nextTime);
			Calendar date = Calendar.getInstance();
			date.setTime(DateUtil.getStockRealDate());
			List<StockBasicInfo> stockBasicInfoList = stockBasicInfoDao.query(null);
			if (stockBasicInfoList == null) {
				return;
			}
			for(String code : StockUtil.indexStocks){
				StockBasicInfo shStockRate = new StockBasicInfo();
				shStockRate.setStockCode(code);
				stockBasicInfoList.add(shStockRate);
			}
			for (StockBasicInfo stockBasicInfo : stockBasicInfoList) {
				try {
					File dir = new File(SysConfig.getProperty("data.persistence.rootpath") + SysConfig.getProperty("data.persistence.subpath.stock.chargedetail.directory")
							+ stockBasicInfo.getStockCode());
					if (!dir.exists()) {
						dir.mkdirs();
					}
				} catch (Exception e) {
					LOG.error("创建股票号码文件夹" + stockBasicInfo.getStockCode() + "时发生错误:", e);
				}
			}
			
			while(true){
				if(date.getTimeInMillis() >= startDate.getTimeInMillis()){
					if (DateUtil.isWeekEnd(startDate.getTime())) {
						LOG.warn(Constants.FILE_DATE_FORMAT.get().format(startDate.getTime()) + "是周末，不需要爬取");
						startDate.add(Calendar.DATE, 1);
						TaskManager.refreshTaskWatermark(getClass(), DateUtil.getStockRealDateByDate(startDate.getTime()));
						continue;
					}
					
					for (StockBasicInfo stockBasicInfo : stockBasicInfoList) {
						parseStock(stockBasicInfo, commonThreadPoolExecutor, startDate);
					}
					while(commonThreadPoolExecutor.getActiveCount() > 0){
						try{
							LOG.info("线程池未执行完本次循环的所有任务，等十秒");
							Thread.currentThread().sleep(10000);
						}catch(Throwable t){
							LOG.error("",t);
						}
					}
					stockBasicInfoList = fixStockMissing(Constants.FILE_DATE_FORMAT.get().format(startDate.getTime()));
					LOG.info("开始补漏:共{}条", stockBasicInfoList.size());
					for (StockBasicInfo stockBasicInfo : stockBasicInfoList) {
						parseStock(stockBasicInfo, commonThreadPoolExecutor, startDate);
					}
					while(commonThreadPoolExecutor.getActiveCount() > 0){
						try{
							LOG.info("线程池未执行完本次循环的所有任务，等十秒");
							Thread.currentThread().sleep(10000);
						}catch(Throwable t){
							LOG.error("",t);
						}
					}
					startDate.add(Calendar.DATE, 1);
					TaskManager.refreshTaskWatermark(getClass(), DateUtil.getStockRealDateByDate(startDate.getTime()));
				}else{
					break;
				}
			}
			
			int x = 0;
			while(commonThreadPoolExecutor.getActiveCount() > 0 && x++ < 20){
				try{
					LOG.info("线程池未执行完，等五分钟");
					Thread.currentThread().sleep(300000);
				}catch(Throwable t){
					LOG.error("",t);
				}
			}
		} catch (Exception e) {
			LOG.error("爬取成交明细详情任务时发生错误", e);
		}
	}
	

	public void parseStock(StockBasicInfo stockBasicInfo, ThreadPoolExecutor executor, Calendar startDate){
		File targetFile = null;
		String fileName = "";
		try {
			fileName = SysConfig.getProperty("data.persistence.rootpath") + SysConfig.getProperty("data.persistence.subpath.stock.chargedetail.directory")
			+ stockBasicInfo.getStockCode() + File.separator + Constants.FILE_DATE_FORMAT.get().format(startDate.getTime());
			targetFile = new File(fileName + ".xls");
			if (targetFile.exists()) {
				//LOG.info("文档{}已存在", targetFile.getPath());
			}else if(new File(fileName + ".csv").exists()){
				//LOG.info("文档{}已存在", fileName + ".csv");
			}else{
				LOG.info("文档{}正在爬取", stockBasicInfo.getStockCode());
				Future future = executor.submit(new DownLoadRunnable(targetFile, stockBasicInfo.getStockCode(), Constants.FILE_DATE_FORMAT.get().format(startDate.getTime())));
				future.get(6, TimeUnit.MINUTES);
			}
		} catch (RejectedExecutionException e) {
			try{
				Thread.sleep(1000);
				Future future = executor.submit(new DownLoadRunnable(targetFile, stockBasicInfo.getStockCode(), Constants.FILE_DATE_FORMAT.get().format(startDate.getTime())));
				future.get(6, TimeUnit.MINUTES);
			}catch(Exception e1){
				LOG.error("爬取股票号码" + stockBasicInfo.getStockCode() + "时发生错误:", e1);
			}
		} catch (Exception e) {
			LOG.error("爬取股票号码" + stockBasicInfo.getStockCode() + "时发生错误:", e);
		}
	}
	
	public List<StockBasicInfo> fixStockMissing(String date){
		try {
			List<StockBasicInfo> stockInfoList = stockBasicInfoDao.queryMissingStockData(date);
			return stockInfoList;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<StockBasicInfo> ();
		}
	}

	class DownLoadRunnable implements Runnable {
		private String date;
		private String stockNum;

		public DownLoadRunnable(File file, String stockNum, String date) {
			this.stockNum = stockNum;
			this.date = date;
		}

		public void run() {
			downloadNet(date, stockNum);
		}

		String down_load_url_formatter = SysConfig.getProperty("netease.download.url.formatter");

		public boolean downloadNet(String date, String stockNum) {
			// 下载网络文件
			int byteread = 0;
			try {
				File f = new File(SysConfig.getProperty("data.persistence.rootpath") + SysConfig.getProperty("data.persistence.subpath.stock.chargedetail.directory") + stockNum, date + ".xls");
				if (f.exists()) {
					LOG.info(f.getPath() + f.getName() + "数据文档已存在");
					return true;
				}
				String prefix = StockUtil.getMarketTagByStockCodeInNetEase(stockNum);
				URL url = new URL(String.format(down_load_url_formatter, YEAR, date, prefix, stockNum));
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(5000);
				InputStream inStream = conn.getInputStream();
				FileOutputStream fs = new FileOutputStream(f);
				byte[] buffer = new byte[13 << 2];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				fs.flush();
				fs.close();
				NeteaseCrawRecord neteaseCrawRecord = new NeteaseCrawRecord();
				neteaseCrawRecord.setCraw_date(new Date());
				neteaseCrawRecord.setStock_code(Integer.parseInt(stockNum));
				neteaseCrawRecord.setStock_date(date);
				neteaseCrawRecord.setResult("Success");
				neteaseCrawlRecordDao.insert(neteaseCrawRecord);
			} catch (Throwable e) {
				NeteaseCrawRecord neteaseCrawRecord = new NeteaseCrawRecord();
				neteaseCrawRecord.setCraw_date(new Date());
				neteaseCrawRecord.setStock_code(Integer.parseInt(stockNum));
				neteaseCrawRecord.setStock_date(date);
				neteaseCrawRecord.setResult(e.getMessage().substring(0, e.getMessage().length()> 481 ? 480 : e.getMessage().length()));
				neteaseCrawlRecordDao.insert(neteaseCrawRecord);
				LOG.error("保存数据文档时发生错误.\n股票号码：" + stockNum + "; 日期: " + date+", "+e.getMessage());
				return false;
			}
			return true;
		}
	}
}
