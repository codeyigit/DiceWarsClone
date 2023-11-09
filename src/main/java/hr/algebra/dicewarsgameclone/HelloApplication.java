package hr.algebra.dicewarsgameclone;

import hr.algebra.Controller.GameController;
import hr.algebra.Model.Game;
import hr.algebra.Model.GameBoard;
import hr.algebra.Model.Player;
import hr.algebra.View.GameBoardView;
import hr.algebra.View.GameView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {

        GameBoard gameBoard = new GameBoard(6, 6);

        Game gameModel = new Game(gameBoard);


        GameBoardView gameBoardView = new GameBoardView(6, 6);
        GameView gameView = new GameView(gameBoardView);


        GameController gameController = new GameController(gameModel, gameView);


        Scene scene = new Scene(gameView.getVBox(), 800, 600);
        primaryStage.setTitle("Dice Wars Clone");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}