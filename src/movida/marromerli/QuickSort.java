package movida.marromerli;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class QuickSort implements Sorter {
    private Random rand = new Random();

    private <T> int partition(List<T> elements, Comparator<T> comparator, int begin, int end) {
        int inf = begin;
        int sup = end + 1;

        int pivot = begin + this.rand.nextInt(end - begin + 1);
        T x = elements.get(pivot);
        elements.set(pivot, elements.get(begin));
        elements.set(begin, x);
        T temp = null;

        while (true) {
            do {
                inf++;
            } while (inf <= end && comparator.compare(elements.get(inf), x) <= 0);
            do {
                sup--;
            } while (comparator.compare(elements.get(sup), x) > 0);

            if (inf < sup) {
                temp = elements.get(inf);
                elements.set(inf, elements.get(sup));
                elements.set(sup, temp);
            } else
                break;
        }

        temp = elements.get(begin);
        elements.set(begin, elements.get(sup));
        elements.set(sup, temp);

        return sup;
    }

    private <T> void quicksortRecursive(List<T> elements, Comparator<T> comparator, int begin, int end) {
        if (begin >= end) {
            return;
        }
        int pivot = partition(elements, comparator, begin, end);
        quicksortRecursive(elements, comparator, begin, pivot - 1);
        quicksortRecursive(elements, comparator, pivot + 1, end);
    }

    @Override
    public <T> void sort(List<T> elements, Comparator<T> comparator) {
        quicksortRecursive(elements, comparator, 0, elements.size() - 1);
    }
}
