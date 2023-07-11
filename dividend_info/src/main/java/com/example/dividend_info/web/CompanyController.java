package com.example.dividend_info.web;

import com.example.dividend_info.model.Company;
import com.example.dividend_info.model.constants.CacheKeys;
import com.example.dividend_info.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import persist.entity.CompanyEntity;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final CacheManager redisCacheManager;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword){
        var result = this.companyService.getCompanyNameByKeyword(keyword);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('READ')")
    @GetMapping
    public ResponseEntity<?> searchCompany(final Pageable pageable){
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }

    @PostMapping
    @PreAuthorize("hasRole('READWRITE')")
    public ResponseEntity<?> addCompany(@RequestParam Company request){
        String ticker = request.getTicker().trim();

        if(ObjectUtils.isEmpty(ticker))
            throw new RuntimeException("Ticker is Empty ");

        Company company = this.companyService.save(ticker);
        this.companyService.addAutocompleteKeyword(company.getName());
        return ResponseEntity.ok(company);
    }

    @PreAuthorize("hasRole('READWRITE')")
    @DeleteMapping("/{ticker}")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker){
        String companyName = this.companyService.deleteCompany(ticker);
        this.cleaerFinanceCache(companyName);
        return ResponseEntity.ok(companyName);
    }

    public void cleaerFinanceCache(String companyName){
        this.redisCacheManager.getCache(CacheKeys.KEY_FINANCE).evict(companyName);
    }
}
