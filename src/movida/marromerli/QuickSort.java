package movida.marromerli;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Implementa l'ordinamento QuickSort.
 */
public class QuickSort implements Sorter {
    private Random rand = new Random();

    /**
     * Divide una lista in elementi minori e maggiori di un pivot.
     *
     * @param elements   Gli elementi da dividere
     * @param comparator Comparatore per determinare l'ordinamento
     * @param begin      Inizio del sottovettore considerato
     * @param end        Fine del sottovettore considerato
     * @param <T>        Tipo degli elementi
     * @return La posizione del pivot
     */
    private <T> int partition(List<T> elements, Comparator<T> comparator, int begin, int end) {
        int inf = begin;
        int sup = end + 1;

        int pivot = begin + this.rand.nextInt(end - begin + 1);
        T x = elements.get(pivot);
        elements.set(pivot, elements.get(begin));
        elements.set(begin, x);
        T temp;

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

    /**
     * Ordina una lista in-loco.
     *
     * @param keys       Gli elementi da ordinare.
     * @param comparator Comparatore per stabilire l'ordinamento.
     * @param <T>        Tipo generico degli elementi.
     */
    @Override
    public <T> void sort(List<T> keys, Comparator<T> comparator) {
        quicksortRecursive(keys, comparator, 0, keys.size() - 1);
    }
}