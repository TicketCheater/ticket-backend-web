package com.ticketcheater.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketcheater.web.controller.request.GameRequest;
import com.ticketcheater.web.exception.ErrorCode;
import com.ticketcheater.web.exception.TicketApplicationException;
import com.ticketcheater.web.service.GameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("컨트롤러 - 게임")
@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("관리자가 안정적으로 게임을 생성한다")
    @Test
    @WithMockUser
    void givenGameInfo_whenCreating_thenSavesGame() throws Exception {
        doNothing().when(gameService).createGame(any(), any());

        mvc.perform(post("/admin/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameRequest("e_sports", "title", "home", "away", "place", Timestamp.from(Instant.now())))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("로그인하지 않은 유저가 게임을 생성할 경우 오류를 내뱉는다")
    @Test
    @WithAnonymousUser
    void givenUnauthorizedUser_whenCreating_thenThrowsError() throws Exception {
        doThrow(new TicketApplicationException(ErrorCode.INVALID_TOKEN)).when(gameService).createGame(any(), any());

        mvc.perform(post("/admin/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameRequest("e_sports", "title", "home", "away", "place", Timestamp.from(Instant.now())))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("관리자가 아닌 유저가 게임을 생성할 경우 오류를 내뱉는다")
    @Test
    @WithMockUser
    void givenNonAdminUser_whenCreating_thenThrowsError() throws Exception {
        doThrow(new TicketApplicationException(ErrorCode.INVALID_PERMISSION)).when(gameService).createGame(any(), any());

        mvc.perform(post("/admin/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameRequest("e_sports", "title", "home", "away", "place", Timestamp.from(Instant.now())))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @DisplayName("카테고리가 적절하지 않은 게임을 생성할 경우 오류를 내뱉는다")
    @Test
    @WithMockUser
    void givenInvalidCategory_whenCreating_thenThrowsError() throws Exception {
        doThrow(new TicketApplicationException(ErrorCode.CATEGORY_NOT_FOUND)).when(gameService).createGame(any(), any());

        mvc.perform(post("/admin/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameRequest("wrong", "title", "home", "away", "place", Timestamp.from(Instant.now())))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.CATEGORY_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("게임을 안정적으로 조회한다")
    @Test
    @WithMockUser
    void givenNothing_whenSearching_thenReturnsGames() throws Exception {
        when(gameService.getGames(any())).thenReturn(Page.empty());

        mvc.perform(get("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("로그인하지 않은 유저가 게임을 조회하는 경우 오류를 내뱉는다")
    @Test
    @WithAnonymousUser
    void givenUnAuthorizedUser_whenSearching_thenThrowsError() throws Exception {
        doThrow(new TicketApplicationException(ErrorCode.INVALID_TOKEN)).when(gameService).getGames(any());

        mvc.perform(get("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("카테고리가 적절한 게임을 안정적으로 조회한다")
    @Test
    @WithMockUser
    void givenValidCategory_whenSearchingWithCategory_thenReturnsGames() throws Exception {
        String category = "e_sports";
        when(gameService.getGamesByCategory(eq(category), any())).thenReturn(Page.empty());

        mvc.perform(get("/e_sports")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("카테고리가 적절하지 않은 게임을 조회할 경우 오류를 내뱉는다")
    @Test
    @WithMockUser
    void givenInValidCategory_whenSearchingWithCategory_thenThrowsError() throws Exception {
        String wrongCategory = "wrong";
        doThrow(new TicketApplicationException(ErrorCode.CATEGORY_NOT_FOUND)).when(gameService).getGamesByCategory(eq(wrongCategory), any());

        mvc.perform(get("/wrong")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.CATEGORY_NOT_FOUND.getStatus().value()));
    }

    @DisplayName("로그인하지 않은 유저가 게임을 조회할 경우 오류를 내뱉는다")
    @Test
    @WithMockUser
    void givenUnAuthorizedUser_whenSearchingWithCategory_thenThrowsError() throws Exception {
        String category = "e_sports";
        doThrow(new TicketApplicationException(ErrorCode.INVALID_TOKEN)).when(gameService).getGamesByCategory(eq(category), any());

        mvc.perform(get("/e_sports")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("관리자가 안정적으로 게임을 수정한다")
    @Test
    @WithMockUser
    void givenGameInfo_whenUpdating_thenUpdatesGame() throws Exception {
        doNothing().when(gameService).updateGame(any(), eq(1L), any());

        mvc.perform(put("/admin/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new GameRequest("e_sports", "title", "home", "away", "place", Timestamp.from(Instant.now())))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("로그인하지 않은 유저가 게임을 수정할 경우 오류를 내뱉는다")
    @Test
    @WithAnonymousUser
    void givenUnAuthorizedUser_whenUpdating_thenThrowsError() throws Exception {
        doThrow(new TicketApplicationException(ErrorCode.INVALID_TOKEN)).when(gameService).updateGame(any(), eq(1L), any());

        mvc.perform(put("/admin/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameRequest("e_sports", "title", "home", "away", "place", Timestamp.from(Instant.now())))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @DisplayName("관리자가 아닌 유저가 게임을 수정할 경우 오류를 내뱉는다")
    @Test
    @WithMockUser
    void givenNonAdminUser_whenUpdating_thenThrowsError() throws Exception {
        doThrow(new TicketApplicationException(ErrorCode.INVALID_PERMISSION)).when(gameService).updateGame(any(), eq(1L), any());

        mvc.perform(put("/admin/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameRequest("e_sports", "title", "home", "away", "place", Timestamp.from(Instant.now())))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @DisplayName("카테고리가 적절하지 않은 게임을 수정할 경우 오류를 내뱉는다")
    @Test
    @WithMockUser
    void givenInvalidCategory_whenUpdating_thenThrowsError() throws Exception {
        doThrow(new TicketApplicationException(ErrorCode.CATEGORY_NOT_FOUND)).when(gameService).updateGame(any(), eq(1L), any());

        mvc.perform(put("/admin/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new GameRequest("wrong", "title", "home", "away", "place", Timestamp.from(Instant.now())))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.CATEGORY_NOT_FOUND.getStatus().value()));
    }

}
