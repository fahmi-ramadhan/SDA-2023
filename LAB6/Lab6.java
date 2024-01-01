import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class Lab6 {
    private static InputReader in;
    private static PrintWriter out;
    static AVLTree tree = new AVLTree();

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int N = in.nextInt();
        for (int i = 0; i < N; i++) {
            grow();
            // printTree(tree.root, "", true);
        }


        int Q = in.nextInt();
        for (int i = 0; i < Q; i++) {
            char query = in.nextChar();

            if (query == 'G') { grow(); }
            else if (query == 'P') { pick(); }
            else if (query == 'F') { fall(); }
            else { height(); }
            // printTree(tree.root, "", true);
        }

        out.close();
    }

    static void grow() {
        int n = in.nextInt();
        tree.root = tree.insert(tree.root, n);
    }

    static void pick() {
        int n = in.nextInt();
        if (tree.find(tree.root, n) != null) {
            tree.root = tree.delete(tree.root, n);
            out.println(n);
        } else {
            out.println(-1);
        }
    }

    static void fall() {
        if (tree.root != null) {
            int maxKey = tree.findMax(tree.root).key;
            tree.root = tree.delete(tree.root, maxKey);
            out.println(maxKey);
        } else {
            out.println(-1);
        }
    }

    static void height() {
        if (tree.root != null) {
            out.println(tree.root.height);
        } else {
            out.println(0);
        }
    }

    // taken from https://www.programiz.com/dsa/avl-tree
    // a method to print the contents of a Tree data structure in a readable
    // format. it is encouraged to use this method for debugging purposes.
    // to use, simply copy and paste this line of code:
    // printTree(tree.root, "", true);
    static void printTree(Node currPtr, String indent, boolean last) {
        if (currPtr != null) {
            out.print(indent);
            if (last) {
                out.print("R----");
                indent += "   ";
            } else {
                out.print("L----");
                indent += "|  ";
            }
            out.println(currPtr.key);
            printTree(currPtr.left, indent, false);
            printTree(currPtr.right, indent, true);
        }
    }

    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the
    // usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit
    // Exceeded caused by slow input-output (IO)
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public char nextChar() {
            return next().charAt(0);
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

    }
}

class Node {
    int key;
    int height;
    Node left, right;

    Node(int key) {
        this.key = key;
        this.height = 1;
    }
}

class AVLTree {
    Node root;

    int height(Node N) { 
        if (N == null) 
            return 0; 
        return N.height; 
    } 

    int getBalance(Node N) { 
        // source: https://www.geeksforgeeks.org/deletion-in-an-avl-tree/
        if (N == null) 
            return 0; 
        return height(N.left) - height(N.right); 
    } 

    Node insert(Node node, int key) {
        if (node == null) {
            node =  new Node(key);
        } else if ( key < node.key ) {
            node.left = insert(node.left, key);
        } else if ( key > node.key ) {
            node.right = insert(node.right, key);
        }
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        node = balancing(node);
        return node;
    }

    Node findMax(Node node) {
        Node current = node;
        while (current.right != null)
            current = current.right;
        return current;
    }

    Node delete(Node node, int key) {
        // Source: https://www.geeksforgeeks.org/deletion-in-an-avl-tree/
        if (node == null) {
            return node;
        }
        if (key < node.key) {
            node.left = delete(node.left, key);
        } else if (key > node.key) {
            node.right = delete(node.right, key);
        } else {
            // node with only one child or no child 
            if ((node.left == null) || (node.right == null)) {
                Node temp = null;
                if (temp == node.left) {
                    temp = node.right;
                } else {
                    temp = node.left;
                }
                // No child case 
                if (temp == null) {
                    temp = node;
                    node = null;
                } 
                // One child case 
                else {
                    node = temp;
                }
            } 
            // node with two children
            else {
                Node temp = findMax(node.left);
                node.key = temp.key;
                node.left = delete(node.left, temp.key);
            }
        }

        if (node == null) 
            return node;

        node.height = Math.max(height(node.left), height(node.right)) + 1;
        return balancing(node);
    }

    Node balancing(Node node) {
        // source: https://www.geeksforgeeks.org/deletion-in-an-avl-tree/
        int balance = getBalance(node); 
 
        // Left Left
        if (balance > 1 && getBalance(node.left) >= 0) 
            return singleRightRotate(node); 
 
        // Left Right 
        if (balance > 1 && getBalance(node.left) < 0) { 
            node.left = singleLeftRotate(node.left); 
            return singleRightRotate(node); 
        } 
 
        // Right Right 
        if (balance < -1 && getBalance(node.right) <= 0) 
            return singleLeftRotate(node); 
 
        // Right Left 
        if (balance < -1 && getBalance(node.right) > 0) { 
            node.right = singleRightRotate(node.right); 
            return singleLeftRotate(node); 
        } 
 
        return node; 
    }

    Node singleLeftRotate(Node node) {
        Node node2 = node.right;
        node.right = node2.left;
        node2.left = node;
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        node2.height = Math.max(height(node2.left), height(node2.right)) + 1;
        return node2;
    }

    Node singleRightRotate(Node node) {
        Node node2 = node.left;
        node.left = node2.right;
        node2.right = node;
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        node2.height = Math.max(height(node2.left), height(node2.right)) + 1;
        return node2;
    }

    Node find(Node node, int key) {
        if (node == null || node.key == key) {
            return node;
        }
        if (node.key > key) {
            return find(node.left, key);
        } else {
            return find(node.right, key);
        }
    }
}