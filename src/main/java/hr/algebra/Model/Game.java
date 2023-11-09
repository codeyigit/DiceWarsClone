package hr.algebra.Model;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class Game implements Serializable {
    private GameBoard board;
    private Player currentPlayer;
    private Player player1;
    private Player player2;

    public void setBoard(GameBoard board) {
        this.board = board;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    private boolean gameStarted;

    public Game(GameBoard board) {
        this.board = board;
        this.player1 = new Player(Color.RED);
        this.player2 = new Player(Color.BLUE);
        this.currentPlayer = player1;
        this.gameStarted = false;
    }

    public GameBoard getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }
}
