package game;

import server.ClientHandler;

public class User {
    private ClientHandler handler;
    private GamePlatform platform = null;

    public User(ClientHandler handler) {
        this.handler = handler;
    }

    public void setPlatform(GamePlatform platform) {
        this.platform = platform;
    }

    public GamePlatform getPlatform() throws Exception {
        if (platform == null) {
            throw new Exception("User's platform isn't defined yet!");
        }
        return platform;
    }

    public void sendMessage(String msg) {
        handler.sendMessage(msg);
    }
}
