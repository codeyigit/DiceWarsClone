package hr.algebra.chat;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class RemoteChatServiceImpl implements RemoChatService {
    List<String> chatMessagesList;
    public RemoteChatServiceImpl(){
        chatMessagesList = new ArrayList<>();
    }
    @Override
    public void sendChatMessage(String chatMessage) throws RemoteException {
        chatMessagesList.add(chatMessage);

    }

    @Override
    public List<String> getAllChatMessages() throws RemoteException {
        return chatMessagesList;
    }
}
