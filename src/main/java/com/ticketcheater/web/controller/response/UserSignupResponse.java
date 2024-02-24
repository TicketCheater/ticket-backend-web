package com.ticketcheater.web.controller.response;

import com.ticketcheater.web.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignupResponse {
    private Long id;
    private String username;

    public static UserSignupResponse from(UserDTO user) {
        return new UserSignupResponse(user.getId(), user.getUsername());
    }
}
