package hr.algebra.Controller;

import hr.algebra.Model.Game;
import hr.algebra.Model.GameBoard;
import hr.algebra.Model.Player;
import hr.algebra.Utillities.ReflectionUtils;
import hr.algebra.View.GameView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GameController {
    private static final String CLASSES_PATH = "target/classes/";
    private static final String DOCUMENTATION_PATH="target/";
    private static final String EXT =".txt";
    private final Game model;
    private final GameView view;
    private boolean selecting = false;

    private int selectedRow = -1;
    private int selectedCol=-1;
    private Random random = new Random();

    public GameController(Game model, GameView view) {
        this.model = model;
        this.view = view;
        attachEventHandlers();
    }
    private void attachEventHandlers() {
        view.getPrepareButton().setOnAction(event -> prepareBoard());
        view.getStartButton().setOnAction(event -> startGame());
        view.getEndTurnButton().setOnAction(event -> endTurn());
        view.getGenerateDocumentationMenuItem().setOnAction(event ->generateDocumentation());
        view.getSaveGameMenuItem().setOnAction(event ->saveGame());
        view.getLoadGameMenuItem().setOnAction(event->loadGame());



        for (int row = 0; row < model.getBoard().getRows(); row++) {
            for (int col = 0; col < model.getBoard().getCols(); col++) {
                Button cellButton = view.getGameBoardView().getButton(row, col);
                final int r = row;
                final int c = col;
                cellButton.setOnAction(event -> handleCellClick(r, c));
            }
        }

    }
    private void handleCellClick(int row, int col) {
        if (!model.isGameStarted()) {
            return;
        }

        Button clickedButton = view.getGameBoardView().getButton(row, col);
        Player cellOwner = model.getBoard().getCellOwner(row, col);
        Player currentPlayer = model.getCurrentPlayer();

        if (!selecting && cellOwner == currentPlayer && model.getBoard().getDiceCount(row, col) > 1) {
            if (hasAdjacentEnemy(row, col, currentPlayer)) {

                selecting = true;
                selectedRow = row;
                selectedCol = col;
                clickedButton.setStyle("-fx-base: #00FF00;");
            }
        } else if (selecting) {
            if (selectedRow == row && selectedCol == col) {

                deselectCell(clickedButton, currentPlayer);
            } else if (isAdjacent(selectedRow, selectedCol, row, col)) {

                if (cellOwner != currentPlayer) {
                    performAttack(row, col);
                }
            }
        }
    }
    private boolean isAdjacent(int row1, int col1, int row2, int col2) {
        return Math.abs(row1 - row2) + Math.abs(col1 - col2) == 1;
    }
    private boolean hasAdjacentEnemy(int row, int col, Player player) {
        GameBoard board = model.getBoard();

        if (row > 0 && board.getCellOwner(row - 1, col) != player) return true;
        if (row < board.getRows() - 1 && board.getCellOwner(row + 1, col) != player) return true;
        if (col > 0 && board.getCellOwner(row, col - 1) != player) return true;
        if (col < board.getCols() - 1 && board.getCellOwner(row, col + 1) != player) return true;

        return false;
    }

    private void deselectCell(Button clickedButton, Player player) {
        selecting = false;
        selectedRow = -1;
        selectedCol = -1;
        String colorStyle = player.getPlayerColor() == Color.RED ? "-fx-base: #FF0000;" : "-fx-base: #0000FF;";
        clickedButton.setStyle(colorStyle);
    }
    private void performAttack(int targetRow, int targetCol) {
        if (selecting) {
            int attackDice = model.getBoard().getDiceCount(selectedRow, selectedCol);
            int defenseDice = model.getBoard().getDiceCount(targetRow, targetCol);
            int attackScore = rollDice(attackDice);
            int defenseScore = rollDice(defenseDice);


            if (attackScore > defenseScore) {
                model.getBoard().setCellOwner(targetRow, targetCol, model.getCurrentPlayer());
                model.getBoard().setDiceCount(targetRow, targetCol, attackDice - 1);
                model.getBoard().setDiceCount(selectedRow, selectedCol, 1);
                updateCellView(targetRow, targetCol, attackDice - 1);
                updateCellView(selectedRow, selectedCol, 1);
            } else {

                model.getBoard().setDiceCount(selectedRow, selectedCol, 1);
                updateCellView(selectedRow, selectedCol, 1);
            }

            Button attackingButton = view.getGameBoardView().getButton(selectedRow, selectedCol);
            deselectCell(attackingButton, model.getCurrentPlayer());
            selecting = false;
            selectedRow = -1;
            selectedCol = -1;


            checkGameEnd();
        }
    }

    private int rollDice(int numberOfDice) {
        int sum = 0;
        for (int i = 0; i < numberOfDice; i++) {
            sum += random.nextInt(6) + 1;
        }
        return sum;
    }

    private void updateCellView(int row, int col, int diceCount) {
        Button cellButton = view.getGameBoardView().getButton(row, col);
        cellButton.setText(String.valueOf(diceCount));
        Player owner = model.getBoard().getCellOwner(row, col);
        String colorStyle = owner.getPlayerColor() == Color.RED ? "-fx-base: #FF0000" : "-fx-base: #0000FF";
        cellButton.setStyle(colorStyle);
    }
    private void prepareBoard() {
        Random random = new Random();
        GameBoard board = model.getBoard();
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                int diceCount = random.nextInt(6) + 1;
                Player owner = random.nextBoolean() ? model.getPlayer1() : model.getPlayer2();
                board.setDiceCount(row, col, diceCount);
                board.setCellOwner(row, col, owner);


                Button cellButton = view.getGameBoardView().getButton(row, col);
                cellButton.setText(String.valueOf(diceCount));
                String colorStyle = owner.getPlayerColor() == Color.RED ? "-fx-base: #FF0000;" : "-fx-base: #0000FF;";
                cellButton.setStyle(colorStyle);
            }
        }

    }
    private void startGame() {
        model.setGameStarted(true);

        view.getPrepareButton().setDisable(true);
        view.getStartButton().setDisable(true);

        view.getEndTurnButton().setDisable(false);


        Player startingPlayer = model.getPlayer1();
        model.setCurrentPlayer(startingPlayer);


        updateInfoTextArea("Game started. It's Player " +
                (startingPlayer == model.getPlayer1() ? "1" : "2") + "'s turn.");


        updateBoardView();
        updatePlayerCells(startingPlayer);

    }

    private void updateInfoTextArea(String text) {
        view.getInfoTextArea().setText(text);
    }

    private void updateBoardView() {
        GameBoard board = model.getBoard();
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                updateCellView(row, col);
            }
        }
    }

    private void updateCellView(int row, int col) {
        Button cellButton = view.getGameBoardView().getButton(row, col);
        Player cellOwner = model.getBoard().getCellOwner(row, col);
        int diceCount = model.getBoard().getDiceCount(row, col);

        cellButton.setText(String.valueOf(diceCount));
        String colorStyle = cellOwner.getPlayerColor() == Color.RED ? "-fx-base: #FF0000" : "-fx-base: #0000FF";
        cellButton.setStyle(colorStyle);
    }

    private void updatePlayerCells(Player player) {
        GameBoard board = model.getBoard();
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                if (board.getCellOwner(row, col) == player) {

                    int diceCount = board.getDiceCount(row, col);
                    Button cellButton = view.getGameBoardView().getButton(row, col);


                    cellButton.setText(Integer.toString(diceCount));
                    String colorStyle = player.getPlayerColor() == Color.RED ? "-fx-base: #FF0000" : "-fx-base: #0000FF";
                    cellButton.setStyle(colorStyle);
                }
            }
        }
    }
    private void endTurn() {

        Player currentPlayer = model.getCurrentPlayer();
        currentPlayer = currentPlayer == model.getPlayer1() ? model.getPlayer2() : model.getPlayer1();
        model.setCurrentPlayer(currentPlayer);


        addDiceToPlayerCells(currentPlayer);

        updateInfoTextArea("Player " + (currentPlayer == model.getPlayer1() ? "1" : "2") + "'s turn.");

        //updatePlayerCells(currentPlayer);
        //updateBoardView();
    }

    private void addDiceToPlayerCells(Player player) {
        GameBoard board = model.getBoard();
        int diceAdded = 0; // For debugging purpose.

        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                if (board.getCellOwner(row, col) == player) {
                    int currentDice = board.getDiceCount(row, col);
                    if (currentDice < 8) {
                        board.setDiceCount(row, col, currentDice + 1);
                        Button cellButton = view.getGameBoardView().getButton(row, col);
                        cellButton.setText(Integer.toString(currentDice + 1));
                        diceAdded++;
                    }
                }
            }
        }

    }
    private void checkGameEnd() {
        int player1Cells = countPlayerCells(model.getPlayer1());
        int player2Cells = countPlayerCells(model.getPlayer2());

        if (player1Cells == 0) {
            model.setGameStarted(false);
            showAlert("Game Over", "Player 2 wins the game!");
        } else if (player2Cells == 0) {
            model.setGameStarted(false);
            showAlert("Game Over", "Player 1 wins the game!");
        }
    }

    private int countPlayerCells(Player player) {
        int count = 0;
        GameBoard board = model.getBoard();
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                if (board.getCellOwner(row, col) == player) {
                    count++;
                }
            }
        }
        return count;
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    private void generateDocumentation(){
        try {
            showAlert("Documentation Status","Documentation is generated successfuly");
            Files.walkFileTree(Paths.get(CLASSES_PATH), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    createDocumentation(file);
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void createDocumentation(Path path) throws IOException {
        if (path.toString().contains("$") || path.toString().contains("module-info.class")) {
            return;
        }
        String className = StreamSupport.stream(path.spliterator(), false)
                .skip(2)
                .map(p -> p.toString().contains(".") ? p.toString().substring(0, p.toString().indexOf(".")) : p.toString())
                .collect(Collectors.joining("."));
        try {
            Class<?> clazz = Class.forName(className);

            StringBuilder documentation = new StringBuilder();
            ReflectionUtils.readClassAndMembersInfo(clazz, documentation);

            Files.writeString(Paths.get(DOCUMENTATION_PATH, clazz.getSimpleName() + EXT), documentation.toString());

        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }

    }
    public void saveGame() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Save Files", "*.save")
        );
        File file = fileChooser.showSaveDialog(view.getvBox().getScene().getWindow());
        if (file != null) {
            if (!file.getName().endsWith(".save")) {
                file = new File(file.getAbsolutePath() + ".save");
            }

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(model);
                showAlert("Save Game Status","Game Saved Succesfully");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Save Error", "An error occurred while saving the game: ");
            }
        }
    }
    public void loadGame() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Save Files", "*.save")
        );
        File file = fileChooser.showOpenDialog(view.getvBox().getScene().getWindow());
        if (file != null) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                Game loadedModel = (Game) in.readObject();
                model.setBoard(loadedModel.getBoard());
                model.setPlayer1(loadedModel.getPlayer1());
                model.setPlayer2(loadedModel.getPlayer2());
                model.setCurrentPlayer(loadedModel.getCurrentPlayer());
                model.setGameStarted(loadedModel.isGameStarted());

                System.out.println(loadedModel.getPlayer1());
                System.out.println(loadedModel.getPlayer2());
                System.out.println(loadedModel.getCurrentPlayer());
                System.out.println(loadedModel.getBoard());
                System.out.println(model.getPlayer1());
                System.out.println(model.getPlayer2());
                System.out.println(model.getCurrentPlayer());
                System.out.println(model.getBoard());


                updatePlayerCells(model.getCurrentPlayer());
                updateBoardView();
                showAlert("Load Game Status","Game Loaded Succesfully");
                updateInfoTextArea("Game loaded.");
                updateGameControls();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                showAlert("Load Error", "Game could not be loaded. An error occured.: " + e.getMessage());
            }
        }
    }
    private void updateGameControls() {
        GameBoard board = model.getBoard();
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                Button button = view.getGameBoardView().getButton(row, col);
                Player owner = board.getCellOwner(row, col);
                if (owner != null) {
                    String color = owner.getPlayerColor().toString().replace("0x", "#");
                    button.setStyle("-fx-base: " + color + ";");
                }
            }

        }
        view.getPrepareButton().setDisable(model.isGameStarted());
        view.getStartButton().setDisable(model.isGameStarted());
        view.getEndTurnButton().setDisable(!model.isGameStarted());
    }


}