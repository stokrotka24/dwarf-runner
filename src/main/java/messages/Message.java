package messages;

import com.google.gson.annotations.SerializedName;

/**
 * generic Message class
 * @param <T> type of content
 */
public class Message<T> {

    public MessageType header;

    @SerializedName("client_id")
    public int clientId;
    public T content;

    public Message(final MessageType type, final T content) {
        header = type;
        this.content = content;
    }

    public Message(MessageType type) {
        header = type;
    }
}
