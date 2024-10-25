package cat.itacademy.s05.t01.n01.blackjack.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Game entity representing each game")
@Document(collection = "game")
public class Game {
    @Id
    private String id;
    private Player player; // Només un jugador
    private Deck deck;
    private Dealer dealer;
    private List<Card> playerCards;
    private List<Card> dealerCards;
    private boolean isActive;


    // Nou constructor que accepta un jugador
    public Game() {
        this.player = player; // Assignar el jugador
        this.deck = new Deck(); // Inicialitzar el deck
        this.playerCards = new ArrayList<>();
        this.dealerCards = new ArrayList<>();
        this.isActive = true; // Establir l'estat del joc a actiu per defecte
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }


    public void startGame() {
        this.deck = new Deck();
        this.deck.shuffle();
        this.dealer = new Dealer();
        this.isActive = true;

        // Afegeix dues cartes al jugador i una al dealer
        playerCards.add(deck.draw());
        playerCards.add(deck.draw());
        dealerCards.add(deck.draw());

        player.setPlaying(true);
    }

    public void dealCardToPlayer() {
        if (deck.isEmpty()) {
            throw new IllegalStateException("The deck is empty. There are no more cards to deal.");
        }
        Card dealtCard = deck.draw();
        playerCards.add(dealtCard);
    }

    public void playerStopsDrawing() {
        player.setPlaying(false);
    }

}




