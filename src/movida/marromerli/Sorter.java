package movida.marromerli;

import java.util.Comparator;
import java.util.List;

public interface Sorter {
    <T> void sort(List<T> keys, Comparator<T> comparator);
}