package com.ticketcheater.web.entity;

import com.ticketcheater.web.exception.ErrorCode;
import com.ticketcheater.web.exception.TicketApplicationException;

public enum Category {
    BASEBALL,
    SOCCER,
    BASKETBALL,
    VOLLEYBALL,
    E_SPORTS;

    public static Category fromString(String param) {
        try {
            return Category.valueOf(param.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TicketApplicationException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

}
