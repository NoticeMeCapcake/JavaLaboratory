import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class A4 {
    static Node head = new Node(-1, -1);
    static Node tail = new Node(-1, -1);
    static int capacity = 0;
    static Map<Integer, Node> map = new HashMap<>();

    public static void main(String[] args) throws IOException {
        setup();
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            var settings = Arrays.stream(reader.readLine().split(" "))
                    .mapToInt(Integer::parseInt).toArray();

            capacity = settings[1];

            for (int i = 0; i < settings[0]; i++) {
                var operation = Arrays.stream(reader.readLine().split(" "))
                        .mapToInt(Integer::parseInt).toArray();

                switch (operation[0]) {
                    case 2 -> addPair(operation[1], operation[2]);
                    case 1 -> System.out.println(get(operation[1]));
                }
            }
        }
    }

    private static void setup() {
        head.right = tail;
        tail.left = head;
    }

    private static int get(int key) {
        if (!map.containsKey(key)) {
            return -1;
        }

        var retrievedNode = map.get(key);
        removeNode(retrievedNode);
        insertNode(retrievedNode);
        return retrievedNode.value;
    }

    private static void removeNode(Node node) {
        node.left.right = node.right;
        node.right.left = node.left;
    }

    private static void addPair(int key, int value) {
        Node newNode = new Node(key, value);
        if (map.containsKey(key)) {
            removeNode(map.get(key));
        }
        map.put(key, newNode);

        if (map.size() > capacity) {
            removeNode(map.remove(tail.left.key));
        }

        insertNode(newNode);
    }

    private static void insertNode(Node newNode) {
        newNode.right = head.right;
        head.right.left = newNode;
        newNode.left = head;
        head.right = newNode;
    }

    private static class Node {
        int key;
        int value;
        Node left;
        Node right;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}
