package com.ticketcheater.web.fixture;

import com.ticketcheater.web.dto.GameDTO;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

public class GameDTOFixture {

    public static GameDTO get(String category) {
        return GameDTO.of(
                category,
                "title",
                "home",
                "away",
                "place",
                Timestamp.from(Instant.now())
        );
    }
}
