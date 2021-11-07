package messages;

/**
 * Represents header of message
 * Server Id should be constant and
 * not assigned to any client
 */
public class MessageHeader {
    public static final int SERVER_ID = 0;

    public final int type;
    public final int senderId;

    public MessageHeader(final MessageType type) {
        this.type = type.ordinal();
        senderId = SERVER_ID;
    }
}
