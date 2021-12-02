package game.json;

import com.google.gson.annotations.SerializedName;
import game.Dwarf;

import java.util.List;

public class DwarfsLocationListDelivery {
    @SerializedName("dwarfs_list")
    private List<Dwarf> dwarfs;

    public DwarfsLocationListDelivery(List<Dwarf> dwarfs) {
        this.dwarfs = dwarfs;
    }

    public List<Dwarf> getDwarfs() {
        return dwarfs;
    }

    public void setDwarfs(List<Dwarf> dwarfs) {
        this.dwarfs = dwarfs;
    }
}
