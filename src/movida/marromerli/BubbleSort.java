package movida.marromerli;

import java.util.Comparator;

public class BubbleSort<T> implements Sorter {
    @Override
    public void sort(T[] keys, Comparator<T> comparator) {
        int size = keys.length;

        for (int i = 0; i < size - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < size - 1 - i; j++) {
                if (comparator.compare(keys[j], keys[j + 1]) > 0) {
                    Comparable<T> temp = keys[j];
                    keys[j] = keys[j + 1];
                    keys[j + 1] = temp;

                    swapped = true;
                }
            }

            if(swapped == false) break;
        }
    }
}