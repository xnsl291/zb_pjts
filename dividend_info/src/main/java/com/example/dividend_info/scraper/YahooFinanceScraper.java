package com.example.dividend_info.scraper;


import com.example.dividend_info.model.Company;
import com.example.dividend_info.model.Dividend;
import com.example.dividend_info.model.ScrapedResult;
import com.example.dividend_info.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper{
    private static final String STATISTIC_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400;

    @Override
    public ScrapedResult scrap(Company company){
        var scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000  ; //milliSec -> second

            String url = String.format(STATISTIC_URL, company.getTicker(), START_TIME, now );
            Connection conn = Jsoup.connect(url);
            Document document = conn.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test","historical-prices");

            Element tableElement = parsingDivs.get(0);

            Element tbody = tableElement.children().get(1); //thead:0 , tbody:1, tfoot:2

            List<Dividend> dividends = new ArrayList<>();

            for(Element e: tbody.children())
            {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }
                String[] splits = txt.split(" ");
                int month = Month.str2Num(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",",""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month <0 )
                    throw new RuntimeException("Unexpected Month enum value -> "+splits[0]);

                dividends.add( new Dividend(LocalDateTime.of(year,month,day,0,0) ,dividend ));


            }
            scrapedResult.setDividends(dividends);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return scrapedResult;

    }

    @Override
    public Company scrapCompanyByTicker(String ticker){
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleElement = document.getElementsByTag("h1").get(0);
            String title = titleElement.text().split(" - ")[1].trim();

            return new Company(ticker,title);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
