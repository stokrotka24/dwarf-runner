package messages;

import org.junit.jupiter.api.BeforeAll;
import server.MenuServer;

public abstract class AbstractCommunicationTest {
    private static boolean isServerRunning = false;
    protected static int lobbyCounter = 0;
    @BeforeAll
    static void setupServer() {
        if (!isServerRunning) {
            Thread serverThread = new Thread(() -> {
                MenuServer menuServer = new MenuServer();
                menuServer.go();
            });
            isServerRunning = true;
            serverThread.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
