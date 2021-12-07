package game.json;

import com.google.gson.annotations.SerializedName;
import messages.JsonRequired;

public class WebMove {
    @JsonRequired
    @SerializedName("arrow")
    private String arrow;

    public String getArrow() {
        return arrow;
    }

    public void setArrow(String arrow) {
        this.arrow = arrow;
    }
}
