import java.io.*;
import java.util.*;

public class Lab8 {
    private static InputReader in;
    private static PrintWriter out;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int N = in.nextInt();
        int E = in.nextInt();
        Graph graph = new Graph(N);

        for (int i = 0; i < E; i++) {
            int A = in.nextInt();
            int B = in.nextInt();
            long W = in.nextLong();
            graph.addEdge(A, B, W);
        }

        int H = in.nextInt(); 
        ArrayList<Integer> treasureNodes = new ArrayList<Integer>();
        for (int i = 0; i < H; i++) {
            int K = in.nextInt();
            treasureNodes.add(K);
        }

        int Q = in.nextInt();
        int O = in.nextInt();
        HashMap<Integer, ArrayList<Long>> vertexMap = new HashMap<Integer, ArrayList<Long>>();
        while (Q-- > 0) {
            Long totalOxygenNeeded = (long) 0;

            int T = in.nextInt();
            int davePosition = 1;
            while (T-- > 0) {
                int D = in.nextInt();
                // Update total oxygen dibutuhkan
                if (vertexMap.containsKey(davePosition)) {
                    totalOxygenNeeded += vertexMap.get(davePosition).get(D);
                }
                else {
                    vertexMap.put(davePosition, graph.dijkstra(davePosition));
                    totalOxygenNeeded += vertexMap.get(davePosition).get(D);
                }
                // Update posisi Dave
                davePosition = D;
            }
            // Implementasi Dave kembali ke daratan
            if (vertexMap.containsKey(davePosition)) {
                totalOxygenNeeded += vertexMap.get(davePosition).get(1);
            }
            else {
                vertexMap.put(davePosition, graph.dijkstra(davePosition));
                totalOxygenNeeded += vertexMap.get(davePosition).get(1);
            }
            // Cetak 0 (rute tidak aman) atau 1 (rute aman)
            if (totalOxygenNeeded >= O) {
                out.println(0);
            }
            else {
                out.println(1);
            }
        }

        out.close();
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

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }
    }
}

// TODO: Implementasi Graph
class Graph {
    List<List<Edge>> adj;
    int vertices;

    public Graph(int n) {
        this.vertices = n;
        this.adj = new ArrayList<>();
        for (int i = 0; i <= n; i++)
            this.adj.add(new ArrayList<>());
    }

    public void addEdge(int from, int to, long weight) {
        adj.get(from).add(new Edge(to, weight));
        adj.get(to).add(new Edge(from, weight));
    }

    // source: https://github.com/edutjie/sda222/blob/main/07/Lab7.java
    public ArrayList<Long> dijkstra(int source) {
        if (source == 0)
            return null;
        ArrayList<Long> dist = new ArrayList<>();
        for (int i = 0; i <= vertices; i++)
            dist.add(Long.MAX_VALUE);
        dist.set(source, (long) 0);

        PriorityQueue<Pair> pq = new PriorityQueue<>();
        pq.add(new Pair(source, 0));

        while (!pq.isEmpty()) {
            Pair curr = pq.poll();
            int v = curr.src; // source
            long w = curr.oxygen; // jumlah oksigen

            if (w > dist.get(v))
                continue;

            for (Edge e : this.adj.get(v)) {
                int u = e.to;
                long weight = e.weight;
                if (dist.get(v) + weight < dist.get(u)) {
                    dist.set(u, dist.get(v) + weight);
                    pq.add(new Pair(u, dist.get(u)));
                }
            }
        }

        return dist;
    }
}

class Edge {
    int to;
    long weight;

    public Edge(int to, long weight) {
        this.to = to;
        this.weight = weight;
    }
}

class Pair implements Comparable<Pair> {
    int src;
    long oxygen;

    public Pair(int src, long oxygen) {
        this.src = src;
        this.oxygen = oxygen;
    }

    @Override
    public int compareTo(Pair o) {
        return (int) (this.oxygen - o.oxygen);
    }
}
