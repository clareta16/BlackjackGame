package cat.itacademy.s05.t01.n01.blackjack.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Configuration
public class MySqlconfig {

    private final DatabaseClient databaseClient;

    public MySqlconfig(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @PostConstruct
    public void initializeDatabase() {
        databaseClient.sql("CREATE TABLE IF NOT EXISTS players ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "username VARCHAR(255) NOT NULL, "
                        + "is_playing BOOLEAN DEFAULT FALSE, "
                        + "wins INT DEFAULT 0, "
                        + "total_cards_value INT DEFAULT 0);")
                .then() // This ensures the table creation completes first
                .doOnSuccess(unused -> {
                    System.out.println("Table created if it did not exist.");
                    checkAndInsertTestPlayer();
                })
                .doOnError(error -> System.err.println("Error creating table: " + error.getMessage()))
                .subscribe();
    }

    private void checkAndInsertTestPlayer() {
        databaseClient.sql("SELECT COUNT(*) FROM players;")
                .map(row -> row.get(0, Integer.class))
                .first()
                .flatMap(count -> {
                    if (count == 0) {
                        return databaseClient.sql("INSERT INTO players (username, is_playing, wins, total_cards_value) "
                                        + "VALUES ('TestPlayer', false, 0, 0);")
                                .fetch()
                                .rowsUpdated()
                                .doOnSuccess(countInserted -> System.out.println("Test player inserted. Rows affected: " + countInserted))
                                .doOnError(error -> {
                                    System.err.println("Error inserting test player: " + error.getMessage());
                                    error.printStackTrace(); // Print stack trace for deeper insight
                                });
                    } else {
                        System.out.println("Test player already exists.");
                        return Mono.empty();
                    }
                })
                .subscribe();
    }
}
