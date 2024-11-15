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
        if (e == null) {
            return false;
        }
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

    @Override
    public E lower(E e) {
        var lowestPrevElem = buildPathToLowestLevel(e).poll();

        return Optional.ofNullable(lowestPrevElem)
                .map(Entry::getValue)
                .orElse(null);
    }

    @Override
    public E floor(E e) {
        var lowestPrevElem = buildPathToLowestLevel(e).poll();

        return Optional.ofNullable(lowestPrevElem)
                .map(entry -> checkNextIsEqual(entry, e) ? entry.next : entry)
                .map(Entry::getValue)
                .orElse(null);
    }

    @Override
    public E ceiling(E e) {
        var lowestPrevElem = buildPathToLowestLevel(e).poll();

        return Optional.ofNullable(lowestPrevElem)
                .map(entry -> checkNextIsEqual(entry, e) ? entry.next.next : entry.next)
                .map(Entry::getValue)
                .orElse(null);
    }

    @Override
    public E higher(E e) {
        var lowestPrevElem = buildPathToLowestLevel(e).poll();

        return Optional.ofNullable(lowestPrevElem)
                .map(Entry::getNext)
                .map(Entry::getValue)
                .orElse(null);
    }

    @Override
    public E pollFirst() {
        var lowestHead = getTheLowestHead();

        var valueToRemove = lowestHead.next.value;

        return size != 0
                ? removeElement(valueToRemove)
                        ? valueToRemove
                        : null
                : null;
    }

    private Entry<E> getTheLowestHead() {
        var lowestHead = head;
        while (lowestHead.height > 1) {
            lowestHead = lowestHead.down;
        }
        return lowestHead;
    }

    private Entry<E> getHighestElement() {
        var iter = head;
        while (true) {
            while (iter.next != null) {
                iter = iter.next;
            }
            if (iter.height == 1) {
                return iter;
            }
            iter = iter.down;
        }
    }

    @Override
    public E pollLast() {
        if (size == 0) {
            return null;
        }
        var highestEntry = getHighestElement();
        removeElement(highestEntry.value);
        return highestEntry.value;
    }

    @Override
    public Iterator<E> iterator() {
        var set = this;
        return new Iterator<>() {
            private final SkipListSet<E> s = SkipListSet.this;
            private Entry<E> current = getTheLowestHead();
            private boolean removed = true;

            @Override
            public boolean hasNext() {
                return current.next != null;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                removed = false;
                return (current = current.next).value;
            }

            @Override
            public void remove() {
                if (removed) {
                    throw new IllegalStateException();
                }
                s.remove(current.value);
                removed = true;
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
        return new NavigableSubSet<>(this,
                false, fromElement, fromInclusive,
                false,toElement, toInclusive);
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return new NavigableSubSet<>(this, true, null, false,
                false, toElement, inclusive);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return new NavigableSubSet<>(this, false, fromElement, inclusive,
                true, null, false);
    }

    @Override
    public Comparator<? super E> comparator() {
        return cmp;
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public E first() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return getTheLowestHead().next.value;
    }

    @Override
    public E last() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        return getHighestElement().value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        head = Entry.<E>builder().height(1).build();
        size = 0;
    }

//    private static class DescendingSubSet<E> extends NavigableSubSet<E> {
//
//    }

    private static class NavigableSubSet<E> extends AbstractSet<E>
        implements NavigableSet<E> {
        SkipListSet<E> skipListSet;
        E from;
        E to;
        Comparator<? super E> cmp;
        boolean fromInclusive;
        boolean toInclusive;
        boolean fromStart;
        boolean toEnd;

//        DescendingSubSet<E> descendingSubSetView;

        public NavigableSubSet(SkipListSet<E> skipListSet,
                               boolean fromStart, E from, boolean fromInclusive,
                               boolean toEnd, E to, boolean toInclusive) {
            if (!fromStart && !toEnd) {
                if (cpr(skipListSet.cmp, from, to) > 0)
                    throw new IllegalArgumentException("fromKey > toKey");
            } else {
                if (!fromStart) // type check
                    cpr(skipListSet.cmp, from, to);
                if (!toEnd)
                    cpr(skipListSet.cmp, from, to);
            }
            this.skipListSet = skipListSet;
            this.fromStart = fromStart;
            this.from = from;
            this.fromInclusive = fromInclusive;
            this.toEnd = toEnd;
            this.to = to;
            this.toInclusive = toInclusive;
            this.cmp = skipListSet.comparator();
        }

        @Override
        public boolean add(E e) {
            if (!checkItemInRange(e))
                throw new IllegalArgumentException("key out of range");
            return skipListSet.add(e);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean contains(Object o) {
            return checkItemInRange((E) o) && skipListSet.contains(o);
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean remove(Object o) {
            return checkItemInRange((E) o) ? skipListSet.remove(o) : false;
        }

        @Override
        public E lower(E e) {
            var lower = skipListSet.lower(e);
            if (checkItemInRange(lower)) {
                return lower;
            }

            return null;
        }

        final boolean tooLow(Object key) {
            if (!fromStart) {
                int c = cpr(cmp, key, from);
                return c < 0 || (c == 0 && !fromInclusive);
            }
            return false;
        }

        final boolean tooHigh(Object key) {
            if (!toEnd) {
                int c = cpr(cmp, key, to);
                return c > 0 || (c == 0 && !toInclusive);
            }
            return false;
        }

        final boolean inRange(Object key) {
            return !tooLow(key) && !tooHigh(key);
        }

        private boolean checkItemInRange(E item) {
            if (item == null) {
                return false;
            }

            return inRange(item);
        }

        @Override
        public E floor(E e) {
            var floor = skipListSet.floor(e);
            if (checkItemInRange(floor)) {
                return floor;
            }

            return null;
        }

        @Override
        public E ceiling(E e) {
            var ceiling = skipListSet.ceiling(e);
            if (checkItemInRange(ceiling)) {
                return ceiling;
            }

            return null;
        }

        @Override
        public E higher(E e) {
            var higher = skipListSet.higher(e);
            if (checkItemInRange(higher)) {
                return higher;
            }

            return null;
        }

        @Override
        public E pollFirst() {
            var lowest = fromInclusive ? skipListSet.ceiling(from) : skipListSet.higher(from);
            if (checkItemInRange(lowest)) {
                skipListSet.remove(lowest);
                return lowest;
            }

            return null;
        }

        @Override
        public E pollLast() {
            var highest = toInclusive ? skipListSet.floor(to) : skipListSet.lower(to);
            if (checkItemInRange(highest)) {
                skipListSet.remove(highest);
                return highest;
            }

            return null;
        }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<>() {
                private final NavigableSet<E> s = NavigableSubSet.this;
                private Entry<E> current = NavigableSubSet.this.skipListSet.buildPathToLowestLevel(from).poll();
                private boolean removed = true;

                @Override
                public boolean hasNext() {
                    return current.next != null && checkItemInRange(current.next.value);
                }

                @Override
                public E next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }

                    removed = false;
                    return (current = current.next).value;
                }

                @Override
                public void remove() {
                    if (removed) {
                        throw new IllegalStateException();
                    }
                    s.remove(current.value);
                    removed = true;
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
            if (!checkItemInRange(fromElement) || !checkItemInRange(toElement)) {
                throw new IllegalArgumentException("bounds out of range");
            }
            return new NavigableSubSet<>(skipListSet,
                    false, fromElement, fromInclusive,
                    false, toElement, toInclusive);
        }

        @Override
        public SortedSet<E> subSet(E fromElement, E toElement) {
            return subSet(fromElement, true, toElement, false);
        }

        @Override
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            if (!checkItemInRange(toElement)) {
                throw new IllegalArgumentException("toElement out of range");
            }
            return new NavigableSubSet<>(skipListSet,
                    true, null, false,
                    false, toElement, inclusive);
        }

        @Override
        public SortedSet<E> headSet(E toElement) {
            return headSet(toElement, false);
        }

        @Override
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            if (!checkItemInRange(fromElement)) {
                throw new IllegalArgumentException("toElement out of range");
            }
            return new NavigableSubSet<>(skipListSet,
                    false, fromElement, inclusive,
                    true, null, false);
        }

        @Override
        public SortedSet<E> tailSet(E fromElement) {
            return tailSet(fromElement, true);
        }

        @Override
        public Comparator<? super E> comparator() {
            return cmp;
        }

        @Override
        public E first() {
            var ceiling = ceiling(from);
            if (ceiling == null) {
                throw new NoSuchElementException();
            }

            return ceiling;
        }

        @Override
        public E last() {
            var floor = floor(to);
            if (floor == null) {
                throw new NoSuchElementException();
            }

            return floor;
        }

        @Override
        public int size() {
            var lowest = skipListSet.buildPathToLowestLevel(from).poll();
            var size = 0;
            while (lowest != null && isBeforeEnd(lowest.getValue())) {
                if (checkItemInRange(lowest.getValue())) {
                    size++;
                }

                lowest = lowest.next;
            }
            return size;
        }

        private boolean isBeforeEnd(E e) {
            if (e == null) {
                return false;
            }
            return !tooHigh(e);
        }

        @Override
        public void clear() {
            var lowest = skipListSet.buildPathToLowestLevel(from).poll();
            while (lowest != null && isBeforeEnd(lowest.getValue())) {
                if (checkItemInRange(lowest.getValue())) {
                    skipListSet.remove(lowest.getValue());
                }

                lowest = lowest.next;
            }
        }

        @Override
        public String toString() {
            var sb = new StringBuilder();
            var currentLevelHead = skipListSet.head;
            do {
                Entry<E> currentEntry = currentLevelHead;
                do {
                    if (currentEntry.value == null || checkItemInRange(currentEntry.value)) {
                        sb.append(currentEntry.value);
                        sb.append(" -> ");
                    }
                    currentEntry = currentEntry.next;
                } while (currentEntry != null);
                sb.append(System.lineSeparator());
                currentLevelHead = currentLevelHead.down;
            } while (currentLevelHead != null);
            return sb.toString();
        }
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
