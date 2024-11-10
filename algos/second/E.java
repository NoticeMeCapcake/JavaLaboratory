package second;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.PriorityQueue;

public class E {
    public static void main(String[] args) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            var arraysCount = Integer.parseInt(reader.readLine());
            var resultingArray = new PriorityQueue<Integer>();
            for (int i = 0; i < arraysCount; i++) {
                reader.readLine();
                Arrays.stream(reader.readLine().split(" "))
                        .mapToInt(Integer::parseInt).forEach(resultingArray::add);

            }
            var resultingString = new StringBuilder();
            var space = " ";
            var count = resultingArray.size();
            for (int i = 0; i < count; i++) {
                resultingString.append(resultingArray.poll()).append(space);
            }
            System.out.println(resultingString);
        }
    }
}
