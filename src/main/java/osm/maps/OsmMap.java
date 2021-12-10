package osm.maps;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import osm.Coordinates;
import osm.Node;
import osm.Way;
import server.Logger;

public abstract class OsmMap {

    //public ArrayList<Way> ways;
    public ArrayList<Node> nodes;

    public void parseMap(String file) {
        List<Way> ways = new ArrayList<>();
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
                    nodes.add(new Node(id, lon, lat));
                }
            }
            for (Way way : ways
            ) {
                List<Coordinates> temp = new ArrayList<>();
                for (Long id : way.getNodes()) {
                    temp.add(this.getCoordsById(id));
                }
                nodes.stream().filter(node -> node.getId().equals(way.getNodes().get(0)))
                    .forEach(node -> node.addNeighbor(temp.get(1)));
                for (int i = 1; i < temp.size() - 1; i++) {
                    int finalI = i;
                    nodes.stream().filter(node -> node.getId().equals(way.getNodes().get(finalI)))
                        .forEach(node -> {
                            node.addNeighbor(temp.get(finalI - 1));
                            node.addNeighbor(temp.get(finalI + 1));
                        });
                }
                nodes.stream()
                    .filter(node -> node.getId().equals(way.getNodes().get(temp.size() - 1)))
                    .forEach(node -> node.addNeighbor(temp.get(temp.size() - 2)));
            }
        } catch (IOException | ParseException e) {
            Logger.getInstance().error(e.getMessage());
        }
    }

    public Coordinates getCoordsById(Long id) {
        return nodes.stream().filter(node -> node.getId().equals(id)).findFirst().get().getCoords();
    }

    public Node getNodeByCoords(Coordinates coords) {
        return nodes.stream().filter(node -> node.getCoords().equals(coords)).findFirst().get();
    }
}
