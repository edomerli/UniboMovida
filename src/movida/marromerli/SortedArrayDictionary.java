package movida.marromerli;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

//TODO: Deve supportare chiavi nulle?
public class SortedArrayDictionary<K extends Comparable<K>, V> implements Dictionary<K, V> {
    private K[] arrayKeys;
    private V[] arrayValues;
    private Integer arrayCount;
    private Integer arraySize;

    class SearchResult {
        private Integer position;
        private Boolean found;

        public SearchResult(int position, boolean found) {
            this.position = position;
            this.found = found;
        }

        public Boolean getFound() {
            return found;
        }

        public Integer getPosition() {
            return position;
        }
    }

    public SortedArrayDictionary() {
        this.arrayCount = 0;
        resize(1);
    }

    private void resize(int newSize) {
        //TODO: Questo è unsafe, ma non vedo soluzioni migliori
        K[] newKeys = (K[]) new Object[newSize];
        V[] newValues = (V[]) new Object[newSize];
        //TODO: Errore se si ridimensiona incorrettamente?

        for (int i = 0; i < this.arrayCount; i++) {
            newKeys[i] = this.arrayKeys[i];
            newValues[i] = this.arrayValues[i];
        }

        this.arrayKeys = newKeys;
        this.arrayValues = newValues;
        this.arraySize = newSize;
    }

    private SearchResult keyLookup(K k) {
        int min = 0;
        int max = this.arrayCount;
        int medium = (min + max) / 2;//TODO: Nome migliore, inizializzazione corretta
        while (min <= max) {
            medium = (min + max) / 2;
            if (k.compareTo(this.arrayKeys[medium]) == 0) {
                return new SearchResult(medium, true);
            } else if (k.compareTo(this.arrayKeys[medium]) > 0) {
                min = medium;
            } else {
                max = medium;
            }
        }
        //TODO: Qual è la posizione corretta di inserimento?
        return new SearchResult(medium, false);
    }

    @Override
    public V search(K key) {
        int min = 0;
        int max = this.arrayCount;

        SearchResult result = this.keyLookup(key);

        if (result.getFound()) {
            return this.arrayValues[result.getPosition()];
        } else {
            return null;
        }
    }

    @Override
    public void insert(K k, V v) {
        if (this.arrayCount == this.arraySize) {
            this.resize(this.arraySize * 2);
        }

        SearchResult result = this.keyLookup(k);

        if (!result.getFound()) {
            //Sposta gli elementi successivi
            this.arrayCount++;
            for (int i = this.arrayCount; i > result.getPosition(); i--) {
                this.arrayKeys[i] = this.arrayKeys[i - 1];
                this.arrayValues[i] = this.arrayValues[i - 1];
            }

            //Inserisci la nuova chiave
            this.arrayKeys[result.getPosition()] = k;
        }

        //Inserisci/Sostituisci il valore
        this.arrayValues[result.getPosition()] = v;
    }

    @Override
    public void remove(K key) {
        SearchResult result = this.keyLookup(key);

        if (result.getFound()) {
            //Shift the elements
            for (int i = result.getPosition(); i < this.arrayCount - 1; i++) {
                this.arrayKeys[i] = this.arrayKeys[i + 1];
                this.arrayValues[i] = this.arrayValues[i + 1];
            }
            this.arrayCount--;
        }
    }

    @Override
    public void clear() {
        this.arrayCount = 0;
    }

    @Override
    public List<V> searchAll(K key, BiPredicate<K, K> match){
        // TODO: implement
        return new ArrayList<V>();
    }
}