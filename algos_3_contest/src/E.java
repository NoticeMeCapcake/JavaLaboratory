import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class E {
    public static void main(String[] args) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            PriorityQueue<Integer> minHeap = new PriorityQueue<>(Comparator.<Integer>naturalOrder().reversed());
            int k = Integer.parseInt(reader.readLine().split(" ")[2]);
            var data = Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            var operations = reader.readLine();
            var l = 0;
            var r = 0;
            minHeap.add(data[0]);
            var sb = new StringBuilder();
            for (int i = 0; i < operations.length(); i++) {
                if (operations.charAt(i) == 'L') {
                    if (data[l++] <= minHeap.peek()) {
                        minHeap.clear();
                        fillTheHeap(minHeap, data, l, r, k);
                    }
                }
                else if (operations.charAt(i) == 'R') {
                    if (data[++r] <= minHeap.peek() || minHeap.size() < k) {
                        if (minHeap.size() == k) {
                            minHeap.poll();
                        }
                        minHeap.add(data[r]);
                    }
                }
                if (minHeap.size() < k) {
                    sb.append(-1);
                } else {
                    sb.append(minHeap.peek());
                }
                sb.append(System.lineSeparator());
            }
            System.out.println(sb);
        }
    }

    private static void fillTheHeap(PriorityQueue<Integer> minHeap, int[] data, int l, int r, int k) {
        for (int i = l; i <= r; i++) {
            if (!minHeap.isEmpty()) {
                if (data[i] <= minHeap.peek()) {
                    minHeap.add(data[i]);
                }
            }
            else {
                minHeap.add(data[i]);
            }
        }

        while (minHeap.size() > k) {
            minHeap.poll();
        }
    }
}
