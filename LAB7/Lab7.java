import java.util.*;
import java.io.*;

public class Lab7 {

    static class Box implements Comparable<Box> {
        int id;
        long value;
        String state;

        Box(int id, long value, String state) {
            this.id = id;
            this.value = value;
            this.state = state;
        }

        @Override
        public int compareTo(Box other) {
            if (this.value == other.value) { return this.id - other.id; }
            return this.value - other.value > 0 ? -1 : 1;
        }
    }

    static class BoxContainer {
        public ArrayList<Box> heap;
        public int size;
        public HashMap<Integer, Integer> idToIndexMap;

        public BoxContainer() {
            this.heap = new ArrayList<>();
            this.idToIndexMap = new HashMap<>();
        }

        public static int getParentIndex(int i) {
            return (i - 1) / 2;
        }

        public static int getLeftChildIndex(int i) {
            return 2 * i + 1;
        }

        public static int getRightChildIndex(int i) {
            return 2 * (i + 1);
        }

        public void percolateUp(int i) {
            while (i > 0 && heap.get(i).compareTo(heap.get(getParentIndex(i))) < 0) {
                swap(i, getParentIndex(i));
                i = getParentIndex(i);
            }
        }

        public void percolateDown(int i) {
            while (i < size) {
                int leftChildIndex = getLeftChildIndex(i);
                int rightChildIndex = getRightChildIndex(i);
                int largestIndex = i;

                if (leftChildIndex < heap.size() && heap.get(leftChildIndex).compareTo(heap.get(largestIndex)) < 0) {
                    largestIndex = leftChildIndex;
                }

                if (rightChildIndex < heap.size() && heap.get(rightChildIndex).compareTo(heap.get(largestIndex)) < 0) {
                    largestIndex = rightChildIndex;
                }

                if (largestIndex == i) {
                    break;
                }

                swap(i, largestIndex);
                i = largestIndex;
            }
        }

        public void insert(Box box) {
            heap.add(box);
            idToIndexMap.put(box.id, heap.size() - 1);
            percolateUp(heap.size() - 1);
            size++;
        }

        public Box peek() {
            return heap.get(0);
        }

        public void swap(int firstIndex, int secondIndex) {
            Box firstBox = heap.get(firstIndex);
            Box secondBox = heap.get(secondIndex);
            heap.set(firstIndex, secondBox);
            heap.set(secondIndex, firstBox);
            idToIndexMap.put(firstBox.id, secondIndex);
            idToIndexMap.put(secondBox.id, firstIndex);
        }

        public void updateBox(Box box) {
            int index = idToIndexMap.get(box.id);
            heap.set(index, box);
            percolateUp(index);
            percolateDown(index);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);
    
        int N = Integer.parseInt(br.readLine());
    
        ArrayList<Box> boxes = new ArrayList<>();
        BoxContainer boxContainer = new BoxContainer();
    
        for (int i = 0; i < N; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            long value = Long.parseLong(st.nextToken());
            String state = st.nextToken();
    
            Box box = new Box(boxes.size(), value, state);
            boxes.add(box);
            boxContainer.insert(box);
        }
    
        int T = Integer.parseInt(br.readLine());
    
        for (int i = 0; i < T; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            String command = st.nextToken();
    
            if ("A".equals(command)) {
                long V = Long.parseLong(st.nextToken());
                String S = st.nextToken();

                Box box = new Box(boxes.size(), V, S);
                boxes.add(box);
                boxContainer.insert(box);
            } else if ("D".equals(command)) {
                int I = Integer.parseInt(st.nextToken());
                int J = Integer.parseInt(st.nextToken());

                Box boxI = boxes.get(I);
                Box boxJ = boxes.get(J);

                if (!boxI.state.equals(boxJ.state)) {
                    if ((boxI.state.equals("ROCK") && boxJ.state.equals("SCISSOR")) ||
                        (boxI.state.equals("PAPER") && boxJ.state.equals("ROCK")) ||
                        (boxI.state.equals("SCISSOR") && boxJ.state.equals("PAPER"))) {
                        boxI.value += boxJ.value;
                        boxJ.value /= 2;
                    } else {
                        boxJ.value += boxI.value;
                        boxI.value /= 2;
                    }
            
                    boxContainer.updateBox(boxI);
                    boxContainer.updateBox(boxJ);
                }
            } else if ("N".equals(command)) {
                int I = Integer.parseInt(st.nextToken());

                if (I > 0) {
                    Box boxI = boxes.get(I);
                    Box boxPrev = boxes.get(I - 1);
            
                    if (!boxI.state.equals(boxPrev.state)) {
                        if ((boxI.state.equals("ROCK") && boxPrev.state.equals("SCISSOR")) ||
                            (boxI.state.equals("PAPER") && boxPrev.state.equals("ROCK")) ||
                            (boxI.state.equals("SCISSOR") && boxPrev.state.equals("PAPER"))) {
                            boxI.value += boxPrev.value;
                            boxContainer.updateBox(boxI);
                        }
                    }
                }
            
                if (I < boxes.size() - 1) {
                    Box boxI = boxes.get(I);
                    Box boxNext = boxes.get(I + 1);
            
                    if (!boxI.state.equals(boxNext.state)) {
                        if ((boxI.state.equals("ROCK") && boxNext.state.equals("SCISSOR")) ||
                            (boxI.state.equals("PAPER") && boxNext.state.equals("ROCK")) ||
                            (boxI.state.equals("SCISSOR") && boxNext.state.equals("PAPER"))) {
                            boxI.value += boxNext.value;
                            boxContainer.updateBox(boxI);
                        }
                    }
                }
            }

            Box topBox = boxContainer.peek();
            pw.println(topBox.value + " " + topBox.state);
            // pw.println(topBox.id);
        }
    
        pw.flush();
        pw.close();
    }   
}