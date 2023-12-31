package com.example.account.dto;

import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryTransactionResponse
{
        private TransactionType transactionType;
        private String accountNumber;
        private TransactionResultType transactionResultType;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;


        public static QueryTransactionResponse from(TransactionDto transactionDto)
        {
            return QueryTransactionResponse.builder()
                    .transactionType(transactionDto.getTransactionType())
                    .accountNumber(transactionDto.getAccountNumber())
                    .transactionResultType(transactionDto.getTransactionResultType())
                    .transactionId(transactionDto.getTransactionId())
                    .amount(transactionDto.getAmount())
                    .transactedAt(transactionDto.getTransactedAt())
                    .build();
        }
}