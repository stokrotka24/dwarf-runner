package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import net.GenericMsgContent;
import net.Message;
import net.MessageBuilder;
import net.MessageParser;
import net.MessageTypes;

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
        MessageBuilder msgBuilder = new MessageBuilder();
        msgBuilder.setType(MessageTypes.SERVER_HELLO);
        mainLoop:
        while (true) {
            try {
                System.out.println("Waiting for client message");
                StringBuilder builder = new StringBuilder();
                Integer bracketCount = 0;
                do {
                    int nextCh = in.read();
                    if (nextCh == -1) {
                        break mainLoop;
                    }
                    String nextChar = Character.toString((char) nextCh);
                    if (nextChar.equals("{")) {
                        bracketCount += 1;
                    } else if (nextChar.equals("}")) {
                        bracketCount -= 1;
                    }
                    builder.append(nextChar);
                } while (bracketCount > 0);
                Message<GenericMsgContent> message;
                try {
                    message = MessageParser.fromJsonString(builder.toString(), GenericMsgContent.class);
                } catch (Exception e) {
                    continue;
                }
                if (message.header.senderId > 0) {
                    msgBuilder.addField("response", "Good senderId");
                } else {
                    msgBuilder.addField("response", "Wrong senderId");
                }
                String outJson = MessageParser.toJsonString(msgBuilder.get());
                System.out.println("Client wrote :" + MessageParser.toJsonString(message));
                out.println(outJson);
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