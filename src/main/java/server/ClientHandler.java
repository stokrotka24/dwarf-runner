package server;

import messages.Message;
import messages.MessageParser;
import messages.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ClientHandler
 */
public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private PrintStream clientInput;
    private BufferedReader clientOutput;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final Integer maxJsonLength;
    private int id;
    public LinkedBlockingQueue<String> output;
    private static final Logger logger = Logger.getInstance();

    public void sendMessage(String message) {
        logger.info("sending message: " + message);
        if (clientInput != null) {
            clientInput.print(message + '\n');
        }
    }

    public void stopRunning() {
        this.isRunning.set(false);
    }

    public Boolean isRunning() {
        return this.isRunning.get();
    }

    @Override
    public void run() {
        mainLoop: while (isRunning.get()) {
            try {
                StringBuilder builder = new StringBuilder();
                int bracketCount = 0;
                int nextCh;
                do {
                    nextCh = clientOutput.read();
                    if (nextCh == -1) {
                        isRunning.set(false);
                        break mainLoop;
                    }
                } while (nextCh != '{');
                do {
                    String nextChar = Character.toString((char) nextCh);
                    if (nextChar.equals("{")) {
                        bracketCount += 1;
                    } else if (nextChar.equals("}")) {
                        bracketCount -= 1;
                    }
                    builder.append(nextChar);
                    if (builder.length() > this.maxJsonLength) {
                        continue mainLoop;
                    }
                    nextCh = clientOutput.read();
                    if (nextCh == -1) {
                        isRunning.set(false);
                        break mainLoop;
                    }
                } while (bracketCount > 0);
                output.put(builder.toString());
            } catch (IOException e) {
                logger.error(e.getMessage());
                clientInput.close();
                try {
                    clientOutput.close();
                } catch (IOException e1) {
                    logger.error(e1.getMessage());
                }
                return;
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                continue mainLoop;
            }
        }
        clientInput.close();
        disconnectUser();
        try {
            clientOutput.close();
            clientSocket.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    private void disconnectUser() {
        Message<Object> msg = new Message<>(MessageType.DISCONNECT, null);
        msg.clientId = id;
        try {
            output.put(MessageParser.toJsonString(msg));
        } catch (InterruptedException e) {
            logger.warning(e.getMessage());
        }
    }

    private void initStreams() {
        try {
            clientInput = new PrintStream(clientSocket.getOutputStream(), true);
            clientOutput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public ClientHandler(Socket clientSocket, Integer maxJsonLength) {
        this.clientSocket = clientSocket;
        this.maxJsonLength = maxJsonLength;
        this.output = new LinkedBlockingQueue<String>();
        // TODO because of dummy users
        // can be rm later but doesn't have to
        if (clientSocket.isConnected()) {
            initStreams();
        }
    }

    public ClientHandler(Socket clientSocket, Integer maxJsonLength, LinkedBlockingQueue<String> output) {
        this.clientSocket = clientSocket;
        this.maxJsonLength = maxJsonLength;
        this.output = output;
        initStreams();
    }

    public void setId(int id) {
        this.id = id;
    }
}
