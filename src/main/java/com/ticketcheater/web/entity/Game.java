package com.ticketcheater.web.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Table(indexes = {
        @Index(columnList = "category"),
        @Index(columnList = "started_at")
})
@Entity(name = "\"game\"")
public class Game extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) private Category category;

    @Column(name = "title") private String title;

    @Column(name = "home") private String home;

    @Column(name = "away") private String away;

    @Column(name = "place") private String place;

    @Column(name = "started_at") private Timestamp startedAt;

    public static Game of(Category category, String title, String home, String away, String place, Timestamp startedAt) {
        Game game = new Game();
        game.setCategory(category);
        game.setTitle(title);
        game.setHome(home);
        game.setAway(away);
        game.setPlace(place);
        game.setStartedAt(startedAt);
        return game;
    }

}
