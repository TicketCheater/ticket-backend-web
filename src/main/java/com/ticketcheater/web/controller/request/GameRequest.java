package com.ticketcheater.web.controller.request;

import com.ticketcheater.web.dto.GameDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class GameRequest {
    private String category;
    private String title;
    private String home;
    private String away;
    private String place;
    private Timestamp startedAt;

    public GameDTO toDto() {
        return GameDTO.of(category, title, home, away, place, startedAt);
    }

}
