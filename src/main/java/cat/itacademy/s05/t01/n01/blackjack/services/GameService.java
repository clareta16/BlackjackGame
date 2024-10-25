package cat.itacademy.s05.t01.n01.blackjack.services;

import cat.itacademy.s05.t01.n01.blackjack.exception.GameNotFoundException;
import cat.itacademy.s05.t01.n01.blackjack.exception.PlayerNotFoundException;
import cat.itacademy.s05.t01.n01.blackjack.model.Deck;
import cat.itacademy.s05.t01.n01.blackjack.model.Game;
import cat.itacademy.s05.t01.n01.blackjack.model.Player;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import cat.itacademy.s05.t01.n01.blackjack.repository.GameRepository;
import cat.itacademy.s05.t01.n01.blackjack.repository.PlayerRepository;

@Service
public class GameService {

    private Deck deck;

    public GameService() {
        this.deck = new Deck(); // Properly instantiate the Deck
    }

    public Deck getDeck() {
        return deck; // This will return the initialized deck
    }

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    public Mono<Game> createGame(String playerName) {
        Player player = new Player(playerName);
        Game game = new Game();
        game.setPlayer(player); // Només un jugador
        return gameRepository.save(game);
    }

    public Mono<Game> getGameDetails(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game with ID " + id + " not found")));
    }

    public Mono<Game> playGame(String gameId, String moveType) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game with ID " + gameId + " not found")))
                .flatMap(game -> {
                    if ("hit".equalsIgnoreCase(moveType)) {
                        game.dealCardToPlayer(); // Tractar de treure una carta al jugador
                    } else if ("stand".equalsIgnoreCase(moveType)) {
                        game.playerStopsDrawing(); // Deixa de jugar
                    } else {
                        return Mono.error(new IllegalArgumentException("Invalid move type: " + moveType));
                    }

                    return gameRepository.save(game); // Guarda l'estat actualitzat del joc
                });
    }
    public Mono<Void> deleteGame(String id) {
        // Ensure id is a valid ObjectId
        if (!ObjectId.isValid(id)) {
            return Mono.error(new GameNotFoundException("Invalid ID format"));
        }

        return gameRepository.findById(new ObjectId(id)) // Convert id to ObjectId
                .flatMap(game -> gameRepository.delete(game)) // Delete the game if found
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found"))); // Handle case where game is not found
    }

    public Mono<Player> getRankingByUsername(String username) {
        return playerRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player with username " + username + " not found")));
    }

}



