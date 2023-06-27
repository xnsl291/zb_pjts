package com.example.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_REQUEST("잘못된 요청입니다"),
    USER_NOT_FOUND("사용자 없습니다"),
    ACCOUNT_NOT_FOUND("계좌가 없습니다"),
    TRANSACTION_NOT_FOUND("해당 거래가 없습니다"),
    AMOUNT_EXCEED_BALANCE("거래 금액이 계좌 잔액보다 큽니다"),
    USER_ACCOUNT_UNMATCHED(" 사용자와 계좌의 소유주가 다릅니다"),
    CANCEL_MUST_FULL("부분취소는 불가능합니다"),
    TOO_OLD_TO_CANCEL("1년 이상 지난 거래는 취소가 불가능합니다"),
    TRANSACTION_ACCOUNT_UNMATCHED("이 거래는 해당 계좌에서 발생하지 않았습니다"),
    ACCOUNT_ALREADY_UNREGISTERED("계좌가 이미 해지되었습니다"),
    BALANCE_NOT_EMPTY("잔액이 있는 계좌는 해지할 수 없습니다"),
    MAX_ACCOUNT_PER_USER_ID("사용자가 최대로 가질 수 있는 계좌 수는 10개입니다");
    private final String description;
}
