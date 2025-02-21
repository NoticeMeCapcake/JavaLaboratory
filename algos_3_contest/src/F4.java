import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;

public class F4 {
    public static void main(String[] args) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            var subString = reader.readLine();
            var string = reader.readLine();

            if (string.isEmpty() || subString.isEmpty()) {
                System.out.println("No");
                return;
            }

            var letterToCountForSubString = new HashMap<Character, Integer>();
            var letterCountForCurrentSubstring = new HashMap<Character, Integer>();

            for (int i = 0; i < subString.length(); i++) {
                var currentChar = subString.charAt(i);
                letterToCountForSubString.put(currentChar, letterToCountForSubString.getOrDefault(currentChar, 0) + 1);
                letterCountForCurrentSubstring.put(string.charAt(i), letterCountForCurrentSubstring.getOrDefault(string.charAt(i), 0) + 1);
            }

            var charsOfSubString = letterToCountForSubString.keySet();

            for (int i = 0; i <= string.length() - subString.length(); i++) {
                var isAnagram = true;
                for (var currentCharacter : charsOfSubString) {
                    if (!Objects.equals(letterCountForCurrentSubstring.getOrDefault(currentCharacter, 0),
                            letterToCountForSubString.get(currentCharacter))) {
                        isAnagram = false;
                        break;
                    }
                }
                if (isAnagram) {
                    System.out.println("Yes");
                    return;
                }

                if (i + subString.length() < string.length()) {
                    letterCountForCurrentSubstring.put(string.charAt(i),
                            letterCountForCurrentSubstring.get(string.charAt(i)) - 1);
                    char nextChar = string.charAt(i + subString.length());
                    letterCountForCurrentSubstring.put(nextChar,
                            letterCountForCurrentSubstring.getOrDefault(nextChar, 0) + 1);
                }
            }

            System.out.println("No");
        }
    }
}
