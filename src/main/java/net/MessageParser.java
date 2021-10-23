package net;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

public class MessageParser<T> implements IMessageParser<T> {
    private final Gson gson = new Gson();
    final Class<T> typeParameterClass;

    public MessageParser(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    public <G> String toJsonString(final Message<G> msg) {
        return gson.toJson(msg);
    }

    public <G> G jsonElementToObject(JsonElement element, Class<G> classType) {
        return gson.fromJson(element, classType);
    }

    public Message<T> fromJsonString(final String msg) {
        return gson.fromJson(msg, TypeToken.getParameterized(Message.class, typeParameterClass).getType());
    }
}
