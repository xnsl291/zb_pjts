package com.example.dividend_info.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class ErrorResponse {
    private int code;
    private String message;
}
