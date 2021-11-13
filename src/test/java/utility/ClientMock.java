package utility;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientMock implements Runnable {
    private DataOutputStream dataOut  = null;
    private Socket skt                = null;
    public LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private PrintStream out;

    public ClientMock(String address, int port) {
        try {
            skt = new Socket(address, port);
            System.out.println("Connection Established!! ");
            dataOut = new DataOutputStream(skt.getOutputStream());
            out = new PrintStream(skt.getOutputStream(), true);
        } catch(IOException uh) {
            System.out.println(uh.getMessage());
        }
    }

    public void sendMsg(String msg) {
        System.out.println("Sending msg: " + msg);
        out.print(msg);
    }

    public void close() {
        try {
            dataOut.close();
            skt.close();
        }
        catch(IOException io) {
            System.out.println(io.getMessage());
        }
    }

    @Override
    public void run() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            skt.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                StringBuilder builder = new StringBuilder();
                Integer bracketCount = 0;
                do {
                    int nextCh = br.read();
                    if (nextCh == -1) {
                        return;
                    }
                    String nextChar = Character.toString((char) nextCh);
                    if (nextChar.equals("{")) {
                        bracketCount += 1;
                    } else if (nextChar.equals("}")) {
                        bracketCount -= 1;
                    }
                    builder.append(nextChar);
                } while (bracketCount > 0);
                System.out.println("got response: " + builder.toString());
                queue.add(builder.toString());
            } catch (IOException e) {
                try {
                    br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return;
            }
        }
    }
}
