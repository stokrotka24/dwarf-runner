package messages;

import org.junit.jupiter.api.*;
import utility.ClientMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicCommunicationTest extends AbstractCommunicationTest {
    private static final ClientMock client = new ClientMock("localhost", 2137);

    @BeforeAll
    static void prepareClient() {
        Thread thread = new Thread(client);
        thread.start();
        try {
            client.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendTrash_ShouldIgnore() {
        String request = "{{}{}{{trash}}}";
        client.sendMsg(request);
        try {
            Thread.sleep(200);
            assertEquals(client.queue.size(), 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void noHeader_ShouldGetErrorMsg() {
        String request = "{\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "        \"gametype\": \"solo\",\n" +
                "        \"players_amount\": 6,\n" +
                "        \"map\": 1,\n" +
                "        \"endgame_cond\": 1,\n" +
                "        \"web_speed\": 3,\n" +
                "        \"mobile_max_speed\": 5,\n" +
                "        \"dwarves_amount\": 4\n" +
                "    }\n" +
                "}";
        client.sendMsg(request);

        try {
            String response1 = client.queue.take();
            assertEquals(MessageType.ERROR, MessageParser.getMsgHeader(response1));
        } catch (InterruptedException | MessageException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void incorrectHeader_ShouldGetErrorMsg() {
        String request = "{\n" +
                "    \"header\": \"INCORRECT_HEADER\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "        \"gametype\": \"solo\",\n" +
                "        \"players_amount\": 6,\n" +
                "        \"map\": 1,\n" +
                "        \"endgame_cond\": 1,\n" +
                "        \"web_speed\": 3,\n" +
                "        \"mobile_max_speed\": 5,\n" +
                "        \"dwarves_amount\": 4\n" +
                "    }\n" +
                "}";
        client.sendMsg(request);

        try {
            String response1 = client.queue.take();
            assertEquals(MessageType.ERROR, MessageParser.getMsgHeader(response1));
        } catch (InterruptedException | MessageException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void typoInJson_ShouldGetErrorMsg() {
        String request = "{\n" +
                "    \"header\": \"CREATE_LOBBY_REQUEST\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "        \"game_type\": \"solo\",\n" +
                "        \"players_amount\": 6,\n" +
                "        \"map\": 1,\n" +
                "        \"endgame_cond\": 1,\n" +
                "        \"web_speed\": 3,\n" +
                "        \"mobile_max_speed\": 5,\n" +
                "        \"dwarves_amount\": 4\n" +
                "    }\n" +
                "}";
        client.sendMsg(request);

        try {
            String response1 = client.queue.take();
            assertEquals(MessageType.ERROR, MessageParser.getMsgHeader(response1));
        } catch (InterruptedException | MessageException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void wrongTypeInJson_ShouldGetErrorMsg() {
        String request = "{\n" +
                "    \"header\": \"CREATE_LOBBY_REQUEST\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "        \"game_type\": \"solo\",\n" +
                "        \"players_amount\": 6.6,\n" +
                "        \"map\": 1,\n" +
                "        \"endgame_cond\": 1,\n" +
                "        \"web_speed\": 3,\n" +
                "        \"mobile_max_speed\": 5,\n" +
                "        \"dwarves_amount\": 4\n" +
                "    }\n" +
                "}";
        client.sendMsg(request);

        try {
            String response1 = client.queue.take();
            assertEquals(MessageType.ERROR, MessageParser.getMsgHeader(response1));
        } catch (InterruptedException | MessageException e) {
            e.printStackTrace();
        }
    }
}
