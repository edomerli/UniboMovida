package movida.marromerli;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Implementa un dizionario usando un Albero Binario
 * di Ricerca (ABR).
 *
 * @param <K> Il tipo delle chiavi
 * @param <V> Il tipo dei valori associati
 */
public class ABR<K extends Comparable<K>, V> implements Dictionary<K, V> {
    /**
     * Rappresenta un nodo di un ABR.
     */
    private class Node {
        public K key;
        public V value;
        public Node left, right;

        /**
         * Crea un nuovo nodo.
         * @param key La chiave del nodo
         * @param value Il valore associato
         */
        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            left = null;
            right = null;
        }
    }

    private Node root;

    /**
     * Crea un nuovo ABR.
     */
    public ABR() {
        root = null;
    }

    /**
     * Inserisce una coppia (key, value) nel dizionario.
     * @param key La chiave che identifica il contenuto
     * @param value Il contenuto da associare alla chiave (viene aggiornato se
     *              la chiave era già presente nel dizionario)
     */
    @Override
    public void insert(K key, V value) {
        root = insertRecursive(key, value, root);
    }

    /**
     * Inserisce ricorsivamente un elemento nell'ABR.
     * @param k Chiave da inserire
     * @param v Valore da inserire
     * @param root Radice dell'ABR
     * @return Il nodo che contiene <code>k</code>
     *         e <code>v</code>
     */
    private Node insertRecursive(K k, V v, Node root) {
        if (root == null) {
            Node leaf = new Node(k, v);
            return leaf;
        }

        if (k.compareTo(root.key) < 0) root.left = insertRecursive(k, v, root.left);
        else if (k.compareTo(root.key) > 0) root.right = insertRecursive(k, v, root.right);
        else root.value = v;

        return root;
    }

    /**
     * Cerca una chiave nel dizionario.
     * @param key La chiave da cercare
     * @return Il contenuto associato a key se questa esiste, altrimenti null
     */
    @Override
    public V search(K key) {
        return searchRecursive(key, root);
    }

    /**
     * Cerca ricorsivamente una chiave nel dizionario.
     * @param k La chiave da cercare
     * @return Il contenuto associato a k se questa esiste, altrimenti null
     */
    private V searchRecursive(K k, Node root) {
        if (root == null) return null;   // Key is missing

        if (k.compareTo(root.key) == 0) return root.value;

        else if (k.compareTo(root.key) < 0) return searchRecursive(k, root.left);
        else return searchRecursive(k, root.right);
    }

    /**
     * Rimuove un elemento dal dizionario. Se la chiave non è presente, non
     * fa nulla.
     * @param key La chiave dell'elemento da rimuovere
     */
    @Override
    public void remove(K key) {
        root = removeRecursive(key, root);
    }

    /**
     * Rimuove ricorsivamente un elemento dal dizionario.
     * Se la chiave non è presente, non fa nulla.
     * @param k La chiave dell'elemento da rimuovere
     */
    private Node removeRecursive(K k, Node root) {
        if (root == null) return root;    // Key is missing

        if (k.compareTo(root.key) == 0) {
            // Case 1: node to be deleted it's a leaf
            if (root.left == null && root.right == null) {
                root = null;
                return root;
            }

            // Case 2: node to be deleted has 1 child
            if(root.left == null) return root.right;
            else if(root.right == null) return root.left;

            // Case 3: node to be deleted it's an internal node (2 children)
            Node successor = minSubtree(root.right);
            root.key = successor.key;
            root.value = successor.value;

            root.right = removeRecursive(root.key, root.right);
        } else if (k.compareTo(root.key) < 0) root.left = removeRecursive(k, root.left);
        else root.right = removeRecursive(k, root.right);

        return root;
    }

    /**
     * Restituisce il minimo di un sottoalbero.
     *
     * @param root La radice del sottoalbero
     * @return Il nodo contenente la chiave minima
     */
    private Node minSubtree(Node root) {
        if (root.left == null) return root;
        return minSubtree(root.left);
    }

    /**
     * Rimuove tutti gli elementi dal dizionario.
     */
    @Override
    public void clear() {
        root = null;
    }

    /**
     * Cerca tutti gli elementi che coincidono con una data chiave,
     * usando un criterio personalizzato.
     *
     * @param key   La chiave da cercare
     * @param match Il predicato per stabilire se un elemento corrisponde
     * @return Tutti gli x.value tali che match(x.key, key) == true
     */
    @Override
    public List<V> searchAll(K key, BiPredicate<K, K> match) {
        return searchAllRecursive(key, match, root);
    }

    /**
     * Cerca ricorsivamente tutti gli elementi che coincidono con una data chiave,
     * usando un criterio personalizzato.
     *
     * @param k     La chiave da cercare
     * @param match Il predicato per stabilire se un elemento corrisponde
     * @param root  La radice dell'albero in cui cercare
     * @return Tutti gli x.value tali che match(x.key, key) == true
     */
    private List<V> searchAllRecursive(K k, BiPredicate<K, K> match, Node root) {
        if (root == null) return new ArrayList<V>();

        List<V> resultsRecursive = searchAllRecursive(k, match, root.left);
        resultsRecursive.addAll(searchAllRecursive(k, match, root.right));
        if (match.test(root.key, k)) resultsRecursive.add(root.value);

        return resultsRecursive;
    }
}