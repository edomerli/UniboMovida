package movida.marromerli;

import movida.commons.Collaboration;
import movida.commons.Movie;
import movida.commons.Person;

import java.util.*;

public class CollaborationGraph {
    private HashMap<Person, Set<Collaboration>> graph;

    // TODO: store the resulting graphs (BFS and MST) to return if nothing changes?

    public CollaborationGraph() {
        this.graph = new HashMap<Person, Set<Collaboration>>();
    }

    public boolean addCollaboration(Collaboration collaboration) {
        if (!graph.containsKey(collaboration.getActorA())) {
            graph.put(collaboration.getActorA(), new HashSet<>());
        }
        return graph.get(collaboration.getActorA()).add(collaboration);
    }

    private void addActor(Person actor) {
        if (!graph.containsKey(actor)) {
            graph.put(actor, new HashSet<>());
        }
    }

    private void addPair(Person actor1, Person actor2, Movie movie) {
        Person actorA, actorB;
        if (actor1.compareTo(actor2) > 0) {
            actorA = actor1;
            actorB = actor2;
        } else {
            actorA = actor2;
            actorB = actor1;
        }

        //Ignorati se sono già presenti
        addActor(actorA);
        addActor(actorB);

        Collaboration collaboration = null;
        Set<Collaboration> collaborations = graph.get(actorA);

        for (Collaboration c : collaborations) {
            if (c.getActorB().equals(actorB)) {
                collaboration = c;
            }
        }

        if (collaboration == null) {
            //Non c'è una collaborazione, creiamola
            collaboration = new Collaboration(actorA, actorB);

            //Aggiungiamo la collaborazione per entrambi
            graph.get(actorA).add(collaboration);
            graph.get(actorB).add(collaboration);
        }

        if (!collaboration.getMovies().contains(movie)) {
            collaboration.getMovies().add(movie);
        }
    }

    //TODO: Ottimizzare?
    public void addMovie(Movie movie) {
        for (Person actor1 : movie.getCast()) {
            for (Person actor2 : movie.getCast()) {
                if (!actor1.equals(actor2)) {
                    addPair(actor1, actor2, movie);
                }
            }
        }
    }

    public void removeMovie(Movie movie) {
        List<Person> actorsToRemove = new ArrayList<>();
        for (Person actor : movie.getCast()) {
            List<Collaboration> collaborationsToRemove = new ArrayList<>();
            Set<Collaboration> collaborations = graph.get(actor);

            for (Collaboration collaboration : collaborations) {
                collaboration.getMovies().remove(movie);
                if (collaboration.getMovies().isEmpty()) {
                    collaborationsToRemove.add(collaboration);
                }
            }

            //Remove empty collaborations
            for (Collaboration collaborationToRemove : collaborationsToRemove) {
                collaborations.remove(collaborationToRemove);
            }

            if (collaborations.isEmpty()) {
                actorsToRemove.add(actor);
            }
        }

        for (Person actorToRemove : actorsToRemove) {
            graph.remove(actorToRemove);
        }
    }

    public void clear() {
        this.graph.clear();
    }

    private Collaboration[] getCollaborationsOf(Person actor) {
        Set<Collaboration> collaborations = graph.get(actor);
        if (collaborations == null) {
            return new Collaboration[0];
        } else {
            return (Collaboration[]) collaborations.toArray(new Collaboration[collaborations.size()]);
        }
    }

    public Person[] getDirectCollaboratorsOf(Person actor) {
        Collaboration[] collaborations = getCollaborationsOf(actor);
        Person[] collaborators = new Person[collaborations.length];

        for (int i = 0; i < collaborations.length; i++) {
            if (collaborations[i].getActorA().equals(actor)) {
                collaborators[i] = collaborations[i].getActorB();
            } else {
                collaborators[i] = collaborations[i].getActorA();
            }
        }

        return collaborators;
    }

    public Person[] getTeamOf(Person actor) {
        HashSet<Person> visited = new HashSet<>();
        Queue<Person> queue = new LinkedList<>();
        queue.add(actor);

        while(!queue.isEmpty()){
            Person u = queue.poll();
            for (Collaboration collaboration : getCollaborationsOf(u)) {
                Person collaborator;
                if (collaboration.getActorA().equals(u)) {
                    collaborator = collaboration.getActorB();
                } else {
                    collaborator = collaboration.getActorA();
                }

                if(!visited.contains(collaborator) && collaborator != actor) {
                    visited.add(collaborator);
                    queue.add(collaborator);
                }
            }
        }

        return visited.toArray(new Person[0]);
    }


    //PriorityQueue implementata per essere più simile in
    //funzionamento alla PriorityQueue studiata a lezione
    static class DictionaryPriorityQueue<K extends Comparable<K>> {
        private PriorityQueue<Entry<K>> queue;

        static class Entry<K extends Comparable<K>> implements Comparable<Entry<K>> {
            private K key;
            private double priority;

            public Entry(K key, double priority) {
                this.key = key;
                this.priority = priority;
            }

            public K getKey() {
                return this.key;
            }

            public double getPriority() {
                return this.priority;
            }

            @Override
            public int compareTo(Entry<K> entry) {
                return this.key.compareTo(entry.getKey());
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if ((obj instanceof Entry)) {
                    Entry other = (Entry) obj;
                    return this.key.equals(other.key);
                } else {
                    return false;
                }
            }
        }

        public DictionaryPriorityQueue() {
            queue = new PriorityQueue<>();
        }

        public void add(K key, double priority) {
            queue.add(new Entry<>(key, priority));
        }

        public K remove() {
            Entry<K> entry = queue.remove();
            return entry.getKey();
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }

        public void updatePriority(K key, double newPriority) {
            //Non sappiamo quant'è la sua priorità, ma non
            //importa per l'eliminazione
            if (!queue.contains(new Entry<>(key, -1))) {
                throw new RuntimeException("La chiave non esiste.");
            }

            queue.remove(new Entry<>(key, -1));
            queue.add(new Entry<>(key, newPriority));
        }
    }

    public Collaboration[] maximiseCollaborations(Person actor) {
        HashMap<Person, Double> d = new HashMap<>();
        HashMap<Person, Collaboration> bestCollaboration = new HashMap<>();

        d.put(actor, Double.NEGATIVE_INFINITY);

        DictionaryPriorityQueue<Person> Q = new DictionaryPriorityQueue<>();
        Q.add(actor, Double.NEGATIVE_INFINITY);

        while (!Q.isEmpty()) {
            Person u = Q.remove();
            for (Collaboration collaboration : getCollaborationsOf(u)) {
                Person collaborator;
                if (collaboration.getActorA().equals(u)) {
                    collaborator = collaboration.getActorB();
                } else {
                    collaborator = collaboration.getActorA();
                }

                if (!d.containsKey(collaborator)) {
                    Q.add(collaborator, -collaboration.getScore());
                    d.put(collaborator, -collaboration.getScore());
                    bestCollaboration.put(collaborator, collaboration);
                } else if (-collaboration.getScore() < d.get(collaborator)) {
                    Q.updatePriority(collaborator, -collaboration.getScore());
                    d.put(collaborator, -collaboration.getScore());
                    bestCollaboration.put(collaborator, collaboration);
                }
            }
        }

        return bestCollaboration.values().toArray(new Collaboration[bestCollaboration.size()]);
    }
}
