package com.acorn.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum HttpHeaderNames {

    HEADER_AUTH("Authorization"),
    HEADER_BEARER("Bearer ")
    ;

    private String name;
}
