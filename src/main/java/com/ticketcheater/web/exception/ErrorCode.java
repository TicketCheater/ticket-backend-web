package com.ticketcheater.web.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DUPLICATED_USERNAME(HttpStatus.CONFLICT, "Duplicated Username"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"),
    INVALID_PASSWORD(HttpStatus.NOT_FOUND, "password is not valid"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Expired token")
    ;

    private final HttpStatus status;
    private final String message;

}
