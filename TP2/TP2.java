import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TP2 {
    private static InputReader in;
    private static PrintWriter out;
    static int[] jumlahSiswaPerKelas;
    static List<Siswa> daftarSiswa = new ArrayList<>();
    static DoublyLinkedList sekolah = new DoublyLinkedList();
    
    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int M = in.nextInt();               // Jumlah kelas
        jumlahSiswaPerKelas = new int[M];   // Jumlah siswa per kelas

        // Input jumlah siswa per kelas
        for (int i = 0; i < M; i++) { jumlahSiswaPerKelas[i] = in.nextInt(); }

        // Memasukkan siswa ke AVLTree (kelas) dan AVLTree ke DoublyLinkedList (sekolah)
        for (int jumlahSiswa : jumlahSiswaPerKelas) {
            AVLTree kelas = new AVLTree(-1);
            for (int j = 0; j < jumlahSiswa; j++) {
                int poin = in.nextInt();
                Siswa siswa = new Siswa(poin, daftarSiswa.size() + 1);
                daftarSiswa.add(siswa);
                kelas.insert(kelas.root, siswa);
            }
            sekolah.add(kelas);
        }

        int Q = in.nextInt();   // Jumlah query

        for (int i = 0; i < Q; i++) {
            String query = in.next();
            if (query.equals("T")) { T(); }
            else if (query.equals("C")) { C(); }
            else if (query.equals("G")) { G(); }
            else if (query.equals("S")) { S(); }
            else if (query.equals("K")) { K(); }
            else if (query.equals("A")) { A(); }
        }
        out.close();
    }

    static void T() {
        // Input poinTugas dan idSiswa
        int poinTugas = in.nextInt();
        int idSiswa = in.nextInt();
        // Jika idSiswa lebih besar dari jumlah siswa, maka tidak ada siswa dengan id tersebut
        if (idSiswa > daftarSiswa.size()) { out.println(-1); return; }
        // Mengakses siswa dari daftarSiswa dan kelas tempat Pakcil berada
        Siswa siswa = daftarSiswa.get(idSiswa - 1);
        AVLTree currentKelas = sekolah.current.kelas;
        // Jika siswa ada di kelas tempat Pakcil berada
        if (currentKelas.find(currentKelas.root, siswa) != null) {
            // Jika poinTugas = 0, maka tidak ada perubahan poin
            if (poinTugas == 0) { out.println(siswa.poin); return; }
            // Jika poinTugas > 0, maka poin siswa akan bertambah sebanyak poinTugas
            int jumlahTutee = currentKelas.hitungTutee(currentKelas.root, siswa.poin) - 1;
            currentKelas.delete(currentKelas.root, siswa);
            siswa.poin += poinTugas + Math.min(poinTugas, jumlahTutee);
            currentKelas.insert(currentKelas.root, siswa);
            out.println(siswa.poin);
        }
        // Jika siswa tidak ada di kelas tempat Pakcil berada
        else { out.println(-1); }
    }

    static void C() {
        // Input idSiswa
        int idSiswa = in.nextInt();
        // Jika idSiswa lebih besar dari jumlah siswa, maka tidak ada siswa dengan id tersebut
        if (idSiswa > daftarSiswa.size()) { out.println(-1); return; }
        // Mengakses siswa dari daftarSiswa dan kelas tempat Pakcil berada
        Siswa siswa = daftarSiswa.get(idSiswa - 1);
        ListNode currentListNode = sekolah.current;
        AVLTree currentKelas = currentListNode.kelas;
        // Jika siswa ada di kelas tempat Pakcil berada
        if (currentKelas.find(currentKelas.root, siswa) != null) {
            // Jika siswa ini curang pertama kali, maka poinnya akan menjadi 0
            if (siswa.totalCurang == 0) {
                currentKelas.delete(currentKelas.root, siswa);
                siswa.totalCurang++;
                siswa.poin = 0;
                currentKelas.insert(currentKelas.root, siswa);
                out.println(0);
            }
            // Jika siswa ini curang kedua kali, maka poinnya akan menjadi 0 dan dipindahkan ke kelas terburuk
            else if (siswa.totalCurang == 1) {
                currentKelas.delete(currentKelas.root, siswa);
                siswa.totalCurang++;
                siswa.poin = 0;
                ListNode lastListNode = sekolah.last;
                AVLTree lastKelas = lastListNode.kelas;
                lastKelas.insert(lastKelas.root, siswa);
                out.println(lastListNode.kelas.id);
            }
            // Jika siswa ini curang ketiga kali, maka akan di drop out
            else if (siswa.totalCurang == 2) {
                currentKelas.delete(currentKelas.root, siswa);
                siswa.totalCurang++;
                out.println(idSiswa);
            }
            // Jika kelas tempat pakcil berada sekarang berisi 5 siswa, maka siswa akan dipindahkan ke kelas yang lebih buruk
            if (currentKelas.root.size == 5) {
                // Inisialisasi tujuanPindah dengan kelas setelahnya
                ListNode tujuanPindah = currentListNode.next;
                // Jika kelas yang ingin dihapus adalah kelas pertama, pindahkan pointer first ke kelas selanjutnya
                if (currentListNode == sekolah.first) { sekolah.first = currentListNode.next; }
                // Jika kelas yang ingin dihapus adalah kelas terakhir, pindahkan pointer last ke kelas sebelumnya
                // dan ubah tujuan pindah menjadi kelas sebelumnya
                else if (currentListNode == sekolah.last) {
                    sekolah.last = currentListNode.prev;
                    tujuanPindah = currentListNode.prev;
                }
                // Memindahkan semua siswa pada kelas sekarang ke kelas tujuan
                for (int j = 0; j < 5; j++) {
                    Siswa currentSiswa = currentKelas.root.siswa;
                    currentKelas.delete(currentKelas.root, currentSiswa);
                    tujuanPindah.kelas.insert(tujuanPindah.kelas.root, currentSiswa);
                }
                // Menghapus kelas sekarang dari sekolah, mengubah pointer current, dan mengurangi jumlah kelas
                sekolah.current.prev.next = sekolah.current.next;
                sekolah.current.next.prev = sekolah.current.prev;
                sekolah.current = tujuanPindah;
                sekolah.size--;
            }
        }
        // Jika siswa tidak ada di kelas tempat Pakcil berada
        else { out.println(-1); }
    }

    static void G() {
        // Input direction (L atau R) dan mencetak id kelas tempat Pakcil berada setelah pindah
        char direction = in.nextChar();
        out.println(sekolah.move(direction).kelas.id);
    }

    static void S() {
        // Mengakses kelas tempat Pakcil berada sekarang, kelas sebelumnya, dan kelas setelahnya
        ListNode currentListNode = sekolah.current;
        AVLTree currentKelas = currentListNode.kelas;
        AVLTree nextKelas = currentListNode.next.kelas;
        AVLTree prevKelas = currentListNode.prev.kelas;
        // Inisialisasi array untuk menyimpan siswa terbaik dan terburuk dari kelas sebelumnya, kelas setelahnya, dan kelas sekarang
        Siswa[] terburukCurrentKelas = new Siswa[3];
        Siswa[] terbaikNextKelas = new Siswa[3];
        Siswa[] terburukPrevKelas = new Siswa[3];
        Siswa[] terbaikCurrentKelas = new Siswa[3];
        // Jika hanya ada satu kelas, maka tidak ada perubahan
        if (sekolah.size == 1) { out.println("-1 -1"); return; }
        // Kelas tempat pakcil berada sekarang merupakan kelas pertama
        else if (currentKelas == sekolah.first.kelas) {
            for (int i = 0; i < 3; i++) {
                terburukCurrentKelas[i] = currentKelas.findMin(currentKelas.root).siswa;
                currentKelas.delete(currentKelas.root, terburukCurrentKelas[i]);
                terbaikNextKelas[i] = nextKelas.findMax(nextKelas.root).siswa;
                nextKelas.delete(nextKelas.root, terbaikNextKelas[i]);
            }
            for (int i = 0; i < 3; i++) {
                currentKelas.insert(currentKelas.root, terbaikNextKelas[i]);
                nextKelas.insert(nextKelas.root, terburukCurrentKelas[i]);
            }
        }
        // Kelas tempat pakcil berada sekarang merupakan kelas terakhir
        else if (currentKelas == sekolah.last.kelas) {
            for (int i = 0; i < 3; i++) {
                terbaikCurrentKelas[i] = currentKelas.findMax(currentKelas.root).siswa;
                currentKelas.delete(currentKelas.root, terbaikCurrentKelas[i]);
                terburukPrevKelas[i] = prevKelas.findMin(prevKelas.root).siswa;
                prevKelas.delete(prevKelas.root, terburukPrevKelas[i]);
            }
            for (int i = 0; i < 3; i++) {
                currentKelas.insert(currentKelas.root, terburukPrevKelas[i]);
                prevKelas.insert(prevKelas.root, terbaikCurrentKelas[i]);
            }
        }
        // Kelas tempat pakcil berada sekarang bukan merupakan kelas pertama atau terakhir
        else {
            for (int i = 0; i < 3; i++) {
                terburukCurrentKelas[i] = currentKelas.findMin(currentKelas.root).siswa;
                currentKelas.delete(currentKelas.root, terburukCurrentKelas[i]);
                terbaikNextKelas[i] = nextKelas.findMax(nextKelas.root).siswa;
                nextKelas.delete(nextKelas.root, terbaikNextKelas[i]);
                terburukPrevKelas[i] = prevKelas.findMin(prevKelas.root).siswa;
                prevKelas.delete(prevKelas.root, terburukPrevKelas[i]);
                terbaikCurrentKelas[i] = currentKelas.findMax(currentKelas.root).siswa;
                currentKelas.delete(currentKelas.root, terbaikCurrentKelas[i]);
            }
            for (int i = 0; i < 3; i++) {
                currentKelas.insert(currentKelas.root, terburukPrevKelas[i]);
                nextKelas.insert(nextKelas.root, terburukCurrentKelas[i]);
                prevKelas.insert(prevKelas.root, terbaikCurrentKelas[i]);
                currentKelas.insert(currentKelas.root, terbaikNextKelas[i]);
            }
        }
        // Mendapatkan id siswa terbaik dan terburuk dari kelas tempat pakcil berada sekarang dan mencetaknya
        int idSiswaTerbaikCurrentKelas = currentKelas.findMax(currentKelas.root).siswa.id;
        int idSiswaTerburukCurrentKelas = currentKelas.findMin(currentKelas.root).siswa.id;
        out.println(idSiswaTerbaikCurrentKelas + " " + idSiswaTerburukCurrentKelas);
    }

    static void K() {
        // Menyimpan kelas tempat Pakcil berada sekarang
        AVLTree kelasPakcil = sekolah.current.kelas;
        // Melakukan sorting pada semua kelas dan menyimpan hasil sorting-nya ke array daftarKelas
        AVLTree[] daftarKelas = sekolah.sort();
        // Menghapus semua kelas dari DoublyLinkedList sekolah
        sekolah.clear();
        // Memasukkan semua kelas yang sudah diurutkan ke DoublyLinkedList sekolah
        for (int i = 0; i < daftarKelas.length; i++) { sekolah.add(daftarKelas[i]); }
        // Mengakses urutan kelas tempat Pakcil berada sekarang dan mencetaknya
        int posisiPakcil = 1;
        ListNode traverseNode = sekolah.first;
        while (traverseNode.kelas != kelasPakcil) {
            posisiPakcil++;
            traverseNode = traverseNode.next;
        }
        sekolah.current = traverseNode;
        out.println(posisiPakcil);
    }

    static void A() {
        // Input jumlah siswa pada kelas baru
        int jumlahSiswa = in.nextInt();
        // Membuat kelas baru dan megisinya dengan siswa sebanyak jumlahSiswa serta mencetak id-nya
        AVLTree kelas = new AVLTree(-1);
        for (int i = 0; i < jumlahSiswa; i++) {
            Siswa siswa = new Siswa(0, daftarSiswa.size() + 1);
            daftarSiswa.add(siswa);
            kelas.insert(kelas.root, siswa);
        }
        sekolah.add(kelas);
        out.println(sekolah.last.kelas.id);
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
    }
}

