package hr.algebra.View;

import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class GameView {
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
    private Menu documentationMenu;
    private  MenuItem generateDocumentationMenuItem;

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

    public GameView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
        vBox = new VBox();
        menuBar = new MenuBar();
        fileMenu = new Menu("File");
        saveGameMenuItem = new MenuItem("Save Game");
        loadGameMenuItem = new MenuItem("Load Game");
        newGameMenuItem = new MenuItem("New Game");
        fileMenu.getItems().addAll(saveGameMenuItem, loadGameMenuItem,newGameMenuItem);
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
        infoTextArea.setPrefSize(200, 400);
        infoTextArea.setEditable(false);

        vBox.getChildren().add(infoTextArea);
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
