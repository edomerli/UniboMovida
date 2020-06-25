package movida.marromerli;

import movida.commons.Collaboration;
import movida.commons.Movie;
import movida.commons.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;

public class CollaborationGraph {
    private HashMap<Person, Set<Collaboration>> graph;

    public CollaborationGraph() {
        this.graph = new HashMap<Person, Set<Collaboration>>();
    }

    public void clear() {
        this.graph.clear();
    }

    private Collaboration[] getCollaborationsOf(Person actor) {
        Set<Collaboration> collaborations = graph.get(actor);
        if (collaborations == null) {
            return new Collaboration[0];
        } else {
            return (Collaboration[]) collaborations.toArray();
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
            queue.remove(new Entry<>(key, -1));
            queue.add(new Entry<>(key, newPriority));
        }
    }

    public Collaboration[] maximiseCollaborations(Person actor) {
        HashMap<Person, Double> d = new HashMap<>();
        HashMap<Person, Collaboration> bestCollaboration = new HashMap<>();

        d.put(actor, 0.0);

        DictionaryPriorityQueue<Person> Q = new DictionaryPriorityQueue<>();
        Q.add(actor, 0.0);

        while (!Q.isEmpty()) {
            Person u = Q.remove();
            for (Collaboration collaboration : getCollaborationsOf(u)) {
                Person collaborator;
                if (collaboration.getActorA().equals(u)) {
                    collaborator = collaboration.getActorB();
                } else {
                    collaborator = collaboration.getActorA();
                }

                bestCollaboration.put(collaborator, collaboration);

                if (!d.containsKey(collaborator)) {
                    Q.add(collaborator, -collaboration.getScore());
                } else if (-collaboration.getScore() < d.get(collaborator)) {
                    Q.updatePriority(collaborator, -collaboration.getScore());
                }
            }
        }

        return (Collaboration[]) bestCollaboration.values().toArray();
    }
}
