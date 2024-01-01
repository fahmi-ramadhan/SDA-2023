import java.io.*;
import java.util.StringTokenizer;

public class Lab2 {
    private static InputReader in;
    private static PrintWriter out;

    static long maxOddEvenSubSum(long[] a) {
        // TODO: Implement this method
        boolean even = a.length % 2 == 0;
        long thisSum = 0, maxSum = Integer.MIN_VALUE;
        
        if (even) {
            for (int i = 0; i < a.length; i++) {
                thisSum += a[i];
                if (!(a[i] % 2 == 0)) thisSum = 0;
                else maxSum = Math.max(thisSum, maxSum);
                if (thisSum < 0) thisSum = 0;
            }
        } else {
            for (int i = 0; i < a.length; i++) {
                thisSum += a[i];
                if (a[i] % 2 == 0) thisSum = 0;
                else maxSum = Math.max(thisSum, maxSum);
                if (thisSum < 0) thisSum = 0;
            }
        }
        if (maxSum == Integer.MIN_VALUE) maxSum = 0;
        return maxSum;
    }

    public static void main(String[] args) throws IOException {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Read value of N
        int N = in.nextInt();

        // Read value of x
        long[] x = new long[N];
        for (int i = 0; i < N; ++i) {
            x[i] = in.nextLong();
        }

        long ans = maxOddEvenSubSum(x);
        out.println(ans);

        // don't forget to close/flush the output
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
        
        public long nextLong() {
            return Long.parseLong(next());
        }
    }
}