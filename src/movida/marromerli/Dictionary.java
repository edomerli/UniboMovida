package movida.marromerli;

import java.util.List;
import java.util.function.BiPredicate;

public interface Dictionary<K extends Comparable<K>, V> {
    /**
     *
     * @param key la chiave che identifica il contenuto
     * @param value il contenuto da associare alla chiave (viene aggiornato se la chiave era già presente nel dizionario)
     */
    void insert(K key, V value);

    /**
     *
     * @param key la chiave da cercare
     * @return il contenuto associato a key se questa esiste, altrimenti null
     */
    V search(K key);

    void remove(K key);

    void clear();

    List<V> searchAll(K key, BiPredicate<K, K> match);
}
