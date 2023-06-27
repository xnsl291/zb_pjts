package com.example.account.dto;

import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class cancelBalance {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request{
        @NotNull
        private String transactionId;

        @NotBlank
        @Size(min=0, max=10)
        private String accountNumber;

        @NotNull
        @Min(10)
        @Max(10000_000_000L)
        private Long amount;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String accountNumber;
        private TransactionResultType transactionResultType;
        private TransactionType transactionType;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;



        public static Response from(TransactionDto transactionDto)
        {
            return Response.builder()
                    .accountNumber(transactionDto.getAccountNumber())
                    .transactionResultType(transactionDto.getTransactionResultType())
                    .transactionId(transactionDto.getTransactionId())
                    .amount(transactionDto.getAmount())
                    .transactedAt(transactionDto.getTransactedAt())
                    .build();
        }
    }
}
