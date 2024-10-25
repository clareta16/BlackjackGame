package cat.itacademy.s05.t01.n01.blackjack.model;

public class Ranking {
    private String username;
    private int wins;

    public Ranking(String username, int wins) {
        this.username = username;
        this.wins = wins;
    }

    public String getUsername() {
        return username;
    }

    public int getWins() {
        return wins;
    }
}

