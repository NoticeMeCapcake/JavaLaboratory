import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

public class B4 {
    public static void main(String[] args) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            var elemCount = Integer.parseInt(reader.readLine());
            var controlSum = Integer.parseInt(reader.readLine());

            var inputArray = Arrays.stream(reader.readLine().split(" "))
                    .mapToInt(Integer::parseInt).toArray();


            var sumToCount = new HashMap<Long, Long>();
            sumToCount.put(0L, 1L);
            var currentSum = 0L;
            var subArraysCount = 0L;
            for (int i = 0; i < elemCount; i++) {
                currentSum += inputArray[i];
                if (sumToCount.containsKey(currentSum - controlSum)) {
                    subArraysCount += sumToCount.get(currentSum - controlSum);
                }

                sumToCount.put(currentSum, sumToCount.getOrDefault(currentSum, 0L) + 1L);
            }

            System.out.println(subArraysCount);
        }
    }
}