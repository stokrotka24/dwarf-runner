package lobby;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LobbyListDelivery {
    @SerializedName("lobbys_amount")
    public int amount;

    @SerializedName("total_lobbys_amount")
    public int total;

    @SerializedName("lobbys_list")
    public List<Lobby> lobbys;

    public LobbyListDelivery(List<Lobby> lobbys, int total) {
        this.lobbys = lobbys;
        amount = lobbys.size();
        this.total = total;
    }
}
