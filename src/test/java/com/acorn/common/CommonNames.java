package com.acorn.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public enum CommonNames {

    ROLE_USER("ROLE_USER"),
    STATUS_ACTIVE("Active"),
    ACCESS_TOKEN_NAME("accessToken"),
    REFRESH_TOKEN_NAME("refreshToken"),
    HEADER_AUTH("Authorization"),
    HEADER_BEARER("Bearer "),
    ;

    private String name;
}
