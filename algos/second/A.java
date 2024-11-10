package second;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class A {
    public static void main(String[] args) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            var lineCount = Integer.parseInt(reader.readLine());
            var listOfFractionsToSort = new ArrayList<Fraction>(lineCount);

            for (int i = 0; i < lineCount; i++) {
                var numbers = reader.readLine().split(" ");
                listOfFractionsToSort.add(new Fraction(
                        Long.parseLong(numbers[0]),
                        Long.parseLong(numbers[1])
                ));
            }

            listOfFractionsToSort.sort(Fraction::compareTo);

            for (Fraction fraction : listOfFractionsToSort) {
                System.out.println(fraction.p + " " + fraction.q);
            }
        }
    }

    public static class Fraction implements Comparable<Fraction> {
        long p, q;
        Fraction(long p, long q) {
            var gcd = gcd(Math.abs(p), q);
            this.p = p / gcd;
            this.q = q / gcd;
        }

        @Override
        public int compareTo(Fraction o) {
            var left = this.p * o.q;
            var right = o.p * this.q;
            return Long.compare(left, right);
        }
    }

    public static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }
}
