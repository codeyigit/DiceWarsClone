package hr.algebra.View;

import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;

public class GameBoardView {
    private GridPane gridPane;
    private int rows; // Tahtanın satır sayısı
    private int cols; // Tahtanın sütun sayısı

    public GameBoardView(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        gridPane = new GridPane();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Button button = new Button();
                button.setPrefSize(80, 80); // Butonların varsayılan boyutunu ayarla
                gridPane.add(button, col, row);
            }
        }
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public Button getButton(int row, int col) {
        return (Button) gridPane.getChildren().get(row * cols + col);
    }
}
