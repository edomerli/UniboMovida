package movida.marromerli;

import movida.commons.Collaboration;
import movida.commons.Person;

import java.util.ArrayList;
import java.util.HashMap;

public class CollaborationGraph {
    private HashMap<Person, ArrayList<Collaboration>> graph;

    public CollaborationGraph() {
        this.graph = new HashMap<Person, ArrayList<Collaboration>>();
    }

    public void clear() {
        this.graph.clear();
    }
}
