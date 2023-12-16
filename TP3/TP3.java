import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class TP3 {
    private static InputReader in;
    private static PrintWriter out;
    private static boolean[] isTreasureRoom;
    private static List<List<Edge>> nodeMaze;
    private static int V, E;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);
        
        V = in.nextInt();       // number of vertices (ruangan)
        E = in.nextInt();       // number of edges (koridor)

        isTreasureRoom = new boolean[V];
        nodeMaze = new ArrayList<List<Edge>>();
        ArrayList<Integer> treasureRooms = new ArrayList<Integer>();
        
        for (int i = 0; i < V; i++) {
            if (in.nextChar() == 'S') { treasureRooms.add(i); isTreasureRoom[i] = true; }
            nodeMaze.add(new ArrayList<Edge>());
        }

        for (int i = 0; i < E; i++) {
            int A = in.nextInt() - 1;
            int B = in.nextInt() - 1;
            long N = in.nextLong();
            nodeMaze.get(A).add(new Edge(B, N));
            nodeMaze.get(B).add(new Edge(A, N));
        }

        long[][] minGroupSizesFromTreasureRooms = new long[treasureRooms.size()][];
        for (int i = 0; i < treasureRooms.size(); i++) {
            minGroupSizesFromTreasureRooms[i] = dijkstra(treasureRooms.get(i));
        }

        long[] minGroupSizesFromV1 = dijkstra(0);
        long[][] minGroupSizes = new long[V][];

        int Q = in.nextInt();
        for (int i = 0; i < Q; i++) {
            char queryType = in.nextChar();
            if (queryType == 'M') {
                long groupSize = in.nextLong();
                int count = 0;
                for (int j = 0; j < V; j++) {
                    if (minGroupSizesFromV1[j] <= groupSize && isTreasureRoom[j]) count++;
                }
                out.println(count);
            } else if (queryType == 'S') {
                int startId = in.nextInt() - 1;
                long minGroupSize = Long.MAX_VALUE;
                for (int j = 0; j < treasureRooms.size(); j++) {
                    if (minGroupSizesFromTreasureRooms[j][startId] < minGroupSize) {
                        minGroupSize = minGroupSizesFromTreasureRooms[j][startId];
                    }
                }
                out.println(minGroupSize);
            } else if (queryType == 'T') {
                int startId = in.nextInt() - 1;
                int middleId = in.nextInt() - 1;
                int endId = in.nextInt() - 1;
                long groupSize = in.nextLong();
                if (minGroupSizes[startId] == null) {
                    minGroupSizes[startId] = dijkstra(startId);
                }
                if (minGroupSizes[middleId] == null) {
                    minGroupSizes[middleId] = dijkstra(middleId);
                }
                if (minGroupSizes[startId][middleId] <= groupSize && minGroupSizes[middleId][endId] <= groupSize) {
                    out.println("Y");
                } else if (minGroupSizes[startId][middleId] <= groupSize) {
                    out.println("H");
                } else {
                    out.println("N");
                }
            }
        }

        out.close();
    }

    // source: https://github.com/edutjie/sda222/blob/main/TP03/TP03.java
    static long[] dijkstra(int start) {
        MinHeap pq = new MinHeap(E);
        pq.add(new Node(start, 0));
        long[] minGroupSize = new long[V];
        Arrays.fill(minGroupSize, Long.MAX_VALUE);
        minGroupSize[start] = 0;
    
        while (!pq.isEmpty()) {
            Node node = pq.poll();
            if (node.maxWeight > minGroupSize[node.id]) continue;
    
            for (Edge edge : nodeMaze.get(node.id)) {
                long newGroupSize = Math.max(node.maxWeight, edge.weight);
                if (newGroupSize < minGroupSize[edge.to]) {
                    minGroupSize[edge.to] = newGroupSize;
                    pq.add(new Node(edge.to, newGroupSize));
                }
            }
        }
    
        return minGroupSize;
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
                try { tokenizer = new StringTokenizer(reader.readLine()); } 
                catch (IOException e) { throw new RuntimeException(e); }
            }
            return tokenizer.nextToken();
        }
        public char nextChar() { return next().charAt(0); }
        public int nextInt() { return Integer.parseInt(next()); }
        public long nextLong() { return Long.parseLong(next()); }
    }

    static class Edge {
        int to;
        long weight;
        Edge(int to, long weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    static class Node implements Comparable<Node> {
        int id;
        long maxWeight;

        Node(int id, long maxWeight) {
            this.id = id;
            this.maxWeight = maxWeight;
        }

        @Override
        public int compareTo(Node other) {
            return Long.compare(this.maxWeight, other.maxWeight);
        }
    }

    static class MinHeap {
        private Node[] heap;
        private int size;
    
        public MinHeap(int capacity) {
            heap = new Node[capacity];
            size = 0;
        }
    
        public boolean isEmpty() { return size == 0; }
        private int parent(int i) { return (i - 1) / 2; }
        private int leftChild(int i) { return 2 * i + 1; }
        private int rightChild(int i) { return 2 * i + 2; }
    
        public void add(Node node) {
            heap[size] = node;
            percolateUp(size);
            size++;
        }
    
        public Node poll() {
            Node root = heap[0];
            heap[0] = heap[size - 1];
            size--;
            percolateDown(0);
            return root;
        }
    
        private void percolateUp(int i) {
            while (i > 0 && heap[parent(i)].compareTo(heap[i]) > 0) {
                swap(i, parent(i));
                i = parent(i);
            }
        }
    
        private void percolateDown(int i) {
            int left = leftChild(i);
            int right = rightChild(i);
            int smallest = i;
    
            if (left < size && heap[left].compareTo(heap[smallest]) < 0) { smallest = left; }
            if (right < size && heap[right].compareTo(heap[smallest]) < 0) { smallest = right; }
            if (smallest != i) {
                swap(i, smallest);
                percolateDown(smallest);
            }
        }
    
        private void swap(int i, int j) {
            Node temp = heap[i];
            heap[i] = heap[j];
            heap[j] = temp;
        }
    }
}
