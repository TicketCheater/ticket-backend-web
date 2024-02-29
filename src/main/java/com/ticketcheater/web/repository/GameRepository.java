package com.ticketcheater.web.repository;

import com.ticketcheater.web.entity.Category;
import com.ticketcheater.web.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    Page<Game> findAllByCategory(Pageable pageable, Category category);
}
