package cat.itacademy.s05.t01.n01.blackjack.model;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class Dealer {
    private Deck deck;
    private List<Card> cards = new ArrayList<>();
    private static final int STAND_THRESHOLD = 17;

    public Dealer() {

    }

    public List<Card> getCards() {
        return cards;
    }

    public void playTurn() {
        cards.clear();

        while (getCardsValue() < STAND_THRESHOLD) {
            drawCard();
        }
    }

    private void drawCard() {
        if (deck.isEmpty()) {
            throw new IllegalStateException("The deck is empty. Cannot draw a card.");
        }
        cards.add(deck.draw());
    }

    public int getCardsValue() {
        int cardsValue = 0;
        int acesCount = 0;

        for (Card card : cards) {
            cardsValue += card.getValue();
            if (card.getRank().equals("A")) {
                acesCount++;
            }
        }
        while (cardsValue > 21 && acesCount > 0) {
            cardsValue -= 10;
            acesCount--;
        }
        return cardsValue;
    }
}


