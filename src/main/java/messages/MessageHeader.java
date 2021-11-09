package messages;

/**
 * Represents header of message
 * Server Id should be constant and
 * not assigned to any client
 */
public class MessageHeader {

    public final int type;

    public MessageHeader(final MessageType type) {
        this.type = type.ordinal();
    }
}
