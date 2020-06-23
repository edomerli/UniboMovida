package movida.marromerli;

import movida.commons.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MovidaCore implements IMovidaConfig, IMovidaDB, IMovidaSearch {
    private SortingAlgorithm sortingAlgorithm; //Algoritmo usato
    private MapImplementation mapImplementation; //Implementazione di dizionario usata

    private List<Movie> moviesOrderedByVotes, moviesOrderedByYear;
    private List<Person> actors;
    private List<Person> directors;
    // TODO: E' necessario distinguere attori e direttori in 2 array diversi?

    private boolean moviesSortedByVotes, moviesSortedByYear, actorsSorted;

    private Sorter sorter;


    private Dictionary<String, Movie> moviesByTitle;
    private Dictionary<Integer, List<Movie>> moviesByYear;
    private Dictionary<String, List<Movie>> moviesByDirector;
    private Dictionary<String, List<Movie>> moviesByActor;

    private CollaborationGraph graph;

    public MovidaCore() {
        this.sortingAlgorithm = null;
        this.mapImplementation = null;

        this.moviesOrderedByVotes = new ArrayList<Movie>();
        this.moviesOrderedByYear = new ArrayList<Movie>();
        this.actors = new ArrayList<Person>();
        this.directors = new ArrayList<Person>();

        this.moviesSortedByVotes = false;
        this.moviesSortedByYear = false;
        this.actorsSorted = false;

        this.moviesByTitle = null;
        this.moviesByYear = null;
        this.moviesByDirector = null;
        this.moviesByActor = null;
        this.graph = null;
    }

    @Override
    public boolean setMap(MapImplementation m) {
        if ((m == MapImplementation.ArrayOrdinato || m == MapImplementation.ABR) && this.mapImplementation != m) {
            if(m == MapImplementation.ArrayOrdinato){
                this.moviesByTitle = new SortedArrayDictionary<String, Movie>();
                this.moviesByYear = new SortedArrayDictionary<Integer, Movie[]>();
                this.moviesByDirector = new SortedArrayDictionary<String, Movie[]>();
                this.moviesByActor = new SortedArrayDictionary<String, Movie[]>();
                // TODO: graph deve usare lo stesso dizionario che e' settato?
            }
            else{
                this.moviesByTitle = new ABR<String, Movie>();
                this.moviesByYear = new ABR<Integer, List<Movie>>();
                this.moviesByDirector = new ABR<String, List<Movie>>();
                this.moviesByActor = new ABR<String, List<Movie>>();
            }

            this.mapImplementation = m;
            return true;
        } else {
            return false;
        }
        //TODO: è necessario ricostruire da capo il database?
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
    //  (necessari altri dizionari per sapere questa info?)
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

                moviesOrderedByVotes.add(movie);
                moviesOrderedByYear.add(movie);
            }
            s.close();

            this.moviesSortedByVotes = this.moviesSortedByYear = false;
            // TODO: this.actorsSorted = false;
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

    }

    @Override
    public int countMovies() {
        return moviesOrderedByYear.size();
    }

    @Override
    public int countPeople() {
        return 0;
    }

    @Override
    public boolean deleteMovieByTitle(String title) {
        return false;
    }

    @Override
    public Movie getMovieByTitle(String title) {
        return null;
    }

    @Override
    public Person getPersonByName(String name) {
        return null;
    }

    @Override
    public Movie[] getAllMovies() {
        return moviesOrderedByYear.toArray(new Movie[0]);
    }

    // TODO: E se un attore fosse anche stato un direttore?? Ritornerei dei duplicati
    @Override
    public Person[] getAllPeople() {
        List<Person> people = actors;
        people.addAll(directors);
        return people.toArray(new Person[0]);
    }

    @Override
    public Movie[] searchMoviesByTitle(String title) {
        return new Movie[0];
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
            // TODO: throw exception se non e' stato settato il sorter?
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
            // TODO: throw exception se non e' stato settato il sorter?
            sorter.sort(moviesOrderedByYear, (Movie a, Movie b) -> a.getYear() - b.getYear());
            moviesSortedByYear = true;
        }

        int size = moviesOrderedByYear.size();
        if(N >= size) return moviesOrderedByYear.toArray(new Movie[0]);
        return moviesOrderedByYear.subList(size - N, size).toArray(new Movie[0]);
    }

    @Override
    public Person[] searchMostActiveActors(Integer N) {
        return new Person[0];
    }
}