class Siswa implements Comparable<Siswa> {
    int poin;
    int totalCurang;
    int id;

    Siswa(int point, int id) {
        this.poin = point;
        this.id = id;
        this.totalCurang = 0;
    }

    @Override
    public int compareTo(Siswa other) {
        if (this.poin != other.poin) { return this.poin - other.poin; }
        return other.id - this.id;
    }
}

class TreeNode {
    Siswa siswa;
    int height, size;
    TreeNode left, right;

    TreeNode(Siswa siswa) {
        this.siswa = siswa;
        this.height = 1;
        this.size = 1;
    }
}

class AVLTree implements Comparable<AVLTree> {
    TreeNode root;
    int totalPoin = 0;
    int id;
    
    // Constructor
    AVLTree(int id) { this.id = id; }

    // Method untuk menghitung jumlah siswa yang ditutor oleh siswa dengan poin tertentu
    int hitungTutee(TreeNode node, int poin) {
        if (node == null) { return 0; }
        if (node.siswa.poin > poin) { return hitungTutee(node.left, poin); } 
        return 1 + size(node.left) + hitungTutee(node.right, poin);
    }

    // Method untuk mendapatkan tinggi tree dengan root node tertentu
    int height(TreeNode node) { return (node == null) ? 0 : node.height; }

