import java.io.IOException;
import java.net.ServerSocket;

import net.Example;
import net.Message;
import net.MessageParser;
import net.MessageTypes;
import server.ClientHandler;

public class Hello {
    private static final class SocketKiller extends Thread {
        private final ServerSocket serverSocket;

        private SocketKiller(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
            System.out.println("Server exited");
        }
    }

    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(2137);
        } catch (IOException e) {
            System.out.println("Server setup didn't work");
            return;
        }

        Runtime.getRuntime().addShutdownHook(new SocketKiller(serverSocket));

        System.out.println("Server started");
        while (true) {
            try {
                ClientHandler handler = new ClientHandler(serverSocket.accept());
                handler.start();
            } catch (IOException e) {
            }
        }
    }
}
