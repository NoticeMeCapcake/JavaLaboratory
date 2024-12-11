import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class A {
    public static void main(String[] args) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            var dataCount = Integer.parseInt(reader.readLine());

            var leftToRight = new HashMap<String, String>(dataCount);
            var rightToLeft = new HashMap<String, String>(dataCount);
            for (int i = 0; i < dataCount; i++) {
                var data = reader.readLine().split(" ");
                leftToRight.put(data[0], data[1]);
                rightToLeft.put(data[1], data[0]);
            }

            var questionsCount = Integer.parseInt(reader.readLine());
            var result = new StringBuilder();
            for (int i = 0; i < questionsCount; i++) {
                var question = reader.readLine();
                var value = leftToRight.get(question);
                if (value != null) {
                    result.append(value);
                }
                else {
                    result.append(rightToLeft.get(question));
                }
                result.append(System.lineSeparator());
            }
            System.out.println(result);
        }
    }
}
