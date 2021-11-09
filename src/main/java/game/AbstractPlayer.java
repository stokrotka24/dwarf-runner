package game;

import server.ClientHandler;

public class AbstractPlayer {
    private ClientHandler handler;

    public AbstractPlayer(ClientHandler handler){
    	this.handler = handler;
	}
    
    public void sendMessage(String msg) {
        handler.sendMessage(msg);
    }
}
