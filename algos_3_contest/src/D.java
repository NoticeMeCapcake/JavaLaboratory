import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class D {
    public static void main(String[] args)  throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            reader.readLine();

            var preorder = Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            var inorder = new StringBuilder();
            var postorder = new StringBuilder();
            fillOrders(preorder, inorder, postorder, 0, preorder.length - 1);
            System.out.println(postorder);
            System.out.println(inorder);
        }
    }

    public static int findSplitIndex(int[] preorder, int start, int end) {
        var root = preorder[start];
        var splitIndex = start;
        for (int i = start + 1; i <= end; i++) {
            if (preorder[i] >= root) {
                break;
            }
            splitIndex = i;
        }
        return splitIndex;
    }

    private static void fillOrders(int[] preorder, StringBuilder inorder, StringBuilder postorder, int start, int end) {
        if (start > end) return;

        int splitIndex = findSplitIndex(preorder, start, end);

        if (splitIndex > start) {
            fillOrders(preorder, inorder, postorder, start + 1, splitIndex);
        }
        inorder.append(preorder[start]).append(' ');
        if (splitIndex < end) {
            fillOrders(preorder, inorder, postorder, splitIndex + 1, end);
        }
        postorder.append(preorder[start]).append(' ');
    }
}
