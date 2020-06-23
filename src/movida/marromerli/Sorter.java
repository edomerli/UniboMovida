package movida.marromerli;

import java.util.Comparator;

public interface Sorter {
    public <T> void sort(T[] keys, Comparator<T> comparator);
}