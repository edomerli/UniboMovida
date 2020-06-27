package movida.marromerli;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import static java.lang.Integer.max;

/**
 * Implementa un dizionario usando un vettore di coppie.
 *
 * @param <K> Il tipo delle chiavi
 * @param <V> Il tipo dei valori associati
 */
public class SortedArrayDictionary<K extends Comparable<K>, V> implements Dictionary<K, V> {
    private Object[] elements;
    private Integer arrayCount;

    /**
     * Crea un nuovo SortedArrayDictionary.
     */
    public SortedArrayDictionary() {
        this.arrayCount = 0;
        resize(0);
    }

    /**
     * Ridimensiona l'array di elementi.
     *
     * @param newSize La nuova dimensione
     * @throws RuntimeException Se la nuova dimensione è inferiore
     *                          a <code>arrayCount</code>
     */
    private void resize(int newSize) {
        Object[] newElements = new Object[newSize];
        if (newSize < this.arrayCount) {
            throw new RuntimeException("Dimensione inferiore ad arrayCount.");
        }

        for (int i = 0; i < this.arrayCount; i++) {
            newElements[i] = this.elements[i];
        }
        this.elements = newElements;
    }

    /**
     * Restituisce una Pair a una certa posizione.
     *
     * @param position La posizione considerata
     * @return La Pair corrispondente alla posizione
     */
    @SuppressWarnings("unchecked")
    private Pair getPair(int position) {
        // Java non ammette array generici. Per simulare
        // il comportamento di un array generico, manteniamo
        // un array di object e convertiamo a runtime.
        return (Pair) this.elements[position];
    }

    /**
     * Cerca una chiave nel dizionario.
     *
     * @param key La chiave da cercare
     * @return Il contenuto associato a key se questa esiste, altrimenti null
     */
    @Override
    public V search(K key) {
        if (this.arrayCount == 0) {
            //Array vuoto
            return null;
        }

        int min = 0;
        int max = this.arrayCount - 1;
        int middle;
        while (min <= max) {
            middle = (min + max) / 2;
            if (key.compareTo((getPair(middle)).getKey()) < 0) {
                max = middle - 1;
            } else if (key.compareTo((getPair(middle)).getKey()) > 0) {
                min = middle + 1;
            } else {
                return (getPair(middle)).getValue();
            }
        }

        return null;
    }

    /**
     * Inserisce una coppia (key, value) nel dizionario.
     *
     * @param key   La chiave che identifica il contenuto
     * @param value Il contenuto da associare alla chiave (viene aggiornato se
     *              la chiave era già presente nel dizionario)
     */
    @Override
    public void insert(K key, V value) {
        int position = 0;
        boolean exactMatch = false;
        while (position < this.arrayCount) {
            if (key.compareTo((getPair(position)).getKey()) <= 0) {
                exactMatch = key.compareTo((getPair(position)).getKey()) == 0;
                break;
            }
            position++;
        }

        if (!exactMatch) {
            if (this.arrayCount.equals(elements.length)) {
                this.resize(max(elements.length * 2, 1));
            }
            this.arrayCount++;
            //Sposta gli elementi successivi
            for (int i = this.arrayCount - 1; i > position; i--) {
                this.elements[i] = this.elements[i - 1];
            }
        }

        this.elements[position] = new Pair(key, value);
    }

    /**
     * Rimuove un elemento dal dizionario. Se la chiave non è presente, non
     * fa nulla.
     *
     * @param key La chiave dell'elemento da rimuovere
     */
    @Override
    public void remove(K key) {
        int position = 0;
        while (position < this.arrayCount) {
            if (key.compareTo(getPair(position).getKey()) == 0) {
                break;
            }
            position++;
        }

        if (position != this.arrayCount) {
            //Shift the elements
            for (int i = position; i < this.arrayCount - 1; i++) {
                this.elements[i] = this.elements[i + 1];
            }
            this.arrayCount--;
        }

        if (this.arrayCount > 1 && this.arrayCount == elements.length / 4) {
            resize(elements.length / 2);
        }
    }

    /**
     * Rimuove tutti gli elementi dal dizionario.
     */
    @Override
    public void clear() {
        this.arrayCount = 0;
        resize(0);
    }

    /**
     * Ricerca tutti gli elementi che coincidono con una data chiave,
     * usando un criterio personalizzato.
     *
     * @param key   La chiave da cercare
     * @param match Il predicato per stabilire se un elemento corrisponde
     * @return Tutti gli x.value tali che match(x.key, key) == true
     */
    @Override
    public List<V> searchAll(K key, BiPredicate<K, K> match) {
        List<V> results = new ArrayList<>();
        for (int i = 0; i < elements.length; i++) {
            Pair pair = getPair(i);
            if (match.test(pair.getKey(), key)) {
                results.add(pair.getValue());
            }
        }
        return results;
    }

    /**
     * Rappresenta una coppia (chiave, valore).
     */
    private class Pair {
        private K key;
        private V value;

        /**
         * Crea una nuova Pair.
         *
         * @param key   La chiave
         * @param value Il valore associato
         */
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Restituisce la chiave della Pair.
         *
         * @return La chiave
         */
        public K getKey() {
            return key;
        }

        /**
         * Restituisce il valore associato della Pair.
         *
         * @return Il valore associato
         */
        public V getValue() {
            return value;
        }
    }
}