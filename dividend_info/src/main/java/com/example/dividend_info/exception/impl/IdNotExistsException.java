package com.example.dividend_info.exception.impl;

import com.example.dividend_info.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class IdNotExistsException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "Id is not exists";
    }
}
