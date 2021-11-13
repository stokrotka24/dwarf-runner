package messages;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

public class MessageParserTest {
    @ParameterizedTest(name = "{index} => type={0}, expected={1}")
    @CsvSource(value = {
            "SERVER_HELLO; {\"header\":\"SERVER_HELLO\",\"client_id\":0}",
            "CLIENT_HELLO; {\"header\":\"CLIENT_HELLO\",\"client_id\":0}"
    }, delimiter = ';')
    void toJsonString_HeaderOnly(MessageType type, String expected) {
        var msg = new Message<>(type);
        var actual = MessageParser.toJsonString(msg);
        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "{index} => type={0}, a={1}, b={2} expected={1}")
    @CsvSource(value = {
            "SERVER_HELLO; 7; Hello World; {\"header\":\"SERVER_HELLO\",\"client_id\":0,\"content\":{\"a\":7,\"b\":\"Hello World\",\"c\":[1,2,3,4]}}",
            "CLIENT_HELLO; -12345; δ; {\"header\":\"CLIENT_HELLO\",\"client_id\":0,\"content\":{\"a\":-12345,\"b\":\"δ\",\"c\":[1,2,3,4]}}"
    }, delimiter = ';')
    public void toJsonString_WithContent(MessageType type, int a, String b, String expected) {
        var msg = new Message<>(type, new ExampleClass(a, b));
        var actual = MessageParser.toJsonString(msg);
        assertEquals(expected, actual);
    }

    @Test
    public void toJsonString_GenericContent() {
        String expected = "{\"header\":\"LOBBY_LIST_DELIVERY\",\"client_id\":0,\"content\":{\"json\":{\"someInt\":1,\"someString\":\"Hello\",\"someObject\":{\"a\":7,\"b\":\"Hello World\",\"c\":[1,2,3,4]}}}}";
        MessageBuilder builder = new MessageBuilder(MessageType.LOBBY_LIST_DELIVERY);
        builder.addField("someInt", 1);
        builder.addField("someString", "Hello");
        builder.addField("someObject", new ExampleClass(7, "Hello World"));
        var customMessage = builder.get();

        var actual = MessageParser.toJsonString(customMessage);
        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "{index} => msg={0}, expectedType={1}, expectedId={2}")
    @CsvSource(value = {
            "{\"header\":\"SERVER_HELLO\",\"client_id\":0}; SERVER_HELLO; 0",
            "{\"header\":\"CLIENT_HELLO\",\"client_id\":3}; CLIENT_HELLO; 3"
    }, delimiter = ';')
    void fromJsonString_HeaderOnly(String msg, MessageType expectedType, int expectedId) {
        Message actual = MessageParser.fromJsonString(msg, Object.class);
        assertEquals(expectedType, actual.header);
        assertEquals(expectedId, actual.clientId);
    }

    @ParameterizedTest(name = "{index} => msg={0}, expectedA={1}, expectedB={2}")
    @CsvSource(value = {
            "{\"header\":\"SERVER_HELLO\",\"client_id\":0,\"content\":{\"a\":7,\"b\":\"Hello World\",\"c\":[1,2,3,4]}}; 7; Hello World",
            "{\"header\":\"CLIENT_HELLO\",\"client_id\":0,\"content\":{\"a\":-12345,\"b\":\"δ\",\"c\":[1,2,3,4]}}; -12345; δ"
    }, delimiter = ';')
    void fromJsonString_WithContent(String msg, int expectedA, String expectedB) {
        Message<ExampleClass> actual = MessageParser.fromJsonString(msg, ExampleClass.class);
        assertEquals(expectedA, actual.content.a);
        assertEquals(expectedB, actual.content.b);
    }

    @Test
    void fromJsonString_GenericContent() {
        String msg = "{\"header\":\"CREATE_LOBBY_REQUEST\", \"client_id\": 14,\"content\":{\"json\":{\"someInt\":1,\"someString\":\"Hello\",\"someObject\":{\"a\":7,\"b\":\"Hello World\",\"c\":[1,2,3,4]}}}}";

        Message<GenericMsgContent> actual = MessageParser.fromJsonString(msg, GenericMsgContent.class);

        assertTrue(actual.content.hasField("someInt"));
        assertTrue(actual.content.hasField("someObject"));
        assertFalse(actual.content.hasField("someFloat"));

        assertEquals(1, actual.content.get("someInt").getAsInt());
        assertEquals("Hello", actual.content.get("someString").getAsString());
        assertEquals(7, MessageParser.jsonElementToObject(actual.content.get("someObject"), ExampleClass.class).a);
        assertNull(actual.content.get("someFloat"));
    }

    public static class ExampleClass {
        private final int a;
        private final String b;
        private final Integer[] c = {1,2,3,4};

        public ExampleClass(int a, String b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public String getB() {
            return b;
        }

        public Integer[] getC() {
            return c;
        }
    }
}
