package movida.marromerli;

import org.junit.jupiter.api.Test;

import java.io.File;

class MovidaCoreTest {

    @Test
    public void test1() {
        MovidaCore m = new MovidaCore();

        String inputPath = "src/movida/commons/esempio-formato-dati.txt";
        String outputPath = "src/movida/commons/esempio-formato-dati-output.txt";
        File f = new File(inputPath);
        m.loadFromFile(f);

        System.out.println(m.getAllMovies().length);

        File output = new File(outputPath);
        m.saveToFile(output);
    }

    @Test
    public void topKRecent() {

    }

    @Test
    public void topKVoted() {

    }

    @Test void topKActive() {

    }
}