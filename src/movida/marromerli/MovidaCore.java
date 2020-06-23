package movida.marromerli;

import movida.commons.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class MovidaCore implements IMovidaConfig, IMovidaDB, IMovidaSearch {
    private SortingAlgorithm sortingAlgorithm; //Algoritmo usato
    private MapImplementation mapImplementation; //Implementazione di dizionario usata

    private ArrayList<Movie> movies;
    private ArrayList<Person> actors;
    private ArrayList<Person> directors;
    // TODO: E' necessario distinguere attori e direttori in 2 array diversi?

    private Sorter sorter;

    public MovidaCore() {
        this.movies = new ArrayList<Movie>();
        this.actors = new ArrayList<Person>();
        this.directors = new ArrayList<Person>();
    }

    @Override
    public boolean setMap(MapImplementation m) {
        if (m == MapImplementation.ArrayOrdinato || m == MapImplementation.ABR) {
            this.mapImplementation = m;
            return true;
        } else {
            return false;
        }
        //TODO: è necessario ricostruire da capo il database?
    }

    @Override
    public boolean setSort(SortingAlgorithm a) {
        if (a == SortingAlgorithm.BubbleSort || a == SortingAlgorithm.QuickSort) {
            this.sortingAlgorithm = a;
            
            if(a == SortingAlgorithm.BubbleSort){
                sorter = new BubbleSort();
            }
            else sorter = new QuickSort();

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

                movies.add(movie);
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

            for (Movie movie : this.movies) {
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
        return 0;
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
        return movies.toArray(new Movie[0]);
    }

    @Override
    public Person[] getAllPeople() {
        ArrayList<Person> people = actors;
        people.addAll(directors);
        return people.toArray(new Person[0]);
    }

    @Override
    public Movie[] searchMoviesByTitle(String title) {
        return new Movie[0];
    }

    @Override
    public Movie[] searchMoviesInYear(Integer year) {
        return new Movie[0];
    }

    @Override
    public Movie[] searchMoviesDirectedBy(String name) {
        return new Movie[0];
    }

    @Override
    public Movie[] searchMoviesStarredBy(String name) {
        return new Movie[0];
    }

    @Override
    public Movie[] searchMostVotedMovies(Integer N) {
        return new Movie[0];
    }

    @Override
    public Movie[] searchMostRecentMovies(Integer N) {
        return new Movie[0];
    }

    @Override
    public Person[] searchMostActiveActors(Integer N) {
        return new Person[0];
    }
}
