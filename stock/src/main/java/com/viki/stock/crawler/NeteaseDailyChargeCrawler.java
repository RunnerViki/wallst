package com.viki.stock.crawler;

import com.viki.stock.bean.StockBasicInfo;
import com.viki.stock.bean.StockDailyCharge;
import com.viki.stock.dao.DailyChargeDao;
import com.viki.stock.dao.StockBasicInfoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Viki on 2016/11/14.
 * 网易股票
 */
public class NeteaseDailyChargeCrawler {

    public String url_formatter = "http://quotes.money.163.com/trade/lsjysj_%s.html?year=%d&season=%d";


    //http://quotes.money.163.com/service/chddata.html?code=0601857&start=20071105&end=20161122&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP
    public Integer seasons = 4;

    protected Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    @Qualifier("StockBasicInfoDao")
    private StockBasicInfoDao stockBasicInfoDao;

    @Autowired
    @Qualifier("DailyChargeDao")
    private DailyChargeDao dailyChargeDao;



    public void run(){
        List<StockBasicInfo> stockBasicInfoList = stockBasicInfoDao.query(null);
        if (stockBasicInfoList == null) {
            return;
        }
        for (StockBasicInfo stockBasicInfo : stockBasicInfoList) {
            try{
                crawlPage(stockBasicInfo.getStockCode());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void crawlPage(String stockCode){
        if(download(stockCode)){
            readCsvNio(new File("E:\\stk\\stock\\dailyreport\\"+stockCode+".csv"), stockCode);
        }

    }



    /*public static void main(String[] args) {
        //new NeteaseDailyChargeCrawler().crawlPage("601901");
        //File file = new File("http://quotes.money.163.com/service/chddata.html?code=601901&start=20110810&end=20161114&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP");
        //System.out.println(file.length());
        NeteaseDailyChargeCrawler neteaseDailyChargeCrawler = new NeteaseDailyChargeCrawler();
        if(neteaseDailyChargeCrawler.download("601901")){
            neteaseDailyChargeCrawler.readCsvNio(new File("E:\\stk\\stock\\dailyreport\\601901.csv"));
        }
    }*/

    public boolean download(String stockCode){
        try {
            /*Document document = JsoupUtil.crawlPage("http://quotes.money.163.com/service/chddata.html?code=0601901&start=20100101&end=20161114&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP");
            System.out.println(document.text());*/

            //File f = new File(SysConfig.getProperty("data.persistence.rootpath") + SysConfig.getProperty("data.persistence.subpath.stock.dailyreport") , "601901" + ".xls");
            File f = new File("E:\\stk\\stock\\dailyreport_new\\"+stockCode+".csv");
            if (f.exists()) {
                //LOG.info(f.getPath() + f.getName() + "数据文档已存在");
                return false;
            }
            f.createNewFile();
            int byteread = 0;
            String prefix = stockCode.startsWith("6") ? "0" : "1";
            URL url = new URL("http://quotes.money.163.com/service/chddata.html?code="+prefix+stockCode+"&start=20010810&end=20161122&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP");
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private void readCsvNio(File f, String stockCode){
        FileInputStream fi;
        try {
            fi = new FileInputStream(f);
            FileChannel fc = fi.getChannel();
            int length = (int) fc.size();
            MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0,	length);
            byte[] content = new byte[length];
            buffer.get(content, 0, length);
            String con = new String(content, "gb2312");
            String[] lines = con.split("\n");
            String[] eles;
            List<StockDailyCharge> stockDeilyChargeList = new ArrayList<StockDailyCharge>(2000);
            int lineIndex = 0;
            for(String line : lines){
                if(lineIndex == 0){
                    lineIndex++;
                    continue;
                }
                lineIndex++;
                eles = line.split(",");
                try{
                    StockDailyCharge stockDeilyCharge = new StockDailyCharge();
                    stockDeilyCharge.setADD_DATE(simpleDateFormat.parse(eles[0].trim()));
                    if(!eles[12].trim().equals("None") && !eles[12].trim().isEmpty()){
                        stockDeilyCharge.setCHENG_JIAO_E(eles[12].trim().equals("None") ? "0" : eles[12].trim());
                    }

                    if(!eles[11].trim().equals("None") && !eles[11].trim().isEmpty()){
                        stockDeilyCharge.setCHENG_JIAO_LIANG(eles[11].trim().equals("None") ? "0" : eles[11].trim());
                    }
                    if(!eles[10].trim().equals("None") && !eles[10].trim().isEmpty()){
                        stockDeilyCharge.setHUAN_SHOU_LV(eles[10].trim().equals("None") ? "0" : eles[10].trim());
                    }
                    if(!eles[6].trim().equals("None") && !eles[6].trim().isEmpty()){
                        stockDeilyCharge.setJIN_KAI(eles[6].trim().equals("None") ? "0" : eles[6].trim());
                    }
                    if(!eles[3].trim().equals("None") && !eles[3].trim().isEmpty()){
                        stockDeilyCharge.setZUI_XIN_JIA(eles[3].trim().equals("None") ? "0" : eles[3].trim());
                    }
                    if(!eles[0].trim().equals("None") && !eles[0].trim().isEmpty()){
                        stockDeilyCharge.setREPORT_TIME(eles[0].trim().equals("None") ? "0" : eles[0].trim());
                    }
                    stockDeilyCharge.setSTOCK_CODE(stockCode);
                    if(!eles[2].trim().equals("None") && !eles[2].trim().isEmpty()){
                        stockDeilyCharge.setSTOCK_NAME(eles[2].trim().replace("'",""));
                    }
                    if(!eles[8].trim().equals("None") && !eles[8].trim().isEmpty()){
                        stockDeilyCharge.setZHANG_DIE_E(eles[8].trim().equals("None") ? "0" : eles[8].trim());
                    }

                    if(!eles[9].trim().equals("None") && !eles[9].trim().isEmpty()){
                        stockDeilyCharge.setZHANG_DIE_FU(eles[9].trim().equals("None") ? "0" : eles[9].trim());
                    }
                    stockDeilyCharge.setZIN_JI_JIN_LIU_RU("0");
                    if(!eles[7].trim().equals("None") && !eles[7].trim().isEmpty()){
                        stockDeilyCharge.setZOU_SHOU(eles[7].trim().equals("None") ? "0" : eles[7].trim());
                    }
                    if(!eles[5].trim().equals("None") && !eles[5].trim().isEmpty()){
                        stockDeilyCharge.setZUI_DI_JIA(eles[5].trim().equals("None") ? "0" : eles[5].trim());
                    }
                    if(!eles[4].trim().equals("None") && !eles[4].trim().isEmpty()){
                        stockDeilyCharge.setZUI_GAO_JIA(eles[4].trim().equals("None") ? "0" : eles[4].trim());
                    }
                    dailyChargeDao.merge(stockDeilyCharge);
                }catch(Exception e){
                    if(e instanceof DuplicateKeyException){
                        //TODO为什么不能新建一个catch MySQLIntegrityConstraintViolationException
                    }else{
                        LOG.error("csv异常{}", line, e);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("csv批量插入异常{}", f.getPath(), e.getMessage());
        }

    }
}
