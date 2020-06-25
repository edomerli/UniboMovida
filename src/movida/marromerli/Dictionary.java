package movida.marromerli;

import java.util.List;
import java.util.function.BiPredicate;

public interface Dictionary<K extends Comparable<K>, V> {
    void insert(K key, V value);

    V search(K key);

    void remove(K key);

    void clear();

    List<V> searchAll(K key, BiPredicate<K, K> match);
}
