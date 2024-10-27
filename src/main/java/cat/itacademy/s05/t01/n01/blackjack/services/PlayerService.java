package cat.itacademy.s05.t01.n01.blackjack.services;

import cat.itacademy.s05.t01.n01.blackjack.exception.PlayerNotFoundException;
import cat.itacademy.s05.t01.n01.blackjack.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import cat.itacademy.s05.t01.n01.blackjack.repository.PlayerRepository;

import java.util.UUID;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public Mono<Player> changePlayerUsername(String playerId, String newUsername) {
        return playerRepository.findById(playerId)
                .flatMap(player -> {
                    player.setUsername(newUsername);
                    return playerRepository.save(player);
                })
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player with ID " + playerId + " not found")));
    }
}

