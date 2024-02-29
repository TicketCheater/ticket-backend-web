package com.ticketcheater.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ticketcheater.web.entity.User;
import com.ticketcheater.web.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String email;
    private String nickname;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime removedAt;

    public static UserDTO from(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getRemovedAt()
        );
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return removedAt == null;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return removedAt == null;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return removedAt == null;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return removedAt == null;
    }

}
