package movida.marromerli;

import java.util.PriorityQueue;

/**
 * Una PriorityQueue implementata per avere un comportamento
 * più simile a quello studiato a lezione.
 *
 * @param <K> Il tipo della chiave
 */
class VariablePriorityQueue<K extends Comparable<K>> {
    private PriorityQueue<Entry<K>> queue;

    /**
     * Crea una nuova VariablePriorityQueue.
     */
    public VariablePriorityQueue() {
        queue = new PriorityQueue<>();
    }

    /**
     * Aggiunge un elemento.
     *
     * @param key      Elemento da aggiungere
     * @param priority Priorità dell'elemento
     */
    public void add(K key, double priority) {
        queue.add(new Entry<>(key, priority));
    }

    /**
     * Rimuove l'elemento con priorità più bassa.
     *
     * @return L'elemento con priorità più bassa
     */
    public K remove() {
        Entry<K> entry = queue.remove();
        return entry.getKey();
    }

    /**
     * Restituisce <code>true</code> se la coda è vuota,
     * <code>false</code> altrimenti.
     *
     * @return <code>true</code> se la coda è vuota,
     * <code>false</code> altrimenti.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Aggiorna la priorità di un elemento.
     *
     * @param key         L'elemento da aggiornare
     * @param newPriority La nuova priorità
     */
    public void updatePriority(K key, double newPriority) {
        //Non sappiamo quant'è la sua priorità, ma non
        //importa per l'eliminazione
        if (!queue.contains(new Entry<>(key, -1))) {
            throw new RuntimeException("La chiave non esiste.");
        }

        queue.remove(new Entry<>(key, -1));
        queue.add(new Entry<>(key, newPriority));
    }

    /**
     * Rappresenta un elemento della PriorityQueue.
     *
     * @param <K> Il tipo di elemento
     */
    static class Entry<K extends Comparable<K>> implements Comparable<Entry<K>> {
        private K key;
        private double priority;

        public Entry(K key, double priority) {
            this.key = key;
            this.priority = priority;
        }

        /**
         * Restituisce la chiave.
         *
         * @return La chiave
         */
        public K getKey() {
            return this.key;
        }

        /**
         * Restituisce la priorità.
         *
         * @return La priorità dell'elemento.
         */
        public double getPriority() {
            return this.priority;
        }

        @Override
        public int compareTo(Entry<K> entry) {
            return Double.compare(this.priority, entry.priority);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if ((obj instanceof Entry)) {
                Entry other = (Entry) obj;
                return this.key.equals(other.key);
            } else {
                return false;
            }
        }
    }
}