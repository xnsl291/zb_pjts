package com.example.dividend_info.scheduler;

import com.example.dividend_info.model.Company;
import com.example.dividend_info.model.ScrapedResult;
import com.example.dividend_info.model.constants.CacheKeys;
import com.example.dividend_info.persist.CompanyRepository;
import com.example.dividend_info.persist.DividendRepository;
import com.example.dividend_info.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import persist.entity.CompanyEntity;
import persist.entity.DividendEntity;

import java.util.List;

@EnableCaching
@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private Scraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;

    @CacheEvict(value = CacheKeys.KEY_FINANCE, allEntries = true )
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling(){
        List<CompanyEntity> companies = this.companyRepository.findAll();

        for (var company: companies){
            log.info("scraping scheduler is started : "+company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap( new Company(company.getName(), company.getTicker()));

            scrapedResult.getDividends().stream()
                    .map(e-> new DividendEntity(company.getId(), e))
                    .forEach(e-> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists)
                            this.dividendRepository.save(e);
                    });
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
