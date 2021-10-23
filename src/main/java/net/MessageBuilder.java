package net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Builder stores json object
 * allowing to build msg content as wished
 */
public class MessageBuilder implements IMessageBuilder {
    private final JsonObject json = new JsonObject();
    private final Gson gson = new Gson();
    private MessageTypes type = MessageTypes.ERROR;

    public MessageBuilder(final MessageTypes type) {
        this.type = type;
    }

    public MessageBuilder() {}

    @Override
    public <T> void addField(String label, T value) {
        json.add(label, gson.toJsonTree(value));
    }

    @Override
    public Message<GenericMsgContent> get() {
        return new Message<>(type, new GenericMsgContent(json));
    }

    @Override
    public void setType(final MessageTypes type) {
        this.type = type;
    }
}
