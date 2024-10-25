package cat.itacademy.s05.t01.n01.blackjack.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import cat.itacademy.s05.t01.n01.blackjack.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import cat.itacademy.s05.t01.n01.blackjack.services.PlayerService;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/player")
@Tag(name = "Player Management", description = "Operations related to managing players in Blackjack")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PutMapping("/{playerId}")
    @Operation(summary = "Change player's username", description = "Change the username of a specific player.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username updated successfully"),
            @ApiResponse(responseCode = "404", description = "Player not found"),
            @ApiResponse(responseCode = "400", description = "Invalid username")
    })
    public Mono<ResponseEntity<Player>> changePlayerUsername(
            @PathVariable String playerId, @RequestBody String newUsername) {
        // Comprovar que el nou nom d'usuari no sigui nul o buit
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return playerService.changePlayerUsername(playerId, newUsername)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}


