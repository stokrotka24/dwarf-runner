package messages;

import org.junit.jupiter.api.BeforeAll;
import server.MenuServer;

public abstract class AbstractCommunicationTest {
    @BeforeAll
    static void setupServer() {
        Thread serverThread = new Thread(() -> {
            MenuServer menuServer = new MenuServer();
            menuServer.go();
        });
        serverThread.start();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
