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
        Game game = new Game();
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
        Game game = new Game();
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
        Game game = new Game();
        game.setId(gameId);

        // Simular la resposta del servei per la jugada
        when(gameService.playGame(gameId, moveType)).thenReturn(Mono.just(game));

        // Cridar al mètode playGame del controlador
        Mono<ResponseEntity<Game>> response = gameController.playGame(gameId, moveType);

        // Verificar que la resposta és la correcta
        StepVerifier.create(response)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    assertEquals(gameId, responseEntity.getBody().getId());
                    return true;
                })
                .verifyComplete();

        // Verificar que el servei ha estat cridat una vegada
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
        String gameId = "invalid-id";

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
        // Arrange
        String username = "TestPlayer";
        Player player = new Player(username);
        player.setWins(5); // Assuming the player has 5 wins for this test
        Ranking rankingResponse = new Ranking(player.getUsername(), player.getWins());

        // Mock the service to return the player when called with the username
        when(gameService.getRankingByUsername(username)).thenReturn(Mono.just(player));

        // Act
        Mono<ResponseEntity<Ranking>> response = gameController.getRanking(username);

        // Assert
        StepVerifier.create(response)
                .expectNextMatches(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    assertEquals(rankingResponse.getUsername(), responseEntity.getBody().getUsername());
                    assertEquals(rankingResponse.getWins(), responseEntity.getBody().getWins());
                    return true;
                })
                .verifyComplete();

        // Verify that the service method was called with the correct username
        verify(gameService, times(1)).getRankingByUsername(username);
    }

}



