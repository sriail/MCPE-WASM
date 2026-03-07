package org.simpleframework.xml.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

public class WeakCache<T> implements Cache<T> {
    private WeakCache<T>.SegmentList list;

    public WeakCache() {
        this(10);
    }

    public WeakCache(int size) {
        this.list = new SegmentList(size);
    }

    public boolean isEmpty() {
        Iterator i$ = this.list.iterator();
        while (i$.hasNext()) {
            if (!i$.next().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void cache(Object key, T value) {
        map(key).cache(key, value);
    }

    public T take(Object key) {
        return map(key).take(key);
    }

    public T fetch(Object key) {
        return map(key).fetch(key);
    }

    public boolean contains(Object key) {
        return map(key).contains(key);
    }

    private WeakCache<T>.Segment map(Object key) {
        return this.list.get(key);
    }

    private class SegmentList implements Iterable<WeakCache<T>.Segment> {
        private List<WeakCache<T>.Segment> list = new ArrayList();
        private int size;

        public SegmentList(int size2) {
            this.size = size2;
            create(size2);
        }

        public Iterator<WeakCache<T>.Segment> iterator() {
            return this.list.iterator();
        }

        public WeakCache<T>.Segment get(Object key) {
            int segment = segment(key);
            if (segment < this.size) {
                return this.list.get(segment);
            }
            return null;
        }

        private void create(int size2) {
            int count = size2;
            while (true) {
                int count2 = count;
                count = count2 - 1;
                if (count2 > 0) {
                    this.list.add(new Segment());
                } else {
                    return;
                }
            }
        }

        private int segment(Object key) {
            return Math.abs(key.hashCode() % this.size);
        }
    }

    private class Segment extends WeakHashMap<Object, T> {
        private Segment() {
        }

        public synchronized void cache(Object key, T value) {
            put(key, value);
        }

        public synchronized T fetch(Object key) {
            return get(key);
        }

        public synchronized T take(Object key) {
            return remove(key);
        }

        public synchronized boolean contains(Object key) {
            return containsKey(key);
        }
    }
}
