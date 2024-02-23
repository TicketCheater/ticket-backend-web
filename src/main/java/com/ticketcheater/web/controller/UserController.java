package com.ticketcheater.web.controller;

import com.ticketcheater.web.controller.request.UserLoginRequest;
import com.ticketcheater.web.controller.request.UserReissueRequest;
import com.ticketcheater.web.controller.request.UserSignupRequest;
import com.ticketcheater.web.controller.response.Response;
import com.ticketcheater.web.controller.response.UserLoginResponse;
import com.ticketcheater.web.controller.response.UserReissueResponse;
import com.ticketcheater.web.controller.response.UserSignupResponse;
import com.ticketcheater.web.jwt.JwtTokenProvider;
import com.ticketcheater.web.jwt.TokenDTO;
import com.ticketcheater.web.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public Response<UserSignupResponse> signup(@RequestBody UserSignupRequest request) {
        return Response.success(UserSignupResponse.from(userService.signup(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getNickname()
        )));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        TokenDTO token = userService.login(request.getUsername(), request.getPassword());
        return Response.success(new UserLoginResponse(token.accessToken(), token.refreshToken()));
    }

    @PostMapping("/reissue")
    public Response<UserReissueResponse> reissue(@RequestBody UserReissueRequest request, Authentication authentication) {
        return Response.success(UserReissueResponse.from(jwtTokenProvider.reissueAccessToken(
                authentication.getName(),
                request.getRefreshToken()
        )));
    }

    @PostMapping("/logout")
    public Response<Void> logout(Authentication authentication) {
        userService.logout(authentication.getName());
        return Response.success();
    }

}
