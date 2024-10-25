package cat.itacademy.s05.t01.n01.blackjack.controllers;

import cat.itacademy.s05.t01.n01.blackjack.exception.GameNotFoundException;
import cat.itacademy.s05.t01.n01.blackjack.model.Deck;
import cat.itacademy.s05.t01.n01.blackjack.model.Ranking;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import cat.itacademy.s05.t01.n01.blackjack.model.Game;
import cat.itacademy.s05.t01.n01.blackjack.model.Player;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import cat.itacademy.s05.t01.n01.blackjack.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/game")
@Tag(name = "Game Management", description = "Operations related to managing Blackjack games")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping("/start")
    public ResponseEntity<String> startGame() {
        Deck deck = gameService.getDeck(); // Get the deck from the service
        if (deck.isEmpty()) {
            return ResponseEntity.badRequest().body("Deck is empty!");
        }
        // Proceed with game logic
        return ResponseEntity.ok("Game started");
    }


    @PostMapping("/new")
    @Operation(summary = "Create a new game", description = "Create a new Blackjack game.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Game created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Game.class))),
            @ApiResponse(responseCode = "400", description = "Invalid player name")
    })
    public Mono<ResponseEntity<Game>> createGame(@RequestBody String playerName) {
        if (playerName == null || playerName.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return gameService.createGame(playerName)
                .map(game -> ResponseEntity.status(HttpStatus.CREATED).body(game));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get game details", description = "Retrieve details of a specific game by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Game.class))),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public Mono<ResponseEntity<Game>> getGameDetails(@PathVariable String id) {
        return gameService.getGameDetails(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/play")
    @Operation(summary = "Play a game move", description = "Play a move in an existing Blackjack game.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Move played successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Game.class))),
            @ApiResponse(responseCode = "404", description = "Game or player not found"),
            @ApiResponse(responseCode = "400", description = "Invalid move type or parameters")
    })
    public Mono<ResponseEntity<Game>> playGame(@PathVariable String id, @RequestParam String moveType) {

        // Validar el tipus de moviment
        if (!isValidMove(moveType)) {
            // Retorna un 400 Bad Request amb un joc null
            return Mono.just(ResponseEntity.badRequest().body(null)); // O canvia a un missatge d'error
        }

        // Passar les opcions i la quantitat d'aposta al servei
        return gameService.playGame(id, moveType)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Funció per validar el tipus de moviment
    private boolean isValidMove(String moveType) {
        return moveType.equalsIgnoreCase("hit") ||
                moveType.equalsIgnoreCase("stand");

    }


    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete a game", description = "Delete an existing game by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Game deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public Mono<ResponseEntity<Object>> deleteGame(@PathVariable String id) {
        return gameService.deleteGame(id)
                .then(Mono.just(ResponseEntity.noContent().build())) // No content returned on successful deletion
                .onErrorResume(GameNotFoundException.class, e ->
                        Mono.just(ResponseEntity.notFound().build()) // Respond with 404 if the game is not found
                );
    }



    @GetMapping("/ranking")
    @Operation(summary = "Get player ranking", description = "Retrieve ranking of players based on game performance.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ranking retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    public Mono<ResponseEntity<Ranking>> getRanking(@RequestParam String username) {
        return gameService.getRankingByUsername(username)
                .map(player -> new Ranking(player.getUsername(), player.getWins()))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}