    // Method untuk mendapatkan jumlah node pada tree dengan root node tertentu
    int size(TreeNode node) { return (node == null) ? 0 : node.size; }

    // Method untuk mendapatkan balance factor dari tree dengan root node tertentu
    int getBalance(TreeNode node) { return (node == null) ? 0 : height(node.left) - height(node.right); }

    // Method untuk memasukkan siswa baru ke dalam AVLTree
    void insert(TreeNode node, Siswa siswa) {
        totalPoin += siswa.poin;
        root = insertHelper(node, siswa);
    }

    // Method helper untuk melakukan insert siswa baru ke dalam AVLTree
    TreeNode insertHelper(TreeNode node, Siswa siswa) {
        if (node == null) {
            node =  new TreeNode(siswa);
        } else if (siswa.compareTo(node.siswa) < 0) {
            node.left = insertHelper(node.left, siswa);
        } else {
            node.right = insertHelper(node.right, siswa);
        }
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        node.size = 1 + size(node.left) + size(node.right);
        node = balanced(node);
        return node;
    }

    // Method untuk mencari siswa dengan rank terbaik pada kelas
    TreeNode findMax(TreeNode node) {
        TreeNode current = node;
        while (current.right != null) { current = current.right; }
        return current;
    }

    // Method untuk mencari siswa dengan rank terburuk pada kelas
    TreeNode findMin(TreeNode node) {
        TreeNode current = node;
        while (current.left != null) { current = current.left; }
        return current;
    }

