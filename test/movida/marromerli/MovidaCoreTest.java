package movida.marromerli;

import movida.commons.Movie;
import movida.commons.Person;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovidaCoreTest {

    private MovidaCore loadDefaultCore() {
        MovidaCore m = new MovidaCore();

        String inputPath = "src/movida/commons/esempio-formato-dati.txt";
        File f = new File(inputPath);
        m.loadFromFile(f);

        return m;
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