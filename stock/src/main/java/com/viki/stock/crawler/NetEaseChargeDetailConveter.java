package com.viki.stock.crawler;

import com.viki.stock.config.SysConfig;
import com.viki.stock.dao.StockChargeDetailDao;
import com.viki.stock.pojo.StockChargeDetailBean;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class NetEaseChargeDetailConveter {

	private static final Logger LOG = LoggerFactory.getLogger(NetEaseChargeDetailConveter.class.getSimpleName());
	

	
	@Autowired
	@Qualifier("StockChargeDetailDao")
	private StockChargeDetailDao stockChargeDetailDao;
	
	@Autowired
	@Qualifier("commonThreadPoolExecutor")
	private ThreadPoolExecutor commonThreadPoolExecutor;
	
	public void run(){
		File root = new File(SysConfig.getProperty("data.persistence.rootpath") + SysConfig.getProperty("data.persistence.subpath.stock.chargedetail.directory"));
		if(!root.isDirectory()){
			LOG.error("根{}不是一个目录", root.getPath());
			return;
		}
		File[] stocks = root.listFiles();
		for(File f : stocks){
			String stockCode = f.getName();
			if(!f.isDirectory()){
				LOG.error("{}不是一个目录", f.getPath());
				continue;
			}
			File[] dailyRecords = f.listFiles();
			
			for(File record : dailyRecords){
				String date = record.getName().split("\\.")[0];
				if(!record.isFile()){
					LOG.error("{}不是一个文件",record.getPath());
					continue;
				}
				if(record.getName().endsWith("csv")){
					continue;
				}
				if(!record.getName().endsWith("xls")){
					commonThreadPoolExecutor.submit(new ExcelReader(record, stockCode, date, "csv"));
				}else{
					commonThreadPoolExecutor.submit(new ExcelReader(record, stockCode, date, "xls"));
				}
			}
		}
	}
	
	class ExcelReader implements Runnable{
		
		private SimpleDateFormat chargeTimeFormat = new SimpleDateFormat("yyyyMMddHH:mm:SS");
		
		private SimpleDateFormat normalChargeTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		
		java.text.DecimalFormat formatter = new java.text.DecimalFormat("########");
		
		DecimalFormat priceChangeFormater = new DecimalFormat("#.00");  
		
		private File f;
		private String stockCode;
		private String date;
		private String type;
		public ExcelReader(File f, String stockCode, String date, String fileType){
			this.f = f;
			this.stockCode = stockCode;
			this.date = date;
			this.type = fileType;
		}
		

		@Override
		public void run() {
			if(type.equalsIgnoreCase("xls")){
				this.readExcel(this.f, stockCode, date);
			}else{
				readCsvNio(this.f, stockCode, date);
			}
		}
		
		private void readCsvNio(File f, String stockCode, String date){
			FileInputStream fi;
			try {
				fi = new FileInputStream(f);
				FileChannel fc = fi.getChannel();
				int length = (int) fc.size();
				MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0,	length);
				byte[] content = new byte[length];
				buffer.get(content, 0, length);
				String con = new String(content);
				String[] lines = con.split("\n");
				String[] eles;
				List<StockChargeDetailBean> stockChargeDetailBeanList = new ArrayList<StockChargeDetailBean>(2000);
				for(String line : lines){
					eles = line.split(",");
					try{
						StockChargeDetailBean stockChargeDetailBean = new StockChargeDetailBean();
						stockChargeDetailBean.setSTOCK_CODE(eles[0].trim());
						stockChargeDetailBean.setCHARGE_TIME(eles[1].trim());
						stockChargeDetailBean.setPRICE(eles[2].trim());
						stockChargeDetailBean.setPRICE_CHANGE(eles[3].trim());
						stockChargeDetailBean.setAMOUNT(eles[4].trim());
						stockChargeDetailBean.setVOLUME(eles[5].trim());
						stockChargeDetailBean.setTYPE(eles[6].trim());
						//stockChargeDetailDao.merge(stockChargeDetailBean);
						stockChargeDetailBeanList.add(stockChargeDetailBean);
					}catch(Exception e){
						if(e instanceof DuplicateKeyException){
							//TODO为什么不能新建一个catch MySQLIntegrityConstraintViolationException
						}else{
							LOG.error("csv异常{}", line, e.getMessage());
						}
					}
				}
				try{
					if(stockChargeDetailBeanList.size() > 0){
						HashMap<String,Object> params = new HashMap<String,Object>();
						params.put("list", stockChargeDetailBeanList);
						params.put("table_num", (Integer.parseInt(stockCode) + 10 ) % 10);
						stockChargeDetailDao.batchInsert(params);
					}
					LOG.info("完成{}",f.getPath());
				}catch(Exception e){
					LOG.error("csv批量插入异常{}", f.getPath(), e.getMessage());
				}
			} catch (Exception e) {
				LOG.error("csv批量插入异常{}", f.getPath(), e.getMessage());
			}
			
		}
		
		private void readCsv(File f, String stockCode, String date){
			try {
				BufferedReader reader = new BufferedReader(new FileReader(f));
				String line;
				String[] eles;
				List<StockChargeDetailBean> stockChargeDetailBeanList = new ArrayList<StockChargeDetailBean>(2000);
				while((line = reader.readLine()) != null){
					eles = line.split(",");
					try{
						StockChargeDetailBean stockChargeDetailBean = new StockChargeDetailBean();
						stockChargeDetailBean.setSTOCK_CODE(eles[0].trim());
						stockChargeDetailBean.setCHARGE_TIME(eles[1].trim());
						stockChargeDetailBean.setPRICE(eles[2].trim());
						stockChargeDetailBean.setPRICE_CHANGE(eles[3].trim());
						stockChargeDetailBean.setAMOUNT(eles[4].trim());
						stockChargeDetailBean.setVOLUME(eles[5].trim());
						stockChargeDetailBean.setTYPE(eles[6].trim());
						//stockChargeDetailDao.merge(stockChargeDetailBean);
						stockChargeDetailBeanList.add(stockChargeDetailBean);
					}catch(Exception e){
						if(e instanceof DuplicateKeyException){
							//TODO为什么不能新建一个catch MySQLIntegrityConstraintViolationException
						}else{
							LOG.error("csv异常{}", line, e.getMessage());
						}
					}
				}
				try{
					if(stockChargeDetailBeanList.size() > 0){
						HashMap<String,Object> params = new HashMap<String,Object>();
						params.put("list", stockChargeDetailBeanList);
						params.put("table_num", (Integer.parseInt(stockCode) + 10 ) % 10);
						stockChargeDetailDao.batchInsert(params);
					}
					LOG.info("完成{}",f.getPath());
				}catch(Exception e){
					LOG.error("csv批量插入异常{}", f.getPath(), e.getMessage());
				}
			} catch (Exception e) {
				LOG.error("csv异常", e);
			}
			
		}
		
		private void readExcel(File f, String stockCode, String date){
			try {
				HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(f));
				HSSFSheet sheet = workbook.getSheetAt(0);
				int rowNum = sheet.getLastRowNum();
				HSSFRow row;
				String type;
				StringBuffer content = new StringBuffer();
				List<StockChargeDetailBean> stockChargeDetailBeanList = new ArrayList<StockChargeDetailBean>(2000);
				for(int rn = 1; rn <= rowNum; rn++ ){
					try{
						row = sheet.getRow(rn);
						StockChargeDetailBean stockChargeDetailBean = new StockChargeDetailBean();
						stockChargeDetailBean.setSTOCK_CODE(stockCode);
						stockChargeDetailBean.setCHARGE_TIME(normalChargeTimeFormat.format(chargeTimeFormat.parse(date+row.getCell(0).getStringCellValue())));//2015063015:00:05
						stockChargeDetailBean.setPRICE(row.getCell(1).getNumericCellValue()+"");
						stockChargeDetailBean.setPRICE_CHANGE(priceChangeFormater.format(row.getCell(2).getNumericCellValue()));
						stockChargeDetailBean.setAMOUNT(row.getCell(3).getNumericCellValue()+"");
						stockChargeDetailBean.setVOLUME(formatter.format(row.getCell(4).getNumericCellValue()));
						type = row.getCell(5).getStringCellValue();
						type = type.trim().equals("买盘") ? "B" : (type.trim().equals("卖盘") ? "S" : "N");
						stockChargeDetailBean.setTYPE(type);
						try{
							content.append(stockChargeDetailBean.toString()+"\n");
						}catch(Exception e){}
						stockChargeDetailBeanList.add(stockChargeDetailBean);
					}catch(Throwable t){
						if(t instanceof DuplicateKeyException){
							//TODO为什么不能新建一个catch MySQLIntegrityConstraintViolationException
						}else{
							LOG.error("File：{}",f.getPath(),t);
						}
					}
				}
				try{
					if(stockChargeDetailBeanList.size() > 0){
						if(stockChargeDetailBeanList.size() > 0){
							HashMap<String,Object> params = new HashMap<String,Object>();
							params.put("list", stockChargeDetailBeanList);
							params.put("table_num", (Integer.parseInt(stockCode) + 10 ) % 10);
							stockChargeDetailDao.batchInsert(params);
						}
						String path = f.getPath().replace(".xls", ".csv");
						if(new File(path).exists()){
							f.delete();
							return;
						}
						FileWriter fw = new FileWriter(path);
						fw.write(content.toString());
						fw.flush();
						fw.close();
						LOG.info("完成{}",f.getPath());
						f.delete();
					}
				}catch(Exception e){
					LOG.error("xls批量插入异常{}", f.getPath(), e.getMessage());
				}
				
			} catch (Exception e) {
				LOG.error("{}", f.getPath(), e.getMessage());
			}
			
		}
	}
	
	
}
