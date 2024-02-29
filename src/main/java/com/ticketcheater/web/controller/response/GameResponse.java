package com.ticketcheater.web.controller.response;

import com.ticketcheater.web.dto.GameDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GameResponse {

    private Long id;
    private String category;
    private String title;
    private String home;
    private String away;
    private String place;
    private LocalDateTime startedAt;

    public static GameResponse from(GameDTO game) {
        return new GameResponse(
                game.getId(),
                game.getCategory().toString(),
                game.getTitle(),
                game.getHome(),
                game.getAway(),
                game.getPlace(),
                game.getStartedAt()
        );
    }

}
