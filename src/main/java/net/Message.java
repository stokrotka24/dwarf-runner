package net;

/**
 * generic Message class
 * @param <T> type of content
 */
public class Message<T> {
    public MessageHeader header;
    public T content;

    public Message(final MessageTypes type, final T content) {
        header = new MessageHeader(type);
        this.content = content;
    }

    public Message(MessageTypes type) {
        header = new MessageHeader(type);
    }
}
