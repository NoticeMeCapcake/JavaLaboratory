import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;

public class D1 {
    public static void main(String[] args) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));

        var backPackParameters = readIntArray(reader);
        var N = backPackParameters[0];
        var M = backPackParameters[1];

        var weights = readIntArray(reader);
        var costs = readIntArray(reader);

        var dp = new int[N + 1][M + 1];

        for (var i = 1; i <= N; i++) {
            for (var j = 1; j <= M; j++) {
                if (weights[i - 1] <= j) {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i - 1][j - weights[i - 1]] + costs[i - 1]);
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }

        var pickedItems = new LinkedList<Integer>();
        var i = N;
        var j = M;
        while (i > 0 && j > 0) {
            if (dp[i][j] != dp[i - 1][j]) {
                pickedItems.addFirst(i);
                j -= weights[i - 1];
            }
            i--;
        }

        var resultSB = new StringBuilder();
        for (var item : pickedItems) {
            resultSB.append(item)
                    .append(System.lineSeparator());
        }
        System.out.println(resultSB);
    }

    private static int[] readIntArray(BufferedReader reader) throws IOException {
        return Arrays.stream(reader.readLine().split(" "))
                .mapToInt(Integer::parseInt)
                .toArray();
    }
}