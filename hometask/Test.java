public class Test {
    public static void main(String[] args) {
        var set = new SkipListSet<Integer>();
        System.out.println(set);
        set.add(5);
        System.out.println(set);
        set.add(6);
        set.add(7);
        System.out.println(set);
        set.add(3);
        System.out.println(set);
        set.add(5);
        System.out.println(set);
    }
}
