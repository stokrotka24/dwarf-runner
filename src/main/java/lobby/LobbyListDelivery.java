package lobby;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LobbyListDelivery {
    @SerializedName("lobbys_amount")
    private int amount;

    @SerializedName("total_lobbys_amount")
    private int total;

    @SerializedName("lobbys_list")
    private List<Lobby> lobbys;

    public LobbyListDelivery(List<Lobby> lobbys, int total) {
        this.lobbys = lobbys;
        amount = lobbys.size();
        this.total = total;
    }

    public int getAmount() {
        return amount;
    }

    public int getTotal() {
        return total;
    }

    public List<Lobby> getLobbys() {
        return lobbys;
    }
}
