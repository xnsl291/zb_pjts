package com.zb.weatherApp.error;

public class InvalidDate extends RuntimeException{
    private static final String MSG = "너무 미래입니다";

    public InvalidDate(){
        super(MSG);
    }
}
