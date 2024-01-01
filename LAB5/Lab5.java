import java.io.*;
import java.util.StringTokenizer;

/**
 * Note:
 * 1. Mahasiswa tidak diperkenankan menggunakan data struktur dari library seperti ArrayList, LinkedList, dll.
 * 2. Mahasiswa diperkenankan membuat/mengubah/menambahkan class, class attribute, instance attribute, tipe data, dan method 
 *    yang sekiranya perlu untuk menyelesaikan permasalahan.
 * 3. Mahasiswa dapat menggunakan method {@code traverse()} dari class {@code DoublyLinkedList}
 *    untuk membantu melakukan print statement debugging.
 */
public class Lab5 {

  private static InputReader in;
  private static PrintWriter out;
  private static DoublyLinkedList rooms = new DoublyLinkedList();

  public static void main(String[] args) {
    InputStream inputStream = System.in;
    in = new InputReader(inputStream);
    OutputStream outputStream = System.out;
    out = new PrintWriter(outputStream);

    int N = in.nextInt();

    for (int i = 0; i < N; i++) {
      char command = in.nextChar();
      char direction;

      switch (command) {
        case 'A':
          direction = in.nextChar();
          char type = in.nextChar();
          add(type, direction);
          break;
        case 'D':
          direction = in.nextChar();
          out.println(delete(direction));
          break;
        case 'M':
          direction = in.nextChar();
          out.println(move(direction));
          break;
        case 'J':
          direction = in.nextChar();
          out.println(jump(direction));
          break;
      }
    }

    out.println(rooms.traverse());

    out.close();
  }

  public static void add(char type, char direction) {
    rooms.add(type, direction);
  }

  public static int delete(char direction) {
    ListNode deletedRoom = rooms.delete(direction);
    return deletedRoom.id;
  }

  public static int move(char direction) {
    ListNode destRoom = rooms.move(direction);
    return destRoom.id;
  }

  public static int jump(char direction) {
    if (rooms.current.element == 'S') { 
        rooms.move(direction);
        while (rooms.current.element != 'S') {
            rooms.move(direction);
        }
        return rooms.current.id;
    } else {
        return -1;
    }
  }

  // taken from https://codeforces.com/submissions/Petr
  // together with PrintWriter, these input-output (IO) is much faster than the
  // usual Scanner(System.in) and System.out
  // please use these classes to avoid your fast algorithm gets Time Limit
  // Exceeded caused by slow input-output (IO)
  private static class InputReader {

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

class DoublyLinkedList {

  private int nodeIdCounter = 1;
  ListNode first;
  ListNode current;
  ListNode last;
  int size = 0;

  /*
   * Method untuk menambahkan ListNode ke sisi kiri (prev) atau kanan (next) dari {@code current} ListNode
   */
  public ListNode add(char element, char direction) {
    if (size == 0) {
        ListNode newNode = new ListNode(element, nodeIdCounter++);
        newNode.next = newNode;
        newNode.prev = newNode;
        first = newNode;
        current = newNode;
        last = newNode;
        size++;
        return newNode;
    } else {
        if (direction == 'L') {
            ListNode newNode = new ListNode(element, nodeIdCounter++);
            newNode.next = current;
            newNode.prev = current.prev;
            current.prev.next = newNode;
            current.prev = newNode;
            size++;
            return newNode;
        } else {
            ListNode newNode = new ListNode(element, nodeIdCounter++);
            newNode.next = current.next;
            newNode.prev = current;
            current.next.prev = newNode;
            current.next = newNode;
            size++;
            return newNode;
        }
    }
  }

  /**
   * Method untuk menghapus ListNode di sisi kiri (prev) atau kanan (next) dari {@code current} ListNode
   */
  public ListNode delete(char direction) {
    if (direction == 'L') {
        ListNode deletedNode = current.prev;
        current.prev = deletedNode.prev;
        deletedNode.prev.next = current;
        size--;
        return deletedNode;
    } else if (direction == 'R') {
        ListNode deletedNode = current.next;
        current.next = deletedNode.next;
        deletedNode.next.prev = current;
        size--;
        return deletedNode;
    }
    return null;
  }

  /*
   * Method untuk berpindah ke kiri (prev) atau kanan (next) dari {@code current} ListNode
   */
  public ListNode move(char direction) {
    if (direction == 'L') {
        current = current.prev;
        return current;
    } else {
        current = current.next;
        return current;
    }
  }

  /**
   * Method untuk mengunjungi setiap ListNode pada DoublyLinkedList
   */
  public String traverse() {
    ListNode traverseNode = first;
    StringBuilder result = new StringBuilder();
    do {
      result.append(traverseNode + ((traverseNode.next != first) ? " | " : ""));
      traverseNode = traverseNode.next;
    } while (traverseNode != first);

    return result.toString();
  }
}

class ListNode {

  char element;
  ListNode next;
  ListNode prev;
  int id;

  ListNode(char element, int id) {
    this.element = element;
    this.id = id;
  }

  public String toString() {
    return String.format("(ID:%d Elem:%s)", id, element);
  }
}