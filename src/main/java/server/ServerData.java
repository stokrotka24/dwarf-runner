package server;

/*
* singleton supposed to give access to basic server properties
* to many places
*/
public class ServerData {
    private static ServerData instance;
    private Boolean isDebug = false;
    private Integer serverPort;

    public Integer getServerPort() {
        return this.serverPort;
    }

    public Boolean getIsDebug() {
        return isDebug;
    }

    public void setIsDebug(Boolean isDebug) {
        this.isDebug = isDebug;
    }

    private ServerData(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public static ServerData getInstance() {
        if (instance == null) {
            instance = new ServerData(2137);
        }
        return instance;
    }

    public static ServerData getInstance(Integer serverPort) {
        if (instance == null) {
            instance = new ServerData(serverPort);
        }
        return instance;
    }
}
