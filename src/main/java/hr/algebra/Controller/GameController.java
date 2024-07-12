package hr.algebra.Controller;

import hr.algebra.Model.*;
import hr.algebra.Networking.NetworkingUtils;
import hr.algebra.Utillities.ReflectionUtils;
import hr.algebra.View.GameView;
import hr.algebra.chat.RemoChatService;
import hr.algebra.dicewarsgameclone.HelloApplication;
import hr.algebra.threads.GetTheLatestMoveThread;
import hr.algebra.threads.SaveMoveThread;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GameController {
    private static final String CLASSES_PATH = "target/classes/";
    private static RemoChatService chatServiceStub;
    private static final String DOCUMENTATION_PATH="target/";
    private static final String EXT =".txt";
    private static Game model;
    private static GameView view;
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
        view.getGenerateDocumentationMenuItem().setOnAction(event -> generateDocumentation());
        view.getSaveGameMenuItem().setOnAction(event -> saveGame());
        view.getLoadGameMenuItem().setOnAction(event -> loadGame());
        view.getSendButton().setOnAction(event -> sentChatMessage());
        view.getReplayGameMenuItem().setOnAction(event ->replayTheLastGame());
        view.getNewGameMenuItem().setOnAction(event->clearGameBoard());


        for (int row = 0; row < model.getBoard().getRows(); row++) {
            for (int col = 0; col < model.getBoard().getCols(); col++) {
                Button cellButton = view.getGameBoardView().getButton(row, col);
                final int r = row;
                final int c = col;
                cellButton.setOnAction(event -> handleCellClick(r, c));
            }
        }
        if (!PlayerType.SINGLE_PLAYER.name().equals(HelloApplication.playerLoggedIn.name())) {
            try {
                Registry registry = LocateRegistry.getRegistry(NetworkConfiguration.SERVER_HOST, NetworkConfiguration.RMI_PORT);
                chatServiceStub = (RemoChatService) registry.lookup(RemoChatService.REMOTE_CHAT_OBJECT_NAME);
            } catch (RemoteException | NotBoundException ex) {
                ex.printStackTrace();
            }


            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> refreshChatTextArea()));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.playFromStart();
            view.getMessageField().setOnKeyPressed((e) -> {
                if (e.getCode() == KeyCode.ENTER) {
                    sentChatMessage();
                }
            });
        }
        //GetTheLatestMoveThread newLatestMoveThread = new GetTheLatestMoveThread(view.getLastMoveLabel());
        //Thread runnerThread =  new Thread(newLatestMoveThread);
        //runnerThread.start();



        //Platform.runLater(newLatestMoveThread);
        //Executor executor = Executors.newSingleThreadExecutor();
        //executor.execute(newLatestMoveThread);
        //newLatestMoveThread.getTheLastGameMove();

    }
        public void refreshChatTextArea () {
            view.getChatArea().clear();
            try {
                List<String> listOfMessages = chatServiceStub.getAllChatMessages();
                for (String chatMessage : listOfMessages) {
                    view.getChatArea().appendText(chatMessage + "\n");
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }

        }
        public void sentChatMessage () {
            try {
                String chatMessage = HelloApplication.playerLoggedIn + " : " + view.getMessageField().getText();
                view.getMessageField().clear();
                chatServiceStub.sendChatMessage(chatMessage);
            } catch (RemoteException ex) {
                ex.printStackTrace();
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

        if(!HelloApplication.playerLoggedIn.name().equals(PlayerType.SINGLE_PLAYER.name())) {
            if (HelloApplication.playerLoggedIn.name().equals(PlayerType.CLIENT.name()))
                NetworkingUtils.sendGameboardToServer(model);

            else {
                NetworkingUtils.sendGameboardToClient(model);
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
        String colorStyle = player.getPlayerColor().equals(Color.RED) ? "-fx-base: #FF0000;" : "-fx-base: #0000FF;";
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
                MoveLocation moveLocation= new MoveLocation(targetRow,targetCol);

                GameMove newGameMove = new GameMove(model.getCurrentPlayer() == model.getPlayer1() ? "Player1" : "Player2", moveLocation, LocalDateTime.now());
                XmlUtils.saveGameMoveToXml(newGameMove);
                //GameMoveUtils.saveMove(newGameMove);
                //SaveMoveThread newSaveMoveThread = new SaveMoveThread(newGameMove);
                //Thread newStarterThread = new Thread(newSaveMoveThread);
                //newStarterThread.start();


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
        String colorStyle = owner.getPlayerColor().equals(Color.RED) ? "-fx-base: #FF0000" : "-fx-base: #0000FF";
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
                String colorStyle = owner.getPlayerColor().equals(Color.RED) ? "-fx-base: #FF0000;" : "-fx-base: #0000FF;";
                cellButton.setStyle(colorStyle);
            }
        }
        if (!PlayerType.SINGLE_PLAYER.name().equals(HelloApplication.playerLoggedIn.name())) {
            if (HelloApplication.playerLoggedIn.name().equals(PlayerType.CLIENT.name()))
                NetworkingUtils.sendGameboardToServer(model);
            else {
                NetworkingUtils.sendGameboardToClient(model);
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
        if (!PlayerType.SINGLE_PLAYER.name().equals(HelloApplication.playerLoggedIn.name())) {
            changeGameState();
            if (HelloApplication.playerLoggedIn.name().equals(PlayerType.CLIENT.name()))
                NetworkingUtils.sendGameboardToServer(model);
            else {
                NetworkingUtils.sendGameboardToClient(model);
            }
        }
        saveInitialState();
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
        String colorStyle = cellOwner.getPlayerColor().equals(Color.RED) ? "-fx-base: #FF0000" : "-fx-base: #0000FF";
        cellButton.setStyle(colorStyle);
    }
    private static void updatePlayerCells(Player player) {
        GameBoard board = model.getBoard();
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                if (board.getCellOwner(row, col) == player) {

                    int diceCount = board.getDiceCount(row, col);
                    Button cellButton = view.getGameBoardView().getButton(row, col);


                    cellButton.setText(Integer.toString(diceCount));
                    String colorStyle = player.getPlayerColor().equals(Color.RED) ? "-fx-base: #FF0000" : "-fx-base: #0000FF";
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
        if (!PlayerType.SINGLE_PLAYER.name().equals(HelloApplication.playerLoggedIn.name())) {
            changeGameState();

            if (HelloApplication.playerLoggedIn.name().equals(PlayerType.CLIENT.name())) {
                NetworkingUtils.sendGameboardToServer(model);

            } else {

                NetworkingUtils.sendGameboardToClient(model);

            }
        }
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
            showAlert("Documentation Status","Documentation is generated successfully");
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

                showAlert("Save Game Status","Game Saved Successfully");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Save Error", "An error occurred while saving the game: ");
            }
        }
    }
    public static void restoreGameBoard(Game GameModelToRestore){
        model.setBoard(GameModelToRestore.getBoard());
        model.setPlayer1(GameModelToRestore.getPlayer1());
        model.setPlayer2(GameModelToRestore.getPlayer2());
        model.setCurrentPlayer(GameModelToRestore.getCurrentPlayer());
        model.setGameStarted(GameModelToRestore.isGameStarted());
        updatePlayerCells(GameModelToRestore.getPlayer1());
        updatePlayerCells(GameModelToRestore.getPlayer2());
        updateGameControls();
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
                restoreGameBoard(loadedModel);
                showAlert("Load Game Status","Game Loaded Successfully");
                updateInfoTextArea("Game loaded.");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                showAlert("Load Error", "Game could not be loaded. An error occurred.: " + e.getMessage());
            }
        }
    }
    private static void updateGameControls() {
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
    public static void changeGameState() {

        boolean isClient = HelloApplication.playerLoggedIn == PlayerType.CLIENT;
        boolean isServer = HelloApplication.playerLoggedIn == PlayerType.SERVER;
        boolean isPlayer1Turn = model.getCurrentPlayer().equals(model.getPlayer1());
        boolean isPlayer2Turn = model.getCurrentPlayer().equals(model.getPlayer2());
        boolean enableInteraction = (isClient && isPlayer1Turn) || (isServer && isPlayer2Turn);

        GameBoard board = model.getBoard();
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                Button cellButton = view.getGameBoardView().getButton(row, col);
                cellButton.setDisable(!enableInteraction);
            }
        }
        view.getEndTurnButton().setDisable(!enableInteraction);
    }
    public void replayTheLastGame() {
        loadInitialState();
        List<GameMove> gameMoves = XmlUtils.readGameMovesFromXmlFile();
        GameBoard board = model.getBoard();
        view.getEndTurnButton().setDisable(true);
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                Button cellButton = view.getGameBoardView().getButton(row, col);
                cellButton.setDisable(true);
            }
        }
        AtomicInteger moveIndex = new AtomicInteger(0);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (moveIndex.get() < gameMoves.size()) {
                GameMove move = gameMoves.get(moveIndex.getAndIncrement());
                applyMove(move);
                updateBoardView();
            }
        }));
        timeline.setCycleCount(gameMoves.size());
        timeline.setOnFinished(e -> {
            showAlert("Replay Finished", "The replay of the game has finished.");
            clearGameBoard();
            for (int row = 0; row < board.getRows(); row++) {
                for (int col = 0; col < board.getCols(); col++) {
                    Button cellButton = view.getGameBoardView().getButton(row, col);
                    cellButton.setDisable(false);
                }
            }
        });
        timeline.playFromStart();
    }

    private void applyMove(GameMove move) {
        int row = move.getLocation().getX();
        int col = move.getLocation().getY();
        Player player = move.getPlayer().equals("Player1") ? model.getPlayer1() : model.getPlayer2();
        model.getBoard().setCellOwner(row, col, player);

    }

    public void clearGameBoard() {
        GameBoard board = model.getBoard();
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                board.setDiceCount(row, col, 0);
                board.setCellOwner(row, col, null);
                updateCellViewForClear(row, col, 0, null);
            }
        }
        model.setGameStarted(false);
        resetPlayers();
        updateGameControls();
    }
    private void resetPlayers() {
        model.getPlayer1().setCellCount(0);
        model.getPlayer2().setCellCount(0);
    }
    private void updateCellViewForClear(int row, int col, int diceCount, Player player) {
        Button cellButton = view.getGameBoardView().getButton(row, col);
        view.getInfoTextArea().setText("");
        view.getLastMoveLabel().setText("");
        cellButton.setText(diceCount > 0 ? String.valueOf(diceCount) : "");
        if (player != null) {
            String colorStyle = player.getPlayerColor().equals(Color.RED) ? "-fx-base: #FF0000" : "-fx-base: #0000FF";
            cellButton.setStyle(colorStyle);
        } else {
            cellButton.setStyle("");
        }
    }
    public void saveInitialState() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("initial_state.save"))) {
            out.writeObject(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadInitialState() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("initial_state.save"))) {
            Game loadedModel = (Game) in.readObject();
            restoreGameBoard(loadedModel);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}