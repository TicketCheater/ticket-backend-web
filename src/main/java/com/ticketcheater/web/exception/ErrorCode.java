package com.ticketcheater.web.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DUPLICATED_USERNAME(HttpStatus.CONFLICT, "Duplicated Username"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"),
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "Game not founded"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found"),
    INVALID_PASSWORD(HttpStatus.NOT_FOUND, "password is not valid"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Expired token"),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "User has invalid permission"),
    ;

    private final HttpStatus status;
    private final String message;

}
