package movida.marromerli;

public interface Dictionary<K, V> {
    void insert(K key, V value);

    V search(K key);

    void remove(K key);

    void clear();
}
