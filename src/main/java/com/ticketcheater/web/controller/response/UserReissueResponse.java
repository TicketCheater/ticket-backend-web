package com.ticketcheater.web.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserReissueResponse {
    private String accessToken;

    public static UserReissueResponse from(String accessToken) {
        return new UserReissueResponse(accessToken);
    }

}
