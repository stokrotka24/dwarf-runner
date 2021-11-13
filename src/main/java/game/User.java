package game;

import server.ClientHandler;

public class User {
    private ClientHandler handler;

    public User(ClientHandler handler){
        this.handler = handler;
    }

    public void sendMessage(String msg) {
        handler.sendMessage(msg);
    }
}
