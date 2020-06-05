package Structs;
import java.util.*;

public class Graph<T> {


    // We use Hashmap to store the edges in the graph
    private Map<T, List<T>> map = new HashMap<>();
    private T startingNode;

    // This function adds a new Node to the graph
    public void addNode(T s)
    {
        if(map.isEmpty()) {
            startingNode = s;
        }
        map.put(s, new LinkedList<>());
    }

    public void clear() {
        map.clear();
        startingNode = null;
    }

    public T getStartingNode() {
        return startingNode;
    }

    public void removeNode(T t) {
        for (T v : map.keySet()) {
           map.get(v).removeIf(node -> node.equals(t));
        }
        map.remove(t);
    }

    // This function adds the edge
    // between source to destination
    public void addEdge(T source, T destination)
    {
        if (!map.containsKey(source))
            addNode(source);

        if (!map.containsKey(destination))
            addNode(destination);

        if(!hasEdge(source, destination) && source != destination) {
            map.get(source).add(destination);
        }
    }

    public void removeEdge(T source, T destination) {
        map.get(source).remove(destination);
    }

    public Set<T> getNodes() {
        return map.keySet();
    }

    public boolean edgeExists(T source, T destination) {
        return map.get(source).contains(destination);
    }

    public List<T> getEdges(T t) {
        return map.get(t);
    }

    public int isEdgeCount(T t) {
        int count = 0;
        for (T v : map.keySet()) {
            if(map.get(v).contains(t)) {
                count++;
            }
        }
        return count;
    }

    public int getEdgesForNodeCount(T t) {
        return getEdges(t).size();
    }

    // This function gives the count of vertices
    public int getNodeCount()
    {
        return map.keySet().size();
    }

    // This function gives the count of edges
    public int getEdgesCount()
    {
        int count = 0;
        for (T v : map.keySet()) {
            count += map.get(v).size();
        }

        return count;
    }

    // This function gives whether
    // a Node is present or not.
    public boolean hasNode(T s)
    {
        return map.containsKey(s);
    }

    // This function gives whether an edge is present or not.
    public boolean hasEdge(T s, T d)
    {
        return map.get(s).contains(d);
    }

    public boolean isEdge(T s) {
        for(T t: map.keySet()) {
            if(map.get(t).contains(s)) {
                return true;
            }
        }
        return false;
    }

    private List<T> DFS(T t, List<T> visited)
    {
        visited.add(t);
        for (T n : getEdges(t)) {
            if (!visited.contains(n)) {
                DFS(n, visited);
            }
        }
        return visited;
    }

    public List<T> DFS()
    {
        return DFS(getStartingNode(), new ArrayList<>());
    }

    public boolean isIsolatedNode(T s) {
        return !isEdge(s) && map.get(s).isEmpty();
    }

    public void trimLeaves() {
        map.keySet().removeIf(key -> map.get(key).isEmpty());
    }


    public Map<T, List<T>> getMap() {
        return map;
    }

    // Prints the adjancency list of each Node.
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (T v : map.keySet()) {
            i = 0;
            builder.append(v.toString());
            if(map.get(v).size() > 0) {
                builder.append( " -> ");
            }

            for (T w : map.get(v)) {
                i++;
                builder.append(w.toString());
                if(map.get(v).size() != i) {
                   builder.append(", ");
                }
            }
            builder.append(";");
            builder.replace(builder.toString().length() - 2, builder.toString().length() - 2, "");
            builder.append("\n");
        }

        return (builder.toString());
    }


} 