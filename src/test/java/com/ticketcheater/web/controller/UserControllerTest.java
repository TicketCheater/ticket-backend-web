package com.ticketcheater.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketcheater.web.controller.request.UserLoginRequest;
import com.ticketcheater.web.controller.request.UserReissueRequest;
import com.ticketcheater.web.controller.request.UserSignupRequest;
import com.ticketcheater.web.dto.UserDTO;
import com.ticketcheater.web.exception.ErrorCode;
import com.ticketcheater.web.exception.TicketApplicationException;
import com.ticketcheater.web.jwt.JwtTokenProvider;
import com.ticketcheater.web.jwt.TokenDTO;
import com.ticketcheater.web.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("컨트롤러 - 회원")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    UserService userService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("회원가입이 정상동작한다")
    @Test
    @WithAnonymousUser
    void givenUser_whenSignup_thenSavesUser() throws Exception {
        String username = "username";
        String password = "password";
        String email = "email";
        String nickname = "nickname";

        when(userService.signup(username, password, email, nickname)).thenReturn(mock(UserDTO.class));

        mvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserSignupRequest("username", "password", "email", "nickname"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("중복된 회원정보로 회원가입할 경우 오류를 내뱉는다")
    @Test
    @WithAnonymousUser
    void givenDuplicatedUser_whenSignup_thenThrowsError() throws Exception {
        String username = "username";
        String password = "password";
        String email = "email";
        String nickname = "nickname";

        when(userService.signup(username, password, email, nickname)).thenThrow(new TicketApplicationException(ErrorCode.DUPLICATED_USERNAME));

        mvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserSignupRequest("username", "password", "email", "nickname"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DUPLICATED_USERNAME.getStatus().value()));
    }

    @DisplayName("로그인이 정상동작한다")
    @Test
    @WithAnonymousUser
    void givenUser_whenLogin_thenReturnsToken() throws Exception {
        String username = "username";
        String password = "password";

        when(userService.login(username, password)).thenReturn(mock(TokenDTO.class));

        mvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserLoginRequest("username", "password"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("존재하지 않는 유저가 로그인 시 오류를 내뱉는다")
    @Test
    @WithAnonymousUser
    void givenNonExistentUser_whenLogin_thenThrowsError() throws Exception {
        String username = "username";
        String password = "password";

        when(userService.login(username, password)).thenThrow(new TicketApplicationException(ErrorCode.USER_NOT_FOUND));

        mvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserLoginRequest("username", "password"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.USER_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("다른 비밀번호를 입력한 유저가 로그인할 경우 오류를 내뱉는다")
    @Test
    @WithAnonymousUser
    void givenWrongPasswordUser_whenLogin_thenThrowsError() throws Exception {
        String username = "username";
        String password = "password";

        when(userService.login(username, password)).thenThrow(new TicketApplicationException(ErrorCode.INVALID_PASSWORD));

        mvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserLoginRequest("username", "password"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PASSWORD.getStatus().value()));
    }

    @DisplayName("Refresh 토큰을 입력하면 Access 토큰을 재발급한다")
    @Test
    @WithMockUser
    void givenRefreshToken_whenReissue_thenReceivesToken() throws Exception {
        String username = "username";
        String refreshToken = "refresh_token";

        when(jwtTokenProvider.reissueAccessToken(username, refreshToken)).thenReturn("new_token");

        mvc.perform(post("/users/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserReissueRequest("refresh_token"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("로그인하지 않은 유저가 Access 토큰 재발급할 경우 오류를 내뱉는다")
    @Test
    @WithAnonymousUser
    void givenUnauthorizedUser_whenReissue_thenThrowsError() throws Exception {
        mvc.perform(post("/users/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserReissueRequest("refresh_token"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("만료된 Refresh 토큰을 입력하면 Access 토큰을 재발급하지 않는다")
    @Test
    @WithMockUser
    void givenExpiredRefreshToken_whenReissue_thenThrowsError() throws Exception {
        doThrow(new TicketApplicationException(ErrorCode.EXPIRED_TOKEN)).when(jwtTokenProvider).reissueAccessToken(any(), eq("refresh_token"));
        mvc.perform(post("/users/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserReissueRequest("refresh_token"))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.EXPIRED_TOKEN.getStatus().value()));
    }

    @DisplayName("로그아웃이 정상동작한다")
    @Test
    @WithMockUser
    void givenNothing_whenLogout_thenDeletesToken() throws Exception {
        mvc.perform(post("/users/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("로그인하지 않은 유저가 로그아웃할 경우 오류를 내뱉는다")
    @Test
    @WithAnonymousUser
    void givenUnauthorizedUser_whenLogout_thenThrowsError() throws Exception {
        mvc.perform(post("/users/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

}
