package movida.marromerli;

import java.util.Comparator;

//TODO: Deve supportare chiavi nulle?
public class SortedArrayDictionary<K extends Comparable<K>, V> implements Dictionary<K, V> {
    private K[] arrayKeys;
    private V[] arrayValues;
    private Integer arrayCount;
    private Integer arraySize;
    private Comparator<K> comparator;

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

    public SortedArrayDictionary(Comparator<K> comparator) {
        this.comparator = comparator;
    }

    private void doubleSize() {
        //TODO: Questo è unsafe, ma non vedo soluzioni migliori
        K[] newKeys = (K[]) new Object[this.arraySize * 2];
        V[] newValues = (V[]) new Object[this.arraySize * 2];

        for (int i = 0; i < this.arrayCount; i++) {
            newKeys[i] = this.arrayKeys[i];
            newValues[i] = this.arrayValues[i];
        }

        this.arrayKeys = newKeys;
        this.arrayValues = newValues;
        this.arraySize *= 2;
    }

    private void halveSize(){
        //TODO: Questo è unsafe, ma non vedo soluzioni migliori
        K[] newKeys = (K[]) new Object[this.arraySize / 2];
        V[] newValues = (V[]) new Object[this.arraySize / 2];

        for (int i = 0; i < this.arrayCount; i++){
            newKeys[i] = this.arrayKeys[i];
            newValues[i] = this.arrayValues[i];
        }

        this.arrayKeys = newKeys;
        this.arrayValues = newValues;
        this.arraySize /= 2;
    }


    private SearchResult keyLookup(K k) {
        int min = 0;
        int max = this.arrayCount; //TODO: -1?
        int medium = (min + max) / 2;//TODO: Nome migliore, inizializzazione corretta
        while (min <= max) {
            medium = (min + max) / 2;
            if (this.comparator.compare(k, this.arrayKeys[medium]) == 0) {
                return new SearchResult(medium, true);
            } else if (this.comparator.compare(k, this.arrayKeys[medium]) > 0) {
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
        //TODO: Controllare prima il tipo?
        int min = 0;
        int max = this.arrayCount;

        SearchResult result = this.keyLookup(key);

        //TODO: Non mi piace molto getKey()
        if (result.getFound()) {
            return this.arrayValues[result.getPosition()];
        } else {
            //TODO: è comportamento standard?
            return null;
        }
    }

    @Override
    public void insert(K k, V v) {
        if (this.arrayCount == this.arraySize) {
            this.doubleSize();
        }

        //TODO: Ottimizzare ricerca?
        int index = 0;
        for (index = 0; index < this.arrayCount; index++) {
            if (this.comparator.compare(k, this.arrayKeys[index]) == 0) {
                this.arrayValues[index] = v;
                //TODO: Ritornare?
            } else if (this.comparator.compare(k, this.arrayKeys[index]) > 0) {
                break;
            }
        }

        for (int i = this.arrayCount; i > index; i--) {
            this.arrayKeys[i] = this.arrayKeys[i - 1];
            this.arrayValues[i] = this.arrayValues[i - 1];
        }

        this.arrayKeys[index] = k;
        this.arrayValues[index] = v;
        this.arrayCount++;
    }

    @Override
    public void remove(K key) {
        //TODO: Controllare prima il tipo?
    }

    @Override
    public void clear() {
        this.arrayCount = 0;
    }
}