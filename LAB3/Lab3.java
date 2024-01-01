import java.io.*;
import java.util.StringTokenizer;
import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;

public class Lab3{
    private static InputReader in;
    private static PrintWriter out;
    
    private static ArrayList<Queue<Integer>> listGedung = new ArrayList<>();
    private static int index = 0;
    private static boolean kanan = true;
    private static long T;
    private static int X, C, Q;
    private static long poinTotal;
    private static boolean menang = false;

    // Metode GA
    static String GA() {
        //TODO: Implement this method
        kanan = !kanan;
        if (kanan) return "KANAN";
        else return "KIRI";
    }

    // Metode S
    static long S(int Si){
        //TODO: Implement this method
        int poin = 0;
        
        if (menang) {
            out.println("MENANG");
            return -1;
        }

        for (int i = 0; i < Si; i++) {
            if (listGedung.get(index).isEmpty()) break;
            poin += listGedung.get(index).poll();
            if (poinTotal + poin >= T) {
                menang = true;
                out.println("MENANG");
                return -1;
            }
        }

        do {
            if (kanan) index++;
            else index--;

            if (index < 0) index = X-1;
            else if (index == X) index = 0;
        } while (listGedung.get(index).isEmpty());

        poinTotal += poin;

        return poin;
    }

    // Template
    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);
        
        // Read input
        T = in.nextLong();
        X = in.nextInt();
        C = in.nextInt();
        Q = in.nextInt();

        for (int i = 0; i < X; i++) {
            Queue<Integer> gedung = new LinkedList<>();
            // Insert into ADT
            for (int j = 0; j < C; j++) {
                int Ci = in.nextInt();
                gedung.add(Ci);
            }
            listGedung.add(gedung);
        }

        // Process the query
        for (int i = 0; i < Q; i++) {
            String perintah = in.next();
            if (perintah.equals("GA")) {
                out.println(GA());
            } else if (perintah.equals("S")) {
                int Si = in.nextInt();
                long poin = S(Si);
                if (poin == -1) continue;
                else out.println(poin);
            }
        }

        // don't forget to close the output
        out.close();
    }
    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit Exceeded caused by slow input-output (IO)
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

        public long nextLong(){
            return Long.parseLong(next());
        }

    }
}