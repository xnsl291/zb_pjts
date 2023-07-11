package com.example.dividend_info.service;

import com.example.dividend_info.exception.impl.NoCompanyException;
import com.example.dividend_info.model.Company;
import com.example.dividend_info.model.Dividend;
import com.example.dividend_info.model.ScrapedResult;
import com.example.dividend_info.model.constants.CacheKeys;
import com.example.dividend_info.persist.CompanyRepository;
import com.example.dividend_info.persist.DividendRepository;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import persist.entity.CompanyEntity;
import persist.entity.DividendEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {
    private CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key ="#companyName", value = CacheKeys.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName){
        log.info("search company : "+companyName);
        CompanyEntity company = this.companyRepository.findByName(companyName)
                                     .orElseThrow(() -> new NoCompanyException());

        List< DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        List<Dividend> dividends  = dividendEntities.stream()
                                                    .map(e -> new Dividend(e.getDate(),e.getDividend()))
                                                    .collect(Collectors.toList());

        return new ScrapedResult( new Company(company.getTicker(),company.getName())
                                ,dividends);

    }
}
