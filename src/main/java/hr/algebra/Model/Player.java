package hr.algebra.Model;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.Serializable;

public class Player implements Serializable {
    private transient Color playerColor;
    private int cellCount;

    public Player(Color playerColor) {
        this.playerColor = playerColor;
        this.cellCount = 0;
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public int getCellCount() {
        return cellCount;
    }

    public void setCellCount(int cellCount) {
        this.cellCount = cellCount;
    }
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(playerColor.getRed());
        out.writeDouble(playerColor.getGreen());
        out.writeDouble(playerColor.getBlue());
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        double red = in.readDouble();
        double green = in.readDouble();
        double blue = in.readDouble();
        playerColor = Color.color(red,green,blue);
    }
}