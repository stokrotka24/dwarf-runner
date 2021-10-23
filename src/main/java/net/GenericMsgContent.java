package net;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GenericMsgContent {
    private JsonObject json;

    public GenericMsgContent(final JsonObject json) {
        this.json = json;
    }

    public GenericMsgContent() {}

    public boolean hasField(final String label) {
        return json.has(label);
    }

    public JsonElement get(final String label) {
        return json.get(label);
    }
}
