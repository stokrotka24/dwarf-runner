package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientAccepter extends Thread {
    public MenuServer server;

    @Override
    public void run() {
        ServerSocket socket;
        try {
            socket = new ServerSocket(2137);
        } catch (IOException e) {
            return;
        }
        while (true) {
            try {
                Socket newClient = socket.accept();
                ClientHandler newHandler = new ClientHandler(newClient, 20128, server.inMsgQueue);
                Thread thread = new Thread(newHandler);
                thread.start();
                server.addInput(newHandler);
            } catch (IOException e) {
                continue;
            } catch (Exception e) {
                break;
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
        }

    }

    public ClientAccepter(MenuServer server) {
        this.server = server;
    }

}
