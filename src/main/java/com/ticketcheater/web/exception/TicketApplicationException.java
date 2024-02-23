package com.ticketcheater.web.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketApplicationException extends RuntimeException {

    private ErrorCode code;
    private String message;

    public TicketApplicationException(ErrorCode code) {
        this.code = code;
        this.message = null;
    }

    @Override
    public String getMessage() {
        if (message == null) return code.getMessage();
        else return String.format("%s. %s", code.getMessage(), message);
    }

}
