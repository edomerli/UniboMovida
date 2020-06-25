package movida.marromerli;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MovidaCoreTest {

    @Test
    public void test1() {
        MovidaCore m = new MovidaCore();

        String inputPath = "src/movida/commons/esempio-formato-dati.txt";
        String outputPath = "src/movida/commons/esempio-formato-dati-output.txt";
        File f = new File(inputPath);
        m.loadFromFile(f);

        System.out.println(Arrays.toString(m.getAllMovies()));
        boolean deleted = m.deleteMovieByTitle("Cape Fear");
        System.out.println(deleted);

        File output = new File(outputPath);
        m.saveToFile(output);
    }
}