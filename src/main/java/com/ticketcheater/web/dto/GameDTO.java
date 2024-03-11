package com.ticketcheater.web.dto;

import com.ticketcheater.web.entity.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class GameDTO {

    private Long id;
    private String category;
    private String title;
    private String home;
    private String away;
    private String place;
    private Timestamp startedAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp removedAt;

    public static GameDTO of(String category, String title, String home, String away, String place, Timestamp startedAt) {
        return GameDTO.of(null, category, title, home, away, place, startedAt, null, null, null);
    }

    public static GameDTO of(Long id, String category, String title, String home, String away, String place, Timestamp startedAt, Timestamp createdAt, Timestamp updatedAt, Timestamp removedAt) {
        return new GameDTO(id, category, title, home, away, place, startedAt, createdAt, updatedAt, removedAt);
    }

    public static GameDTO from(Game game) {
        return new GameDTO(
                game.getId(),
                game.getCategory().toString(),
                game.getTitle(),
                game.getHome(),
                game.getAway(),
                game.getPlace(),
                game.getStartedAt(),
                game.getCreatedAt(),
                game.getUpdatedAt(),
                game.getRemovedAt()
        );
    }

}
