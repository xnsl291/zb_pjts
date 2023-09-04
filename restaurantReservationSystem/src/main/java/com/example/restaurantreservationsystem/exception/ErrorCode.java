package com.example.restaurantreservationsystem.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("사용자가 존재하지 않습니다"),
    RESTAURANT_NOT_FOUND("해당 식당이 존재하지 않습니다"),
    BUSINESS_EXISTS("사업이 모두 삭제되지 않았습니다."),
    RESERVATION_NOT_FOUND("예약이 존재하지 않습니다"),
    INVALID_TIME("시간의 형식이 잘못되었습니다"),
    PASSWORD_UNMATCHED("비밀번호가 일치하지 않습니다"),
    NO_AUTHORITY("권한이 없습니다"),
    DUPLICATED_USER_ID("중복된 유저아이디가 존재합니다");

    private final String description;

}
