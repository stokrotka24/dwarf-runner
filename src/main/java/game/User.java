package game;

import server.ClientHandler;

import java.util.Optional;

//TODO user id is necessary
public class User {
    private ClientHandler handler;
    private GamePlatform platform = null;

    public User(ClientHandler handler) {
        this.handler = handler;
    }

    public void setPlatform(GamePlatform platform) {
        this.platform = platform;
    }

    public Optional<GamePlatform> getPlatform() {
        return Optional.of(platform);
    }

    public void sendMessage(String msg) {
        handler.sendMessage(msg);
    }

    public ClientHandler getHandler() {
        return this.handler;
    }
}
