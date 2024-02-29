package com.ticketcheater.web.controller;

import com.ticketcheater.web.controller.request.GameRequest;
import com.ticketcheater.web.controller.response.GameResponse;
import com.ticketcheater.web.controller.response.Response;
import com.ticketcheater.web.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/admin/open")
    public Response<Void> createGame(@RequestBody GameRequest request, Authentication authentication) {
        gameService.createGame(authentication.getName(), request.toDto());
        return Response.success();
    }

    @GetMapping
    public Response<Page<GameResponse>> getGames(@PageableDefault(size=5, sort="startedAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return Response.success(gameService.getGames(pageable).map(GameResponse::from));
    }

    @GetMapping("/{category}")
    public Response<Page<GameResponse>> getGamesByCategory(
            @PathVariable String category,
            @PageableDefault(size=5, sort="startedAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return Response.success(gameService.getGamesByCategory(category, pageable).map(GameResponse::from));
    }

    @PutMapping("/admin/update/{gameId}")
    public Response<Void> updateGame(
            @PathVariable Long gameId,
            @RequestBody GameRequest request,
            Authentication authentication
    ) {
        gameService.updateGame(authentication.getName(), gameId, request.toDto());
        return Response.success();
    }

}
