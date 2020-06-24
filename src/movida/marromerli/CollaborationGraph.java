package movida.marromerli;

import movida.commons.Collaboration;
import movida.commons.Person;

import java.util.ArrayList;

public class CollaborationGraph {
    private Dictionary<Person, ArrayList<Collaboration>> graph;

    public CollaborationGraph() {
        this.graph = new ABR<Person, ArrayList<Collaboration>>();
    }
}
