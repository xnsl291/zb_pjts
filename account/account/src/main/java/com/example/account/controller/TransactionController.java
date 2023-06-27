package com.example.account.controller;

import com.example.account.dto.QueryTransactionResponse;
import com.example.account.dto.UseBalance;
import com.example.account.dto.cancelBalance;
import com.example.account.exception.AccountException;
import com.example.account.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 잔액 관련 컨트롤러
 * 1. 잔액사용
 * 2. 잔액 사용 취소
 * 3. 거래 확인
 */


@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    @PostMapping("/transaction/use")
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request request)
    {
        try {
            return UseBalance.Response.from(transactionService.useBalance(request.getUserId(),
                    request.getAccountNumber(), request.getAmount()));
        }
        catch(AccountException e)
        {
            log.error("fail to use balance");
            transactionService.saveFailedUseTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;

        }
    }

    @PostMapping("/transaction/cancel")
    public cancelBalance.Response cancelBalance(
            @Valid @RequestBody cancelBalance.Request request)
    {
        try {
            return cancelBalance.Response.from(
                    transactionService.cancelBalance(request.getTransactionId(),
                    request.getAccountNumber(), request.getAmount()));
        }
        catch(AccountException e)
        {
            log.error("fail to use balance");
            transactionService.saveFailedCancelTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            throw e;

        }
    }

    @GetMapping("transaction/{transactionId}")
    public QueryTransactionResponse queryTransaction(
        @PathVariable String transactionId){
    return QueryTransactionResponse.from(transactionService.queryTransaction(transactionId));


    }


}
