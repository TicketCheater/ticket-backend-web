package com.ticketcheater.web.service;

import com.ticketcheater.web.dto.GameDTO;
import com.ticketcheater.web.entity.Category;
import com.ticketcheater.web.entity.Game;
import com.ticketcheater.web.entity.UserRole;
import com.ticketcheater.web.exception.ErrorCode;
import com.ticketcheater.web.exception.TicketApplicationException;
import com.ticketcheater.web.fixture.GameDTOFixture;
import com.ticketcheater.web.fixture.UserFixture;
import com.ticketcheater.web.repository.GameRepository;
import com.ticketcheater.web.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("비즈니스 로직 - 게임")
@SpringBootTest
class GameServiceTest {

    @Autowired
    GameService sut;

    @MockBean
    GameRepository gameRepository;

    @MockBean
    UserRepository userRepository;

    @DisplayName("관리자가 안정적으로 게임을 생성한다")
    @Test
    void givenGameInfo_whenCreating_thenSavesGame() {
        String username = "master";
        String category = "e_sports";
        GameDTO dto = GameDTOFixture.get(category);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(UserFixture.get(UserRole.ADMIN)));

        Assertions.assertDoesNotThrow(() -> sut.createGame(username, dto));
    }

    @DisplayName("관리자가 아닌 유저가 게임을 생성할 경우 오류를 내뱉는다")
    @Test
    void givenNonAdminUser_whenCreating_thenThrowsError() {
        String username = "username";
        GameDTO dto = mock(GameDTO.class);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(UserFixture.get(UserRole.USER)));

        TicketApplicationException exception = Assertions.assertThrows(TicketApplicationException.class,
                () -> sut.createGame(username, dto));

        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, exception.getCode());
    }

    @DisplayName("카테고리가 적절하지 않은 게임을 생성할 경우 오류를 내뱉는다")
    @Test
    void givenInvalidCategory_whenCreating_thenThrowsError() {
        String username = "master";
        String category = "wrong";
        GameDTO dto = GameDTOFixture.get(category);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(UserFixture.get(UserRole.ADMIN)));

        TicketApplicationException exception = Assertions.assertThrows(TicketApplicationException.class,
                () -> sut.createGame(username, dto));

        Assertions.assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getCode());
    }

    @DisplayName("게임을 안정적으로 조회한다")
    @Test
    void givenNothing_whenSearching_thenReturnGames() {
        Pageable pageable = mock(Pageable.class);

        when(gameRepository.findAll(pageable)).thenReturn(Page.empty());

        Assertions.assertDoesNotThrow(() -> sut.getGames(pageable));
    }

    @DisplayName("카테고리가 적절한 게임을 안정적으로 조회한다")
    @Test
    void givenValidCategory_whenSearching_thenReturnGames() {
        String category = "e_sports";
        Pageable pageable = mock(Pageable.class);

        when(gameRepository.findAllByCategory(pageable, Category.fromString(category))).thenReturn(Page.empty());

        Assertions.assertDoesNotThrow(() -> sut.getGamesByCategory(category, pageable));
    }

    @DisplayName("카테고리가 적절하지 않은 게임을 조회할 경우 오류를 내뱉는다")
    @Test
    void givenInvalidCategory_whenSearching_thenThrowsError() {
        String wrongCategory = "wrong";
        Pageable pageable = mock(Pageable.class);

        TicketApplicationException exception = Assertions.assertThrows(TicketApplicationException.class,
                () -> sut.getGamesByCategory(wrongCategory, pageable));

        Assertions.assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getCode());
    }

    @DisplayName("관리자가 안정적으로 게임을 수정한다")
    @Test
    void givenGameInfo_whenUpdating_thenUpdatesGame() {
        String username = "master";
        Long gameId = 1L;
        String category = "e_sports";
        GameDTO dto = GameDTOFixture.get(category);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(UserFixture.get(UserRole.ADMIN)));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(mock(Game.class)));

        Assertions.assertDoesNotThrow(() -> sut.updateGame(username, gameId, dto));
    }

    @DisplayName("관리자가 아닌 유저가 게임을 수정할 경우 오류를 내뱉는다")
    @Test
    void givenNonAdminUser_whenUpdating_thenThrowsError() {
        String username = "username";
        Long gameId = 1L;
        GameDTO dto = mock(GameDTO.class);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(UserFixture.get(UserRole.USER)));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(mock(Game.class)));

        TicketApplicationException exception = Assertions.assertThrows(TicketApplicationException.class,
                () -> sut.updateGame(username, gameId, dto));

        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, exception.getCode());
    }

    @DisplayName("카테고리가 적절하지 않은 게임을 수정할 경우 오류를 내뱉는다")
    @Test
    void givenInvalidCategory_whenUpdating_thenThrowsError() {
        String username = "username";
        Long gameId = 1L;
        String category = "wrong";
        GameDTO dto = GameDTOFixture.get(category);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(UserFixture.get(UserRole.ADMIN)));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(mock(Game.class)));

        TicketApplicationException exception = Assertions.assertThrows(TicketApplicationException.class,
                () -> sut.updateGame(username, gameId, dto));

        Assertions.assertEquals(ErrorCode.CATEGORY_NOT_FOUND, exception.getCode());
    }

    @DisplayName("DB 에 없는 게임을 수정할 경우 오류를 내뱉는다")
    @Test
    void givenNonExistentGameId_whenUpdating_thenThrowsError() {
        String username = "username";
        Long gameId = 1L;
        GameDTO dto = mock(GameDTO.class);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(UserFixture.get(UserRole.ADMIN)));
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        TicketApplicationException exception = Assertions.assertThrows(TicketApplicationException.class,
                () -> sut.updateGame(username, gameId, dto));

        Assertions.assertEquals(ErrorCode.GAME_NOT_FOUND, exception.getCode());
    }

}
