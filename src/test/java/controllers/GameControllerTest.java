package controllers;

import cat.itacademy.s05.t01.n01.blackjack.controllers.GameController;
import cat.itacademy.s05.t01.n01.blackjack.exception.GameNotFoundException;
import cat.itacademy.s05.t01.n01.blackjack.model.Game;
import cat.itacademy.s05.t01.n01.blackjack.model.Player;
import cat.itacademy.s05.t01.n01.blackjack.model.Ranking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import cat.itacademy.s05.t01.n01.blackjack.services.GameService;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCreateGame_Success() {
        String playerName = "TestPlayer";
        Game game = new Game(new Player(playerName));
        game.setId("1");
        game.setPlayer(new Player(playerName));

        when(gameService.createGame(playerName)).thenReturn(Mono.just(game));

        Mono<ResponseEntity<Game>> response = gameController.createGame(playerName);

        StepVerifier.create(response)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                    assertEquals(playerName, responseEntity.getBody().getPlayer().getUsername());
                    return true;
                })
                .verifyComplete();

        verify(gameService, times(1)).createGame(playerName);
    }

    @Test
    void testCreateGame_InvalidPlayerName() {
        Mono<ResponseEntity<Game>> response = gameController.createGame("");

        StepVerifier.create(response)
                .expectNextMatches(responseEntity -> HttpStatus.BAD_REQUEST.equals(responseEntity.getStatusCode()))
                .verifyComplete();
    }

    @Test
    void testGetGameDetails_Success() {
        String gameId = "1";
        String playerName = "TestPlayer";
        Game game = new Game(new Player(playerName));
        game.setId(gameId);

        when(gameService.getGameDetails(gameId)).thenReturn(Mono.just(game));

        Mono<ResponseEntity<Game>> response = gameController.getGameDetails(gameId);

        StepVerifier.create(response)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    assertEquals(gameId, responseEntity.getBody().getId());
                    return true;
                })
                .verifyComplete();

        verify(gameService, times(1)).getGameDetails(gameId);
    }

    @Test
    void testGetGameDetails_NotFound() {
        String gameId = "1";

        when(gameService.getGameDetails(gameId)).thenReturn(Mono.empty());

        Mono<ResponseEntity<Game>> response = gameController.getGameDetails(gameId);

        StepVerifier.create(response)
                .expectNextMatches(responseEntity -> HttpStatus.NOT_FOUND.equals(responseEntity.getStatusCode()))
                .verifyComplete();

        verify(gameService, times(1)).getGameDetails(gameId);
    }

    @Test
    void testPlayGame_Success() {
        String gameId = "1";
        String moveType = "hit";

        String playerName = "TestPlayer";
        Game game = new Game(new Player(playerName));
        game.setId(gameId);

        when(gameService.playGame(gameId, moveType)).thenReturn(Mono.just(game));

        Mono<ResponseEntity<Game>> response = gameController.playGame(gameId, moveType);

        StepVerifier.create(response)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    assertEquals(gameId, responseEntity.getBody().getId());
                    return true;
                })
                .verifyComplete();

        verify(gameService, times(1)).playGame(gameId, moveType);
    }

    @Test
    void testPlayGame_NotFound() {
        String gameId = "1";
        String moveType = "hit";

        when(gameService.playGame(gameId, moveType)).thenReturn(Mono.empty());

        Mono<ResponseEntity<Game>> response = gameController.playGame(gameId, moveType);

        StepVerifier.create(response)
                .expectNextMatches(responseEntity -> HttpStatus.NOT_FOUND.equals(responseEntity.getStatusCode()))
                .verifyComplete();

        verify(gameService, times(1)).playGame(gameId, moveType);
    }

    @Test
    void testDeleteGame_NotFound() {
        String gameId = "invalidId";

        when(gameService.deleteGame(gameId)).thenReturn(Mono.error(new GameNotFoundException("Game not found")));

        Mono<ResponseEntity<Object>> response = gameController.deleteGame(gameId);

        StepVerifier.create(response)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
                    return true;
                })
                .verifyComplete();

        verify(gameService, times(1)).deleteGame(gameId);
    }

    @Test
    void testGetRanking_Success() {
        String username = "TestPlayer";
        Player player = new Player(username);
        player.setWins(5);
        Ranking rankingResponse = new Ranking(player.getUsername(), player.getWins());

        when(gameService.getRankingByUsernameId(username)).thenReturn(Mono.just(player));

        Mono<ResponseEntity<Ranking>> response = gameController.getRanking(username);

        StepVerifier.create(response)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    assertEquals(rankingResponse.getId(), responseEntity.getBody().getId());
                    assertEquals(rankingResponse.getWins(), responseEntity.getBody().getWins());
                    return true;
                })
                .verifyComplete();

        verify(gameService, times(1)).getRankingByUsernameId(username);
    }
}




