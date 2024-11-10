package second;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.PriorityQueue;

public class D {
    public static void main(String[] args) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            reader.readLine();
            var operands = Arrays.stream(reader.readLine().split(" "))
                    .mapToInt(Integer::parseInt).collect(PriorityQueue::new, PriorityQueue<Integer>::add, PriorityQueue<Integer>::addAll);

            double commissionSum = 0;
            while (operands.size() > 1) {
                var first = operands.poll();
                var second = operands.poll();
                var sum = first + second;
                operands.add(sum);
                commissionSum += sum * 0.05d;
            }

            System.out.println(commissionSum);
        }
    }
}
