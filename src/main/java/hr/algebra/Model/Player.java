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
    private void writeObject(java.io.ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeFloat((float) playerColor.getRed());
        oos.writeFloat((float) playerColor.getGreen());
        oos.writeFloat((float) playerColor.getBlue());
        oos.writeFloat((float) playerColor.getOpacity());
    }

    private void readObject(java.io.ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        float red = ois.readFloat();
        float green = ois.readFloat();
        float blue = ois.readFloat();
        float opacity = ois.readFloat();
        playerColor = Color.color(red, green, blue, opacity);
    }
}