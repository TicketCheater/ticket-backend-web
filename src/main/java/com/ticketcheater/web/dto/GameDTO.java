package com.ticketcheater.web.dto;

import com.ticketcheater.web.entity.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GameDTO {

    private Long id;
    private String category;
    private String title;
    private String home;
    private String away;
    private String place;
    private LocalDateTime startedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime removedAt;

    public static GameDTO of(String category, String title, String home, String away, String place, LocalDateTime startedAt) {
        return GameDTO.of(null, category, title, home, away, place, startedAt, null, null, null);
    }

    public static GameDTO of(Long id, String category, String title, String home, String away, String place, LocalDateTime startedAt, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime removedAt) {
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
