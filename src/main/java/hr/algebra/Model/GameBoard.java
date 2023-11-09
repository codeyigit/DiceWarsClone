package hr.algebra.Model;

import java.io.Serializable;

public class GameBoard implements Serializable {
    private int rows;
    private int cols;
    private int[][] diceCounts;
    private Player[][] cellOwners;

    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.diceCounts = new int[rows][cols];
        this.cellOwners = new Player[rows][cols];
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getDiceCount(int row, int col) {
        return diceCounts[row][col];
    }

    public void setDiceCount(int row, int col, int diceCount) {
        this.diceCounts[row][col] = diceCount;
    }

    public Player getCellOwner(int row, int col) {
        return cellOwners[row][col];
    }

    public void setCellOwner(int row, int col, Player player) {
        this.cellOwners[row][col] = player;
    }
}