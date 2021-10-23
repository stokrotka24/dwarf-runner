package net;

import com.google.gson.JsonElement;

public interface IMessageParser<T> {
    <G> String toJsonString(final Message<G> msg);

    <G> G jsonElementToObject(JsonElement element, Class<G> classType);
    Message<T> fromJsonString(final String msg);
}
