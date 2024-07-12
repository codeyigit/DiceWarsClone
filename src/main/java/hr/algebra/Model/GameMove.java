package hr.algebra.Model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class GameMove implements Serializable {

    private String Player;
    private MoveLocation location;
    private LocalDateTime dateTime;

    public GameMove(String player, MoveLocation location, LocalDateTime dateTime) {
        Player = player;
        this.location = location;
        this.dateTime = dateTime;
    }


    public String getPlayer() {
        return Player;
    }

    public void setPlayer(String player) {
        Player = player;
    }

    public MoveLocation getLocation() {
        return location;
    }

    public void setLocation(MoveLocation location) {
        this.location = location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
