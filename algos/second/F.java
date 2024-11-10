package second;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class F {
    public static void radixSort(long[] arr) {
        var maxElem = getMax(arr);

        ArrayList<Long>[] buckets = new ArrayList[10];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new ArrayList<>();
        }
        long divisor = 1L;
        long mod = 10L;

        while (maxElem / divisor > 0) {
            for (var elem : arr) {
                buckets[(int) (Long.divideUnsigned(elem, divisor) % mod)].add(elem);
            }

            divisor *= 10L;

            int index = 0;
            for (var bucket : buckets) {
                for (var elem : bucket) {
                    arr[index++] = elem;
                }
                bucket.clear();
            }
        }
    }

    private static long getMax(long[] arr) {
        long max = arr[0];
        for (var elem : arr) {
            if (Long.compareUnsigned(elem, max) > 0) {
                max = elem;
            }
        }
        return max;
    }

    public static void main(String[] args) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            var numbersCount = Integer.parseInt(reader.readLine());

            long[] inputArray = new long[numbersCount];
            for (int i = 0; i < numbersCount; i++) {
                inputArray[i] = Long.parseUnsignedLong(reader.readLine());
            }

            radixSort(inputArray);

            for (long num : inputArray) {
                System.out.println(num);
            }
        }
    }
}
