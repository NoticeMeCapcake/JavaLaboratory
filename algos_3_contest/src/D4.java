import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

public class D4 {
    static final char start = 'a';
    public static void main(String[] args) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            var operationsCount = Integer.parseInt(reader.readLine());
            var pokeDex = new HashSet<String>();
            var result = new StringBuilder();
            for (int i = 0; i < operationsCount; i++) {
                var operationParams = reader.readLine().split(" ");
                if (operationParams[0].equals("+")) {
                    pokeDex.add(cipherWord(operationParams[1]));
                }
                else {
                    var word = cipherWord(operationParams[1]);
                    if (pokeDex.contains(word)) {
                        result.append("YES").append(System.lineSeparator());
                    }
                    else {
                        result.append("NO").append(System.lineSeparator());
                    }
                }
            }
            System.out.println(result);
        }
    }

    private static String cipherWord(String word) {
        var cipherTable = new HashMap<Character, Character>();
        char shift = 0;
        var result = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
           if (!cipherTable.containsKey(word.charAt(i))) {
               cipherTable.put(word.charAt(i), (char) (start + shift));
               shift++;
           }
           result.append(cipherTable.get(word.charAt(i)));
        }
        return result.toString();
    }
}
