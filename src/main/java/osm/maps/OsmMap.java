package osm.maps;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import osm.Node;
import osm.Way;

public abstract class OsmMap {

    public ArrayList<Way> ways;
    public ArrayList<Node> nodes;

    public void parseMap(String file) {
        ways = new ArrayList<>();
        nodes = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(file)) {
            JSONObject obj = (JSONObject) jsonParser.parse(reader);
            JSONArray osmMap = (JSONArray) obj.get("elements");
            for (Object o : osmMap) {
                JSONObject element = (JSONObject) o;
                if (element.get("type").toString().equals("way")) {
                    Long id = (Long) element.get("id");
                    ArrayList<Long> _nodes = new ArrayList<>();
                    JSONArray jNodes = (JSONArray) element.get("nodes");
                    for (Object jNode : jNodes) {
                        _nodes.add((Long) jNode);
                    }
                    ways.add(new Way(id, _nodes));
                } else {
                    Long id = (Long) element.get("id");
                    Double lat = (Double) element.get("lat");
                    Double lon = (Double) element.get("lon");
                    nodes.add(new Node(id, lat, lon));
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
