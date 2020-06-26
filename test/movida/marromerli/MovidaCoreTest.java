package movida.marromerli;

import movida.commons.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.Integer.min;
import static org.junit.jupiter.api.Assertions.*;

class MovidaCoreTest {

    private MovidaCore loadDefaultCore() {
        MovidaCore m = new MovidaCore();

        String inputPath = "src/movida/commons/esempio-formato-dati.txt";
        File f = new File(inputPath);
        m.loadFromFile(f);

        return m;
    }

    private void generateFile(List<Movie> movies, File f){
        try {
            FileWriter writer = new FileWriter(f, false);

            for (Movie movie : movies) {
                writer.append(movie.toString() + "\n\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new MovidaFileException();
        }
    }

    private List<String> generateTitles(int N, int duplicatesN) {
        String[] choices = {"Oceans 8", "Ant-man", "Harry Potter", "Il signore degli anelli", "Spiderman 1", "La vita è bella", "Che bella giornata",
                            "3 uomini e una gamba", "Avengers Endgame", "Avengers Age of Ultron", "E.T.", "The Shield", "El Camino", "The Simpsons",
                            "Mission Impossible", "Pirati dei caraibi", "Indiana Jones", "Jurassic Park", "Inception", "KingsMan", "Shutter Island",
                            "Bad Boys", "Uncut gems", "Spy", "The Irishman", "Greenbook", "Fast and Furious", "Free guy", "IronMan", "Thor"};
        List<String> titles = new ArrayList<>();
        for(int i=0; i<min(N, choices.length)-duplicatesN; i++){
            titles.add(choices[i]);
        }
        for(int i=0; i<min(duplicatesN, choices.length); i++){
            titles.add(choices[i].toUpperCase());
        }
        return titles;
    }

    private List<Person> generatePeople(int N) {
        String[] choices = {"Matteo", "Giovanni", "Luca", "Giacomo", "Francesco", "Francesca", "Edoardo", "Samuele", "Lorenzo",
                            "Gregorio", "Lucia", "Margherita", "Anna", "Andrea", "Riccardo", "Nike", "Mattia", "Mirco", "Marco",
                            "Alessio", "Alessia", "Federico", "Giuseppe", "Saverio", "Simone", "Alessandro", "Davide"};
        List<Person> names = new ArrayList<>();
        for(int i=0; i<min(N, choices.length); i++){
            Person p = new Person(choices[i]);
            names.add(p);
        }

        return names;
    }

    private List<Movie> generateMovies(List<Person> staff, List<String> titles, int N) {
        Random rand = new Random();
        List<Movie> movies = new ArrayList<>();
        for(int i=0; i<N; ++i){
            List<Person> castList = new ArrayList<>();
            boolean[] chosen = new boolean[staff.size()];
            int cast_size = rand.nextInt(staff.size() + 1);
            for(int j=0; j<cast_size; ++j) {
                int person_index;
                do{
                    person_index = rand.nextInt(staff.size());
                }
                while(chosen[person_index]);
                chosen[person_index] = true;
                castList.add(staff.get(person_index));
            }

            String title = titles.get(i);
            Person director = staff.get(rand.nextInt(staff.size()));
            Movie m = new Movie(title, rand.nextInt(2020), rand.nextInt(10000), castList.toArray(new Person[0]), director);
            movies.add(m);
        }

        return movies;
    }

    @Test
    public void settingsTest() {
        MovidaCore m = new MovidaCore();
        assertFalse(m.setMap(MapImplementation.ABR));
        assertFalse(m.setSort(SortingAlgorithm.QuickSort));

        assertTrue(m.setSort(SortingAlgorithm.BubbleSort));
        assertFalse(m.setSort(SortingAlgorithm.BubbleSort));

        assertTrue(m.setMap(MapImplementation.ArrayOrdinato));
        assertFalse(m.setMap(MapImplementation.ArrayOrdinato));
        assertTrue(m.setMap(MapImplementation.ABR));
    }

    /*
    @Test
    public void importTest() {
        MovidaCore m = new MovidaCore();
        Person hero1 = new Person("IronMan");
        Person hero2 = new Person("Hulk");
        Person hero3 = new Person("Capitan America");
        Person director1 = new Person("StanLee");
        Person[] cast1 = new Person[]{hero1, hero2, hero3};
        Movie movie1 = new Movie("Avengers Endgame", 2020, 99999, cast1, director1);
        m.importMovie(movie1);

        Person person1 = new Person("Geronimo Stilton");
        Person person2 = new Person("Hugh Jackman");
        Person person3 = new Person("Mickey Mouse");
        Person director2 = new Person("Quentin Tarantino");
        Person[] cast2 = new Person[]{person1, person2, person3};
        Movie movie2 = new Movie("Via col Vento", 2020, 1000, cast2, director2);
        m.importMovie(movie2);
        assertEquals(2, m.countMovies());

        m.deleteMovieByTitle("Via col Vento");
        assertEquals(1, m.countMovies());
    }
    */

    @Test
    public void DBTest() {
        // --- dataset generation ---
        int num_movies = 28, num_people = 20, num_duplicates_titles = 7;
        List<Person> people = generatePeople(num_people);
        List<String> titles = generateTitles(num_movies, num_duplicates_titles);
        List<Movie> movies = generateMovies(people, titles, num_movies);

        String inputPath = "src/movida/commons/test-dati.txt";
        String outputPath = "src/movida/commons/test-dati-output.txt";
        File input_file = new File(inputPath);
        File output_file = new File(outputPath);
        generateFile(movies, input_file);

        MovidaCore m = new MovidaCore();
        m.loadFromFile(input_file);
        m.saveToFile(output_file);

        // --- actual test ---

        assertEquals(num_movies - num_duplicates_titles, m.countMovies());
        assertEquals(num_people, m.countPeople());

        for(int i=num_duplicates_titles; i<num_movies; ++i) {
            assertEquals(movies.get(i), m.getMovieByTitle(movies.get(i).getTitle()));
        }

        for(Person p : people) {
            assertEquals(p, m.getPersonByName(p.getName()));
        }

        assertArrayEquals(movies.subList(num_duplicates_titles, movies.size()).toArray(new Movie[0]), m.getAllMovies());

        Collections.sort(people);
        Person[] movida_people = m.getAllPeople();
        Arrays.sort(movida_people);
        assertArrayEquals(people.toArray(new Person[0]), movida_people);

        m.setMap(MapImplementation.ArrayOrdinato);
        m.setMap(MapImplementation.ABR);

        assertEquals(num_movies - num_duplicates_titles, m.countMovies());
        assertEquals(num_people, m.countPeople());

        for(int i=num_duplicates_titles; i<num_movies; ++i) {
            assertEquals(movies.get(i), m.getMovieByTitle(movies.get(i).getTitle()));
        }

        for(Person p : people) {
            assertEquals(p, m.getPersonByName(p.getName()));
        }

        assertArrayEquals(movies.subList(num_duplicates_titles, movies.size()).toArray(new Movie[0]), m.getAllMovies());

        Collections.sort(people);
        movida_people = m.getAllPeople();
        Arrays.sort(movida_people);
        assertArrayEquals(people.toArray(new Person[0]), movida_people);


    }


    @Test
    public void topKRecent() {
        // --- dataset generation ---
        int num_movies = 0, num_people = 0, num_duplicate_titles = 0;
        List<Person> people = generatePeople(num_people);
        List<String> titles = generateTitles(num_movies, num_duplicate_titles);
        List<Movie> movies = generateMovies(people, titles, num_movies);

        String inputPath = "src/movida/commons/test-dati.txt";
        String outputPath = "src/movida/commons/test-dati-output.txt";
        File input_file = new File(inputPath);
        File output_file = new File(outputPath);
        generateFile(movies, input_file);

        MovidaCore m = new MovidaCore();
        m.loadFromFile(input_file);
        m.saveToFile(output_file);

        // --- actual test ---


    }

    @Test
    public void topKVoted() {

    }

    @Test void topKActive() {

    }

    @Test
    public void basicTest() {
        MovidaCore m = loadDefaultCore();

        assertNull(m.getPersonByName(""));
        assertNotNull(m.getPersonByName("Martin Scorsese"));
        assertEquals(10, m.getAllMovies().length);
        assertEquals(33, m.getAllPeople().length);
        assertEquals(2, m.searchMoviesDirectedBy("Martin Scorsese").length);
        assertEquals(2, m.searchMoviesInYear(1997).length);
        assertEquals(2, m.searchMoviesStarredBy("Robert De Niro").length);
        assertEquals(6, m.getDirectCollaboratorsOf(m.getPersonByName("Harrison Ford")).length);
    }

    @Test
    public void test1() {
        MovidaCore m = loadDefaultCore();

        System.out.println(Arrays.toString(m.getAllMovies()));
        boolean deleted = m.deleteMovieByTitle("Cape Fear");
        System.out.println(deleted);

        String outputPath = "src/movida/commons/esempio-formato-dati-output.txt";
        File output = new File(outputPath);
        m.saveToFile(output);
    }

    @Test
    public void caseInsensitivityTest() {
        MovidaCore m = loadDefaultCore();

        assertTrue(m.deleteMovieByTitle("pulp FICTION"));
        assertNotNull(m.getPersonByName("JODIE foster"));
        assertEquals(1, m.searchMoviesByTitle("cONTAct").length);
        assertEquals(2, m.searchMoviesStarredBy("Robert De Niro").length);
        assertEquals(3, m.searchMoviesStarredBy("harrison ford").length);

        //Controlla case insensitivity della ricerca parziale
        assertEquals(2, m.searchMoviesByTitle("The").length);
    }
}