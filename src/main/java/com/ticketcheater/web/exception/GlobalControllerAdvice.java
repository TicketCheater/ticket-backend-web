package com.ticketcheater.web.exception;

import com.ticketcheater.web.controller.response.Response;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(TicketApplicationException.class)
    public ResponseEntity<?> errorHandler(TicketApplicationException e) {
        log.error("Error occurs {}", e.toString());
        return ResponseEntity.status(e.getCode().getStatus())
                .body(Response.error(e.getCode().name()));
    }

}
