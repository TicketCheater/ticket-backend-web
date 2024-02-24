package com.ticketcheater.web.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserReissueResponse {
    private String accessToken;

    public static UserReissueResponse from(String accesToken) {
        return new UserReissueResponse(accesToken);
    }

}
