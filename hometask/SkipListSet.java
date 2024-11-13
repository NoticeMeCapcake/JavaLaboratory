import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class SkipListSet<E>
        extends AbstractSet<E>
        implements NavigableSet<E> {
    private static final int MAX_DEPTH = 32;
    private static final float PROBABILITY = 0.25f;
    private Entry<E> head = Entry.<E>builder().height(1).build();
    private final Comparator<? super E> cmp;
    private int size;

    // TODO: подумать, как считать размер (случай с равенством) и как удалять верхние уровни

    // NOTE: even ArrayList implementation has this warning suppression
    @SuppressWarnings("unchecked")
    public SkipListSet() {
        cmp = null;
        size = 0;
    }

    public SkipListSet(Comparator<? super E> comparator) {
        this.cmp = comparator;
        size = 0;
    }

    public SkipListSet(Collection<E> c) {
        this();
        putAll(c);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static int cpr(Comparator c, Object x, Object y) {
        return (c != null) ? c.compare(x, y) : ((Comparable)x).compareTo(y);
    }

    private void putAll(Collection<E> c) {
        for (E e : c) {
            put(e);
        }
    }

    private boolean put(E e) {
        if (size == 0) {
            head.next = Entry.<E>builder()
                    .value(e)
                    .height(1)
                    .build();
            size++;
            return true;
        }

        // construct path to lower level and find place for the new entry
        var pathToLowestLevel = buildPathToLowestLevel(e);
        var previousEntry = pathToLowestLevel.poll();
        assert previousEntry != null;
        if (checkNextIsEqual(previousEntry, e)) {
            return false;
        }

        var pushedEntry = Entry.<E>builder()
                .height(previousEntry.height) // 1 in this context
                .value(e)
                .next(previousEntry.next)
                .down(null)
                .build();
        previousEntry.next = pushedEntry;

        // push entry up by flipping the dice
        while (Math.random() < PROBABILITY && head.height < MAX_DEPTH) {
            pushedEntry = pushEntryIntoUpperLevel(pushedEntry, pathToLowestLevel.poll());
        }
        size++;
        return true;
    }

    /**
     * Returns null if not found same elem
     */
    private Deque<Entry<E>> buildPathToLowestLevel(E e) {
        var previousEntry = head;
        var pathToLowestLevel = new ArrayDeque<Entry<E>>();
        do {
            var entryToCompare = previousEntry.next;
            while (entryToCompare != null && cpr(cmp, e, entryToCompare.value) > 0) {
                var tmp = entryToCompare;
                entryToCompare = entryToCompare.next;
                previousEntry = tmp;
            }
            pathToLowestLevel.addFirst(previousEntry);
            previousEntry = previousEntry.down;
        } while (previousEntry != null);
        return pathToLowestLevel;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        var currentLevelHead = head;
        do {
            Entry<E> currentEntry = currentLevelHead;
            do {
                sb.append(currentEntry.value);
                currentEntry = currentEntry.next;
                sb.append(" -> ");
            } while (currentEntry != null);
            sb.append(System.lineSeparator());
            currentLevelHead = currentLevelHead.down;
        } while (currentLevelHead != null);
        return sb.toString();
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

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        var foundElem = buildPathToLowestLevel((E) o).getFirst();
        return checkNextIsEqual(foundElem, o);
    }

    private boolean checkNextIsEqual(Entry<E> prev, Object o) {
        return prev.next != null && cpr(cmp, prev.next.value, o) == 0;
    }

    @Override
    public boolean add(E e) {
        return put(e);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        return removeElement((E) o);
    }

    private boolean removeElement(E e) {
        var pathToLowestLevel = buildPathToLowestLevel(e);
        var prev = pathToLowestLevel.poll();
        assert prev != null;
        if (!checkNextIsEqual(prev, e)) {
            return false;
        }
        while (prev != null && checkNextIsEqual(prev, e)) {
            unlink(prev);
            prev = pathToLowestLevel.poll();
        }
        size--;
        return true;
    }

    private void unlink(Entry<E> entry) {
        entry.next = entry.next.next;
    }

    //TODO: подумать, как обработать случай с попаданием в существующий элемент
    @Override
    public E lower(E e) {
        var pathToLowestLevel = buildPathToLowestLevel(e);
        if (pathToLowestLevel == null) {
            return null;
        }
        return pathToLowestLevel.getLast().value;
    }

    @Override
    public E floor(E e) {
        var pathToLowestLevel = buildPathToLowestLevel(e);
        if (pathToLowestLevel == null) {
            return null;
        }
        if (pathToLowestLevel.isEmpty()) {
            return e;
        }
        return pathToLowestLevel.getLast().value;
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

    // TODO: завести указатель на последний элемент
    @Override
    public E pollLast() {
        return null;
    }

    // TODO: для скорости можно держать указатель на голову нижнего уровня либо спускаться вниз
    @Override
    public Iterator<E> iterator() {
        var set = this;
        return new Iterator<>() {
            private final SkipListSet<E> s = set;
            private Entry<E> current = Entry.<E>builder()
                    .next(head)
                    .build();

            @Override
            public boolean hasNext() {
                return current.next != null;
            }

            @Override
            public E next() {
                return (current = current.next).value;
            }

            @Override
            public void remove() {
                s.remove(current.value);
            }

            @Override
            public void forEachRemaining(Consumer<? super E> action) {
                Iterator.super.forEachRemaining(action);
            }
        };
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
        return cmp;
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
        return head.value;
    }

    @Override
    public E last() {
        return null;
    }

    @Override
    public int size() {
        return size;
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
