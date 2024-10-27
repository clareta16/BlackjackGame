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
        this.deck = new Deck();
    }

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    public Mono<Game> createGame(String playerName) {
        if (playerName == null || playerName.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Player name can't be null or empty"));
        }

        Player player = new Player(playerName);

        return playerRepository.save(player)
                .flatMap(savedPlayer -> {
                    Game game = new Game(savedPlayer);
                    return gameRepository.save(game);
                });
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
                        game.dealCardToPlayer();
                        int playerScore = game.getPlayerCardsValue();

                        if (playerScore > 21) {
                            game.setResult("Player loses!");
                            game.setActive(false);
                        }
                    } else if ("stand".equalsIgnoreCase(moveType)) {
                        game.playerStopsDrawing();
                        game.getDealer().playTurn(game.getDeck());

                        int playerScore = game.getPlayerCardsValue();
                        int dealerScore = game.getDealer().getCardsValue();

                        if (dealerScore > 21 || playerScore > dealerScore) {
                            game.setResult("Player wins!");
                        } else if (playerScore < dealerScore) {
                            game.setResult("Dealer wins!");
                        } else {
                            game.setResult("It's a tie!");
                        }
                    } else {
                        return Mono.error(new IllegalArgumentException("Invalid move type: " + moveType));
                    }
                    return gameRepository.save(game);
                });
    }

    public Mono<Player> getRankingByUsernameId(String id) {
        return playerRepository.findById(id)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player with username " + id + " not found")));
    }

    public Mono<Void> deleteGame(String id) {
        if (!ObjectId.isValid(id)) {
            return Mono.error(new GameNotFoundException("Invalid ID format"));
        }

        return gameRepository.findById(id)
                .flatMap(game -> gameRepository.delete(game))
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found")));
    }
}

