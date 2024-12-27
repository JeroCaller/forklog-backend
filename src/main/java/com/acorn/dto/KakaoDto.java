package com.acorn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@ToString
public class KakaoDto {

    @Data
    public static class OAuthToken {
        @JsonProperty("token_type")
        private final String tokenType = "bearer";

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("expires_in")
        private Integer expiresIn;

        @JsonProperty("refresh_token")
        private String refreshToken;

        @JsonProperty("refresh_token_expires_in")
        private Integer refreshTokenExpiresIn;
    }

    @Data
    public static class KakaoAccount {
    	private String name;
        private String email;
        private String ageRange;
        private String birthyear;
        private String gender;
    }
}