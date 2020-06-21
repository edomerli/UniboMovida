package movida.marromerli;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        String inputPath = "src/movida/commons/esempio-formato-dati.txt";
        String outputPath = "src/movida/commons/esempio-formato-dati-output.txt";
        File f = new File(inputPath);
        MovidaCore m = new MovidaCore();
        m.loadFromFile(f);
        File output = new File(outputPath);
        m.saveToFile(output);
    }
}
