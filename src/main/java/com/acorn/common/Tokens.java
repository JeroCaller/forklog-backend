package com.acorn.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public enum Tokens {

    ACCESS_TOKEN("accessToken", 60 * 60),  // 1h
    REFRESH_TOKEN("refreshToken", 7 * 24 * 60 * 60)  // 7 days
    ;

    private String tokenName;
    private int maxAgeInSeconds;
}
