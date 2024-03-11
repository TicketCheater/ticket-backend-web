package com.ticketcheater.web.service;

import com.ticketcheater.web.config.TestContainerConfig;
import com.ticketcheater.web.exception.ErrorCode;
import com.ticketcheater.web.exception.TicketApplicationException;
import com.ticketcheater.web.fixture.UserFixture;
import com.ticketcheater.web.repository.UserCacheRepository;
import com.ticketcheater.web.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("비즈니스 로직 - 회원")
@ExtendWith(TestContainerConfig.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService sut;

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserCacheRepository userCacheRepository;

    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @DisplayName("회원가입이 정상동작한다")
    @Test
    void givenUser_whenSignup_thenSavesUser() {
        String username = "username";
        String password = "password";
        String email = "email";
        String nickname = "nickname";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(password)).thenReturn("password_encrypt");
        when(userRepository.save(any()))
                .thenReturn(UserFixture.get(
                        username,
                        "password_encrypt",
                        email,
                        nickname
                ));

        Assertions.assertDoesNotThrow(() -> sut.signup(username, password, email, nickname));
    }

    @DisplayName("중복된 회원정보로 회원가입할 경우 오류를 내뱉는다")
    @Test
    void givenDuplicatedUser_whenSignup_thenThrowsError() {
        String username = "username";
        String password = "password";
        String email = "email";
        String nickname = "nickname";

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(UserFixture.get(username, password, email, nickname)));

        TicketApplicationException exception = Assertions.assertThrows(TicketApplicationException.class,
                () -> sut.signup(username, password, email, nickname));

        Assertions.assertEquals(ErrorCode.DUPLICATED_USERNAME, exception.getCode());
    }

    @DisplayName("로그인이 정상동작한다")
    @Test
    void givenUser_whenLogin_thenReturnsToken() {
        String username = "username";
        String password = "password";
        String email = "email";
        String nickname = "nickname";

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(UserFixture.get(
                        username,
                        "password",
                        email,
                        nickname
                )));
        when(bCryptPasswordEncoder.matches(password, "password")).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> sut.login(username, password));
    }

    @DisplayName("존재하지 않는 유저가 로그인할 경우 오류를 내뱉는다")
    @Test
    void givenNonExistentUser_whenLogin_thenThrowsError() {
        String username = "username";
        String password = "password";

        when(userCacheRepository.getUser(username)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        TicketApplicationException exception = Assertions.assertThrows(TicketApplicationException.class,
                () -> sut.login(username, password));

        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, exception.getCode());
    }

    @DisplayName("다른 비밀번호를 입력한 유저가 로그인할 경우 오류를 내뱉는다")
    @Test
    void givenWrongPasswordUser_whenLogin_thenThrowsError() {
        String username = "username";
        String password = "password";
        String wrongPassword = "password1";
        String email = "email";
        String nickname = "nickname";

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(UserFixture.get(
                        username,
                        wrongPassword,
                        email,
                        nickname
                )));
        when(bCryptPasswordEncoder.matches(password, wrongPassword)).thenReturn(false);

        TicketApplicationException exception = Assertions.assertThrows(TicketApplicationException.class,
                () -> sut.login(username, password));

        Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, exception.getCode());
    }

}