    // Method untuk menghapus siswa dari AVLTree
    void delete(TreeNode node, Siswa siswa) {
        if (find(root, siswa) != null) { totalPoin -= siswa.poin; }
        root = deleteHelper(node, siswa);
    }

    // Method helper untuk melakukan delete siswa dari AVLTree
    // Sumber: https://www.geeksforgeeks.org/deletion-in-an-avl-tree/ (dengan modifikasi)
    TreeNode deleteHelper(TreeNode node, Siswa siswa) {
        // Jika node null, maka tidak ada yang dihapus
        if (node == null) { return node; }
        // Jika rank siswa yang ingin dihapus lebih rendah dari rank node saat ini, maka siswa ada di subtree kiri
        if (siswa.compareTo(node.siswa) < 0) { node.left = deleteHelper(node.left, siswa); }
        // Jika rank siswa yang ingin dihapus lebih tinggi dari rank node saat ini, maka siswa ada di subtree kanan
        else if (siswa.compareTo(node.siswa) > 0) { node.right = deleteHelper(node.right, siswa); }
        // Jika rank siswa yang ingin dihapus ada pada node saat ini, maka node ini yang akan dihapus
        else {
            // Jika node memiliki satu atau tidak ada child
            if ((node.left == null) || (node.right == null)) {
                TreeNode temp = null;
                if (temp == node.left) { temp = node.right; } 
                else { temp = node.left; }
                node = temp;
            } 
            // Jika node memiliki dua child, gunakan inorder predecessor untuk menggantikan node saat ini
            else {
                TreeNode temp = findMax(node.left);
                node.siswa = temp.siswa;
                node.left = deleteHelper(node.left, temp.siswa);
            }
        }
        // Jika node null, artinya node yang dihapus adalah leaf node
        if (node == null) { return node; }
        // Update height dan size
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        node.size = 1 + size(node.left) + size(node.right);
        // Lakukan balancing
        return balanced(node);
    }

    // Method untuk melakukan balancing pada AVLTree
    // sumber: https://www.geeksforgeeks.org/deletion-in-an-avl-tree/ (dengan modifikasi)
    TreeNode balanced(TreeNode node) {
        int balance = getBalance(node); 
        // Left Left
        if (balance == 2 && getBalance(node.left) == 1) { return singleRightRotate(node); }
        // Right Right 
        if (balance == -2 && getBalance(node.right) == -1) { return singleLeftRotate(node); }
        // Left Right 
        if (balance == 2 && getBalance(node.left) == -1) { 
            node.left = singleLeftRotate(node.left); 
            return singleRightRotate(node); 
        } 
        // Right Left 
        if (balance == -2 && getBalance(node.right) == 1) { 
            node.right = singleRightRotate(node.right); 
            return singleLeftRotate(node); 
        }
        return node; 
    }

    // Method untuk melakukan single left rotate pada AVLTree
    TreeNode singleLeftRotate(TreeNode node) {
        TreeNode node2 = node.right;
        node.right = node2.left;
        node2.left = node;
        // Update height
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        node2.height = Math.max(height(node2.left), height(node2.right)) + 1;
        // Update size
        node.size = 1 + size(node.left) + size(node.right);
        node2.size = 1 + size(node2.left) + size(node2.right);
        return node2;
    }

