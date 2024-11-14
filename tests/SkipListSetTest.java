import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Comparator;
import java.util.TreeSet;

import static org.junit.Assert.*;

@SuppressWarnings("ALL")
public class SkipListSetTest {
    SkipListSet<Integer> set;

    void setUp() {
        set = new SkipListSet<>();
        fillTheSetUp();
    }

    void setUpComparator() {
        set = new SkipListSet<>();
        fillTheSetUp();
    }

    void fillTheSetUp() {
        set.add(12);
        set.add(14);
        set.add(16);
        set.add(11);
        set.add(7);
        set.add(9);
        System.out.println(set);
    }

    @Test
    public void testPositive_add() {
        var set = new SkipListSet<Integer>(Comparator.comparing(x -> x, Integer::compare));
        System.out.println(set);
        assertTrue(set.add(5));
        assertEquals(1, set.size());
        System.out.println(set);
        assertTrue(set.add(6));
        assertEquals(2, set.size());
        assertTrue(set.add(7));
        assertEquals(3, set.size());
        System.out.println(set);
        assertTrue(set.add(3));
        assertEquals(4, set.size());
        System.out.println(set);
        assertFalse(set.add(5));
        assertEquals(4, set.size());
        System.out.println(set);
    }

    @Test
    public void testComparatorPositive_add() {
        var set = new SkipListSet<Integer>(Comparator.comparing(x -> x, Integer::compare));
        System.out.println(set);
        assertTrue(set.add(5));
        assertEquals(1, set.size());
        System.out.println(set);
        assertTrue(set.add(6));
        assertEquals(2, set.size());
        assertTrue(set.add(7));
        assertEquals(3, set.size());
        System.out.println(set);
        assertTrue(set.add(3));
        assertEquals(4, set.size());
        System.out.println(set);
        assertFalse(set.add(5));
        assertEquals(4, set.size());
        System.out.println(set);
    }

    @Test
    public void testPositive_contains() {
        setUp();
        assertTrue(set.contains(7));
        assertTrue(set.contains(9));
        assertTrue(set.contains(12));
        assertFalse(set.contains(15));
        assertFalse(set.contains(10));
        assertFalse(set.contains(3));
    }

    @Test
    public void testComparatorPositive_contains() {
        setUpComparator();
        assertTrue(set.contains(7));
        assertTrue(set.contains(9));
        assertTrue(set.contains(12));
        assertFalse(set.contains(15));
        assertFalse(set.contains(10));
        assertFalse(set.contains(3));
    }

    @Test
    public void testPositive_remove() {
        setUp();
        assertTrue(set.remove(7));
        assertEquals(5, set.size());
        System.out.println(set);
        assertTrue(set.remove(9));
        assertEquals(4, set.size());
        System.out.println(set);
        assertTrue(set.remove(12));
        assertEquals(3, set.size());
        assertFalse(set.remove(15));
        assertEquals(3, set.size());
        assertFalse(set.remove(10));
        assertEquals(3, set.size());
        assertFalse(set.remove(3));
        assertEquals(3, set.size());
        System.out.println(set);
        assertTrue(set.remove(16));
        assertEquals(2, set.size());
        System.out.println(set);
    }

    @Test
    public void testComparatorPositive_remove() {
        setUpComparator();
        assertTrue(set.remove(7));
        assertEquals(5, set.size());
        System.out.println(set);
        assertTrue(set.remove(9));
        assertEquals(4, set.size());
        System.out.println(set);
        assertTrue(set.remove(12));
        assertEquals(3, set.size());
        assertFalse(set.remove(15));
        assertEquals(3, set.size());
        assertFalse(set.remove(10));
        assertEquals(3, set.size());
        assertFalse(set.remove(3));
        assertEquals(3, set.size());
        System.out.println(set);
        assertTrue(set.remove(16));
        assertEquals(2, set.size());
        System.out.println(set);
    }

    @Test
    public void testIterator() {
        setUp();
        for (var elem : set) {
            System.out.println(elem);
        }
        var iter = set.iterator();
        iter.next();
        iter.next();
    }

    @Test
    public void playground() {
        setUp();
        assertEquals(Integer.valueOf(7), set.first());
        assertEquals(Integer.valueOf(16), set.last());
        assertEquals(Integer.valueOf(7), set.pollFirst());
        System.out.println(set);
        assertEquals(Integer.valueOf(16), set.pollLast());
        System.out.println(set);
    }

    @Test
    public void playground2() {
        var set = new TreeSet<Integer>();
        set.add(1);
        set.add(2);
        set.add(3);
//        set.add();
        set.add(5);
        set.add(6);
        var subset = set.subSet(2, 6);
        System.out.println(subset);
        subset.add(4);
        System.out.println(subset);
        subset.remove(4);
        System.out.println(subset);
        subset.remove(1);
        System.out.println(subset);
        System.out.println(set);

    }
    @Test
    public void playground3() {
        var set = new TreeSet<Integer>();
        set.add(1);
        set.add(2);
        set.add(3);
//        set.add();
        set.add(5);
        set.add(6);
        System.out.println(set);
        var subset = set.descendingSet();
        System.out.println(subset);
        subset.add(4);
        System.out.println(subset);
        System.out.println(set);
        subset.remove(4);
        System.out.println(subset);
        System.out.println(set);
        var subSubSet = subset.subSet(6, 2);
        System.out.println(subSubSet);
        subSubSet.remove(5);
        System.out.println(subSubSet);
        System.out.println(subset);
        System.out.println(set);
//        System.out.println(subset);
//        subset.remove(4);
//        System.out.println(subset);
//        subset.remove(1);
//        System.out.println(subset);
//        System.out.println(set);

    }
}
