package hr.algebra.View;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.Serializable;

public class GameView implements Serializable {
    private TextArea chatArea;
    private TextField messageField;
    private Button sendButton;
    private VBox vBox;
    private GameBoardView gameBoardView;
    private TextArea infoTextArea;
    private Button prepareButton;
    private Button startButton;
    private Button endTurnButton;
    private MenuBar menuBar;
    private Menu fileMenu;
    private MenuItem saveGameMenuItem;
    private MenuItem loadGameMenuItem;
    private MenuItem newGameMenuItem;
    private MenuItem replayGameMenuItem;
    private Menu documentationMenu;
    private  MenuItem generateDocumentationMenuItem;
    private Label lastMoveLabel;

    public VBox getvBox() {
        return vBox;
    }

    public void setvBox(VBox vBox) {
        this.vBox = vBox;
    }

    public MenuItem getSaveGameMenuItem() {
        return saveGameMenuItem;
    }

    public void setSaveGameMenuItem(MenuItem saveGameMenuItem) {
        this.saveGameMenuItem = saveGameMenuItem;
    }

    public MenuItem getLoadGameMenuItem() {
        return loadGameMenuItem;
    }

    public void setLoadGameMenuItem(MenuItem loadGameMenuItem) {
        this.loadGameMenuItem = loadGameMenuItem;
    }

    public MenuItem getGenerateDocumentationMenuItem() {
        return generateDocumentationMenuItem;
    }

    public void setGenerateDocumentationMenuItem(MenuItem generateDocumentationMenuItem) {
        this.generateDocumentationMenuItem = generateDocumentationMenuItem;
    }

    public TextArea getChatArea() {
        return chatArea;
    }

    public TextField getMessageField() {
        return messageField;
    }

    public Button getSendButton() {
        return sendButton;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public MenuItem getReplayGameMenuItem() {
        return replayGameMenuItem;
    }

    public Label getLastMoveLabel() {
        return lastMoveLabel;
    }

    public void setLastMoveLabel(Label lastMoveLabel) {
        this.lastMoveLabel = lastMoveLabel;
    }

    public Menu getFileMenu() {
        return fileMenu;
    }

    public MenuItem getNewGameMenuItem() {
        return newGameMenuItem;
    }

    public Menu getDocumentationMenu() {
        return documentationMenu;
    }

    public GameView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
        vBox = new VBox();
        menuBar = new MenuBar();
        lastMoveLabel= new Label("Last Move: ");
        fileMenu = new Menu("File");
        saveGameMenuItem = new MenuItem("Save Game");
        loadGameMenuItem = new MenuItem("Load Game");
        newGameMenuItem = new MenuItem("New Game");
        replayGameMenuItem= new Menu("Replay Game");
        fileMenu.getItems().addAll(saveGameMenuItem, loadGameMenuItem,newGameMenuItem,replayGameMenuItem);
        documentationMenu = new Menu("Documentation");
        generateDocumentationMenuItem = new MenuItem("Generate Documentation");
        documentationMenu.getItems().add(generateDocumentationMenuItem);
        menuBar.getMenus().addAll(fileMenu, documentationMenu);
        vBox.getChildren().add(menuBar);

        vBox.getChildren().add(gameBoardView.getGridPane());

        prepareButton = new Button("Prepare Board");
        startButton = new Button("Start Game");
        endTurnButton = new Button("End Turn");


        endTurnButton.setDisable(true);


        vBox.getChildren().addAll(prepareButton, startButton, endTurnButton);

        infoTextArea = new TextArea();
        infoTextArea.setPrefSize(50, 100);
        infoTextArea.setEditable(false);

        vBox.getChildren().add(infoTextArea);
        vBox.getChildren().add(lastMoveLabel);
        createChatSection();
    }
    private void createChatSection() {
        // Chat alanı
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPrefHeight(500); // Yükseklik ayarı

        // Mesaj yazma alanı ve gönderme butonu
        messageField = new TextField();
        sendButton = new Button("Send");
        messageField.setPrefWidth(600);
        HBox messageBox = new HBox(10, messageField, sendButton);
        VBox chatBox = new VBox(10, chatArea, messageBox);

        // Ana VBox'a chat bölümünü ekle
        vBox.getChildren().add(chatBox);
    }



    public VBox getVBox() {
        return vBox;
    }

    public TextArea getInfoTextArea() {
        return infoTextArea;
    }

    public Button getPrepareButton() {
        return prepareButton;
    }

    public Button getStartButton() {
        return startButton;
    }

    public Button getEndTurnButton() {
        return endTurnButton;
    }

    public GameBoardView getGameBoardView() {
        return gameBoardView;
    }

}
