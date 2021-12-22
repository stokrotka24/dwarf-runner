package messages;

import org.junit.jupiter.api.BeforeAll;
import server.MenuServer;

import java.io.IOException;
import java.net.Socket;

public abstract class AbstractCommunicationTest {
    protected static int lobbyCounter = 0;
    protected static final int defaultPort = 2137;
    @BeforeAll
    static void setupServer() {
        if (isAvailable(defaultPort)) {
            Thread serverThread = new Thread(() -> {
                MenuServer menuServer = new MenuServer();
                menuServer.go();
            });
            serverThread.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isAvailable(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }
}
