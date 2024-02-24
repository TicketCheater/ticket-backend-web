package com.ticketcheater.web.fixture;

import com.ticketcheater.web.entity.User;
import com.ticketcheater.web.entity.UserRole;

public class UserFixture {

    public static User get(String username, String password, String email, String nickname) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setNickname(nickname);
        user.setRole(UserRole.USER);
        return user;
    }

}
