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


    private Dictionary<CaseInsensitiveString, Person> personByName;

    private Dictionary<CaseInsensitiveString, Movie> moviesByTitle;
    private Dictionary<Integer, List<Movie>> moviesByYear;
    private Dictionary<CaseInsensitiveString, List<Movie>> moviesByDirector;
    private Dictionary<CaseInsensitiveString, List<Movie>> moviesByActor;

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
        this.personByName = new ABR<>();
        this.moviesByTitle = new ABR<>();
        this.moviesByYear = new ABR<Integer, List<Movie>>();
        this.moviesByDirector = new ABR<>();
        this.moviesByActor = new ABR<>();
        this.graph = new CollaborationGraph();
    }

    @Override
    public boolean setMap(MapImplementation m) {
        if ((m == MapImplementation.ArrayOrdinato || m == MapImplementation.ABR) && this.mapImplementation != m) {
            if(m == MapImplementation.ArrayOrdinato) {
                this.personByName = new SortedArrayDictionary<>();
                this.moviesByTitle = new SortedArrayDictionary<>();
                this.moviesByYear = new SortedArrayDictionary<Integer, List<Movie>>();
                this.moviesByDirector = new SortedArrayDictionary<>();
                this.moviesByActor = new SortedArrayDictionary<>();
            }
            else {
                this.personByName = new ABR<>();
                this.moviesByTitle = new ABR<>();
                this.moviesByYear = new ABR<>();
                this.moviesByDirector = new ABR<>();
                this.moviesByActor = new ABR<>();
            }

            for(Movie movie : moviesOrderedByVotes) {
                String title = movie.getTitle();
                CaseInsensitiveString caseInsensitiveTitle = new CaseInsensitiveString(title);
                Integer year = movie.getYear();
                Person director = movie.getDirector();
                String directorName = director.getName();
                CaseInsensitiveString caseInsensitiveDirectorName = new CaseInsensitiveString(directorName);
                Person[] cast = movie.getCast();

                personByName.insert(caseInsensitiveDirectorName, director);
                moviesByTitle.insert(caseInsensitiveTitle, movie);
                if (moviesByYear.search(year) == null) moviesByYear.insert(year, new ArrayList<>());
                moviesByYear.search(year).add(movie);
                if (moviesByDirector.search(caseInsensitiveDirectorName) == null)
                    moviesByDirector.insert(caseInsensitiveDirectorName, new ArrayList<>());
                moviesByDirector.search(caseInsensitiveDirectorName).add(movie);

                for (Person actor : cast) {
                    String name = actor.getName();
                    CaseInsensitiveString caseInsensitiveName = new CaseInsensitiveString(name);
                    if (moviesByActor.search(caseInsensitiveName) == null)
                        moviesByActor.insert(caseInsensitiveName, new ArrayList<>());
                    moviesByActor.search(caseInsensitiveName).add(movie);
                }

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
        CaseInsensitiveString caseInsensitiveTitle = new CaseInsensitiveString(title);
        Movie toBeDeleted = moviesByTitle.search(caseInsensitiveTitle);
        if (toBeDeleted == null) return false;

        moviesOrderedByYear.remove(toBeDeleted);
        moviesOrderedByVotes.remove(toBeDeleted);

        moviesByTitle.remove(caseInsensitiveTitle);

        String directorName = toBeDeleted.getDirector().getName();
        CaseInsensitiveString caseInsensitiveDirectorName = new CaseInsensitiveString(directorName);
        moviesByDirector.search(caseInsensitiveDirectorName).remove(toBeDeleted);
        if (moviesByDirector.search(caseInsensitiveDirectorName).size() == 0) {
            moviesByDirector.remove(caseInsensitiveDirectorName);

            // Se non e' anche un attore, rimuovilo dalle persone
            if (moviesByActor.search(caseInsensitiveDirectorName) == null) {
                people.remove(toBeDeleted.getDirector());
                personByName.remove(caseInsensitiveDirectorName);
            }
        }

        Integer year = toBeDeleted.getYear();
        moviesByYear.search(year).remove(toBeDeleted);
        if(moviesByYear.search(year).size() == 0){
            moviesByYear.remove(year);
        }

        for(Person actor : toBeDeleted.getCast()) {
            String name = actor.getName();
            CaseInsensitiveString caseInsensitiveName = new CaseInsensitiveString(name);
            moviesByActor.search(caseInsensitiveName).remove(toBeDeleted);
            if (moviesByActor.search(caseInsensitiveName).size() == 0) {
                moviesByActor.remove(caseInsensitiveName);
                actors.remove(actor);

                // Se non e' anche un direttore, rimuovilo dalle persone
                if (moviesByDirector.search(caseInsensitiveName) == null) {
                    people.remove(actor);
                    personByName.remove(caseInsensitiveName);
                }
            }
        }

        this.graph.removeMovie(toBeDeleted);
        return true;
    }

    @Override
    public Movie getMovieByTitle(String title) {
        CaseInsensitiveString caseInsensitiveTitle = new CaseInsensitiveString(title);
        return moviesByTitle.search(caseInsensitiveTitle);
    }

    @Override
    public Person getPersonByName(String name) {
        CaseInsensitiveString caseInsensitiveName = new CaseInsensitiveString(name);
        return personByName.search(caseInsensitiveName);
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
        CaseInsensitiveString caseInsensitiveTitle = new CaseInsensitiveString(title);
        return moviesByTitle.searchAll(caseInsensitiveTitle, CaseInsensitiveString::contains).toArray(new Movie[0]);
    }

    @Override
    public Movie[] searchMoviesInYear(Integer year) {
        List<Movie> results = moviesByYear.search(year);
        if(results != null) return results.toArray(new Movie[0]);
        return new Movie[0];
    }

    @Override
    public Movie[] searchMoviesDirectedBy(String name) {
        CaseInsensitiveString caseInsensitiveName = new CaseInsensitiveString(name);
        List<Movie> results = moviesByDirector.search(caseInsensitiveName);
        if (results != null) return results.toArray(new Movie[0]);
        return new Movie[0];
    }

    @Override
    public Movie[] searchMoviesStarredBy(String name) {
        CaseInsensitiveString caseInsensitiveName = new CaseInsensitiveString(name);
        List<Movie> results = moviesByActor.search(caseInsensitiveName);
        if (results != null) return results.toArray(new Movie[0]);
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
            sorter.sort(actors, (Person a, Person b) -> moviesByActor.search(new CaseInsensitiveString(a.getName())).size() - moviesByActor.search(new CaseInsensitiveString(a.getName())).size());
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
        CaseInsensitiveString caseInsensitiveTitle = new CaseInsensitiveString(title);
        Integer year = movie.getYear();
        Person director = movie.getDirector();
        String directorName = director.getName();
        CaseInsensitiveString caseInsensitiveDirectorName = new CaseInsensitiveString(directorName);
        Person[] cast = movie.getCast();

        // TODO: il search potrebbe dover essere case insensitive
        if (moviesByTitle.search(caseInsensitiveTitle) == null) {
            moviesOrderedByVotes.add(movie);
            moviesOrderedByYear.add(movie);
            this.moviesSortedByVotes = this.moviesSortedByYear = false;

            moviesByTitle.insert(caseInsensitiveTitle, movie);

            // Se non c'era nessun film con quest'anno registrato
            if (moviesByYear.search(year) == null) {
                moviesByYear.insert(year, new ArrayList<>());
            }
            moviesByYear.search(year).add(movie);

            // Se non era fra i direttori finora, aggiungilo
            if (personByName.search(caseInsensitiveDirectorName) == null) {
                personByName.insert(caseInsensitiveDirectorName, director);
                people.add(director);
                moviesByDirector.insert(caseInsensitiveDirectorName, new ArrayList<>());
            }
            moviesByDirector.search(caseInsensitiveDirectorName).add(movie);

            for (Person actor : cast) {
                String name = actor.getName();
                CaseInsensitiveString caseInsensitiveName = new CaseInsensitiveString(name);
                // Se non era fra gli attori finora, aggiungilo
                if (moviesByActor.search(caseInsensitiveName) == null) {
                    actors.add(actor);
                    moviesByActor.insert(caseInsensitiveName, new ArrayList<>());
                    this.actorsSorted = false;
                }
                moviesByActor.search(caseInsensitiveName).add(movie);

                if (personByName.search(caseInsensitiveName) == null) {
                    personByName.insert(caseInsensitiveName, actor);
                    people.add(actor);
                }
            }
        }

        // TODO: else - se il film esisteva gia...

        this.graph.addMovie(movie);
    }
}
