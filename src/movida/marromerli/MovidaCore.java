package movida.marromerli;

import movida.commons.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MovidaCore implements IMovidaConfig, IMovidaDB, IMovidaSearch, IMovidaCollaborations {
    private SortingAlgorithm sortingAlgorithm; //Algoritmo usato
    private MapImplementation mapImplementation; //Implementazione di dizionario usata

    private List<Movie> moviesOrderedByVotes, moviesOrderedByYear;
    private List<Person> actors, people;

    private Sorter sorter;
    private boolean moviesSortedByVotes, moviesSortedByYear, actorsSorted;


    private Dictionary<String, Person> personByName;

    private Dictionary<String, Movie> moviesByTitle;
    private Dictionary<Integer, List<Movie>> moviesByYear;
    private Dictionary<String, List<Movie>> moviesByDirector;
    private Dictionary<String, List<Movie>> moviesByActor;

    private CollaborationGraph graph;

    public MovidaCore() {
        // Default choices
        this.sortingAlgorithm = SortingAlgorithm.QuickSort;
        this.mapImplementation = MapImplementation.ABR;

        this.moviesOrderedByVotes = new ArrayList<Movie>();
        this.moviesOrderedByYear = new ArrayList<Movie>();
        this.actors = new ArrayList<Person>();
        this.people = new ArrayList<Person>();

        this.moviesSortedByVotes = false;
        this.moviesSortedByYear = false;
        this.actorsSorted = false;
        this.personByName = new ABR<String, Person>();
        this.moviesByTitle = new ABR<String, Movie>();
        this.moviesByYear = new ABR<Integer, List<Movie>>();
        this.moviesByDirector = new ABR<String, List<Movie>>();
        this.moviesByActor = new ABR<String, List<Movie>>();
        this.graph = new CollaborationGraph();
    }

    @Override
    public boolean setMap(MapImplementation m) {
        if ((m == MapImplementation.ArrayOrdinato || m == MapImplementation.ABR) && this.mapImplementation != m) {
            if(m == MapImplementation.ArrayOrdinato){
                this.personByName = new SortedArrayDictionary<String, Person>();
                this.moviesByTitle = new SortedArrayDictionary<String, Movie>();
                this.moviesByYear = new SortedArrayDictionary<Integer, List<Movie>>();
                this.moviesByDirector = new SortedArrayDictionary<String, List<Movie>>();
                this.moviesByActor = new SortedArrayDictionary<String, List<Movie>>();
            }
            else{
                this.personByName = new ABR<String, Person>();
                this.moviesByTitle = new ABR<String, Movie>();
                this.moviesByYear = new ABR<Integer, List<Movie>>();
                this.moviesByDirector = new ABR<String, List<Movie>>();
                this.moviesByActor = new ABR<String, List<Movie>>();
            }

            for(Movie movie : moviesOrderedByVotes){
                importMovie(movie);
            }

            this.mapImplementation = m;

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean setSort(SortingAlgorithm a) {
        if ((a == SortingAlgorithm.BubbleSort || a == SortingAlgorithm.QuickSort) && this.sortingAlgorithm != a) {

            if(a == SortingAlgorithm.BubbleSort){
                sorter = new BubbleSort();
            }
            else sorter = new QuickSort();

            this.sortingAlgorithm = a;
            return true;
        } else {
            return false;
        }
    }

    // TODO: deve anche aggiungere gli attori e i direttori, ma solo quelli non presenti
    //  (necessari altri dizionari per sapere questa info?) e generalmente e' incomleto, non pusha a molte delle strutture
    @Override
    public void loadFromFile(File f) {
        try {
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                //TODO: Abbellire?
                String title = s.nextLine().split(":")[1].trim();
                int year = Integer.parseInt(s.nextLine().split(":")[1].trim());
                String directorName = s.nextLine().split(":")[1].trim();
                String[] castNames = s.nextLine().split(":")[1].trim().split(", ");
                int votes = Integer.parseInt(s.nextLine().split(":")[1].trim());

                Person director = new Person(directorName);
                Person[] cast = new Person[castNames.length];
                for (int i = 0; i < castNames.length; i++) {
                    cast[i] = new Person(castNames[i]);
                }

                Movie movie = new Movie(title, year, votes, cast, director);
                if (s.hasNextLine()) {
                    s.nextLine();
                }

                importMovie(movie);
            }
            s.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new MovidaFileException();
        }
    }

    @Override
    public void saveToFile(File f) {
        try {
            FileWriter writer = new FileWriter(f, false);

            for (Movie movie : this.moviesOrderedByYear) {
                writer.append(movie.toString() + "\n\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new MovidaFileException();
        }
    }

    @Override
    public void clear() {
        moviesOrderedByVotes.clear();
        moviesOrderedByYear.clear();
        actors.clear();
        people.clear();


        personByName.clear();
        moviesByTitle.clear();
        moviesByActor.clear();
        moviesByYear.clear();
        moviesByDirector.clear();

        graph.clear();
    }

    @Override
    public int countMovies() {
        return moviesOrderedByYear.size();
    }

    @Override
    public int countPeople() {
        return people.size();
    }

    @Override
    public boolean deleteMovieByTitle(String title) {
        Movie toBeDeleted = moviesByTitle.search(title);
        if(toBeDeleted == null) return false;

        moviesOrderedByYear.remove(toBeDeleted);
        moviesOrderedByVotes.remove(toBeDeleted);

        moviesByTitle.remove(title);

        String directorName = toBeDeleted.getDirector().getName();
        moviesByDirector.search(directorName).remove(toBeDeleted);
        if(moviesByDirector.search(directorName).size() == 0){
            moviesByDirector.remove(directorName);

            // Se non e' anche un attore, rimuovilo dalle persone
            if(moviesByActor.search(directorName) == null) {
                people.remove(toBeDeleted.getDirector());
                personByName.remove(directorName);
            }
        }

        Integer year = toBeDeleted.getYear();
        moviesByYear.search(year).remove(toBeDeleted);
        if(moviesByYear.search(year).size() == 0){
            moviesByYear.remove(year);
        }

        for(Person actor : toBeDeleted.getCast()){
            String name = actor.getName();
            moviesByActor.search(name).remove(toBeDeleted);
            if(moviesByActor.search(name).size() == 0){
                moviesByActor.remove(name);
                actors.remove(actor);

                // Se non e' anche un direttore, rimuovilo dalle persone
                if(moviesByDirector.search(name) == null){
                    people.remove(actor);
                    personByName.remove(name);
                }
            }
        }

        // TODO: aggiorna collaborationGraph di conseguenza
        return true;
    }

    @Override
    public Movie getMovieByTitle(String title) {
        return moviesByTitle.search(title);
    }

    @Override
    public Person getPersonByName(String name) {
        return personByName.search(name);
    }

    @Override
    public Movie[] getAllMovies() {
        return moviesOrderedByYear.toArray(new Movie[0]);
    }

    @Override
    public Person[] getAllPeople() {
        return people.toArray(new Person[0]);
    }

    @Override
    public Movie[] searchMoviesByTitle(String title) {
        return moviesByTitle.searchAll(title, String::contains).toArray(new Movie[0]);
    }

    @Override
    public Movie[] searchMoviesInYear(Integer year) {
        List<Movie> results = moviesByYear.search(year);
        if(results != null) return results.toArray(new Movie[0]);
        return new Movie[0];
    }

    @Override
    public Movie[] searchMoviesDirectedBy(String name) {
        List<Movie> results = moviesByDirector.search(name);
        if(results != null) return results.toArray(new Movie[0]);
        return new Movie[0];
    }

    @Override
    public Movie[] searchMoviesStarredBy(String name) {
        List<Movie> results = moviesByActor.search(name);
        if(results != null) return results.toArray(new Movie[0]);
        return new Movie[0];
    }

    @Override
    public Movie[] searchMostVotedMovies(Integer N) {
        if(!moviesSortedByVotes){
            sorter.sort(moviesOrderedByVotes, (Movie a, Movie b) -> a.getVotes() - b.getVotes());
            moviesSortedByVotes = true;
        }

        int size = moviesOrderedByVotes.size();
        if(N >= size) return moviesOrderedByVotes.toArray(new Movie[0]);
        return moviesOrderedByVotes.subList(size - N, size).toArray(new Movie[0]);
    }

    @Override
    public Movie[] searchMostRecentMovies(Integer N) {
        if(!moviesSortedByYear){
            sorter.sort(moviesOrderedByYear, (Movie a, Movie b) -> a.getYear() - b.getYear());
            moviesSortedByYear = true;
        }

        int size = moviesOrderedByYear.size();
        if (N >= size) return moviesOrderedByYear.toArray(new Movie[0]);
        return moviesOrderedByYear.subList(size - N, size).toArray(new Movie[0]);
    }

    @Override
    public Person[] searchMostActiveActors(Integer N) {
        if(!actorsSorted){
            sorter.sort(actors, (Person a, Person b) -> moviesByActor.search(a.getName()).size() - moviesByActor.search(a.getName()).size());
            actorsSorted = true;
        }

        int size = actors.size();
        if(N >= size) return actors.toArray(new Person[0]);
        return actors.subList(size - N, size).toArray(new Person[0]);
    }

    @Override
    public Person[] getDirectCollaboratorsOf(Person actor) {
        return graph.getDirectCollaboratorsOf(actor);
    }

    @Override
    public Person[] getTeamOf(Person actor) {
        return new Person[0];
    }

    @Override
    public Collaboration[] maximizeCollaborationsInTheTeamOf(Person actor) {
        return new Collaboration[0];
    }

    private void importMovie(Movie movie){

        String title = movie.getTitle();
        Integer year = movie.getYear();
        Person director = movie.getDirector();
        String directorName = director.getName();
        Person[] cast = movie.getCast();

        // TODO: il search potrebbe dover essere case insensitive
        if(moviesByTitle.search(title) == null) {
            moviesOrderedByVotes.add(movie);
            moviesOrderedByYear.add(movie);
            this.moviesSortedByVotes = this.moviesSortedByYear = false;

            moviesByTitle.insert(title, movie);

            // Se non c'era nessun film con quest'anno registrato
            if(moviesByYear.search(year) == null){
                moviesByYear.insert(year, new ArrayList<>());
            }
            moviesByYear.search(year).add(movie);

            // Se non era fra i direttori finora, aggiungilo
            if(personByName.search(directorName) == null) {
                personByName.insert(directorName, director);
                people.add(director);
                moviesByDirector.insert(directorName, new ArrayList<>());
            }
            moviesByDirector.search(directorName).add(movie);

            for(Person actor : cast) {
                String name = actor.getName();
                // Se non era fra gli attori finora, aggiungilo
                if(moviesByActor.search(name) == null){
                    actors.add(actor);
                    moviesByActor.insert(name, new ArrayList<>());
                    this.actorsSorted = false;
                }
                moviesByActor.search(name).add(movie);

                if(personByName.search(name) == null){
                    personByName.insert(name, actor);
                    people.add(actor);
                }
            }
        }

        // TODO: else - se il film esisteva gia...



        // TODO: pusha nel grafo le informazioni
    }
}
