package messages;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MessageParser {
    private static final ExclusionStrategy strategy = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes field) {
            return field.getName().equals("clientId");
        }
    };

    private static final Gson gson = new GsonBuilder()
            .addSerializationExclusionStrategy(strategy)
            .serializeNulls()
            .create();

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
    public static <T, G> T fromJsonString(final String msg, Class<G> classType) throws MessageException {
        try {
            T parsed = gson.fromJson(msg, TypeToken.getParameterized(Message.class, classType).getType());
            if (checkRequiredFields(classType.getDeclaredFields(), ((Message<G>)(parsed)).content)) {
                return parsed;
            }
        } catch (Exception ignored) {
        }
        throw new MessageException("Server could not parse Message. Content should be " +
                classType.toString() + " which has serializable fields: " +
                getListOfSerializableFields(classType));
    }

    /**
     * Retrieves just the header of string message
     * @param msg string msg to parse
     * @return header of message
     */
    public static MessageType getMsgHeader(final String msg) throws MessageException {
        try {
            Message<Object> parsed = gson.fromJson(msg, TypeToken.getParameterized(Message.class, Object.class).getType());
            if (parsed.header == null) {
                throw new MessageException("Server could not recognize message header. " +
                        "Please, check if your message has header field compatible with documentation.");
            }
            return parsed.header;
        } catch (Exception e) {
            throw new MessageException("Server could not recognize message header. " +
                    "Please, check if your message has header field compatible with documentation.");
        }
    }

    /**
     * Retrieves just the clientId of string message
     * @param msg string msg to parse
     * @return header of message
     */
    public static int getClientId(final String msg) throws MessageException {
        try {
            var jObj = gson.fromJson(msg, JsonObject.class);
            if (jObj.has("client_id")) {
                return jObj.get("client_id").getAsInt();
            }

        } catch (Exception ignored) {
        }
        throw new MessageException("Incorrect general message structure");
    }

    /**
     * Retrieves just the content of string message
     * @param msg string msg to parse
     * @param classType class type of content
     * @param <T> Type of content
     * @return content
     */
    public static <T> T getMsgContent(final String msg, Class<T> classType) throws MessageException {
        Message<T> parsed = gson.fromJson(msg, TypeToken.getParameterized(Message.class, classType).getType());

        if (parsed.content != null) {
            if (checkRequiredFields(classType.getDeclaredFields(), parsed.content)) {
                return parsed.content;
            }
        }
        throw new MessageException("Server could not parse Message. Content should be " +
                classType.toString() + " which has serializable fields: " +
                getListOfSerializableFields(classType));
    }

    /**
     * Instead of string, parses JsonElement to specified type
     * @param element jsonElement to parse
     * @param classType class type of element
     * @param <T> Type of element
     * @return parsed element
     */
    public static <T> T jsonElementToObject(JsonElement element, Class<T> classType) throws MessageException {
        try {
            return gson.fromJson(element, classType);
        } catch (Exception ex) {
            throw new MessageException("Server could not parse Message. Content should be " +
                    classType.toString() + " which has serializable fields: " +
                    getListOfSerializableFields(classType));
        }

    }

    private static <G> String getListOfSerializableFields(Class<G> classType) {
        var fields = classType.getDeclaredFields();
        List<String> annotations = new ArrayList<>();
        for (var f : fields) {
            var name = f.getAnnotation(SerializedName.class);
            if (name != null) {
                annotations.add(name.value() + ": " + f.getType().getSimpleName());
            }
        }
        return annotations.toString();
    }

    private static boolean checkRequiredFields(Field[] fields, Object pojo) {
        if (pojo instanceof List) {
            final List pojoList = (List) pojo;
            for (final Object pojoListPojo : pojoList) {
                if (!checkRequiredFields(pojoListPojo.getClass().getDeclaredFields(), pojoListPojo)) {
                    return false;
                }
            }
            return true;
        }

        for (Field f : fields) {
            if (f.getAnnotation(JsonRequired.class) != null) {
                try {
                    f.setAccessible(true);
                    Object fieldObject = f.get(pojo);
                    if (fieldObject == null ||
                            !checkRequiredFields(fieldObject.getClass().getDeclaredFields(), fieldObject)) {
                        return false;
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new JsonParseException(e);
                }
            }
        }
        return true;
    }
}