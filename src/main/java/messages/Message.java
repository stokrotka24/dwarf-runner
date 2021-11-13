package messages;

import com.google.gson.annotations.SerializedName;

/**
 * generic Message class
 * @param <T> type of content
 */
public class Message<T> {
    public static final int SERVER_ID = 0;

    public MessageType header;

    @SerializedName("client_id")
    public int clientId;
    public T content;

    public Message(final MessageType type, final T content) {
        header = type;
        clientId = SERVER_ID;
        this.content = content;
    }

    public Message(MessageType type) {
        header = type;
    }
}
