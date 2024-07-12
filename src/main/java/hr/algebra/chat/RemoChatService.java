package hr.algebra.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoChatService extends Remote {
    String REMOTE_CHAT_OBJECT_NAME = "hr.algebra.rmi.chat.service";

    void sendChatMessage(String chatMessage) throws RemoteException;;

    List<String> getAllChatMessages() throws RemoteException;;

}
