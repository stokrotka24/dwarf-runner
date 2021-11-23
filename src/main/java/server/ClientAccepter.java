package server;

import messages.Message;
import messages.MessageParser;
import messages.MessageType;

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
                int id = server.addInput(newHandler);
                sendServerHello(newHandler, id);
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

    private void sendServerHello(ClientHandler newHandler, int id) {
        Message<Integer> msg = new Message<>(MessageType.SERVER_HELLO, id);
        newHandler.sendMessage(MessageParser.toJsonString(msg));
    }

    public ClientAccepter(MenuServer server) {
        this.server = server;
    }

}
