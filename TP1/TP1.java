import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.Math;
import java.util.*;

class Pengunjung {
    public String jenis;
    public int id, poin, totalMain, uang;
    public int[] urutanTiapWahana;

    public Pengunjung(int id, String jenis, int poin, int uang, int totalMain, int totalWahana) {
        this.id = id;
        this.jenis = jenis;
        this.poin = poin;
        this.uang = uang;
        this.totalMain = totalMain;
        urutanTiapWahana = new int[totalWahana];
    }
}

class Wahana {
    public int id, harga, poin, kapasitas, kapasitasFT;
    public PriorityQueue<Pengunjung> urutanFT = new PriorityQueue<>((pengunjung, otherPengunjung) -> {
        return sortUrutan(pengunjung, otherPengunjung);
    });
    public PriorityQueue<Pengunjung> urutanReguler = new PriorityQueue<>((pengunjung, otherPengunjung) -> {
        return sortUrutan(pengunjung, otherPengunjung);
    });

    public Wahana(int id, int harga, int poin, int kapasitas, int persentaseFT) {
        this.id = id;
        this.harga = harga;
        this.poin = poin;
        this.kapasitas = kapasitas;
        this.kapasitasFT = (int) Math.ceil((double) kapasitas * persentaseFT / 100 );
    }

    public int sortUrutan(Pengunjung pengunjung, Pengunjung otherPengunjung) {
        if (pengunjung.urutanTiapWahana[this.id-1] < otherPengunjung.urutanTiapWahana[this.id-1]) {return -1;} 
        else if (pengunjung.urutanTiapWahana[this.id-1] > otherPengunjung.urutanTiapWahana[this.id-1]) {return 1;}
        else {if (pengunjung.id < otherPengunjung.id) {return -1;} else {return 1;}}
    }
}

class WahanaUntukO {
    public int totalPointGenap, totalHargaGenap, totalHargaGanjil, totalPointGanjil;
    public String IDWahanaGenap, IDWahanaGanjil;

    public WahanaUntukO() {
        this.totalPointGenap = 0;
        this.totalHargaGenap = 0;
        this.IDWahanaGenap = "";
        this.totalPointGanjil = 0;
        this.totalHargaGanjil = 0;
        this.IDWahanaGanjil = "";
    }
}

public class TP1 {
    private static InputReader in;
    private static PrintWriter out;
    public static Deque<Pengunjung> daftarKeluar;
    public static WahanaUntukO[][] dp;

    public static int A(int IDPengunjung, int IDWahana, Wahana[] wahana, Pengunjung[] pengunjung) {
        Pengunjung tempPengunjung = pengunjung[IDPengunjung-1];
        Wahana tempWahana = wahana[IDWahana-1];
        if (tempPengunjung.uang < tempWahana.harga) {
            return -1;
        } else {
            if (tempPengunjung.jenis.equals("FT")) {
                tempPengunjung.urutanTiapWahana[IDWahana-1] = tempPengunjung.totalMain;
                tempWahana.urutanFT.offer(tempPengunjung);
            } else {
                tempPengunjung.urutanTiapWahana[IDWahana-1] = tempPengunjung.totalMain;
                tempWahana.urutanReguler.offer(tempPengunjung);
            }
            return tempWahana.urutanFT.size() + tempWahana.urutanReguler.size();
        }

    }

