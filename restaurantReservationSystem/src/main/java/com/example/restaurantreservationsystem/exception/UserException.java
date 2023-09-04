package com.example.restaurantreservationsystem.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserException extends RuntimeException{
    private ErrorCode errorCode;
    private String errorMsg;

    public UserException(ErrorCode errorCode)
    {
        this.errorCode = errorCode;
        this.errorMsg = errorCode.getDescription();
    }
}
