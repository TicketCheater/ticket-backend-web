package com.ticketcheater.web.service;

import com.ticketcheater.web.dto.GameDTO;
import com.ticketcheater.web.entity.Category;
import com.ticketcheater.web.entity.Game;
import com.ticketcheater.web.entity.User;
import com.ticketcheater.web.entity.UserRole;
import com.ticketcheater.web.exception.ErrorCode;
import com.ticketcheater.web.exception.TicketApplicationException;
import com.ticketcheater.web.repository.GameRepository;
import com.ticketcheater.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createGame(String username, GameDTO dto) {
        checkAdmin(username);
        gameRepository.save(
                Game.of(
                        Category.fromString(dto.getCategory()),
                        dto.getTitle(),
                        dto.getHome(),
                        dto.getAway(),
                        dto.getPlace(),
                        dto.getStartedAt()
                )
        );
    }

    @Transactional(readOnly = true)
    public Page<GameDTO> getGames(Pageable pageable) {
        return gameRepository.findAll(pageable)
                .map(GameDTO::from);
    }

    @Transactional(readOnly = true)
    public Page<GameDTO> getGamesByCategory(String category, Pageable pageable) {
        return gameRepository.findAllByCategory(pageable, Category.fromString(category))
                .map(GameDTO::from);
    }

    @Transactional
    public void updateGame(String username, Long gameId, GameDTO dto) {
        checkAdmin(username);

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new TicketApplicationException(ErrorCode.GAME_NOT_FOUND));

        game.setCategory(Category.fromString(dto.getCategory()));
        game.setTitle(dto.getTitle());
        game.setHome(dto.getHome());
        game.setAway(dto.getAway());
        game.setPlace(dto.getPlace());
        game.setStartedAt(dto.getStartedAt());

        gameRepository.saveAndFlush(game);
    }

    private void checkAdmin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new TicketApplicationException(ErrorCode.USER_NOT_FOUND, String.format("username is %s", username)));
        if (!user.getRole().equals(UserRole.ADMIN)) {
            throw new TicketApplicationException(ErrorCode.INVALID_PERMISSION);
        }
    }

}
