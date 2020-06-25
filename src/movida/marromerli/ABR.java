package movida.marromerli;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class ABR<K extends Comparable<K>, V> implements Dictionary<K, V> {

    private class Node {
        public K key;
        public V value;
        public Node left, right;

        public Node(K key, V value){
            this.key = key;
            this.value = value;
            left = null;
            right = null;
        }
    }

    private Node root;

    public ABR(){
        root = null;
    }

    @Override
    public void insert(K key, V value){
        root = insertRecursive(key, value, root);
    }

    private Node insertRecursive(K k, V v, Node root){
        if(root == null){
            Node leaf = new Node(k, v);
            return leaf;
        }

        if(k.compareTo(root.key) < 0) root.left = insertRecursive(k, v, root.left);
        else if(k.compareTo(root.key) > 0) root.right = insertRecursive(k, v, root.right);
        else root.value = v;

        return root;
    }

    @Override
    public V search(K key){
        return searchRecursive(key, root);
    }

    private V searchRecursive(K k, Node root){
        if(root == null) return null;   // Key is missing

        if(k.compareTo(root.key) == 0) return root.value;

        else if(k.compareTo(root.key) < 0) return searchRecursive(k, root.left);
        else return searchRecursive(k, root.right);
    }

    @Override
    public void remove(K key){
        root = removeRecursive(key, root);
    }

    private Node removeRecursive(K k, Node root){
        if(root == null) return root;    // Key is missing

        if(k.compareTo(root.key) == 0){
            // Case 1: node to be deleted it's a leaf
            if(root.left == null && root.right == null){
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
        }

        else if(k.compareTo(root.key) < 0) root.left = removeRecursive(k, root.left);
        else root.right = removeRecursive(k, root.right);

        return root;
    }

    private Node minSubtree(Node root){
        if(root.left == null) return root;
        return minSubtree(root.left);
    }

    @Override
    public void clear(){
        root = null;
    }

    @Override
    public List<V> searchAll(K key, BiPredicate<K, K> match){
        return searchAllRecursive(key, match, root);
    }

    private List<V> searchAllRecursive(K k, BiPredicate<K, K> match, Node root){
        if(root == null) return new ArrayList<V>();

        List<V> resultsRecursive = searchAllRecursive(k, match, root.left);
        resultsRecursive.addAll(searchAllRecursive(k, match, root.right));
        if(match.test(root.key, k)) resultsRecursive.add(root.value);

        return resultsRecursive;
    }
}