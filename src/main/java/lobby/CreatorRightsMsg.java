package lobby;

import com.google.gson.annotations.SerializedName;

public class CreatorRightsMsg {
    @SerializedName("lobby_id")
    private int id;

    public CreatorRightsMsg(int id) {
        this.id = id;
    }
}
