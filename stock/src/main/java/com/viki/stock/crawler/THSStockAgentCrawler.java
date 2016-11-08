package com.viki.stock.crawler;

import com.viki.stock.config.JsoupUtil;
import com.viki.stock.config.SysConfig;
import com.viki.stock.dao.StockAgentDao;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */
public class THSStockAgentCrawler {

    private static final Logger LOG = LoggerFactory.getLogger(THSStockAgentCrawler.class.getSimpleName());

    public String page_size = "50";

    public String recent_acvitive_url_formatter = SysConfig.getProperty("stock.agent.recent.acvitive.url.formatter");

    public String agent_charge_history_url_formatter = SysConfig.getProperty("stock.agent.charge.history.url.formatter");

    public String agent_index_url_formatter = SysConfig.getProperty("stock.agent.index.url.formatter");

    @Autowired
    @Qualifier("StockAgentDao")
    private StockAgentDao stockAgentDao;

    public void runInTotal(){
        try{
            int page_index = 1;
            HashMap<String, String> documentMapGetting = new HashMap<>();
            HashMap<String, String> documentMapNotYet = new HashMap<>();
            HashMap<String, String> totalDocumentMap = new HashMap<>();
            while(true){
                try{
                    Document doc = JsoupUtil.crawlPage(String.format(recent_acvitive_url_formatter, page_size, page_index));
                    Elements eles = doc.select(SysConfig.getProperty("stock.agent.recent.active.elements.selector"));
                    if(eles == null || eles.size() == 0){
                        break;
                    }
                    for(Element e : eles){
                        String org_code = "";
                        if(StringUtils.isNotBlank(org_code = e.attr("href"))){
                            org_code = org_code.split("\\/")[org_code.split("\\/").length - 1];
                            documentMapGetting.put(org_code, e.text());
                        }else{
                            continue;
                        }
                    }
                }catch (Exception e){
                    LOG.error("", e);
                }finally {
                    page_index++;
                }
            }
           do{
                totalDocumentMap.putAll(documentMapGetting);
               if(documentMapNotYet.size() > 0){
                   documentMapGetting.clear();
                   documentMapGetting.putAll(documentMapNotYet);
                   totalDocumentMap.putAll(documentMapGetting);
                   documentMapNotYet.clear();
               }

                for(String org_code : documentMapGetting.keySet()){
                    //获取协同营业部基本数据
                    Document docMain = JsoupUtil.crawlPage(String.format(agent_index_url_formatter, org_code));
                    Elements coAgentList = docMain.select(SysConfig.getProperty("stock.agent.index.associate.selector"));
                    for(Element coa : coAgentList){
                        String agentCode = coa.attr("href").split("\\/")[coa.attr("href").split("\\/").length - 1];
                        String agentName = coa.text();
                        if(!totalDocumentMap.containsKey(agentCode)){
                            documentMapNotYet.put(agentCode, agentName);
                        }
                    }
                }
            } while(documentMapNotYet.size() > 0);

            //获取交易详情
            HashMap<String,Object> baseInfoMap = new HashMap<>();

            List<HashMap<String, Object>> records = new ArrayList<HashMap<String, Object>>();
            for(String org_code : totalDocumentMap.keySet()){
                int page_idx = 1;
                while(true){
                    Document doc = JsoupUtil.crawlPage(String.format(agent_charge_history_url_formatter, org_code, page_idx));
                    List<HashMap<String, Object>> recordsPerPage = parseSingleAgentChargeHistory(doc, org_code);
                    if(recordsPerPage.size() <= 0){
                        break;
                    }
                    records.addAll(recordsPerPage);
                    if(records.size() >= 1000){
                        try{
                            baseInfoMap.put("chargeList", records);
                            stockAgentDao.insert(baseInfoMap);
                            records.clear();
                            baseInfoMap.clear();

                        }catch (Exception e){e.printStackTrace();}
                    }
                    page_idx ++ ;
                }
            }

            baseInfoMap.clear();
            baseInfoMap.put("totalAgentInfo", totalDocumentMap);
            stockAgentDao.merge(baseInfoMap);

            if(records.size() > 0){
                baseInfoMap.put("chargeList", records);
                stockAgentDao.insert(baseInfoMap);
                records.clear();
                baseInfoMap.clear();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd");
    private List<HashMap<String, Object>> parseSingleAgentChargeHistory(Document doc, String org_code){
        Elements eles = doc.select("div.m_border > table > tbody > tr");
        List<HashMap<String, Object>> records = new ArrayList<HashMap<String, Object>>();
        if(eles == null || eles.size() == 0){
            return records;
        }
        for(Element e : eles){
            try{
                Elements tds = e.select("td");
                HashMap<String, Object> record = new HashMap<>();
                record.put("agent_code", org_code);
                record.put("charge_date", simpleDateFormat.parse(tds.get(0).text()));
                record.put("stock_name", tds.get(1).text());
                record.put("stock_feature", tds.get(2).text());
                record.put("buy_or_sell", tds.get(4).text());
                record.put("stock_amount", Float.parseFloat(tds.get(5).text()) * 100);
                record.put("stock_sum", Float.parseFloat(tds.get(6).text()) * 10000);
                record.put("stock_code", tds.get(9).select("a").attr("kstock"));
                records.add(record);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return records;
    }

    private void getChargePerAgent(String org_code){

    }

    public String rank_daily_formatter = SysConfig.getProperty("stock.agent.charge.daily.formatter");

    public void runPerday(){
        List<HashMap<String, Object>> records = new ArrayList<HashMap<String, Object>>();
        List<HashMap> stockAgentList = stockAgentDao.query(new HashMap());
        HashMap<String,Object> baseInfoMap = new HashMap<>();
        for(HashMap<String,Object> agent : stockAgentList){
            try{
                String org_code = agent.get("agent_code").toString();
                Document doc = JsoupUtil.crawlPage(String.format(agent_charge_history_url_formatter, org_code, 1));
                List<HashMap<String, Object>> recordsPerPage = parseSingleAgentChargeHistory(doc, org_code);
                if(recordsPerPage.size() <= 0){
                    break;
                }
                records.addAll(recordsPerPage);
                if(records.size() >= 1000){
                    try{
                        baseInfoMap.put("chargeList", records);
                        stockAgentDao.insert(baseInfoMap);
                        records.clear();
                        baseInfoMap.clear();

                    }catch (Exception e){e.printStackTrace();}
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(records.size() > 0){
            baseInfoMap.put("chargeList", records);
            stockAgentDao.insert(baseInfoMap);
            records.clear();
            baseInfoMap.clear();
        }
    }

    public static void main(String[] args) {
        new THSStockAgentCrawler().runInTotal();
    }
}
