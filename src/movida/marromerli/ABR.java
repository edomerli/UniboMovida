package movida.marromerli;

import java.util.Comparator;

public class ABR<K, V> implements Dictionary<K, V> {

    private Comparator<K> comparator;

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

    public ABR(Comparator<K> comparator){
        root = null;
        this.comparator = comparator;
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

        if(comparator.compare(k, root.key) < 0) root.left = insertRecursive(k, v, root.left);
        else if(comparator.compare(k, root.key) > 0) root.right = insertRecursive(k, v, root.right);
        else root.value = v;

        return root;
    }

    @Override
    public V search(K key){
        return searchRecursive(key, root);
    }

    private V searchRecursive(K k, Node root){
        if(root == null) return null;   // Key is missing

        if(comparator.compare(k, root.key) == 0) return root.value;

        else if(comparator.compare(k, root.key) < 0) return searchRecursive(k, root.left);
        else return searchRecursive(k, root.right);
    }

    @Override
    public void remove(K key){
        root = removeRecursive(key, root);
    }

    private Node removeRecursive(K k, Node root){
        if(root == null) return root;    // Key is missing

        if(comparator.compare(k, root.key) == 0){
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

        else if(comparator.compare(k, root.key) < 0) root.left = removeRecursive(k, root.left);
        else root.right = removeRecursive(k, root.right);

        return root;
    }

    private Node minSubtree(Node root){
        if(root.left == null) return root;
        return minSubtree(root.left);
    }

    public void clear(){
        root = null;
        // TODO: il resto lo gestisce il Garbage Collector, right?
    }
}