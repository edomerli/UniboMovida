package movida.marromerli;

import java.util.Random;

public class QuickSort {
    public static <T> void sort(Comparable<T>[] elements) {
        quicksortRecursive(elements, 0, elements.length - 1);
    }

    private static <T> int partition(Comparable<T>[] elements, int begin, int end) {
        int inf = begin;
        int sup = end + 1;

        Random rand = new Random(); //TODO: Resetta il seed?
        int pivot = begin + rand.nextInt(end - begin);
        Comparable<T> x = elements[pivot];
        Comparable<T> temp = null;

        while (true) {
            do {
                inf++;
            } while (inf <= end && elements[inf].compareTo((T) x) <= 0);
            do {
                sup--;
            } while (elements[sup].compareTo((T) x) > 0);

            if (inf < sup) {
                temp = elements[inf];
                elements[inf] = elements[sup];
                elements[sup] = temp;
            } else
                break;
        }

        temp = elements[begin];
        elements[begin] = elements[sup];
        elements[sup] = temp;

        return sup;
    }

    private static <T> void quicksortRecursive(Comparable<T>[] elements, int begin, int end) {
        if (begin >= end) {
            return;
        }
        int pivot = partition(elements, begin, end);
        quicksortRecursive(elements, begin, pivot - 1);
        quicksortRecursive(elements, pivot + 1, end);
    }
}
