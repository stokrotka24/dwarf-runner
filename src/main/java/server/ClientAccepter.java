package server;

import messages.Message;
import messages.MessageParser;
import messages.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientAccepter extends Thread {
    public MenuServer server;
    private static final Logger logger = Logger.getInstance();

    @Override
    public void run() {
        ServerSocket socket;
        try {
            socket = new ServerSocket(ServerData
            .getInstance()
            .getServerPort());
        } catch (IOException e) {
            logger.error(e.getMessage());
            return;
        }
        while (true) {
            try {
                Socket newClient = socket.accept();
                ClientHandler newHandler = new ClientHandler(newClient, 20128, server.inMsgQueue);
                int id = server.addInput(newHandler);
                newHandler.setId(id);
                new Thread(newHandler).start();
                sendServerHello(newHandler, id);
            } catch (IOException e) {
                logger.warning(e.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage());
                break;
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
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
