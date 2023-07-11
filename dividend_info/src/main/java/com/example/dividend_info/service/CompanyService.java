package com.example.dividend_info.service;

import com.example.dividend_info.exception.impl.NoCompanyException;
import com.example.dividend_info.model.Company;
import com.example.dividend_info.model.ScrapedResult;
import com.example.dividend_info.persist.CompanyRepository;
import com.example.dividend_info.persist.DividendRepository;
import com.example.dividend_info.scraper.Scraper;
import lombok.AllArgsConstructor;

import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import persist.entity.CompanyEntity;
import persist.entity.DividendEntity;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Trie trie;

    public Company save(String ticker){
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if(exists)
            throw new RuntimeException("already exist "+ticker);
        return this.storeCompanyNDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable){
        return this.companyRepository.findAll(pageable);
    }
    private Company storeCompanyNDividend(String ticker){
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);

        if(ObjectUtils.isEmpty(company))
            throw new RuntimeException("failed to scrap ticker "+ ticker);

        ScrapedResult result = this.yahooFinanceScraper.scrap(company);

        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = result.getDividends().stream()
                                                        .map(e -> new DividendEntity(companyEntity.getId(),e))
                                                        .collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntities);
        return company;
    }

    public void addAutocompleteKeyword(String keyword){
        this.trie.put(keyword, null);
    }

    public List<String> autocomplete(String keyword){
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    public void deleteAutocompleteKeyword(String keyword){
        this.trie.remove(keyword);
    }

    public List<String> getCompanyNameByKeyword(String keyword){
        Pageable limit = PageRequest.of(0,10);
        Page<CompanyEntity> companyEntities= this.companyRepository.findByNameStartingWithIgnoreCase(keyword,limit);
        return companyEntities.stream()
                .map(e -> e.getName()).collect(Collectors.toList());
    }

    public String deleteCompany(String ticker){
        var company = this.companyRepository.findByTicker(ticker)
                        .orElseThrow(() -> new NoCompanyException());

        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);

        this.deleteAutocompleteKeyword(company.getName());
        return company.getName();
    }
}
