import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

public class test{
    private static InputReader in;
    private static PrintWriter out;
    static Deque<Deque<Integer>> dequeGedung = new ArrayDeque<Deque<Integer>>();
    static Long T;
    static String arah = "KANAN";

    // Metode GA
    static String GA() {
        if (arah == "KANAN") {
            arah = "KIRI";
        }
        else {
            arah = "KANAN";
        }
        return arah;
    }

    // Metode S
    static long S (long Si) {
        long lantai = 0;
        long totalDemeg = 0;

        if (arah == "KANAN"){
            Deque<Integer> temp = dequeGedung.pollFirst();
            while (!temp.isEmpty() && Si > 0) {
                lantai = (int) temp.pop();
                totalDemeg += lantai;
                T = T - lantai;
                Si--;
            }
            if (!temp.isEmpty()) {
                dequeGedung.addLast(temp);
            }
        }

        else {
            Deque<Integer> temp = dequeGedung.pollFirst();
             while (!temp.isEmpty() && Si > 0) {
                lantai = (int) temp.pop();
                totalDemeg += lantai;
                T = T - lantai;
                Si--;
            }
            if (!temp.isEmpty()) {
                dequeGedung.addFirst(temp);
            }
            
            Deque<Integer> tempDua = dequeGedung.pollLast();
            dequeGedung.addFirst(tempDua);
        }

        if (T <= 0) {
            return -1;
        }

        if (dequeGedung.isEmpty()) {
            return -1;
        }

        return totalDemeg;
    }

    // Template
    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);
        
        // Read input
        T = in.nextLong();
        int X = in.nextInt();
        int C = in.nextInt();
        int Q = in.nextInt();

        for (int i = 0; i < X; i++) {
            Deque<Integer> dequeLantai = new ArrayDeque<Integer>(C);

            // Insert into ADT
            for (int j = 0; j < C; j++) {
                int Ci = in.nextInt();
                dequeLantai.addLast(Ci);
            }

           dequeGedung.add(dequeLantai);
        }

        // Process the query
        for (int i = 0; i < Q; i++) {
            String perintah = in.next();
            if (perintah.equals("GA")) {
                out.println(GA());
            } else if (perintah.equals("S")) {
                long Si = in.nextLong();
                long a = S(Si);

                if (a <= 0) {
                    out.println("MENANG");
                }
                else {
                    out.println(a);
                }
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