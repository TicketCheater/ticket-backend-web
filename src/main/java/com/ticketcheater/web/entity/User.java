package com.ticketcheater.web.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(indexes = {
        @Index(columnList = "username", unique = true),
        @Index(columnList = "email", unique = true),
        @Index(columnList = "nickname", unique = true),
        @Index(columnList = "createdAt")
})
@Entity(name = "\"user\"")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username") private String username;

    @Column(name = "password") private String password;

    @Column(name = "email") private String email;

    @Column(name = "nickname") private String nickname;

    @Enumerated(EnumType.STRING) private UserRole role = UserRole.USER;

    public static User of(String username, String password, String email, String nickname) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setNickname(nickname);
        return user;
    }

}