    // Method untuk melakukan single right rotate pada AVLTree
    TreeNode singleRightRotate(TreeNode node) {
        TreeNode node2 = node.left;
        node.left = node2.right;
        node2.right = node;
        // Update height
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        node2.height = Math.max(height(node2.left), height(node2.right)) + 1;
        // Update size
        node.size = 1 + size(node.left) + size(node.right);
        node2.size = 1 + size(node2.left) + size(node2.right);
        return node2;
    }

    // Method untuk mencari siswa pada AVLTree
    TreeNode find(TreeNode node, Siswa siswa) {
        if (node == null) { return node; }
        if (siswa.compareTo(node.siswa) < 0) { return find(node.left, siswa); } 
        if (siswa.compareTo(node.siswa) > 0) { return find(node.right, siswa); }
        return node;
    }

    @Override
    public int compareTo(AVLTree other) {
        double thisAvg = (double) this.totalPoin / this.root.size;
        double otherAvg = (double) other.totalPoin / other.root.size;
        if (thisAvg != otherAvg) { return thisAvg > otherAvg ? -1 : 1; }
        return this.id - other.id;
    }
}

class ListNode {
    AVLTree kelas;
    ListNode next;
    ListNode prev;
    
    ListNode(AVLTree kelas) { this.kelas = kelas; }
}

class DoublyLinkedList {
    private int nodeIdCounter = 1;
    ListNode first;
    ListNode current;
    ListNode last;
    int size = 0;

    // Method untuk menambahkan ListNode ke akhir DoublyLinkedList
    public ListNode add(AVLTree kelas) {
        if (kelas.id == -1) { kelas.id = nodeIdCounter++; }
        ListNode newNode = new ListNode(kelas);
        if (size == 0) {
            newNode.next = newNode;
            newNode.prev = newNode;
            first = newNode;
            current = newNode;
            last = newNode;
        } else {
            newNode.prev = last;
            newNode.next = first;
            last.next = newNode;
            first.prev = newNode;
            last = newNode;
        }
        size++;
        return newNode;
    }

    // Method untuk berpindah ke kiri atau kanan dari current ListNode
    public ListNode move(char direction) {
        if (direction == 'L') { current = current.prev; } 
        else { current = current.next; }
        return current;
    }

    // Method untuk menghapus semua ListNode dari DoublyLinkedList
    public void clear() { first = null; current = null; last = null; size = 0; }

    // Method untuk melakukan merge pada algoritma merge sort
    public void merge(AVLTree arr[], int left, int mid, int right) {
        // Membuat dua array untuk menyimpan AVLTree dari left ke mid dan mid+1 ke right
        int size1 = mid - left + 1;
        int size2 = right - mid;
        AVLTree[] leftArr = new AVLTree[size1];
        AVLTree[] rightArr = new AVLTree[size2];
        // Menyalin AVLTree pada arr ke leftArr dan rightArr
        for (int i = 0; i < size1; i++) { leftArr[i] = arr[left + i]; }
        for (int i = 0; i < size2; i++) { rightArr[i] = arr[mid + 1 + i]; }
        // inisialisasi index untuk leftArr, rightArr, dan arr
        int i = 0, j = 0, k = left;
        // Melakukan merge pada leftArr dan rightArr
        while (i < size1 && j < size2) {
            if (leftArr[i].compareTo(rightArr[j]) < 0) { arr[k] = leftArr[i]; i++; } 
            else { arr[k] = rightArr[j]; j++; }
            k++;
        }
        // Menyalin sisa elemen pada leftArr kemudian rightArr ke arr
        while (i < size1) { arr[k] = leftArr[i]; i++; k++; }
        while (j < size2) { arr[k] = rightArr[j]; j++; k++; }
    }

    // Method untuk melakukan merge sort
    public void mergeSort(AVLTree arr[], int left, int right) {
        if (left < right) {
            // Mencari nilai tengah
            int mid = left + (right - left) / 2;
            // Melakukan merge sort pada setengah kiri dan setengah kanan
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }
    
    // Method untuk melakukan sorting pada DoublyLinkedList dengan menyalin semua AVLTree ke array
    public AVLTree[] sort() {
        // Membuat array untuk menyimpan semua AVLTree
        AVLTree[] arr = new AVLTree[size];
        // Menyalin semua AVLTree ke array
        ListNode node = first;
        for (int i = 0; i < size; i++) {
            arr[i] = node.kelas;
            node = node.next;
        }
        // Melakukan merge sort pada array dan mengembalikan sorted array
        mergeSort(arr, 0, size - 1);
        return arr;
    }

    // sumber untuk query K: https://github.com/eugeniusms/SDA-2022 (dengan modifikasi)
}