    public static String E(int IDWahana, Wahana[] wahana) {
        Wahana tempWahana = wahana[IDWahana-1];
        int counterBermain = tempWahana.kapasitas;
        int counterFT = tempWahana.kapasitasFT;
        StringBuilder IDYangMain = new StringBuilder();

        if (tempWahana.urutanFT.isEmpty() && tempWahana.urutanReguler.isEmpty()) {
            return "-1";
        }

        while (!tempWahana.urutanFT.isEmpty() && counterFT-- > 0) {
            Pengunjung temp = tempWahana.urutanFT.poll();
            if (temp.uang < tempWahana.harga) {
                counterFT++;
                continue;
            } else {
                temp.poin += tempWahana.poin;
                temp.totalMain++;
                counterBermain--;
                temp.uang -= tempWahana.harga;
                if (temp.uang == 0) {
                    daftarKeluar.addLast(temp);
                }
                IDYangMain.append(temp.id + " ");
            }
        }

        while (!tempWahana.urutanReguler.isEmpty() && counterBermain-- > 0) {
            Pengunjung temp = tempWahana.urutanReguler.poll();
            if (temp.uang < tempWahana.harga) {
                counterBermain++;
                continue;
            } else {
                temp.poin += tempWahana.poin;
                temp.totalMain++;
                temp.uang -= tempWahana.harga;
                if (temp.uang == 0) {
                    daftarKeluar.addLast(temp);
                }
                IDYangMain.append(temp.id + " ");
            }
        }

        while (!tempWahana.urutanFT.isEmpty() && counterBermain-- > 0) {
            Pengunjung temp = tempWahana.urutanFT.poll();
            if (temp.uang < tempWahana.harga) {
                counterBermain++;
                continue;
            } else {
                temp.poin += tempWahana.poin;
                temp.totalMain++;
                temp.uang -= tempWahana.harga;
                if (temp.uang == 0) {
                    daftarKeluar.addLast(temp);
                }
                IDYangMain.append(temp.id + " ");
            }
        }

        if (IDYangMain.toString().equals("")) {
            return "-1";
        }
        return IDYangMain.toString().trim();
    }

    public static int S(int IDPengunjung, int IDWahana, Wahana[] wahana) {
        Wahana tempWahana = wahana[IDWahana-1];
        PriorityQueue<Pengunjung> urutanFTCopy = new PriorityQueue<>(tempWahana.urutanFT);
        PriorityQueue<Pengunjung> urutanRegulerCopy = new PriorityQueue<>(tempWahana.urutanReguler);
        int counterAntrean = 0;
        
        while (!urutanFTCopy.isEmpty() || !urutanRegulerCopy.isEmpty()) {
            int counterFT = tempWahana.kapasitasFT;
            int counterBermain = tempWahana.kapasitas;

            while (!urutanFTCopy.isEmpty() && counterFT > 0) {
                Pengunjung temp = urutanFTCopy.poll();
                if (temp.uang < tempWahana.harga) {
                    continue;
                }
                counterAntrean++;
                counterFT--;
                counterBermain--;
                if (temp.id == IDPengunjung) {
                    return counterAntrean;
                } 
            }

            while (!urutanRegulerCopy.isEmpty() && counterBermain > 0) {
                Pengunjung temp = urutanRegulerCopy.poll();
                if (temp.uang < tempWahana.harga) {
                    continue;
                }
                counterAntrean++;
                counterBermain--;
                if (temp.id == IDPengunjung) {
                    return counterAntrean;
                } 
            }

            while (!urutanFTCopy.isEmpty() && counterBermain > 0) {
                Pengunjung temp = urutanFTCopy.poll();
                if (temp.uang < tempWahana.harga) {
                    continue;
                }
                counterAntrean++;
                counterBermain--;
                if (temp.id == IDPengunjung) {
                    return counterAntrean;
                }
            }
        }
        return -1;
    }

    public static int F(int P) {
        if (daftarKeluar.isEmpty()) {
            return -1;
        }
        if (P == 0) {
            Pengunjung temp = daftarKeluar.pollFirst();
            return temp.poin;
        } else {
            Pengunjung temp = daftarKeluar.pollLast();
            return temp.poin;
        }
    }

