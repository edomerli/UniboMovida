package movida.marromerli;

import java.util.Comparator;

import static java.lang.Integer.max;

//TODO: Deve supportare chiavi nulle?
public class SortedArrayDictionary<K extends Comparable<K>, V> implements Dictionary<K, V> {
    private Object[] elements;
    private Integer arrayCount;

    private class Pair {
        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    public SortedArrayDictionary() {
        this.arrayCount = 0;
        resize(0);
    }

    private void resize(int newSize) {
        //TODO: Questo è unsafe, ma non vedo soluzioni migliori
        Object[] newElements = new Object[newSize];
        //TODO: Errore se si ridimensiona incorrettamente?

        for (int i = 0; i < this.arrayCount; i++) {
            newElements[i] = this.elements[i];
        }
        this.elements = newElements;
    }


    @Override
    public V search(K key) {
        if (this.arrayCount == 0) {
            //Array vuoto
            return null;
        }

        int min = 0;
        int max = this.arrayCount - 1;
        int medium;//TODO: Nome migliore, inizializzazione corretta
        while (min <= max) {
            medium = (min + max) / 2;
            if (key.compareTo(((Pair) this.elements[medium]).getKey()) < 0) {
                max = medium - 1;
            } else if (key.compareTo(((Pair) this.elements[medium]).getKey()) > 0) {
                min = medium + 1;
            } else {
                return ((Pair) this.elements[medium]).getValue();
            }
        }

        return null;
    }

    @Override
    public void insert(K k, V v) {
        int position = 0;
        boolean exactMatch = false;
        while (position < this.arrayCount) {
            if (k.compareTo(((Pair) this.elements[position]).getKey()) <= 0) {
                exactMatch = k.compareTo(((Pair) this.elements[position]).getKey()) == 0;
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

        this.elements[position] = new Pair(k, v);
    }

    @Override
    public void remove(K key) {
        int position = 0;
        while (position < this.arrayCount) {
            if (key.compareTo(((Pair) this.elements[position]).getKey()) == 0) {
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

    @Override
    public void clear() {
        this.arrayCount = 0;
        resize(0);
    }
}