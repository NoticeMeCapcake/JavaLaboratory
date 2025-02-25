import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;

public class B1 {
    public static void main(String[] args) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));

        var N = Integer.parseInt(reader.readLine());
        var a = readIntArray(reader);

        var M = Integer.parseInt(reader.readLine());;
        var b = readIntArray(reader);

        var dp = new int[N + 1][M + 1];

        for (var i = 1; i <= N; i++) {
            for (var j = 1; j <= M; j++) {
                if (a[i - 1] == b[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        var resultSubSeq = new LinkedList<Integer>();
        int i = N, j = M;
        while (i > 0 && j > 0) {
            if (a[i - 1] == b[j - 1]) {
                resultSubSeq.addFirst(a[i - 1]);
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        var resultSB = new StringBuilder();
        for (var num : resultSubSeq) {
            resultSB.append(num)
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