    public static String O(WahanaUntukO[][] dp , Wahana[] wahana, Pengunjung pengunjung) {
        int jumlahWahana = wahana.length;// Number of items
        int uangPengunjung = pengunjung.uang;
        
        if (dp[jumlahWahana][uangPengunjung].totalPointGenap == dp[jumlahWahana][uangPengunjung].totalPointGanjil) {
            if (dp[jumlahWahana][uangPengunjung].totalHargaGenap == dp[jumlahWahana][uangPengunjung].totalHargaGanjil) {
                if (dp[jumlahWahana][uangPengunjung].IDWahanaGenap.compareTo(dp[jumlahWahana][uangPengunjung].IDWahanaGanjil) < 0) {
                    return dp[jumlahWahana][uangPengunjung].totalPointGenap + " " + dp[jumlahWahana][uangPengunjung].IDWahanaGenap.trim();
                } else {
                    return dp[jumlahWahana][uangPengunjung].totalPointGanjil + " " + dp[jumlahWahana][uangPengunjung].IDWahanaGanjil.trim();
                }
            } else if (dp[jumlahWahana][uangPengunjung].totalHargaGenap < dp[jumlahWahana][uangPengunjung].totalHargaGanjil) {
                return dp[jumlahWahana][uangPengunjung].totalPointGenap + " " + dp[jumlahWahana][uangPengunjung].IDWahanaGenap.trim();
            } else {
                return dp[jumlahWahana][uangPengunjung].totalPointGanjil + " " + dp[jumlahWahana][uangPengunjung].IDWahanaGanjil.trim();
            }
        }
        if (dp[jumlahWahana][uangPengunjung].totalPointGenap > dp[jumlahWahana][uangPengunjung].totalPointGanjil) {
            return dp[jumlahWahana][uangPengunjung].totalPointGenap + " " + dp[jumlahWahana][uangPengunjung].IDWahanaGenap.trim();  
        }
        return dp[jumlahWahana][uangPengunjung].totalPointGanjil + " " + dp[jumlahWahana][uangPengunjung].IDWahanaGanjil.trim();  
    }

    public static int compareString(String IDwahana1, String IDWahana2) {
        String[] IDs1 = IDwahana1.split(" ");
        String[] IDs2 = IDWahana2.split(" ");

        int banyakLoop = Math.min(IDs1.length, IDs2.length);

        for (int i = 0; i < banyakLoop; i++) {
            int num1 = Integer.parseInt(IDs1[i]);
            int num2 = Integer.parseInt(IDs2[i]);

            if (num1 < num2) {
                return -1;
            } else if (num1 > num2) {
                return 1;
            }
        }
        return Integer.compare(IDs1.length, IDs2.length);
    }

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Read value of T
        int M = in.nextInt();
        Wahana[] wahana = new Wahana[M];
        
        for (int i = 1; i <= M; i++) {
            int harga = in.nextInt();
            int point = in.nextInt();
            int kapasitas = in.nextInt();
            int persentaseFT = in.nextInt();
            wahana[i-1] = new Wahana(i, harga, point, kapasitas, persentaseFT);
        }

        int N = in.nextInt();
        int uangMax = 0;
        Pengunjung[] pengunjung = new Pengunjung[N];

        for (int i = 1; i <= N; i++) {
            String jenis = in.next();
            int uang = in.nextInt();
            if (uang > uangMax) {uangMax = uang;}
            pengunjung[i-1] = new Pengunjung(i, jenis, 0, uang, 0, M);
        }

        daftarKeluar = new ArrayDeque<>();
        int T = in.nextInt();

