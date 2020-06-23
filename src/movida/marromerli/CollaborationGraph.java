package movida.marromerli;

import javafx.util.Pair;
import movida.commons.Movie;

import java.util.LinkedList;

public class CollaborationGraph {
    private LinkedList<Pair<Movie, LinkedList<Movie>>> graph;

    public CollaborationGraph() {
        this.graph = new LinkedList<>();
    }
}
