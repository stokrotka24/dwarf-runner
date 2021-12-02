package game;

import server.ClientHandler;

import java.net.Socket;
import java.util.Optional;

public class User {
    private ClientHandler handler;
    private GamePlatform platform = null;
    private Integer serverId;
    private String username = "Guest";

    // TODO - rm for release
    public User(String username) {
        this.username = username;
        this.handler = new ClientHandler(new Socket(), 1);
    }

    public User(Integer serverId, ClientHandler handler) {
        this.serverId = serverId;
        this.handler = handler;
    }

    public void setPlatform(GamePlatform platform) {
        this.platform = platform;
    }

    public Optional<GamePlatform> getPlatform() {
        if (platform == null) {
            return Optional.empty();
        }
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
