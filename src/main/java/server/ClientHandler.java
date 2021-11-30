package server;

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
    private Socket clientSocket;
    private PrintStream clientInput;
    private BufferedReader clientOutput;
    private AtomicBoolean isRunning = new AtomicBoolean(true);
    private Integer maxJsonLength;
    public LinkedBlockingQueue<String> output;

    public void sendMessage(String message) {
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
                Integer bracketCount = 0;
                do {
                    int nextCh = clientOutput.read();
                    if (nextCh == -1) {
                        isRunning.set(false);
                        break mainLoop;
                    }
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
                } while (bracketCount > 0);
                output.put(builder.toString());
            } catch (IOException e) {
                clientInput.close();
                try {
                    clientOutput.close();
                } catch (IOException e1) {
                }
                return;
            } catch (InterruptedException e) {
                continue mainLoop;
            }
        }
        clientInput.close();
        try {
            clientOutput.close();
            clientSocket.close();
        } catch (IOException e) {
        }

    }

    private void initStreams() {
        try {
            clientInput = new PrintStream(clientSocket.getOutputStream(), true);
            clientOutput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
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
}
