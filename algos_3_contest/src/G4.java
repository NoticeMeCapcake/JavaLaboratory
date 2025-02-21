import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class G4 {
    public static void main(String[] args) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            var elemCount = Integer.parseInt(reader.readLine());
            var elements = Arrays.stream(reader.readLine().split(" ")).mapToLong(Integer::parseInt).toArray();
            var questionsCount = Integer.parseInt(reader.readLine());
            var questions = Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();

            var frozenSet = new FrozenSet(elements);
            var result = new StringBuilder();

            for (var question : questions) {
                var answer = frozenSet.contains(question);
                result.append(answer ? "Yes" : "No").append(System.lineSeparator());
            }
            System.out.println(result);
        }
    }

    private static class FrozenSet {
        Bucket[] buckets;
        Hashing hashing;

        public FrozenSet (long[] elements) {
            rehash(elements);
        }

        private boolean probe(long elementsSize) {
            var bucketSizeSum = 0L;
            for (var bucket : buckets) {
                if (bucket != null) {
                    bucketSizeSum += (long) bucket.rawElements.size() * bucket.rawElements.size();
                }
            }

            return bucketSizeSum > elementsSize * 4;
        }

        private void rehash(long[] elements) {
            do {
                hashing = new Hashing(elements.length);
                buckets = new Bucket[1 << (64 - hashing.shift)];
                for (var elem : elements) {
                    add(elem);
                }
            } while (probe(elements.length));

            for (var bucket : buckets) {
                if (bucket!= null) {
                    bucket.rehash();
                }
            }
        }

        private void add(long elem) {
            var index = getIndex(elem, hashing);
            if (buckets[index] == null) {
                buckets[index] = new Bucket();
            }
            buckets[index].simpleAdd(elem);
        }

        public boolean contains(long elem) {
            var index = getIndex(elem, hashing);
            if (buckets[index] == null) {
                return false;
            }
            return buckets[index].contains(elem);
        }
    }

    private static class Bucket {
        Hashing hashing;
        List<Long> rawElements = new ArrayList<>();
        long[] elements;
        boolean[] accessed;
        int size;

        public void simpleAdd(long elem) {
            rawElements.add(elem);
        }

        public void rehash() {
            hashing = new Hashing(rawElements.size() * rawElements.size());

            size = 1 << (64 - hashing.shift);

            elements = new long[size];
            accessed = new boolean[size];
            var needRepeat = false;
            for (var elem : rawElements) {
                var index = getIndex(elem, hashing);
                if (accessed[index] && elements[index] != elem) {
                    needRepeat = true;
                    break;
                }
                accessed[index] = true;
                elements[index] = elem;
            }

            if (needRepeat) {
                rehash();
            }
        }

        public boolean contains(long elem) {
            var index = getIndex(elem, hashing);
            return accessed[index] && elements[index] == elem;
        }
    }

    private static int getIndex(long element, Hashing hashing) {
        return hashing.hash(element);
    }

    private static class Hashing {
        static Random generator = new Random(System.nanoTime());
        long a;
        long b;
        long shift;

        public Hashing(int elemCount) {
            rehash();
            shift = 1;
            while (1 << shift <= elemCount) {
                shift += 1;
            }

            shift = 64 - shift;
        }

        public void rehash() {
            do {
                a = generateParameter();
            } while (a == 0);
            b = generateParameter();
        }

        private long generateParameter() {
            return generator.nextLong(1L << 62);
        }

        public int hash(long x) {
            return (int) (((a * x + b) << 32 - 64 + shift >>> 32 - 64 + shift) >>> 32);
        }
    }
}