        while (T-- > 0) {
            String aktivitas = in.next();
            switch(aktivitas){
                case "A": {
                    int IDPengunjung = in.nextInt();
                    int IDWahana = in.nextInt();
                    out.println(A(IDPengunjung, IDWahana, wahana, pengunjung));
                    break;
                }
                case "E": {
                    int IDWahana = in.nextInt();
                    out.println(E(IDWahana, wahana));
                    break;
                }
                case "S": {
                    int IDPengunjung = in.nextInt();
                    int IDWahana = in.nextInt();
                    out.println(S(IDPengunjung, IDWahana, wahana));
                    break;
                }
                case "F": {
                    int P = in.nextInt();
                    out.println(F(P));
                    break;
                }
                case "O": {
                    int IDPengunjung = in.nextInt();
                    if (dp == null) {
                        dp = new WahanaUntukO[wahana.length + 1][uangMax + 1];
                    }
                    if(dp[0][0] == null) {
                        for (int i = 1; i <= wahana.length; i++) {
                            int weight = wahana[i-1].harga;
                            int profit = wahana[i-1].poin;
                            for (int j = 0; j <= uangMax; j++) {
                                
                                    if (dp[i-1][j] == null) {
                                        dp[i-1][j] = new WahanaUntukO();
                                    }
                                    dp[i][j] = dp[i-1][j];
                                    if (j >= weight) {
                                        int tempPoint, tempHarga;
                                        String tempIDWahana;
                                        if (i % 2 == 1) {
                                            tempPoint = dp[i - 1][j - weight].totalPointGenap + profit;
                                            tempHarga = dp[i-1][j - weight].totalHargaGenap + weight;
                                            tempIDWahana = dp[i - 1][j - weight].IDWahanaGenap + i + " ";
                                            if (tempPoint == dp[i][j].totalPointGanjil) {
                                                if (tempHarga == dp[i][j].totalHargaGanjil) {
                                                    if (compareString(tempIDWahana, dp[i][j].IDWahanaGanjil) < 0) {
                                                        dp[i][j].totalPointGanjil = tempPoint;
                                                        dp[i][j].totalHargaGanjil = tempHarga;
                                                        dp[i][j].IDWahanaGanjil = tempIDWahana; 
                                                    }
                                                } else if (tempHarga < dp[i][j].totalHargaGanjil) {
                                                    dp[i][j].totalPointGanjil = tempPoint;
                                                    dp[i][j].totalHargaGanjil = tempHarga;
                                                    dp[i][j].IDWahanaGanjil = tempIDWahana;
                                                }
                                            } if (tempPoint > dp[i][j].totalPointGanjil) {
                                                dp[i][j].totalPointGanjil = tempPoint;
                                                dp[i][j].totalHargaGanjil = tempHarga;
                                                dp[i][j].IDWahanaGanjil = tempIDWahana;
                                            }
                                        } else {
                                            tempPoint = dp[i - 1][j - weight].totalPointGanjil + profit;
                                            tempHarga = dp[i-1][j - weight].totalHargaGanjil + weight;
                                            tempIDWahana = dp[i - 1][j - weight].IDWahanaGanjil + i + " ";
                                            if (tempPoint == dp[i][j].totalPointGenap) {
                                                if (tempHarga == dp[i][j].totalHargaGenap) {
                                                    if (compareString(tempIDWahana, dp[i][j].IDWahanaGenap) < 0) {
                                                        dp[i][j].totalPointGenap = tempPoint;
                                                        dp[i][j].totalHargaGenap = tempHarga;
                                                        dp[i][j].IDWahanaGenap = tempIDWahana; 
                                                    }
                                                } else if (tempHarga < dp[i][j].totalHargaGenap) {
                                                    dp[i][j].totalPointGenap = tempPoint;
                                                    dp[i][j].totalHargaGenap = tempHarga;
                                                    dp[i][j].IDWahanaGenap = tempIDWahana;
                                                }
                                            } if (tempPoint > dp[i][j].totalPointGenap) {
                                                dp[i][j].totalPointGenap = tempPoint;
                                                dp[i][j].totalHargaGenap = tempHarga;
                                                dp[i][j].IDWahanaGenap = tempIDWahana;
                                            }
                                        }
                                    }
                            }
                                
                        }
                    }
                    out.println(O(dp, wahana, pengunjung[IDPengunjung-1]));
                    break;
                }
            }
        }
        out.close();
    }
        

        // don't forget to close/flush the output


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
    }
}