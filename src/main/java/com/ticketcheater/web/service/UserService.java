package com.ticketcheater.web.service;

import com.ticketcheater.web.dto.UserDTO;
import com.ticketcheater.web.entity.User;
import com.ticketcheater.web.exception.ErrorCode;
import com.ticketcheater.web.exception.TicketApplicationException;
import com.ticketcheater.web.jwt.JwtTokenProvider;
import com.ticketcheater.web.jwt.TokenDTO;
import com.ticketcheater.web.repository.UserCacheRepository;
import com.ticketcheater.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserCacheRepository userCacheRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserDTO signup(String username, String password, String email, String nickname) {
        userRepository.findByUsername(username).ifPresent(it -> {
            throw new TicketApplicationException(
                ErrorCode.DUPLICATED_USERNAME, String.format("Username is %s", username));
        });

        User user = userRepository.save(User.of(username, encoder.encode(password), email, nickname));
        return UserDTO.from(user);
    }

    public UserDTO loadUserByUsername(String username) throws UsernameNotFoundException {
        return userCacheRepository.getUser(username).orElseGet(
                () -> userRepository.findByUsername(username).map(UserDTO::from).orElseThrow(
                        () -> new TicketApplicationException(ErrorCode.USER_NOT_FOUND, String.format("username is %s", username))
        ));
    }

    public TokenDTO login(String username, String password) {
        UserDTO savedUser = loadUserByUsername(username);
        userCacheRepository.setUser(savedUser);
        if (!encoder.matches(password, savedUser.getPassword())) {
            throw new TicketApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(username);
        String refreshToken = jwtTokenProvider.generateRefreshToken(username);

        return TokenDTO.of(accessToken, refreshToken);
    }

    public void logout(String username) {
        jwtTokenProvider.deleteRefreshToken(username);
    }

}
