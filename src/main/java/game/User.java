package game;

import server.ClientHandler;

import java.util.Optional;

public class User {
    private ClientHandler handler;
    private GamePlatform platform = null;
    private Integer serverId;

    public User(Integer serverId, ClientHandler handler) {
        this.serverId = serverId;
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

    public Integer getServerId() {
        return serverId;
    }
}
