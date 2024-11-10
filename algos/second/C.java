package second;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class C {
    public static void main(String[] args) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            var lineCount = Integer.parseInt(reader.readLine());
            var inputSegments = new ArrayList<int[]>();
            for (int i = 0; i < lineCount; i++) {
                inputSegments.add(Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray());
            }

            inputSegments.sort(Comparator.comparingInt(t -> t[0]));

            var outputSegments = new ArrayList<int[]>();
            var unionSegment = inputSegments.getFirst();
            for (int i = 1; i < inputSegments.size(); i++) {
                var currentSegment = inputSegments.get(i);
                if (currentSegment[0] <= unionSegment[1]) {
                    unionSegment[1] = Math.max(unionSegment[1], currentSegment[1]);
                }
                else {
                    outputSegments.add(unionSegment);
                    unionSegment = currentSegment;
                }
            }
            outputSegments.add(unionSegment);

            System.out.println(outputSegments.size());
            for (int[] segment : outputSegments) {
                System.out.println(segment[0] + " " + segment[1]);
            }
        }
    }
}
