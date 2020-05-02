package movida.marromerli;

import movida.commons.*;

import java.io.File;
import java.io.IOException;

public class MovidaCore implements IMovidaConfig, IMovidaDB, IMovidaSearch {
    private SortingAlgorithm sortingAlgorithm; //Algoritmo usato
    private MapImplementation mapImplementation; //Implementazione di dizionario usata


    @Override
    public void setMap(MapImplementation m) {
        //TODO: I docs dicono che deve restituire un boolean, ma l'implementazione è void
        if (m == MapImplementation.ArrayOrdinato || m == MapImplementation.ABR){
            throw new UnsupportedOperationException("Tipo di dizionario non supportato.");
        }
        else {
            this.mapImplementation = m;
        }
        //TODO: è necessario ricostruire da capo il database?
    }

    @Override
    public void setSort(SortingAlgorithm a) {
        //TODO: I docs dicono che deve restituire un boolean, ma l'implementazione è void
        if (a == SortingAlgorithm.BubbleSort || a == SortingAlgorithm.QuickSort){
            throw new UnsupportedOperationException("Tipo di algoritmo di ordinamento non supportato.");
        }
        else {
            this.sortingAlgorithm = a;
        }
    }

    @Override
    public void loadFromFile(File f) {
        i
    }

    @Override
    public void saveToFile(File f) {

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
        return new Movie[0];
    }

    @Override
    public Person[] getAllPeople() {
        return new Person[0];
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
