package hr.algebra.dicewarsgameclone;

import hr.algebra.Controller.GameController;
import hr.algebra.Model.*;
import hr.algebra.Networking.NetworkingUtils;
import hr.algebra.View.GameBoardView;
import hr.algebra.View.GameView;
import hr.algebra.chat.RemoChatService;
import hr.algebra.chat.RemoteChatServiceImpl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
public class HelloApplication extends Application {

    private static Stage mainStage;
    public static PlayerType playerLoggedIn;
    @Override
    public void start(Stage primaryStage) {

        GameBoard gameBoard = new GameBoard(6, 6);

        Game gameModel = new Game(gameBoard);


        GameBoardView gameBoardView = new GameBoardView(6, 6);
        GameView gameView = new GameView(gameBoardView);

        GameController gameController = new GameController(gameModel, gameView);

        mainStage=primaryStage;
        Scene scene = new Scene(gameView.getVBox(), 800, 600);
        mainStage.setTitle(playerLoggedIn.name());
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void main(String[] args) {
        String playerName = args[0];
        if(PlayerType.SERVER.name().equals(playerName)){
            playerLoggedIn=PlayerType.SERVER;
            new Thread(HelloApplication::startServer).start();

        }
        else if (PlayerType.CLIENT.name().equals(playerName)){
            playerLoggedIn=PlayerType.CLIENT;
            new Thread(HelloApplication::startClient).start();
        }
        else if(PlayerType.SINGLE_PLAYER.name().equals(playerName))
        {
            playerLoggedIn=PlayerType.SINGLE_PLAYER;
        }
        //mainStage.setTitle(playerLoggedIn.name());
        launch(args);
    }
    public static void startServer(){
        startRMIserver(); // Since server has infinite loop we have to implement it before server.
        NetworkingUtils.acceptClientRequests();

    }
    public static void startClient(){
        acceptServerRequests();

    }
    public static void startRMIserver(){
        try {
            Registry registry = LocateRegistry.createRegistry(NetworkConfiguration.RMI_PORT);
            RemoChatService remoteChatService= new RemoteChatServiceImpl();
            RemoChatService skeleton = (RemoChatService)UnicastRemoteObject.exportObject(remoteChatService, NetworkConfiguration.RANDOM_PORT_HINT);
            registry.rebind(RemoChatService.REMOTE_CHAT_OBJECT_NAME, skeleton);
            System.err.println("Object registered in RMI registry");
        }catch(RemoteException ex ){
            ex.printStackTrace();
        }
    }
    public static void acceptServerRequests() {
        try (ServerSocket serverSocket = new ServerSocket(NetworkConfiguration.CLIENT_PORT)){
            System.err.println("Client listening on port:"+ serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Server connected from port : "+ clientSocket.getPort());

                //new Thread(() ->  processPrimitiveClient(clientSocket)).start();
                new Thread(() ->  NetworkingUtils.processSerializableClient(clientSocket)).start();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }



}