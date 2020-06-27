package movida.marromerli;

import java.util.Comparator;
import java.util.List;

/**
 * Implementa l'ordinamento BubbleSort.
 */
public class BubbleSort implements Sorter {
    /**
     * Ordina una lista in-loco.
     *
     * @param keys       Gli elementi da ordinare.
     * @param comparator Comparatore per stabilire l'ordinamento.
     * @param <T>        Tipo generico degli elementi.
     */
    @Override
    public <T> void sort(List<T> keys, Comparator<T> comparator) {
        int size = keys.size();

        for (int i = 0; i < size - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < size - 1 - i; j++) {
                if (comparator.compare(keys.get(j), keys.get(j + 1)) > 0) {
                    T temp = keys.get(j);
                    keys.set(j, keys.get(j + 1));
                    keys.set(j + 1, temp);

                    swapped = true;
                }
            }

            if (!swapped) break;
        }
    }
}