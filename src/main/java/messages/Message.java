package messages;

/**
 * generic Message class
 * @param <T> type of content
 */
public class Message<T> {
    public MessageHeader header;
    public T content;

    public Message(final MessageType type, final T content) {
        header = new MessageHeader(type);
        this.content = content;
    }

    public Message(MessageType type) {
        header = new MessageHeader(type);
    }
}
