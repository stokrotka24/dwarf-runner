package dbconn;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.junit.jupiter.api.*;
import messages.AbstractCommunicationTest;
import utility.ClientMock;

class UserAuthenticatorTest extends AbstractCommunicationTest {
    private static final ClientMock client = new ClientMock("localhost", defaultPort);

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

    /**
     * DUE TO SAVING OF EMAIL AND NICKNAME in DB
     * REMEMBER TO CHANGE
     * EMAIL AND NICKNAME BEFORE USING
     */
    @Test
    void testHandleRegisterRequestShouldSucceed(){
        String request1 = "{\n" +
                "    \"header\": \"REGISTER_REQUEST\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "        \"email\": \"user12336631230@wp.pl\",\n" +
                "        \"password\": \"user2\",\n" +
                "        \"nickname\": \"juserekss366333\"\n" +
                "    }\n" +
                "}";

        client.sendMsg(request1);
        String expected1 = "{\"header\":\"REGISTER_RESPONSE\",\"content\":{\"status\":1,\"failure_reason\":null}}";

        try {
            String response1 = client.queue.take();
            assertEquals(expected1, response1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    void testHandleRegisterRequestShouldFailed(){
        String request1 = "{\n" +
                "    \"header\": \"REGISTER_REQUEST\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "        \"email\": \"USER123330@wp.pl\",\n" +
                "        \"password\": \"user2\",\n" +
                "        \"nickname\": \"juserekss36333\"\n" +
                "    }\n" +
                "}";

        client.sendMsg(request1);
        String expected1 = "{\"header\":\"REGISTER_RESPONSE\",\"content\":{\"status\":0,\"failure_reason\":\"EMAIL_TAKEN\"}}";

        try {
            String response1 = client.queue.take();
            assertEquals(expected1, response1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testHandleRegisterRequestShouldFailed2(){
        String request1 = "{\n" +
                "    \"header\": \"REGISTER_REQUEST\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "        \"email\": \"\",\n" +
                "        \"password\": \"\",\n" +
                "        \"nickname\":\"\"" +
                "    }\n" +
                "}";

        client.sendMsg(request1);
        String expected1 = "{\"header\":\"REGISTER_RESPONSE\",\"content\":{\"status\":0,\"failure_reason\":\"UNKNOWN\"}}";

        try {
            String response1 = client.queue.take();
            assertEquals(expected1, response1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testHandleLoginRequestShouldSucceed() {
        String request1 = "{\n" +
                "    \"header\": \"LOG_IN_REQUEST\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "        \"email\": \"user1@wp.pl\",\n" +
                "        \"password\": \"user1\",\n" +
                "        \"is_mobile\": false\n" +
                "    }\n" +
                "}";
        
        client.sendMsg(request1);

        String expected1 = "{\"header\":\"LOG_IN_RESPONSE\",\"content\":{\"status\":1,\"user_nickname\":\"user1\",\"failure_reason\":null}}";
        try {
            String response1 = client.queue.take();
            assertEquals(expected1, response1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testHandleLoginRequestShouldFail() {
        String request1 = "{\n" +
                "    \"header\": \"LOG_IN_REQUEST\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "        \"email\": \"\",\n" +
                "        \"password\": \"user1\",\n" +
                "        \"is_mobile\": false\n" +
                "    }\n" +
                "}";
        
        client.sendMsg(request1);

        String request2 = "{\n" +
                "    \"header\": \"LOG_IN_REQUEST\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "        \"email\": \"userx@wp.pl\",\n" +
                "        \"password\": \"userx\",\n" +
                "        \"is_mobile\": false\n" +
                "    }\n" +
                "}";
        
        client.sendMsg(request2);

        String request3 = "{\n" +
                "    \"header\": \"LOG_IN_REQUEST\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "        \"email\": \"user1@wp.pl\",\n" +
                "        \"password\": \"user123\",\n" +
                "        \"is_mobile\": false\n" +
                "    }\n" +
                "}";
        
        client.sendMsg(request3);

        //TODO: commented test for already logged in user
        /*String request4 = "{\n" +
                "    \"header\": \"LOG_IN_REQUEST\",\n" +
                "    \"client_id\":" + client.id + ",\n" +
                "    \"content\": {\n" +
                "        \"email\": \"user1@wp.pl\",\n" +
                "        \"password\": \"user1\",\n" +
                "        \"is_mobile\": false\n" +
                "    }\n" +
                "}";
        client.sendMsg(request4);*/

        String expected1 = "{\"header\":\"LOG_IN_RESPONSE\",\"content\":{\"status\":0,\"user_nickname\":null,\"failure_reason\":\"DATA_LOST\"}}";
        String expected2 = "{\"header\":\"LOG_IN_RESPONSE\",\"content\":{\"status\":0,\"user_nickname\":null,\"failure_reason\":\"WRONG_CREDENTIALS\"}}";
        String expected3 = "{\"header\":\"LOG_IN_RESPONSE\",\"content\":{\"status\":0,\"user_nickname\":null,\"failure_reason\":\"WRONG_CREDENTIALS\"}}";
        //String expected4 = "{\"header\":\"LOG_IN_RESPONSE\",\"content\":{\"status\":0,\"user_nickname\":null,\"failure_reason\":\"ALREADY_LOGGED_IN\"}}";
        
        try {
            String response1 = client.queue.take();
            assertEquals(expected1, response1);
            String response2 = client.queue.take();
            assertEquals(expected2, response2);
            String response3 = client.queue.take();
            assertEquals(expected3, response3);
            //String response4 = client.queue.take();
            //assertEquals(expected4, response4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void hash512ShouldSucceed() {
        String toHash = "user123";
        String afterHash;
        MessageDigest digest;
        int hashLength;

        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(toHash.getBytes(StandardCharsets.UTF_8));
            afterHash = Base64.getEncoder().encodeToString(hash);
            hashLength = afterHash.length();
            assertEquals(hashLength, 44);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    void hash256ShouldFail() {
        String toHash = "user123";
        String toHash2 = "user1234";
        String afterHash;
        String afterHash2;
        MessageDigest digest;        

        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(toHash.getBytes(StandardCharsets.UTF_8));
            byte[] hash2 = digest.digest(toHash2.getBytes(StandardCharsets.UTF_8));
            afterHash = Base64.getEncoder().encodeToString(hash);
            afterHash2 = Base64.getEncoder().encodeToString(hash2);
            assertNotEquals(toHash, afterHash);
            assertNotEquals(afterHash, afterHash2);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
