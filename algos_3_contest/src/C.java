import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TreeMap;

public class C {
    public static void main(String[] args) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            var container = new TreeMap<Integer, Integer>();
            var sb = new StringBuilder();
            var count = Integer.parseInt(reader.readLine());
            for (int i = 0; i < count; i++) {
                var data = reader.readLine();
                var operation = data.substring(0, 6);
                if (operation.equals("Insert")) {
                    var value = Integer.parseInt(data.substring(7, data.length() - 1));
                    container.put(value, container.getOrDefault(value, 0) + 1);
                }
                else {
                    var value = 0;
                    if (operation.equals("GetMin")) {
                        value = container.firstKey();
                    }
                    else {
                        value = container.lastKey();
                    }

                    sb.append(value).append(System.lineSeparator());
                    var valueCount = container.get(value);
                    if (valueCount == 1) {
                        container.remove(value);
                    }
                    else {
                        container.put(value, valueCount - 1);
                    }
                }
            }
            System.out.println(sb);
        }
    }
}
