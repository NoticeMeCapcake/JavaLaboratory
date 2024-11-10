package second;

import java.util.ArrayList;
import java.util.Arrays;

public class lab {
    public static void main(String[] args) {
        ArrayList<Long>[] arr = new ArrayList[10];
        Arrays.fill(arr, new ArrayList<Long>());
        arr[1].add(12L);
        arr[1].add(13L);
        System.out.println(arr[1]);
    }
}
