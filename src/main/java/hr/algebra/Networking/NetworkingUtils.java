package hr.algebra.Networking;

import hr.algebra.Controller.GameController;
import hr.algebra.Model.Game;
import hr.algebra.Model.NetworkConfiguration;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkingUtils {
    public static void sendGameboardToServer(Game model) {
        try (Socket clientSocket = new Socket(NetworkConfiguration.SERVER_HOST, NetworkConfiguration.SERVER_PORT)){
            System.err.printf("Client is connecting to %s:%d%n", clientSocket.getInetAddress(), clientSocket.getPort());

            //sendPrimitiveRequest(clientSocket);
            sendSerializableRequest(clientSocket,model);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void sendGameboardToClient(Game model) {
        try (Socket clientSocket = new Socket(NetworkConfiguration.SERVER_HOST, NetworkConfiguration.CLIENT_PORT)){
            System.err.printf("Server is connecting to %s:%d%n", clientSocket.getInetAddress(), clientSocket.getPort());

            //sendPrimitiveRequest(clientSocket);
            sendSerializableRequest(clientSocket,model);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void sendSerializableRequest(Socket client, Game model) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        //oos.writeObject(new Country("Croatia", 1));
        oos.writeObject(model);
        System.out.println("Gameboard send to the server");
    }
    public static void acceptClientRequests() {
        try (ServerSocket serverSocket = new ServerSocket(NetworkConfiguration.SERVER_PORT)){
            System.err.printf("Server listening on port: %d%n", serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.printf("Client connected from port %s%n", clientSocket.getPort());

                //new Thread(() ->  processPrimitiveClient(clientSocket)).start();
                new Thread(() ->  processSerializableClient(clientSocket)).start();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());){
            Game model = (Game)ois.readObject();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    GameController.restoreGameBoard(model);
                    GameController.changeGameState();
                }
            });

            //Country country = (Country)ois.readObject();
            System.out.println("GameBoard received from the client !!");
            oos.writeObject("Confirmed that the game board has been received");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
