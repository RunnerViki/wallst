package com.viki.stock.crawler;

import com.viki.stock.bean.HugeChargeCrarge;
import com.viki.stock.config.JsoupUtil;
import com.viki.stock.config.SysConfig;
import com.viki.stock.dao.HugeChargeDao;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/11/6.
 */
public class SnHugeChargeCrawler {

    private String huge_charge_url_formatter = SysConfig.getProperty("huge.charge.url.formatter");

    @Autowired
    @Qualifier("HugeChargeDao")
    private HugeChargeDao hugeChargeDao;

    public void run(){
        if(!isNeedFlushGlobal()){
            return;
        }
        int page = 1;
        while(true){
            try{
                Document document = JsoupUtil.crawlPage(String.format(huge_charge_url_formatter, page));
                Elements elements = document.select("#dataTable > tbody > tr");
                if(elements == null || elements.size() == 0){
                    break;
                }
                for(Element element : elements){
                    Elements tds = element.select("td");
                    HugeChargeCrarge hugeChargeCrarge = new HugeChargeCrarge();
                    hugeChargeCrarge.setDate(tds.get(0).text());
                    hugeChargeCrarge.setStockCode(tds.get(1).text());
                    hugeChargeCrarge.setStockShort(tds.get(2).text());
                    hugeChargeCrarge.setBidPrice(new BigDecimal(tds.get(3).text()));
                    hugeChargeCrarge.setVolume(new BigDecimal(tds.get(4).text().replace("%", "")).multiply(new BigDecimal(10000)));
                    //hugeChargeCrarge.setClosingPrice(new BigDecimal(tds.get(4).text()));
                    //hugeChargeCrarge.setPremiumRate(new BigDecimal(tds.get(7).text().replace("%", "")));
                    hugeChargeCrarge.setBuyer(tds.get(6).text());
                    hugeChargeCrarge.setSaler(tds.get(7).text());
                    hugeChargeDao.insert(hugeChargeCrarge);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                page ++;
            }
        }
    }

    public boolean isNeedFlushGlobal(){
        HashMap<String,Object> params = new HashMap<>();
        params.put("limit_count", 1);
        List<HugeChargeCrarge> hugeChargeCrargeList = hugeChargeDao.query(params);
        if(hugeChargeCrargeList != null && hugeChargeCrargeList.size() > 0){
            return false;
        }
        return true;
    }
}
