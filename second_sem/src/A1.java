import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class A1 {
    public static void main(String[] args) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));

        var N = Integer.parseInt(reader.readLine());
        var candies = Arrays.stream(reader.readLine().split(" "))
                .mapToInt(Integer::parseInt)
                .toArray();

        Arrays.sort(candies);

        var dp = new ArrayList<ArrayList<Integer>>();
        var maxSubset = new ArrayList<Integer>();

        for (var i = 0; i < N; i++) {
            dp.add(new ArrayList<>());
            dp.get(i).add(candies[i]);
            for (var j = i - 1; j >= 0; j--) {
                if (candies[i] % candies[j] == 0 && dp.get(j).size() + 1 > dp.get(i).size()) {
                    dp.set(i, new ArrayList<>());
                    dp.get(i).add(candies[i]);
                    dp.get(i).addAll(dp.get(j));
                }
            }
            if (dp.get(i).size() > maxSubset.size()) {
                maxSubset = dp.get(i);
            }
        }

        System.out.println(maxSubset.size());
        var resultStringBuilder = new StringBuilder();
        for (var candy : maxSubset) {
           resultStringBuilder.append(candy).append(" ");
        }
        System.out.println(resultStringBuilder);
    }
}