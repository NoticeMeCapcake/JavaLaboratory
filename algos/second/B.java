package second;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class B {
    public static void main(String[] args) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            var referencePoint = Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            var lineCount = Integer.parseInt(reader.readLine());
            var listOfLines = new ArrayList<int[]>();
            for (int i = 0; i < lineCount; i++) {
                var line = Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                listOfLines.add(line);
            }
            listOfLines.sort(Comparator.comparingDouble(t -> countTriangleArea(
                    referencePoint[0], referencePoint[1], t[0], t[1], t[2], t[3])));

            for (int[] line : listOfLines) {
                System.out.println(line[0] + " " + line[1] + " " + line[2] + " " + line[3]);
            }
        }
    }

    public static double countTriangleArea(int x1, int y1, int x2, int y2, int x3, int y3) {
        return Math.abs(((x3 - x1) * (y2 - y1) - (y3 - y1) * (x2 - x1)) / 2.0);
    }
}
