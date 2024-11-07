import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.function.BiFunction;

public class SkipListSet<E>
        extends AbstractSet<E>
        implements NavigableSet<E> {
    private static final int MAX_DEPTH = 32;
    private static final float PROBABILITY = 0.25f;
    private Entry<E> head;
    private final Comparator<? super E> comparator;
    private final BiFunction<? super E, ? super E, Integer> compareComparable;
    private final BiFunction<? super E, ? super E, Integer> compareByComparator;
    private int size;

    // TODO: подумать, как считать размер (случай с равенством) и как удалять верхние уровни

    // NOTE: even ArrayList implementation has this warning suppression
    @SuppressWarnings("unchecked")
    public SkipListSet() {
        comparator = null;
        compareComparable = (o, o2) -> ((Comparable<E>) o).compareTo(o2);
        compareByComparator = null;
        size = 0;
    }

    public SkipListSet(Comparator<? super E> comparator) {
        this.comparator = comparator;
        compareByComparator = comparator::compare;
        compareComparable = null;
        size = 0;
    }

    public SkipListSet(Collection<E> c) {
        this();
        putAll(c);
//        size = c.size();
    }

    private void putAll(Collection<E> c) {
        for (E e : c) {
            put(e);
        }
    }

    private void put(E e) {
        if (head == null) {
            head = Entry.<E>builder()
                    .value(e)
                    .height(1)
                    .build();
        }
        else {
            if (comparator == null) {
                putComparable(e);
            }
            else {
                putWithComparator(e);
            }
        }
    }

    private void putComparable(E e) {
        if (! (e instanceof Comparable)) {
            throw new IllegalArgumentException("E must implement Comparable");
        }
        put(e, compareComparable);
    }

    private void putWithComparator(E e) {
        put(e, compareByComparator);
    }

    private void put(E e, BiFunction<? super E, ? super E, Integer> cmp) {
        if (cmp.apply(e, head.value) < 0) {
            var pathToLowestLevel = new ArrayList<Entry<E>>();
            var currentLevelHead = head;
            do {
                pathToLowestLevel.add(currentLevelHead);
                currentLevelHead = currentLevelHead.down;
            } while (currentLevelHead != null);

            Entry<E> previous = null;
            for (int i = pathToLowestLevel.size() - 1; i >= 0; i--) {
                var newHead = Entry.<E>builder()
                        .height(pathToLowestLevel.get(i).height)
                        .value(e)
                        .next(pathToLowestLevel.get(i))
                        .down(previous)
                        .build();
                previous = newHead;
                head = newHead;
            }
            size++;
            return;
        }

        // construct path to lower level and find place for the new entry
        var pathToLowestLevel = buildPathToLowestLevel(e, cmp);

        if (pathToLowestLevel.isEmpty()) {
            return;
        }

        var previousEntry = pathToLowestLevel.getLast();

        var pushedEntry = Entry.<E>builder()
                .height(previousEntry.height) // 1 in this context
                .value(e)
                .next(previousEntry.next)
                .down(null)
                .build();
        previousEntry.next = pushedEntry;

        // push entry up by flipping the dice
        int i = pathToLowestLevel.size() - 2;
        while (Math.random() < PROBABILITY && head.height < MAX_DEPTH) {
            if (i < 0) {
                pushedEntry = pushEntryIntoUpperLevel(pushedEntry, null);
            }
            else {
                pushedEntry = pushEntryIntoUpperLevel(pushedEntry, pathToLowestLevel.get(i--));
            }
        }
        size++;
    }

    //TODO: подумать, как обработать случай, если e < head или e == head
    /**
     * Returns empty List if found entry with the same value
     */
    //TODO: надо делать полный путь, даже, если мы попали в таргет, тогда можно сравнить последний элемент пути и определить,
    // что мы действительно попали в таргет.
    private List<Entry<E>> buildPathToLowestLevel(E e, BiFunction<? super E, ? super E, Integer> cmp) {
        var preTest = cmp.apply(e, head.value);
        if (preTest < 0) {
            return null;
        }
        else if (preTest == 0) {
            return List.of();
        }
        var previousEntry = head;
        var entryToCompare = head.next;
        var pathToLowestLevel = new ArrayList<Entry<E>>();
        while (previousEntry.height != 1) {
            int compareResult;
            while (entryToCompare != null && (compareResult = cmp.apply(e, entryToCompare.value)) >= 0) {
                if (compareResult == 0) {
                    return List.of();
                }
                var tmp = entryToCompare;
                entryToCompare = entryToCompare.next;
                previousEntry = tmp;
            }
            pathToLowestLevel.add(previousEntry);
            previousEntry = previousEntry.down;
            entryToCompare = previousEntry.next;
        }
        return pathToLowestLevel;
    }

    // if entry is not head ->
    // if prev is null -> new lvl
    // else upper lvl exists -> prev.next = newEntry
    private Entry<E> pushEntryIntoUpperLevel(Entry<E> entry, Entry<E> previous) {
        var newEntry = Entry.<E>builder()
                .height(entry.height + 1)
                .value(entry.value)
                .next(null)
                .down(entry)
                .build();
        if (previous == null) {
            head = Entry.<E>builder()
                    .height(newEntry.height)
                    .value(head.value)
                    .next(newEntry)
                    .down(head)
                    .build();
        }
        else {
            newEntry.next = previous.next;
            previous.next = newEntry;
        }
        return newEntry;
    }

    private List<Entry<E>> getPathToLowestLevelSimple(E value) {
        var pathToLowestLevel = List.<Entry<E>>of();
        if (comparator != null) {
            pathToLowestLevel = buildPathToLowestLevel(value, compareByComparator);
        }
        else {
            if (!(value instanceof Comparable)) {
                throw new IllegalArgumentException("Object must be comparable");
            }
            pathToLowestLevel = buildPathToLowestLevel(value, compareComparable);
        }
        return pathToLowestLevel;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        var pathToLowestLevel = getPathToLowestLevelSimple((E) o);
        return pathToLowestLevel != null && pathToLowestLevel.isEmpty();
    }

    @Override
    public boolean add(E e) {
        var sizeSnapshot = size;
        put(e);
        return sizeSnapshot != size;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public E lower(E e) {
        var pathToLowestLevel = getPathToLowestLevelSimple(e);
        if (pathToLowestLevel == null) {
            return null;
        }
        return pathToLowestLevel.getLast().value;
    }

    @Override
    public E floor(E e) {
        return null;
    }

    @Override
    public E ceiling(E e) {
        return null;
    }

    @Override
    public E higher(E e) {
        return null;
    }

    @Override
    public E pollFirst() {
        return null;
    }

    @Override
    public E pollLast() {
        return null;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return null;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return null;
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return null;
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return null;
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return null;
    }

    @Override
    public Comparator<? super E> comparator() {
        return null;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return null;
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return null;
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return null;
    }

    @Override
    public E first() {
        return null;
    }

    @Override
    public E last() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    private static class Entry<E> {
        private final E value;
        private Entry<E> next;
        private Entry<E> down;
        private final int height;

        public Entry(E value, Entry<E> next, Entry<E> down, int height) {
            this.value = value;
            this.next = next;
            this.down = down;
            this.height = height;
        }

        public void setNext(Entry<E> next) {
            this.next = next;
        }

        public void setDown(Entry<E> down) {
            this.down = down;
        }

        public static <V> Builder<V> builder() {
            return new Builder<>();
        }

        public E getValue() {
            return value;
        }

        public Entry<E> getNext() {
            return next;
        }

        public Entry<E> getDown() {
            return down;
        }

        public int getHeight() {
            return height;
        }

        private static class Builder<V> {
            private V value;
            private Entry<V> next;
            private Entry<V> down;
            private int height;

            public Builder<V> value(V value) {
                this.value = value;
                return this;
            }

            public Builder<V> next(Entry<V> next) {
                this.next = next;
                return this;
            }

            public Builder<V> down(Entry<V> down) {
                this.down = down;
                return this;
            }

            public Builder<V> height(int height) {
                this.height = height;
                return this;
            }

            public Entry<V> build() {
                return new Entry<>(value, next, down, height);
            }
        }
    }
}
