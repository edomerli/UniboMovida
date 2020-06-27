package movida.marromerli;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * Interfaccia per un dizionario.
 *
 * @param <K> Il tipo della chiave
 * @param <V> Il tipo del valore associato
 */
public interface Dictionary<K extends Comparable<K>, V> {
    /**
     * Inserisce una coppia (key, value) nel dizionario.
     *
     * @param key   La chiave che identifica il contenuto
     * @param value Il contenuto da associare alla chiave (viene aggiornato se
     *              la chiave era già presente nel dizionario)
     */
    void insert(K key, V value);

    /**
     * Cerca una chiave nel dizionario.
     *
     * @param key La chiave da cercare
     * @return Il contenuto associato a key se questa esiste, altrimenti null
     */
    V search(K key);

    /**
     * Rimuove un elemento dal dizionario. Se la chiave non è presente, non
     * fa nulla.
     *
     * @param key La chiave dell'elemento da rimuovere
     */
    void remove(K key);

    /**
     * Rimuove tutti gli elementi dal dizionario.
     */
    void clear();

    /**
     * Cerca tutti gli elementi che coincidono con una data chiave,
     * usando un criterio personalizzato.
     *
     * @param key   La chiave da cercare
     * @param match Il predicato per stabilire se un elemento corrisponde
     * @return Tutti gli x.value tali che match(x.key, key) == true
     */
    List<V> searchAll(K key, BiPredicate<K, K> match);
}
