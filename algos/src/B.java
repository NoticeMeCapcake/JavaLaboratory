import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TreeSet;

public class B {
    public static void main(String[] args) throws IOException {
        var max = 1000000000;
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            var count = Integer.parseInt(reader.readLine());

            var previousOperation = '+';
            Integer previousValue = 0;
            var container = new TreeSet<Integer>();
            var sb = new StringBuilder();

            for (int i = 0; i < count; i++) {
                var data = reader.readLine();
                var operation = data.charAt(0);
                var value = Integer.parseInt(data.substring(2));
                if (operation == '?') {
                    previousValue = container.ceiling(value);
                    if (previousValue == null)
                        previousValue = -1;
                    sb.append(previousValue).append(System.lineSeparator());
                }
                else {
                    if (operation == previousOperation) {
                        container.add(value);
                    }
                    else {
                        container.add((previousValue + value) % max);
                    }
                }
                previousOperation = operation;
            }
            System.out.println(sb);
        }
    }
}
