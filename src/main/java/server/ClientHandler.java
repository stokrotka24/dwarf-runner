package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * ClientHandler
 */
public class ClientHandler extends Thread {
    private Socket clientSocket;

    @Override
    public void run() {
        PrintStream out;
        BufferedReader in;
        try {
            out = new PrintStream(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            return;
        }

        while (true) {
            try {
                System.out.println("Waiting for client message");
                String clientMessage = in.readLine();
                if (clientMessage == null ) {
                    break;
                }
                System.out.println("Client wrote :" + clientMessage);
                out.println("Message accepted");
            } catch (IOException e) {
                out.close();
                try {
                    in.close();
                } catch (IOException e1) {
                }
                return;
            }
        }
        out.close();
        try {
            in.close();
            clientSocket.close();
        } catch (IOException e) {
        }

    }

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

}