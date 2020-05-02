package movida.marromerli;

import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.*;

//TODO: I dictionary devono implementare i dictionary standard?
//TODO: Deve supportare chiavi nulle?
public class SortedArrayMap<K extends Comparable<K>, V> implements Map<K, V> {
    private K[] arrayKeys;
    private V[] arrayValues;
    private Integer arrayCount;
    private Integer arraySize;

    private void doubleSize(){
        //TODO: Questo è unsafe, ma non vedo soluzioni migliori
        K[] newKeys = (K[]) new Object[this.arraySize * 2];
        V[] newValues = (V[]) new Object[this.arraySize * 2];

        for (int i = 0; i < this.arrayCount; i++){
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

    private Pair<Boolean, Integer> keyLookup(K k){
        int min = 0;
        int max = this.arrayCount; //TODO: -1?
        int medium = (min + max) / 2;//TODO: Nome migliore, inizializzazione corretta
        while(min <= max){
            medium = (min + max) / 2;
            if (k.compareTo(this.arrayKeys[medium]) == 0){
                return new Pair<Boolean, Integer>(true, medium);
            }
            else if (k.compareTo(this.arrayKeys[medium]) > 0){
                min = medium;
            }
            else {
                max = medium;
            }
        }
        //TODO: Qual è la posizione corretta di inserimento?
        return new Pair<Boolean, Integer>(false, medium);
    }

    @Override
    public int size() {
        return this.arrayCount;
    }

    @Override
    public boolean isEmpty() {
        return this.arrayCount == 0;
    }

    @Override
    public boolean containsKey(Object o) {
        //TODO: Controllare tipo?
        //TODO: Ottimizzare
        for (int i = 0; i < this.arrayCount; i++) {
            if (this.arrayKeys[i].equals(o)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object o) {
        //TODO: Controllare tipo?
        for (int i = 0; i < this.arrayCount; i++) {
            if (this.arrayValues[i].equals(o)){
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(Object o) {
        //TODO: Controllare prima il tipo?
        int min = 0;
        int max = this.arrayCount;

        K key = (K)o;

        Pair<Boolean, Integer> lookup = this.keyLookup(key);

        //TODO: Non mi piace molto getKey()
        if (lookup.getKey()) {
            return this.arrayValues[lookup.getValue()];
        }
        else{
            //TODO: è comportamento standard?
            return null;
        }
    }

    @Override
    public V put(K k, V v) {
        if (this.arrayCount == this.arraySize){
            this.doubleSize();
        }

        //TODO: Ottimizzare ricerca?
        int index = 0;
        for (index = 0; index < this.arrayCount; index++){
            if (k.compareTo(this.arrayKeys[index]) == 0){
                V previousValue = this.arrayValues[index];
                this.arrayValues[index] = v;
                return previousValue;
            }
            else if (k.compareTo(this.arrayKeys[index]) > 0) {
                break;
            }
        }

        for (int i = this.arrayCount; i > index; i--){
            this.arrayKeys[i] = this.arrayKeys[i - 1];
            this.arrayValues[i] = this.arrayValues[i - 1];
        }

        this.arrayKeys[index] = k;
        this.arrayValues[index] = v;
        this.arrayCount++;

        return v;
    }

    @Override
    public V remove(Object o) {
        //TODO: Controllare prima il tipo?
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {

    }

    @Override
    public void clear() {
        this.arrayCount = 0;
    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        SortedArrayMap<K, V> other = (SortedArrayMap<K, V>)o;

        //TODO: Valutare cosa confrontare e come

        return this.arrayCount.equals(other.arrayCount) &&
                this.arraySize.equals(other.arraySize) &&
                Arrays.equals(this.arrayKeys, other.arrayKeys) &&
                Arrays.equals(this.arrayValues, other.arrayValues);
    }

    @Override
    public int hashCode() {
        //TODO: Scegliere un hashCode
        return 0;
    }
}
