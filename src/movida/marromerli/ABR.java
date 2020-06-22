package movida.marromerli;

import movida.commons.Movie;

public class ABR {

    private class Node {
        public Movie movie;
        public Node left, right;

        public Node(Movie m){
            movie = m;
            left = null;
            right = null;
        }
    }

    private Node root;

    public ABR(){
        root = null;
    }

    public void insert(Movie m){
        root = insertRecursive(m, root);
    }

    private Node insertRecursive(Movie m, Node root){
        if(root == null){
            Node leaf = new Node(m);
            return root;
        }

        if(m.compareTo(root.movie) < 0) root.left = insertRecursive(m, root.left);
        else if(m.compareTo(root.movie) > 0) root.right = insertRecursive(m, root.right);

        return root;
    }

    public Movie search(Movie m){
        return searchRecursive(m, root);
    }

    private Movie searchRecursive(Movie m, Node root){
        if(root == null) return null;   // Movie is missing

        if(m.compareTo(root.movie) == 0) return root.movie;

        else if(m.compareTo(root.movie) < 0) return searchRecursive(m, root.left);
        else return searchRecursive(m, root.right);
    }

    public void remove(Movie m){
        root = removeRecursive(m, root);
    }

    private Node removeRecursive(Movie m, Node root){
        if(root == null) return root;    // Movie is missing

        if(m.compareTo(root.movie) == 0){
            // Case 1: node to be deleted it's a leaf
            if(root.left == null && root.right == null){
                root = null;
                return root;
            }

            // Case 2: node to be deleted has 1 child
            if(root.left == null) return root.right;
            else if(root.right == null) return root.left;

            // Case 3: node to be deleted it's an internal node (2 children)
            root.movie = minSubtree(root.right);
            root.right = removeRecursive(root.movie, root.right);
        }

        else if(m.compareTo(root.movie) < 0) root.left = removeRecursive(m, root.left);
        else root.right = removeRecursive(m, root.right);

        return root;
    }

    private Movie minSubtree(Node root){
        if(root.left == null) return root.movie;
        return minSubtree(root.left);
    }
}