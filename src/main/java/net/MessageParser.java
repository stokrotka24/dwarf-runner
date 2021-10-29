package net;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

public class MessageParser {
    private static final Gson gson = new Gson();

    /**
     * Converts Message of any type to json string
     * @param msg msg object to be parsed
     * @param <T> type of content
     * @return json string
     */
    public static <T> String toJsonString(final Message<T> msg) {
        return gson.toJson(msg);
    }

    /**
     * Parses String to message with content of specified type
     * @param msg string msg to parse
     * @param classType class type of message content
     * @param <T> Message<G>
     * @param <G> Type of content
     * @return parsed Message<G> object
     */
    public static <T, G> T fromJsonString(final String msg, Class<G> classType) {
        return gson.fromJson(msg, TypeToken.getParameterized(Message.class, classType).getType());
    }

    /**
     * Retrieves just the header of string message
     * @param msg string msg to parse
     * @return header of message
     */
    public static MessageHeader getMsgHeader(final String msg) {
        Message<Object> parsed = gson.fromJson(msg, TypeToken.getParameterized(Message.class, Object.class).getType());
        return parsed.header;
    }

    /**
     * Retrieves just the content of string message
     * @param msg string msg to parse
     * @param classType class type of content
     * @param <T> Type of content
     * @return content
     */
    public static <T> T getMsgContent(final String msg, Class<T> classType) {
        Message<T> parsed = gson.fromJson(msg, TypeToken.getParameterized(Message.class, classType).getType());
        return parsed.content;
    }

    /**
     * Instead of string, parses JsonElement to specified type
     * @param element jsonElement to parse
     * @param classType class type of element
     * @param <T> Type of element
     * @return parsed element
     */
    public static <T> T jsonElementToObject(JsonElement element, Class<T> classType) {
        return gson.fromJson(element, classType);
    }
}