package com.example.dividend_info.scraper;


import com.example.dividend_info.model.Company;
import com.example.dividend_info.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company) ;
}
