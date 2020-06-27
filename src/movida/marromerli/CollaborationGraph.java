package movida.marromerli;

import movida.commons.Collaboration;
import movida.commons.Movie;
import movida.commons.Person;

import java.util.*;

/**
 * Rappresenta un grafo di collaborazioni tra attori.
 */
public class CollaborationGraph {
    private HashMap<Person, Set<Collaboration>> graph;

    // TODO: store the resulting graphs (BFS and MST) to return if nothing changes?

    public CollaborationGraph() {
        this.graph = new HashMap<Person, Set<Collaboration>>();
    }

    //TODO: Viene solo usato nei test, rimuovere?
    public boolean addCollaboration(Collaboration collaboration) {
        if (!graph.containsKey(collaboration.getActorA())) {
            graph.put(collaboration.getActorA(), new HashSet<>());
        }
        return graph.get(collaboration.getActorA()).add(collaboration);
    }

    /**
     * Aggiunge un attore. Se l'attore è già presente, non
     * fa nulla.
     *
     * @param actor L'attore da aggiungere
     */
    private void addActor(Person actor) {
        if (!graph.containsKey(actor)) {
            graph.put(actor, new HashSet<>());
        }
    }

    /**
     * Aggiunge una coppia di attori che hanno recitato in
     * uno stesso film. Se sono già stati aggiunti, non fa
     * nulla.
     *
     * @param actor1 Il primo attore da aggiungere
     * @param actor2 Il secondo attore da aggiungere
     * @param movie  Il film in cui hanno recitato
     */
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

    /**
     * Aggiunge un film.
     *
     * @param movie Il film da aggiungere
     */
    public void addMovie(Movie movie) {
        for (Person actor1 : movie.getCast()) {
            for (Person actor2 : movie.getCast()) {
                if (!actor1.equals(actor2)) {
                    addPair(actor1, actor2, movie);
                }
            }
        }
    }

    /**
     * Rimuove un film
     *
     * @param movie Il film da rimuovere
     */
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

    /**
     * Elimina tutti i dati contenuti.
     */
    public void clear() {
        this.graph.clear();
    }

    /**
     * Restituisce le collaborazioni di un attore.
     *
     * @param actor L'attore considerato
     * @return Le collaborazioni di <code>actor</code>
     */
    private Collaboration[] getCollaborationsOf(Person actor) {
        Set<Collaboration> collaborations = graph.get(actor);
        if (collaborations == null) {
            return new Collaboration[0];
        } else {
            return (Collaboration[]) collaborations.toArray(new Collaboration[0]);
        }
    }

    /**
     * Restituisce i collaboratori diretti di un attore.
     *
     * @param actor L'attore considerato
     * @return Le collaborazioni di <code>actor</code>
     */
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

    /**
     * Restituisce il gruppo di un attore.
     *
     * @param actor L'attore considerato
     * @return I collaboratori (diretti e indiretti) di <code>actor</code>
     */
    public Person[] getTeamOf(Person actor) {
        HashSet<Person> visited = new HashSet<>();
        Queue<Person> queue = new LinkedList<>();
        queue.add(actor);

        while (!queue.isEmpty()) {
            Person u = queue.poll();
            for (Collaboration collaboration : getCollaborationsOf(u)) {
                Person collaborator;
                if (collaboration.getActorA().equals(u)) {
                    collaborator = collaboration.getActorB();
                } else {
                    collaborator = collaboration.getActorA();
                }

                if (!visited.contains(collaborator) && collaborator != actor) {
                    visited.add(collaborator);
                    queue.add(collaborator);
                }
            }
        }

        return visited.toArray(new Person[0]);
    }

    /**
     * Restituisce l'insieme di collaborazioni di un attore
     * con il punteggio complessivo più alto.
     *
     * @param actor L'attore considerato
     * @return L'insieme di collaborazioni che massimizza il punteggio
     * complessivo
     */
    public Collaboration[] maximiseCollaborations(Person actor) {
        HashMap<Person, Double> d = new HashMap<>();
        HashMap<Person, Collaboration> bestCollaboration = new HashMap<>();

        d.put(actor, Double.NEGATIVE_INFINITY);

        VariablePriorityQueue<Person> Q = new VariablePriorityQueue<>();
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

        return bestCollaboration.values().toArray(new Collaboration[0]);
    }
}
