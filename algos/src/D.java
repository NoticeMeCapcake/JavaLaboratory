import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class D {
    public static void main(String[] args)  throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            var count = Integer.parseInt(reader.readLine());
            var tree = new int[count];
            // 4 2 1 3 6 5 7 -> 4 2 6 1 3 5 7 | 4 2 _ 1 3 _ _
            // 5 3 2 6 5 7-> 5 3 6 2 _ 5 7
            for (int i = 0; i < count; i++) {

            }
        }
    }
}
